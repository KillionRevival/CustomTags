package com.flyerzrule.mc.customtags.database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

import co.killionrevival.killioncommons.database.DatabaseConnection;

public class TagsDatabase extends DatabaseConnection {
    private static TagsDatabase instance;

    private TagsDatabase() {
        super(CustomTags.getKillionUtils().getConsoleUtil());
        createSchema();
        createOwnedTagsTable();
        createSelectedTagsTable();
    }

    public static TagsDatabase getInstance() {
        if (TagsDatabase.instance == null) {
            TagsDatabase.instance = new TagsDatabase();
        }
        return TagsDatabase.instance;
    }

    private boolean createSchema() {
        String query = "CREATE SCHEMA IF NOT EXISTS custom_tags;";
        try {
            this.executeQuery(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean createOwnedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS custom_tags.owned_tags (userid TEXT, tagid TEXT, PRIMARY KEY (userid, tagid));";
        try {
            this.executeQuery(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean createSelectedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS custom_tags.selected_tags (userid TEXT PRIMARY KEY, tagid TEXT);";
        try {
            this.executeQuery(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Tag> getUserOwnedTags(String userId) {
        String query = "SELECT tagid FROM custom_tags.owned_tags WHERE userid = ?;";
        ResultSet rs;
        try {
            rs = this.fetchQuery(query, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        TagsConfig tagsConfig = TagsConfig.getInstance();

        List<Tag> tags = new ArrayList<>();
        try {
            while (rs != null && rs.next()) {
                String tagId = rs.getString("tagid");
                tags.add(tagsConfig.getTagById(tagId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting tagId");
        }
        return tags;
    }

    public boolean giveUserTag(String userId, String tagId) {
        String query = "INSERT INTO custom_tags.owned_tags (userid, tagid) VALUES (?, ?);";
        try {
            this.executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeUserTag(String userId, String tagId) {
        String query = "DELETE FROM custom_tags.owned_tags WHERE userid = ? AND tagid = ?;";
        try {
            this.executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeAllTagsForUser(String userId) {
        String query = "DELETE FROM custom_tags.owned_tags WHERE userid = ?;";
        try {
            this.executeUpdate(query, userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean selectTagForUser(String userId, String tagId) {
        String query = "INSERT INTO custom_tags.selected_tags (userid, tagid) VALUES (?, ?) ON CONFLICT(userid) DO UPDATE SET tagid = excluded.tagid;";
        try {
            this.executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unselectTagForUser(String userId) {
        String query = "DELETE FROM custom_tags.selected_tags WHERE userid = ?;";
        try {
            this.executeUpdate(query, userId);
            PrefixUtils.removeAndSetPrefix(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Tag getSelectedForUser(String userId) {
        String query = "SELECT tagid FROM custom_tags.selected_tags WHERE userid = ?;";

        ResultSet rs;
        try {
            rs = this.fetchQuery(query, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        TagsConfig tagsConfig = TagsConfig.getInstance();

        try {
            if (rs != null && rs.next()) {
                String tagId = rs.getString("tagid");
                return tagsConfig.getTagById(tagId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting tagId");
        }
        return null;
    }
}
