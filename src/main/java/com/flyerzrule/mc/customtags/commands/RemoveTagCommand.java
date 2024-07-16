package com.flyerzrule.mc.customtags.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.Database.TagsDatabase;

public class RemoveTagCommand implements CommandExecutor {

    public RemoveTagCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("removetag")) {
            if (args.length == 2) {
                String username = args[0];
                String tagId = args[1];
                Player player = Bukkit.getPlayer(username);

                if (player != null) {
                    String uuid = player.getUniqueId().toString();
                    TagsDatabase db = TagsDatabase.getInstance();
                    db.removeUserTag(uuid, tagId);
                    CustomTags.getPlugin().getLogger()
                            .info(String.format("%s removed tag %s from user %s(%s)", sender.getName(), tagId,
                                    player.getName(), uuid));
                    return true;
                }
            }
        }
        return false;
    }
}