package com.flyerzrule.mc.customtags.models;

import org.bukkit.Material;

public class Tag {
    private String id;
    private String name;
    private String tag;
    private String tagColor;
    private Material item;
    private String description;
    private boolean obtainable;

    public void fromJsonTag(JsonTag jsonTag) {
        this.id = jsonTag.getId();
        this.name = jsonTag.getName();
        this.tag = jsonTag.getTag();
        this.tagColor = jsonTag.getTagColor();
        this.item = Material.getMaterial(jsonTag.getItemId());
        this.description = jsonTag.getDescription();
        this.obtainable = jsonTag.getObtainable();
    }
}
