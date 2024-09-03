package com.flyerzrule.mc.customtags.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.bukkit.Material;

public class Tag {
    private String id;
    private String name;
    private String tag;
    private String description;
    private Material material;
    private boolean obtainable;
    private Timestamp createDate;

    public Tag(String id, String name, String tag, String description, String material, boolean obtainable) {
        this.setId(id);
        this.setName(name);
        this.setTag(tag);
        this.setDescription(description);
        this.setMaterial(material);
        this.setObtainable(obtainable);
    }

    public Tag(String id, String name, String tag, String description, Material material, boolean obtainable) {
        this.setId(id);
        this.setName(name);
        this.setTag(tag);
        this.setDescription(description);
        this.setMaterial(material);
        this.setObtainable(obtainable);
    }

    public Tag(String id, String name, String tag, String description, String material, boolean obtainable,
            Timestamp createDate) {
        this.setId(id);
        this.setName(name);
        this.setTag(tag);
        this.setDescription(description);
        this.setMaterial(material);
        this.setObtainable(obtainable);
        this.createDate = createDate;
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

    public Timestamp getCreateDate() {
        return this.createDate;
    }

    public String getCreateDateStr() {
        return new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.createDate);
    }

    private String setColors(String input) {
        return input.replace("&", "§");
    }

    @Override
    public String toString() {
        return String.format(
                "tagId: %s, name: %s, tag: %s, description: %s, material: %s, obtainable: %b, createDate: %s", this.id,
                this.name, this.tag, this.description, this.material.name(), this.obtainable,
                new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(this.createDate));
    }
}
