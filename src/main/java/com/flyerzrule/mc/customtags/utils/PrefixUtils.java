package com.flyerzrule.mc.customtags.utils;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;

import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.milkbowl.vault.chat.Chat;

public class PrefixUtils {
    public static void selectPrefix(Player player, String newTag) {
        Chat chat = CustomTags.getChat();

        String serverPrefix = removeTagFromPrefix(player);
        String newPrefix = serverPrefix + " " + newTag + "§r";

        CustomTags.getMyLogger().sendDebug("Setting " + player.getName() + "'s prefix to " + newPrefix);
        chat.setPlayerPrefix(null, player, newPrefix);
    }

    public static void removePrefix(Player player) {
        User user = CustomTags.getLuckPerms().getUserManager().getUser(player.getUniqueId());
        if (user != null) {
            CustomTags.getMyLogger().sendDebug("Removing " + player.getName() + "'s prefix");
            user.data().clear(node -> node.getType() == NodeType.PREFIX);
            CustomTags.getLuckPerms().getUserManager().saveUser(user);
        }
    }

    public static String removeTagFromPrefix(Player player) {
        Chat chat = CustomTags.getChat();
        TagsDatabase db = TagsDatabase.getInstance();

        List<String> allTags = db.getAllTagIds();
        String prefix = chat.getPlayerPrefix(player);
        for (String tag : allTags) {
            prefix = prefix.replace(tag + "§r", "");
        }
        return prefix.trim().replace("§r", "");
    }

    public static void removeAndSetPrefix(Player player) {
        removePrefix(player);

        TagsDatabase db = TagsDatabase.getInstance();
        Tag selectedTag = db.getSelectedForUser(player.getUniqueId().toString());
        if (selectedTag != null) {
            selectPrefix(player, selectedTag.getTag());
        }
    }

    public static void removeAndSetPrefix(String playerId) {
        Player player = CustomTags.getPlugin().getServer().getPlayer(UUID.fromString(playerId));
        if (player != null) {
            CustomTags.getMyLogger().sendDebug(player.getName());
        } else {
            CustomTags.getMyLogger().sendError("PLAYER NULL");
        }

        removeAndSetPrefix(player);
    }
}
