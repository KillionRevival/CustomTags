package com.flyerzrule.mc.customtags.api;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.Database.TagsDatabase;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.models.Tag;

public class API {
    public static boolean giveUserTag(Player player, String tagId) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.giveUserTag(getUUID(player), tagId);
    }

    public static boolean removeUserTag(Player player, String tagId) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.removeUserTag(getUUID(player), tagId);
    }

    public static boolean removeAllUserTags(Player player) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.removeAllTagsForUser(getUUID(player));
    }

    public static List<String> getUserTagIds(Player player) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.getUserOwnedTags(getUUID(player)).stream().map(ele -> ele.getId()).collect(Collectors.toList());
    }

    public static boolean setUserSelectedTag(Player player, String tagId) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.selectTagForUser(getUUID(player), tagId);
    }

    public static boolean removeUserSelectedTag(Player player) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.unselectTagForUser(getUUID(player));
    }

    public static String getUserSelectedTagId(Player player) {
        TagsDatabase db = TagsDatabase.getInstance();
        Tag tag = db.getSelectedForUser(getUUID(player));
        if (tag != null) {
            return tag.getId();
        }
        return "";
    }

    public static List<String> getAvailableTagIds() {
        TagsConfig tagsConfig = TagsConfig.getInstance();
        return tagsConfig.getTagIds();
    }

    private static String getUUID(Player player) {
        return player.getUniqueId().toString();
    }
}
