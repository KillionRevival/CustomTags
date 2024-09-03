package com.flyerzrule.mc.customtags.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;

public class RemoveTagCommand implements CommandExecutor {

    public RemoveTagCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tagremove")) {
            if (args.length == 2) {
                String username = args[0];
                String tagId = args[1];
                Player player = Bukkit.getPlayer(username);

                if (player != null) {
                    CustomTags.getMyLogger().sendDebug(
                            sender.getName() + " sent command " + cmd.getName() + " for player " + player.getName()
                                    + " and tag " + tagId);

                    String uuid = player.getUniqueId().toString();
                    TagsDatabase db = TagsDatabase.getInstance();
                    boolean result = db.removeUserTag(uuid, tagId).isTruthy();

                    // If the removed tag was selected, unselect it
                    Tag currentSelected = db.getSelectedForUser(uuid);
                    if (currentSelected != null) {
                        if (currentSelected.getId().equals(tagId)) {
                            db.unselectTagForUser(uuid);
                        }
                    }

                    if (result) {
                        CustomTags.getMyLogger()
                                .sendInfo(String.format("%s removed tag %s from user %s(%s)", sender.getName(), tagId,
                                        player.getName(), uuid));
                        sender.sendMessage(String.format("Removed tag %s from user %s!", tagId, player.getName()));
                    } else {
                        sender.sendMessage(
                                String.format("Error removing tag %s from user %s!", tagId, player.getName()));
                    }
                    return result;
                }
            }
        }
        return false;
    }
}