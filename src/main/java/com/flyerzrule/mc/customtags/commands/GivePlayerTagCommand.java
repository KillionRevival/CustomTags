package com.flyerzrule.mc.customtags.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GivePlayerTagCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("mytags")) {
            // Check if the sender is a player
            if (sender instanceof Player) {
                Player player = (Player) sender;

                return true;
            }
        }
        return false;
    }
}