package com.flyerzrule.mc.customtags.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.database.TagsDao;

public class AddTagCommand implements CommandExecutor {

    public AddTagCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tagadd")) {
            if (args.length == 2) {
                String username = args[0];
                String tagId = args[1];
                Player player = Bukkit.getPlayer(username);

                if (player != null) {
                    CustomTags.getMyLogger().sendDebug(
                            sender.getName() + " sent command " + cmd.getName() + " for player " + player.getName()
                                    + " and tag " + tagId);
                    String uuid = player.getUniqueId().toString();
                    TagsDao db = TagsDao.getInstance();
                    boolean result = db.giveUserTag(uuid, tagId);
                    if (result) {
                        CustomTags.getMyLogger()
                                .sendInfo(String.format("%s added tag %s to user %s(%s)", sender.getName(), tagId,
                                        player.getName(), uuid));
                        sender.sendMessage(String.format("Added tag %s to user %s!", tagId, player.getName()));
                    } else {
                        sender.sendMessage(String.format("Error adding tag %s to user %s!", tagId, player.getName()));
                    }
                    return result;
                }
            }
        }
        return false;
    }
}