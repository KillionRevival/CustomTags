package com.flyerzrule.mc.customtags.database;

import co.killionrevival.killioncommons.database.DatabaseConnection;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import com.flyerzrule.mc.customtags.CustomTags;

public class TagsDatabase extends DatabaseConnection {
    private static TagsDatabase instance;

    protected TagsDatabase() {
        super(CustomTags.getMyLogger(), CustomTags.getPlugin());
    }

    public static TagsDatabase getInstance() {
        if (instance == null) {
            instance = new TagsDatabase();
        }

        return instance;
    }
}
