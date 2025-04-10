package com.flyerzrule.mc.customtags.api;

import java.util.List;
import java.util.stream.Collectors;

import com.flyerzrule.mc.customtags.database.OwnedTagsDao;
import com.flyerzrule.mc.customtags.database.SelectedTagsDao;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

public class API {
    public static boolean giveUserTag(Player player, String tagId) {
        OwnedTagsDao db = OwnedTagsDao.getInstance();
        return db.giveUserTag(getUUID(player), tagId);
    }

    public static boolean removeUserTag(Player player, String tagId) {
        SelectedTagsDao selectedTagsDao = SelectedTagsDao.getInstance();
        OwnedTagsDao ownedTagsDao = OwnedTagsDao.getInstance();
        Tag currentSelected = selectedTagsDao.getSelectedForUser(getUUID(player));
        if (currentSelected != null) {
            if (currentSelected.getId().equals(tagId)) {
                selectedTagsDao.unselectTagForUser(getUUID(player));
            }
        }
        return ownedTagsDao.removeUserTag(getUUID(player), tagId);
    }

    public static boolean removeAllUserTags(Player player) {
        OwnedTagsDao ownedTagsDao = OwnedTagsDao.getInstance();
        SelectedTagsDao selectedTagsDao = SelectedTagsDao.getInstance();
        Tag currentSelected = selectedTagsDao.getSelectedForUser(getUUID(player));
        if (currentSelected != null) {
            selectedTagsDao.unselectTagForUser(getUUID(player));
        }
        return ownedTagsDao.removeAllTagsForUser(getUUID(player));
    }

    public static List<String> getUserTagIds(Player player) {
        OwnedTagsDao ownedTagsDao = OwnedTagsDao.getInstance();
        return ownedTagsDao.getUserOwnedTags(getUUID(player)).stream().map(Tag::getId).toList();
    }

    public static boolean setUserSelectedTag(Player player, String tagId) {
        SelectedTagsDao selectedTagsDao = SelectedTagsDao.getInstance();
        TagsConfig tagsConfig = TagsConfig.getInstance();
        Tag tag = tagsConfig.getTagById(tagId);
        PrefixUtils.selectPrefix(player, tag.getTag());
        return selectedTagsDao.selectTagForUser(getUUID(player), tagId);
    }

    public static boolean removeUserSelectedTag(Player player) {
        SelectedTagsDao selectedTagsDao = SelectedTagsDao.getInstance();
        PrefixUtils.removePrefix(player);
        return selectedTagsDao.unselectTagForUser(getUUID(player));
    }

    public static String getUserSelectedTagId(Player player) {
        SelectedTagsDao selectedTagsDao = SelectedTagsDao.getInstance();
        Tag tag = selectedTagsDao.getSelectedForUser(getUUID(player));

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
