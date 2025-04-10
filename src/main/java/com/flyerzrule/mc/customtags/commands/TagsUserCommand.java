package com.flyerzrule.mc.customtags.commands;

import java.util.List;

import com.flyerzrule.mc.customtags.database.OwnedTagsDao;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.panels.TagsPanel;
import org.jetbrains.annotations.NotNull;

public class TagsUserCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String @NotNull [] args) {
        if (cmd.getName().equalsIgnoreCase("taguser")) {
            TagsConfig tagsConfig = TagsConfig.getInstance();

            if (args.length == 0) {
                return false;
            }

            // Check if the sender is a player
            if (sender instanceof Player pSender) {
                Player player = Bukkit.getPlayer(args[0]);

                if (player != null) {
                    CustomTags.getMyLogger().sendDebug(
                            sender.getName() + " sent command " + cmd.getName() + " for player " + player.getName());

                    List<Tag> tags = tagsConfig.getTags();

                    OwnedTagsDao db = OwnedTagsDao.getInstance();
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
