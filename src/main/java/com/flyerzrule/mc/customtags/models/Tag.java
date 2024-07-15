package com.flyerzrule.mc.customtags.models;

import org.bukkit.Material;

public class Tag {
    private String id;
    private String name;
    private String tag;
    private Material material;
    private String description;
    private boolean obtainable;

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
        this.name = this.setColors(name);
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = this.setColors(tag);
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setMaterial(String itemId) {
        this.material = Material.getMaterial(itemId.split(":")[1].toUpperCase());
    }

    public void setMaterial(Material item) {
        this.material = item;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = this.setColors(description);
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

    private String setColors(String input) {
        return input.replace("&", "§");
    }
}
