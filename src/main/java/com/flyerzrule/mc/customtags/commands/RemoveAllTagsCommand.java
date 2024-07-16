package com.flyerzrule.mc.customtags.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.Database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;

public class RemoveAllTagsCommand implements CommandExecutor {

    public RemoveAllTagsCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tagremoveall")) {
            if (args.length == 1) {
                String username = args[0];
                Player player = Bukkit.getPlayer(username);

                if (player != null) {
                    String uuid = player.getUniqueId().toString();
                    TagsDatabase db = TagsDatabase.getInstance();
                    boolean result = db.removeAllTagsForUser(uuid);

                    // Remove the selected tag
                    Tag currentSelected = db.getSelectedForUser(uuid);
                    if (currentSelected != null) {
                        db.unselectTagForUser(uuid);
                    }

                    if (result) {
                        CustomTags.getPlugin().getLogger()
                                .info(String.format("%s removed all tags from user %s(%s)", sender.getName(),
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