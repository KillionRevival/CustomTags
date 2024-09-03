package com.flyerzrule.mc.customtags.api;

import java.util.List;

import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.models.Tag;

public interface CustomTagsAPI {
    boolean giveUserTag(Player player, String tagId);

    boolean removeUserTag(Player player, String tagId);

    boolean removeAllUserTags(Player player);

    List<String> getUserTagIds(Player player);

    boolean setUserSelectedTag(Player player, String tagId);

    boolean removeUserSelectedTag(Player player);

    String getUserSelectedTagId(Player player);

    List<String> getAvailableTagIds();

    boolean createTag(String pluginIdentifier, Tag newTag);

    boolean deleteTag(String pluginIdentifier, String tagId);

    boolean modifyTag(String pluginIdentifier, Tag newTag);

    boolean ensureTag(String pluginIdentifier, Tag newTag);

    boolean tagExists(String tagId);

    Tag getTag(String tagId);
}
