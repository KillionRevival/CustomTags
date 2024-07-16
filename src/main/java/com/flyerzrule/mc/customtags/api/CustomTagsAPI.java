package com.flyerzrule.mc.customtags.api;

import java.util.List;

import org.bukkit.entity.Player;

public interface CustomTagsAPI {
    boolean giveUserTag(Player player, String tagId);

    boolean removeUserTag(Player player, String tagId);

    boolean removeAllUserTags(Player player);

    List<String> getUserTagIds(Player player);

    boolean setUserSelectedTag(Player player, String tagId);

    boolean removeUserSelectedTag(Player player);

    String getUserSelectedTagId(Player player);

    List<String> getAvailableTagIds();
}
