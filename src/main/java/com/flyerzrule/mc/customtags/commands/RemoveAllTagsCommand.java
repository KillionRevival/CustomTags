package com.flyerzrule.mc.customtags.commands;

import com.flyerzrule.mc.customtags.database.OwnedTagsDao;
import com.flyerzrule.mc.customtags.database.SelectedTagsDao;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.models.Tag;
import org.jetbrains.annotations.NotNull;

public class RemoveAllTagsCommand implements CommandExecutor {

    public RemoveAllTagsCommand() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String @NotNull [] args) {
        if (cmd.getName().equalsIgnoreCase("tagremoveall")) {
            if (args.length == 1) {
                String username = args[0];
                Player player = Bukkit.getPlayer(username);

                if (player != null) {
                    CustomTags.getMyLogger().sendDebug(
                            sender.getName() + " sent command " + cmd.getName() + " for player " + player.getName());

                    String uuid = player.getUniqueId().toString();
                    OwnedTagsDao ownedTagsDao = OwnedTagsDao.getInstance();
                    SelectedTagsDao selectedTagsDao = SelectedTagsDao.getInstance();
                    boolean result = ownedTagsDao.removeAllTagsForUser(uuid);

                    // Remove the selected tag
                    Tag currentSelected = selectedTagsDao.getSelectedForUser(uuid);
                    if (currentSelected != null) {
                        selectedTagsDao.unselectTagForUser(uuid);
                    }

                    if (result) {
                        CustomTags.getMyLogger()
                                .sendInfo(String.format("%s removed all tags from user %s(%s)", sender.getName(),
                                        player.getName(), uuid));
                        sender.sendMessage(String.format("Removed all tags from user %s!", player.getName()));
                    } else {
                        sender.sendMessage(
                                String.format("Error removing all tags from user %s!", player.getName()));
                    }
                    return result;
                }
            }
        }
        return false;
    }
}