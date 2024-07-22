package com.flyerzrule.mc.customtags.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.config.TagsConfig;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.milkbowl.vault.chat.Chat;

public class PrefixUtils {
    public static void selectPrefix(Player player, String newTag) {
        Chat chat = CustomTags.getChat();

        String serverPrefix = removeTagFromPrefix(player);
        String newPrefix = serverPrefix + " " + newTag + "§r";

        chat.setPlayerPrefix(null, player, newPrefix);
    }

    public static void removePrefix(Player player) {
        User user = CustomTags.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            user.data().clear(node -> node.getType() == NodeType.PREFIX);
            CustomTags.getLuckPerms().getUserManager().saveUser(user);
        }
    }

    public static String removeTagFromPrefix(Player player) {
        TagsConfig tagsConfig = TagsConfig.getInstance();
        Chat chat = CustomTags.getChat();

        List<String> allTags = tagsConfig.getTags().stream().map(ele -> ele.getTag()).collect(Collectors.toList());
        String prefix = chat.getPlayerPrefix(player);
        for (String tag : allTags) {
            prefix = prefix.replace(tag + "§r", "");
        }
        return prefix.trim().replace("§r", "");
    }
}