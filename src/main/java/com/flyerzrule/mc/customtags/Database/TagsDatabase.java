package com.flyerzrule.mc.customtags.Database;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.models.Tag;

public class TagsDatabase extends Database {
    private static TagsDatabase instance;

    private TagsDatabase() {
        super("Tags");
        createOwnedTagsTable();
        createSelectedTagsTable();
    }

    public static TagsDatabase getInstance() {
        if (TagsDatabase.instance == null) {
            TagsDatabase.instance = new TagsDatabase();
        }
        return TagsDatabase.instance;
    }

    private boolean createOwnedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS ownedTags (userId TEXT, tagId TEXT, PRIMARY KEY (userId, tagId));";
        try {
            this.executeQuery(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean createSelectedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS selectedTags (userId TEXT PRIMARY KEY, tagId TEXT);";
        try {
            this.executeQuery(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Tag> getUserOwnedTags(String userId) {
        String query = "SELECT tagId FROM ownedTags WHERE userId = ?;";
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
                String tagId = rs.getString("tagId");
                tags.add(tagsConfig.getTagById(tagId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting tagId");
        }
        return tags;
    }

    public boolean giveUserTag(String userId, String tagId) {
        String query = "INSERT INTO ownedTags (userId, tagId) VALUES (?, ?);";
        try {
            this.executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeUserTag(String userId, String tagId) {
        String query = "DELETE FROM ownedTags WHERE userId = ? AND tagId = ?;";
        try {
            this.executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeAllTagsForUser(String userId) {
        String query = "DELETE FROM ownedTags WHERE userId = ?;";
        try {
            this.executeUpdate(query, userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean selectTagForUser(String userId, String tagId) {
        String query = "INSERT INTO selectedTags (userId, tagId) VALUES (?, ?) ON CONFLICT(userId) DO UPDATE SET tagId = excluded.tagId;";
        try {
            this.executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unselectTagForUser(String userId) {
        String query = "DELETE FROM selectedTags WHERE userId = ?;";
        try {
            this.executeUpdate(query, userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Tag getSelectedForUser(String userId) {
        String query = "SELECT tagId FROM selectedTags WHERE userId = ?;";

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
                String tagId = rs.getString("tagId");
                return tagsConfig.getTagById(tagId);
            }
        } catch (Exception e) {
            System.out.println("Error getting tagId");
        }
        return null;
    }
}
