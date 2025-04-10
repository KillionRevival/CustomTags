package com.flyerzrule.mc.customtags.database;

import com.flyerzrule.mc.customtags.CustomTags;

import co.killionrevival.killioncommons.database.DatabaseConnection;

public class TagsDatabase extends DatabaseConnection {
    private static TagsDatabase instance;

    private TagsDatabase() {
        super(CustomTags.getMyLogger(), CustomTags.getPlugin());
    }

    public static TagsDatabase getInstance() {
        if (TagsDatabase.instance == null) {
            TagsDatabase.instance = new TagsDatabase();
        }
        return TagsDatabase.instance;
    }
}
