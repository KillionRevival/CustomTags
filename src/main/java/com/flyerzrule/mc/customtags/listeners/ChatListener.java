package com.flyerzrule.mc.customtags.listeners;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.events.CustomComponentMessageEvent;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;

public class ChatListener implements Listener {

    public ChatListener() {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        TagsDatabase db = TagsDatabase.getInstance();
        Tag selectedTag = db.getSelectedForUser(player.getUniqueId().toString());

        // Don't do anything for clan and ally chat
        SimpleClans sc = CustomTags.getSimpleClans();
        ClanPlayer clanPlayer = sc.getClanManager().getClanPlayer(player.getUniqueId());
        if (clanPlayer != null
                && (clanPlayer.getChannel() == Channel.CLAN || clanPlayer.getChannel() == Channel.ALLY)) {
            CustomTags.getMyLogger().sendDebug("Chat message is either in clan or ally chat. Skipping...");
            return;
        }

        CompletableFuture<User> userFuture = CustomTags.getLuckPerms().getUserManager().loadUser(player.getUniqueId());
        User user = userFuture.join();

        // Get Premium name color
        String playerColor = "";
        String messageColor = "";

        CachedMetaData userMetaData = user.getCachedData().getMetaData();
        if (userMetaData != null) {
            playerColor = Objects.requireNonNullElse(userMetaData.getMetaValue("username-color"),
                    "");
            messageColor = Objects.requireNonNullElse(userMetaData.getMetaValue("message-color"),
                    "");
        }

        playerColor = playerColor.replace('&', '§');
        messageColor = messageColor.replace('&', '§');

        String prefix = PrefixUtils.removeTagFromPrefix(player);

        // Create the prefix component
        TextComponent prefixComponent = Component.text(" " + prefix.replace('&', '§'));

        // Create tag hover text component
        TextComponent tagComponent = null;
        if (selectedTag != null) {
            String tagHoverContent = String.format("%s\n%s\n%s", selectedTag.getName(), selectedTag.getDescription(),
                    (selectedTag.getObtainable() == true) ? "§aObtainable" : "§4Not-Obtainable");

            tagComponent = Component.text(selectedTag.getTag());
            tagComponent.hoverEvent(Component.text(tagHoverContent));
        }

        // Create the username component
        String nameWithNickname = LegacyComponentSerializer.legacySection().serialize(player.displayName());
        String username;
        if (nameWithNickname.contains("~")) {
            username = nameWithNickname.split("~")[1];
        } else {
            username = player.getName();
        }
        TextComponent usernameComponent = Component.text(playerColor + username + "§r");

        // Create the message component
        TextComponent messageComponent = Component.text(
                messageColor + LegacyComponentSerializer.legacySection().serialize(event.message()) + "§r");

        // Create and call the custom component message event
        CustomComponentMessageEvent customComponentMessageEvent = new CustomComponentMessageEvent(player,
                List.of(prefixComponent, tagComponent, usernameComponent, messageComponent));
        Bukkit.getPluginManager().callEvent(customComponentMessageEvent);

        // Cancel the original event
        event.setCancelled(true);
    }
}
