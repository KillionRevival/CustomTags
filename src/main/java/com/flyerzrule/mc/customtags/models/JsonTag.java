package com.flyerzrule.mc.customtags.models;

public class JsonTag {
    private String id;
    private String name;
    private String tag;
    private String itemId;
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
        this.name = name;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getItemId() {
        return this.itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getObtainable() {
        return this.obtainable;
    }

    public void setObtainable(boolean obtainable) {
        this.obtainable = obtainable;
    }

}
