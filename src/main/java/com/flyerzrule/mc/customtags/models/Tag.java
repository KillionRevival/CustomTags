package com.flyerzrule.mc.customtags.models;

public class Tag {
    private String name;
    private String id;
    private String description;

    public Tag(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Tag(String id, String name) {
        this.id = id;
        this.name = name;
        this.description = "";
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}
