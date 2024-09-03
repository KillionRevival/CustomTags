package com.flyerzrule.mc.customtags.api;

import java.util.List;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.models.Tag;

import co.killionrevival.killioncommons.database.models.ReturnCode;

public interface CustomTagsAPI {
    ReturnCode giveUserTag(Player player, String tagId);

    ReturnCode removeUserTag(Player player, String tagId);

    ReturnCode removeAllUserTags(Player player);

    List<String> getUserTagIds(Player player);

    ReturnCode setUserSelectedTag(Player player, String tagId);

    ReturnCode removeUserSelectedTag(Player player);

    String getUserSelectedTagId(Player player);

    List<String> getAvailableTagIds();

    ReturnCode createTag(String pluginIdentifier, Tag newTag);

    ReturnCode deleteTag(String pluginIdentifier, String tagId);

    ReturnCode modifyTag(String pluginIdentifier, Tag newTag);

    ReturnCode ensureTag(String pluginIdentifier, Tag newTag);

    boolean tagExists(String tagId);

    Tag getTag(String tagId);
}
