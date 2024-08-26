package com.flyerzrule.mc.customtags.models;

import org.bukkit.Material;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Tag {
    private String id;
    private String name;
    private String tag;
    private Material material;
    private String description;
    private boolean obtainable;

    public Tag() {
    }

    public void setName(String name) {
        this.name = this.setColors(name);
    }

    public void setTag(String tag) {
        this.tag = this.setColors(tag);
    }

    public void setMaterial(String itemId) {
        this.material = Material.getMaterial(itemId.split(":")[1].toUpperCase());
    }

    public void setMaterial(Material item) {
        this.material = item;
    }

    public void setDescription(String description) {
        this.description = this.setColors(description);
    }

    private String setColors(String input) {
        return input.replace("&", "§");
    }
}
