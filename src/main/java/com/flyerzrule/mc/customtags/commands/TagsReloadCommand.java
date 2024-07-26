package com.flyerzrule.mc.customtags.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.config.TagsConfig;

public class TagsReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tagreload")) {
            TagsConfig tagsConfig = TagsConfig.getInstance();

            CustomTags.getMyLogger().sendInfo(String.format("Player: %s triggered a reload!",
                    sender.getName()));
            sender.sendMessage("Triggered a reload!");
            tagsConfig.parseFile();
            return true;
        }
        return false;
    }
}
