package com.flyerzrule.mc.customtags.database;

import co.killionrevival.killioncommons.database.DataAccessObject;
import co.killionrevival.killioncommons.database.models.ReturnCode;
import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.database.models.OwnedTag;
import com.flyerzrule.mc.customtags.models.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OwnedTagsDao extends DataAccessObject<OwnedTag> {
    private static OwnedTagsDao instance;

    public OwnedTagsDao(final TagsDatabase database) {
        super(database);

        boolean success = true;
        success &= createSchema() == ReturnCode.SUCCESS;
        success &= createTable();

        if (success) {
            CustomTags.getMyLogger().sendDebug("OwnedTagsDao initialized");
        } else {
            CustomTags.getMyLogger().sendError("Failed to initialize OwnedTagsDao!");
        }
    }

    public static OwnedTagsDao getInstance() {
        if (OwnedTagsDao.instance == null) {
            OwnedTagsDao.instance = new OwnedTagsDao(TagsDatabase.getInstance());
        }
        return OwnedTagsDao.instance;
    }

    public List<OwnedTag> parse(final ResultSet resultSet) throws SQLException {
        final List<OwnedTag> tags = new java.util.ArrayList<>();
        while (resultSet != null && resultSet.next()) {
            String userId = resultSet.getString("userid");
            String tagId = resultSet.getString("tagid");
            tags.add(new OwnedTag(userId, tagId));
        }
        return tags;
    }

    private ReturnCode createSchema() {
        return createSchemaIfNotExists("custom_tags");
    }

    private boolean createTable() {
        String query = "CREATE TABLE IF NOT EXISTS custom_tags.owned_tags (userid TEXT, tagid TEXT, PRIMARY KEY (userid, tagid));";
        try {
            this.executeQuery(query);
            return true;
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError("Failed to create owned_tags table!", e);
        }
        return false;
    }

    public List<Tag> getUserOwnedTags(String userId) {
        String query = "SELECT tagid FROM custom_tags.owned_tags WHERE userid = ?;";
        ResultSet rs;
        List<OwnedTag> userOwnedTags;
        try {
            userOwnedTags = this.fetchQuery(query, userId);
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError("Failed to fetch owned tags!", e);
            return new ArrayList<>();
        }

        TagsConfig tagsConfig = TagsConfig.getInstance();

        List<Tag> tags = new ArrayList<>();
        try {
            for (OwnedTag ownedTag : userOwnedTags) {
                tags.add(tagsConfig.getTagById(ownedTag.tagId()));
            }
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError("Error getting tagId", e);
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
            CustomTags.getMyLogger().sendError("Failed to give user tag!", e);
        }
        return false;
    }

    public boolean removeUserTag(String userId, String tagId) {
        String query = "DELETE FROM custom_tags.owned_tags WHERE userid = ? AND tagid = ?;";
        try {
            this.executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError(e.getMessage());
        }
        return false;
    }

    public boolean removeAllTagsForUser(String userId) {
        String query = "DELETE FROM custom_tags.owned_tags WHERE userid = ?;";
        try {
            this.executeUpdate(query, userId);
            return true;
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError("Failed to remove all tags for user " + userId, e);
        }
        return false;
    }
}
