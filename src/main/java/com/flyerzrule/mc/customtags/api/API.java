package com.flyerzrule.mc.customtags.api;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.models.TagUpdateMethod;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

import co.killionrevival.killioncommons.database.models.ReturnCode;

public class API {
    public static ReturnCode giveUserTag(Player player, String tagId) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.giveUserTag(getUUID(player), tagId);
    }

    public static ReturnCode removeUserTag(Player player, String tagId) {
        TagsDatabase db = TagsDatabase.getInstance();
        Tag currentSelected = db.getSelectedForUser(getUUID(player));
        if (currentSelected != null) {
            if (currentSelected.getId().equals(tagId)) {
                db.unselectTagForUser(getUUID(player));
            }
        }
        return db.removeUserTag(getUUID(player), tagId);
    }

    public static ReturnCode removeAllUserTags(Player player) {
        TagsDatabase db = TagsDatabase.getInstance();
        Tag currentSelected = db.getSelectedForUser(getUUID(player));
        if (currentSelected != null) {
            db.unselectTagForUser(getUUID(player));
        }
        return db.removeAllTagsForUser(getUUID(player));
    }

    public static List<String> getUserTagIds(Player player) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.getUserOwnedTags(getUUID(player)).stream().map(ele -> ele.getId()).collect(Collectors.toList());
    }

    public static ReturnCode setUserSelectedTag(Player player, String tagId) {
        TagsDatabase db = TagsDatabase.getInstance();
        Tag tag = db.getTag(tagId);
        PrefixUtils.selectPrefix(player, tag.getTag());
        return db.selectTagForUser(getUUID(player), tagId);
    }

    public static ReturnCode removeUserSelectedTag(Player player) {
        TagsDatabase db = TagsDatabase.getInstance();
        PrefixUtils.removePrefix(player);
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
        TagsDatabase db = TagsDatabase.getInstance();
        return db.getAllTagIds();
    }

    public static ReturnCode createTag(String pluginIdentifier, Tag newTag) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.createTag(newTag, TagUpdateMethod.PLUGIN, pluginIdentifier);
    }

    public static ReturnCode deleteTag(String pluginIdentifier, String tagId) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.deleteTag(tagId, TagUpdateMethod.PLUGIN, pluginIdentifier);
    }

    public static ReturnCode modifyTag(String pluginIdentifier, Tag newTag) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.modifyTag(newTag, TagUpdateMethod.PLUGIN, pluginIdentifier);
    }

    public static ReturnCode ensureTag(String pluginIdentifier, Tag newTag) {
        TagsDatabase db = TagsDatabase.getInstance();
        if (!db.tagExists(newTag.getId())) {
            return db.createTag(newTag, TagUpdateMethod.PLUGIN, pluginIdentifier);
        }
        return ReturnCode.ALREADY_EXISTS;
    }

    public static boolean tagExists(String tagId) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.tagExists(tagId);
    }

    public static Tag getTag(String tagId) {
        TagsDatabase db = TagsDatabase.getInstance();
        return db.getTag(tagId);
    }

    private static String getUUID(Player player) {
        return player.getUniqueId().toString();
    }
}
