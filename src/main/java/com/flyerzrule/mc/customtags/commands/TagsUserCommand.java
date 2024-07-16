package com.flyerzrule.mc.customtags.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.Database.TagsDatabase;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.panels.TagsPanel;

public class TagsUserCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("taguser")) {
            TagsConfig tagsConfig = TagsConfig.getInstance();

            if (args.length == 0) {
                return false;
            }

            // Check if the sender is a player
            if (sender instanceof Player) {
                Player pSender = (Player) sender;
                Player player = Bukkit.getPlayer(args[0]);

                if (player != null) {
                    List<Tag> tags = tagsConfig.getTags();

                    TagsDatabase db = TagsDatabase.getInstance();
                    List<Tag> ownedTags = db.getUserOwnedTags(player.getUniqueId().toString());

                    new TagsPanel(pSender, player, tags, ownedTags).openOwned();
                    return true;
                } else {
                    sender.sendMessage("Invalid username!");
                }
            } else {
                sender.sendMessage("This command can only be used by players!");
            }
        }
        return false;
    }
}
