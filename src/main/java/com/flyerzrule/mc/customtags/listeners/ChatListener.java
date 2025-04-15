package com.flyerzrule.mc.customtags.listeners;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import com.flyerzrule.mc.customtags.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.database.TagsDao;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;

public class ChatListener implements Listener {

    public ChatListener() {
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        String prefix = PrefixUtils.removeTagFromPrefix(player);

        SimpleClans sc = CustomTags.getSimpleClans();
        ClanPlayer clanPlayer = sc.getClanManager().getClanPlayer(player.getUniqueId());
        if (clanPlayer != null
                && (clanPlayer.getChannel() == Channel.CLAN || clanPlayer.getChannel() == Channel.ALLY)) {
            CustomTags.getMyLogger().sendDebug("Chat message is either in clan or ally chat. Skipping...");
            return;
        }

        CompletableFuture<User> userFuture = CustomTags.getLuckPerms().getUserManager().loadUser(player.getUniqueId());
        User user = userFuture.join();

        CachedMetaData userMetaData = user.getCachedData().getMetaData();
        String playerColor = Objects.requireNonNullElse(userMetaData.getMetaValue("username-color"),
                "");
        String messageColor = Objects.requireNonNullElse(userMetaData.getMetaValue("message-color"),
                "");

        NamedTextColor namedPlayerColor = Utils.getColorFromTag(playerColor);
        NamedTextColor namedMessageColor = Utils.getColorFromTag(messageColor);

        TagsDao db = TagsDao.getInstance();
        Tag selectedTag = db.getSelectedForUser(player.getUniqueId().toString());

        // Rank
        Component rank = LegacyComponentSerializer.legacyAmpersand().deserialize(prefix);

        // Username
        String nameWithNickname = LegacyComponentSerializer.legacySection().serialize(player.displayName());
        String playerUsername;

        if (nameWithNickname.contains("~")) {
            playerUsername = nameWithNickname.split("~")[1];
        } else {
            playerUsername = player.getName();
        }
        Component username = Component.text(playerUsername).color(namedPlayerColor);

        // Message
        Component originalMessage = event.message().color(namedMessageColor);

        // Tag
        Component tag = Component.empty();
        if (selectedTag != null) {
            tag = LegacyComponentSerializer.legacyAmpersand().deserialize(selectedTag.getTag())
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(selectedTag.getDescription())));
        }

        // Combine all components
        Component fullMessage;
        if (selectedTag != null) {
            fullMessage = Component.empty()
                .appendSpace()
                .append(rank)
                .appendSpace()
                .append(tag)
                .appendSpace()
                .append(username)
                .append(Component.text(":", NamedTextColor.DARK_GRAY))
                .appendSpace()
                .append(originalMessage);
        } else {
            fullMessage = Component.empty()
                .appendSpace()
                .append(rank)
                .appendSpace()
                .append(username)
                .append(Component.text(":", NamedTextColor.DARK_GRAY))
                .appendSpace()
                .append(originalMessage);
        }


        event.setCancelled(true);

        Bukkit.getConsoleSender().sendMessage(LegacyComponentSerializer.legacySection().serialize(fullMessage));
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (Utils.isPlayerIgnored(player, p)) {
                continue;
            }
            p.sendMessage(fullMessage);
        }
    }
}
