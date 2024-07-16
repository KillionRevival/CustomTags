package com.flyerzrule.mc.customtags.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.Database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class ChatListener implements Listener {

    public ChatListener() {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String prefix = CustomTags.getChat().getPlayerPrefix(player);

        System.out.println(player.getDisplayName());
        System.out.println(MiniMessage.miniMessage().serialize(event.message()));

        TagsDatabase db = TagsDatabase.getInstance();
        CustomTags.getPlugin().getLogger().info(player.getUniqueId().toString());
        Tag selectedTag = db.getSelectedForUser(player.getUniqueId().toString());

        if (selectedTag != null) {
            // Create hover text component
            String hoverContent = String.format("%s\n%s\n%s", selectedTag.getName(), selectedTag.getDescription(),
                    (selectedTag.getObtainable() == true) ? "§aObtainable" : "§4Not-Obtainable");

            TextComponent hoverComponent = new TextComponent(selectedTag.getTag());
            hoverComponent
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverContent)));

            // Create the prefix component
            TextComponent prefixComponent = new TextComponent(prefix.replace('&', '§'));
            System.out.println(player.displayName().toString());
            TextComponent usernameComponent = new TextComponent(
                    GsonComponentSerializer.gson().serialize(player.displayName()));

            // Create the message component
            TextComponent messageComponent = new TextComponent(MiniMessage.miniMessage().serialize(event.message()));

            // Combine prefix, hoverable tag, and message
            BaseComponent[] baseComponents = new ComponentBuilder("")
                    .append(prefixComponent)
                    .append(" ")
                    .append(hoverComponent)
                    .append(" ")
                    .append(usernameComponent)
                    .append(" ")
                    .append(messageComponent)
                    .create();

            // Cancel the original event and send the new formatted message
            event.setCancelled(true);
            Bukkit.getServer().getConsoleSender().sendMessage(baseComponents);
            for (Player p : CustomTags.getPlugin().getServer().getOnlinePlayers()) {
                p.sendMessage(baseComponents);
            }
        }
    }
}
