package com.flyerzrule.mc.customtags.database;

import co.killionrevival.killioncommons.database.DataAccessObject;
import co.killionrevival.killioncommons.database.models.ReturnCode;
import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.database.models.SelectedTags;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SelectedTagsDao extends DataAccessObject<SelectedTags> {
    private static SelectedTagsDao instance;

    public SelectedTagsDao(final TagsDatabase database) {
        super(database);

        boolean success = true;
        success &= createSchema() == ReturnCode.SUCCESS;
        success &= createTable();

        if (success) {
            CustomTags.getMyLogger().sendDebug("SelectedTagsDao initialized");
        } else {
            CustomTags.getMyLogger().sendError("Failed to initialize SelectedTagsDao!");
        }
    }

    public static SelectedTagsDao getInstance() {
        if (SelectedTagsDao.instance == null) {
            SelectedTagsDao.instance = new SelectedTagsDao(TagsDatabase.getInstance());
        }
        return SelectedTagsDao.instance;
    }

    @Override
    public List<SelectedTags> parse(final ResultSet resultSet) throws SQLException {
        final List<SelectedTags> selectedTags = new java.util.ArrayList<>();
        while (resultSet != null && resultSet.next()) {
            String uuid = resultSet.getString("uuid");
            String tagId = resultSet.getString("tag_id");
            selectedTags.add(new SelectedTags(uuid, tagId));
        }
        return selectedTags;
    }

    private ReturnCode createSchema() {
        return createSchemaIfNotExists("custom_tags");
    }

    private boolean createTable() {
        String query = "CREATE TABLE IF NOT EXISTS custom_tags.selected_tags (userid TEXT PRIMARY KEY, tagid TEXT);";
        try {
            this.executeQuery(query);
            return true;
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError("Failed to create selected_tags table!", e);
        }
        return false;
    }

    public boolean selectTagForUser(String userId, String tagId) {
        String query = "INSERT INTO custom_tags.selected_tags (userid, tagid) VALUES (?, ?) ON CONFLICT(userid) DO UPDATE SET tagid = excluded.tagid;";
        try {
            this.executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError("Failed to select tag for user " + userId, e);
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
            CustomTags.getMyLogger().sendError("Failed to unselect tag for user " + userId, e);
        }
        return false;
    }

    public Tag getSelectedForUser(String userId) {
        String query = "SELECT tagid FROM custom_tags.selected_tags WHERE userid = ?;";

        List<SelectedTags> selectedTags;
        try {
            selectedTags = this.fetchQuery(query, userId);
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError("Failed to get selected tag for user " + userId, e);
            return null;
        }

        TagsConfig tagsConfig = TagsConfig.getInstance();

        try {
            for (SelectedTags selectedTag : selectedTags) {
                return tagsConfig.getTagById(selectedTag.tagId());
            }
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError("Failed to get selected tag for user " + userId, e);
            System.out.println("Error getting tagId");
        }
        return null;
    }
}
