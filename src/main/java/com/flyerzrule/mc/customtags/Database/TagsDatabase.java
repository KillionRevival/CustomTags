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

    private void createOwnedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS ownedTags (userId TEXT, tagId TEXT, PRIMARY KEY (userId, tagId));";
        this.executeQuery(query);
    }

    private void createSelectedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS selectedTags (userId TEXT PRIMARY KEY, tagId TEXT);";
        this.executeQuery(query);
    }

    public List<Tag> getUserOwnedTags(String userId) {
        String query = "SELECT tagId FROM ownedTags WHERE userId = ?;";
        ResultSet rs = this.fetchQuery(query, userId);

        TagsConfig tagsConfig = TagsConfig.getInstance();

        List<Tag> tags = new ArrayList<>();
        try {
            while (rs != null && rs.next()) {
                String tagId = rs.getString("tagId");
                tags.add(tagsConfig.getTagById(tagId));
            }
        } catch (Exception e) {
            System.out.println("Error getting tagId");
        }
        return tags;
    }

    public void giveUserTag(String userId, String tagId) {
        String query = "INSERT INTO ownedTags (userId, tagId) VALUES (?, ?);";
        this.executeUpdate(query, userId, tagId);
    }

    public void removeUserTag(String userId, String tagId) {
        String query = "DELETE FROM ownedTags WHERE userId = ? AND tagId = ?;";
        this.executeUpdate(query, userId, tagId);
    }

    public void removeAllTagsForUser(String userId) {
        String query = "DELETE FROM ownedTags WHERE userId = ?;";
        this.executeUpdate(query, userId);
    }

    public void selectTagForUser(String userId, String tagId) {
        String query = "INSERT INTO selectedTags (userId, tagId) VALUES (?, ?) ON CONFLICT(userId) DO UPDATE SET tagId = excluded.tagId;";
        this.executeUpdate(query, userId, tagId);
    }

    public void unselectTagForUser(String userId) {
        String query = "DELETE FROM selectedTags WHERE userId = ?;";
        this.executeUpdate(query, userId);
    }

    public Tag getSelectedForUser(String userId) {
        String query = "SELECT tagId FROM selectedTags WHERE userId = ?;";

        ResultSet rs = this.fetchQuery(query, userId);

        TagsConfig tagsConfig = TagsConfig.getInstance();

        try {
            if (rs != null && rs.next()) {
                String tagId = rs.getString("tagId");
                System.out.println(tagId);
                return tagsConfig.getTagById(tagId);
            }
        } catch (Exception e) {
            System.out.println("Error getting tagId");
        }
        return null;
    }
}
