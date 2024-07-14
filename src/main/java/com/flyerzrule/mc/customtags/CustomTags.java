package com.flyerzrule.mc.customtags;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.customtags.panels.CustomTagsPanel;

import mc.obliviate.inventory.InventoryAPI;

public class CustomTags extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("CustomTags has been enabled!");
        new InventoryAPI(this).init();
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomTags has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("mytags")) {
            // Check if the sender is a player
            if (sender instanceof Player) {
                Player player = (Player) sender;
                new CustomTagsPanel(player).open();
                return true;
            }
        }
        return false;
    }
}