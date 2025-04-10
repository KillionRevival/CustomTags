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
import com.flyerzrule.mc.customtags.utils.Utils;
import org.jetbrains.annotations.NotNull;

public class AddAllTagsCommand implements CommandExecutor {

    public AddAllTagsCommand() {
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String @NotNull [] args) {
        if (cmd.getName().equalsIgnoreCase("tagaddall")) {
            if (args.length == 1) {
                String username = args[0];
                Player player = Bukkit.getPlayer(username);

                if (player != null) {
                    CustomTags.getMyLogger().sendDebug(
                            sender.getName() + " sent command " + cmd.getName() + " for player " + player.getName());
                    String uuid = player.getUniqueId().toString();
                    OwnedTagsDao db = OwnedTagsDao.getInstance();
                    TagsConfig tagsConfig = TagsConfig.getInstance();

                    List<Tag> allTags = tagsConfig.getTags();
                    List<Tag> ownedTags = db.getUserOwnedTags(uuid);

                    List<String> diffTagIds = Utils.getDifferenceTags(allTags, ownedTags).stream()
                            .map(Tag::getId).toList();

                    boolean result = true;

                    for (String tagId : diffTagIds) {
                        result &= db.giveUserTag(uuid, tagId);
                    }

                    if (result) {
                        CustomTags.getMyLogger()
                                .sendInfo(String.format("%s added all tags to user %s(%s)", sender.getName(),
                                        player.getName(), uuid));
                        sender.sendMessage(String.format("Added all tags to user %s!", player.getName()));
                    } else {
                        sender.sendMessage(
                                String.format("Error adding all tags to user %s!", player.getName()));
                    }
                    return result;
                }
            }
        }
        return false;
    }
}