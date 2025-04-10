package com.flyerzrule.mc.customtags.commands;

import java.util.List;

import com.flyerzrule.mc.customtags.database.OwnedTagsDao;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.panels.TagsPanel;
import org.jetbrains.annotations.NotNull;

public class TagsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String @NotNull [] args) {
        if (cmd.getName().equalsIgnoreCase("tags")) {
            TagsConfig tagsConfig = TagsConfig.getInstance();

            // Check if the sender is a player
            if (sender instanceof Player player) {

                CustomTags.getMyLogger().sendDebug(
                        player.getName() + " sent command " + cmd.getName());

                List<Tag> tags = tagsConfig.getTags();

                OwnedTagsDao db = OwnedTagsDao.getInstance();
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
