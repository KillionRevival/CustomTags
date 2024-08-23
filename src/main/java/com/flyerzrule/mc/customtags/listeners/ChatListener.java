package com.flyerzrule.mc.customtags.listeners;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;

import org.kif.reincarceration.api.IReincarcerationAPI;

public class ChatListener implements Listener {

    public ChatListener() {
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String prefix = PrefixUtils.removeTagFromPrefix(player);

        SimpleClans sc = CustomTags.getSimpleClans();
        ClanPlayer clanPlayer = sc.getClanManager().getClanPlayer(player.getUniqueId());
        if (clanPlayer != null
                && (clanPlayer.getChannel() == Channel.CLAN || clanPlayer.getChannel() == Channel.ALLY)) {
            CustomTags.getMyLogger().sendDebug("Chat message is either in clan or ally chat. Skipping...");
            return;
        }

        IReincarcerationAPI rcApi = Objects
                .requireNonNull(CustomTags.getPlugin().getServer().getServicesManager()
                        .getRegistration(IReincarcerationAPI.class))
                .getProvider();

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

        TagsDatabase db = TagsDatabase.getInstance();
        Tag selectedTag = db.getSelectedForUser(player.getUniqueId().toString());

        // Create the prefix component
        TextComponent prefixComponent = new TextComponent(" " + prefix.replace('&', '§'));
        String prefixHoverContent = "§r";
        if (rcApi.isPlayerInCycle(player)) {
            CustomTags.getMyLogger().sendDebug("Player " + player.getDisplayName() + " is in cycle");
            try {
                prefixHoverContent = String.format("Current modifier: §a%s§r",
                        rcApi.getPlayerModifier(player).getName());
            } catch (SQLException e) {
                CustomTags.getMyLogger()
                        .sendError("Failed to get player: " + player.getDisplayName() + " modifier");
            }
        } else {
            CustomTags.getMyLogger().sendDebug("Player " + player.getDisplayName() + " is not in cycle");
        }
        prefixComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(prefixHoverContent)));

        // Create tag hover text component
        TextComponent tagComponent = null;
        if (selectedTag != null) {
            String tagHoverContent = String.format("%s\n%s\n%s", selectedTag.getName(), selectedTag.getDescription(),
                    (selectedTag.getObtainable() == true) ? "§aObtainable" : "§4Not-Obtainable");

            tagComponent = new TextComponent(selectedTag.getTag());
            tagComponent
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(tagHoverContent)));
        }

        // Create the username component
        String nameWithNickname = LegacyComponentSerializer.legacySection().serialize(player.displayName());
        String username;
        if (nameWithNickname.contains("~")) {
            username = nameWithNickname.split("~")[1];
        } else {
            username = player.getName();
        }
        TextComponent usernameComponent = new TextComponent(playerColor + username + "§r");
        usernameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§r")));

        // Create the message component
        TextComponent messageComponent = new TextComponent(
                messageColor + LegacyComponentSerializer.legacySection().serialize(event.message()) + "§r");
        messageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§r")));

        // Combine prefix, hoverable tag, and message
        BaseComponent[] baseComponents;
        if (selectedTag != null) {
            baseComponents = new ComponentBuilder("")
                    .append(prefixComponent)
                    .append(" ")
                    .append(tagComponent)
                    .append(" ")
                    .append(usernameComponent)
                    .append("§8: §r")
                    .append(messageComponent)
                    .create();
        } else {
            baseComponents = new ComponentBuilder("")
                    .append(prefixComponent)
                    .append(" ")
                    .append(usernameComponent)
                    .append("§8: §r")
                    .append(messageComponent)
                    .create();
        }

        // Cancel the original event and send the new formatted message
        event.setCancelled(true);
        Bukkit.getServer().getConsoleSender().sendMessage(baseComponents);
        for (Player p : CustomTags.getPlugin().getServer().getOnlinePlayers()) {
            p.sendMessage(baseComponents);
        }
    }
}
