package com.flyerzrule.mc.customtags.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.killionrevival.killioncommons.database.DataAccessObject;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

public class TagsDao extends DataAccessObject<Tag> {
    TagsConfig tagsConfig = TagsConfig.getInstance();
    private static TagsDao instance;

    private TagsDao(final TagsDatabase db) {
        super(db);
        createSchema();
        createOwnedTagsTable();
        createSelectedTagsTable();
    }

    public static TagsDao getInstance() {
        if (TagsDao.instance == null) {
            TagsDao.instance = new TagsDao(TagsDatabase.getInstance());
        }
        return TagsDao.instance;
    }

    private boolean createSchema() {
        return createSchemaIfNotExists("custom_tags").isTruthy();
    }

    private boolean createOwnedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS custom_tags.owned_tags (userid TEXT, tagid TEXT, PRIMARY KEY (userid, tagid));";
        try {
            executeQuery(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean createSelectedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS custom_tags.selected_tags (userid TEXT PRIMARY KEY, tagid TEXT);";
        try {
            executeQuery(query);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Tag> getUserOwnedTags(String userId) {
        String query = "SELECT tagid FROM custom_tags.owned_tags WHERE userid = ?;";
        try {
            return fetchQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean giveUserTag(String userId, String tagId) {
        String query = "INSERT INTO custom_tags.owned_tags (userid, tagid) VALUES (?, ?);";
        try {
            executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeUserTag(String userId, String tagId) {
        String query = "DELETE FROM custom_tags.owned_tags WHERE userid = ? AND tagid = ?;";
        try {
            executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeAllTagsForUser(String userId) {
        String query = "DELETE FROM custom_tags.owned_tags WHERE userid = ?;";
        try {
            executeUpdate(query, userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean selectTagForUser(String userId, String tagId) {
        String query = "INSERT INTO custom_tags.selected_tags (userid, tagid) VALUES (?, ?) ON CONFLICT(userid) DO UPDATE SET tagid = excluded.tagid;";
        try {
            executeUpdate(query, userId, tagId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unselectTagForUser(String userId) {
        String query = "DELETE FROM custom_tags.selected_tags WHERE userid = ?;";
        try {
            executeUpdate(query, userId);
            PrefixUtils.removeAndSetPrefix(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Tag getSelectedForUser(String userId) {
        String query = "SELECT tagid FROM custom_tags.selected_tags WHERE userid = ?;";

        try {
            return fetchQuery(query, userId).getFirst();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Tag> parse(ResultSet resultSet) throws SQLException {
        final ArrayList<Tag> tags = new ArrayList<>();
        while (resultSet != null && resultSet.next()) {
            String tagId = resultSet.getString("tagid");
            tags.add(tagsConfig.getTagById(tagId));
        }

        return tags;
    }
}
