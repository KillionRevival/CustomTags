package com.flyerzrule.mc.customtags.api;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.database.TagsDao;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

public class API {
    public static boolean giveUserTag(Player player, String tagId) {
        TagsDao db = TagsDao.getInstance();
        return db.giveUserTag(getUUID(player), tagId);
    }

    public static boolean removeUserTag(Player player, String tagId) {
        TagsDao db = TagsDao.getInstance();
        Tag currentSelected = db.getSelectedForUser(getUUID(player));
        if (currentSelected != null) {
            if (currentSelected.getId().equals(tagId)) {
                db.unselectTagForUser(getUUID(player));
            }
        }
        return db.removeUserTag(getUUID(player), tagId);
    }

    public static boolean removeAllUserTags(Player player) {
        TagsDao db = TagsDao.getInstance();
        Tag currentSelected = db.getSelectedForUser(getUUID(player));
        if (currentSelected != null) {
            db.unselectTagForUser(getUUID(player));
        }
        return db.removeAllTagsForUser(getUUID(player));
    }

    public static List<String> getUserTagIds(Player player) {
        TagsDao db = TagsDao.getInstance();
        return db.getUserOwnedTags(getUUID(player)).stream().map(ele -> ele.getId()).collect(Collectors.toList());
    }

    public static boolean setUserSelectedTag(Player player, String tagId) {
        TagsDao db = TagsDao.getInstance();
        TagsConfig tagsConfig = TagsConfig.getInstance();
        Tag tag = tagsConfig.getTagById(tagId);
        PrefixUtils.selectPrefix(player, tag.getTag());
        return db.selectTagForUser(getUUID(player), tagId);
    }

    public static boolean removeUserSelectedTag(Player player) {
        TagsDao db = TagsDao.getInstance();
        PrefixUtils.removePrefix(player);
        return db.unselectTagForUser(getUUID(player));
    }

    public static String getUserSelectedTagId(Player player) {
        TagsDao db = TagsDao.getInstance();
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
