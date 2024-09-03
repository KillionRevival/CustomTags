package com.flyerzrule.mc.customtags.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.events.CustomComponentMessageEvent;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;

public class CustomComponentMessageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCustomComponentMessage(CustomComponentMessageEvent event) {
        // Cancel the event so that no other plugins can modify the components
        event.setCancelled(true);

        final List<Component> components = event.getComponents();

        Component joinedComponents = Component.join(JoinConfiguration.separator(Component.text(" ")), components);

        Bukkit.getServer().getConsoleSender().sendMessage(Identity.nil(), joinedComponents);
        for (Player p : CustomTags.getPlugin().getServer().getOnlinePlayers()) {
            p.sendMessage(joinedComponents);
        }
    }
}
