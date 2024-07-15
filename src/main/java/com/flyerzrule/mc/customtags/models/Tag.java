package com.flyerzrule.mc.customtags.models;

import org.bukkit.Material;

public class Tag {
    private String id;
    private String name;
    private String tag;
    private Material item;
    private String description;
    private boolean obtainable;

    public void fromJsonTag(JsonTag jsonTag) {
        this.id = jsonTag.getId();
        this.name = jsonTag.getName();
        this.tag = jsonTag.getTag();
        this.item = Material.getMaterial(jsonTag.getItemId().split(":")[1].toUpperCase());
        this.description = jsonTag.getDescription();
        this.obtainable = jsonTag.getObtainable();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Material getItem() {
        return this.item;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isObtainable() {
        return this.obtainable;
    }

    public boolean getObtainable() {
        return this.obtainable;
    }

    public void setObtainable(boolean obtainable) {
        this.obtainable = obtainable;
    }

}
