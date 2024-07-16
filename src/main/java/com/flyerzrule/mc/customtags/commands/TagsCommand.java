package com.flyerzrule.mc.customtags.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.panels.TagsPanel;

public class TagsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tags")) {
            TagsConfig tagsConfig = TagsConfig.getInstance();

            // Check if the sender is a player
            if (sender instanceof Player) {
                Player player = (Player) sender;

                List<Tag> tags = tagsConfig.getTags();

                TagsDatabase db = TagsDatabase.getInstance();
                List<Tag> ownedTags = db.getUserOwnedTags(player.getUniqueId().toString());

                new TagsPanel(player, player, tags, ownedTags).openOwned();
                return true;
            } else {
                sender.sendMessage("This command can only be used by players!");
            }
        }
        return false;
    }
}
