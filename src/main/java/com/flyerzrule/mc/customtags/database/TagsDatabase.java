package com.flyerzrule.mc.customtags.database;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.models.TagUpdateMethod;
import com.flyerzrule.mc.customtags.models.TagUpdateType;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

import co.killionrevival.killioncommons.database.DatabaseConnection;
import co.killionrevival.killioncommons.database.models.ReturnCode;

public class TagsDatabase extends DatabaseConnection {
    private static TagsDatabase instance;

    private TagsDatabase() {
        super(CustomTags.getKillionUtilities().getConsoleUtil());

        boolean failure = false;
        failure |= createSchemaIfNotExists("custom_tags").isTruthy();
        failure |= createOwnedTagsTable().isTruthy();
        failure |= createSelectedTagsTable().isTruthy();
        failure |= createTagsTable().isTruthy();
        failure |= createUpdateHistoryTable().isTruthy();

        if (failure) {
            CustomTags.getMyLogger().sendError("Failed to setup database!");
        }
    }

    public static TagsDatabase getInstance() {
        if (TagsDatabase.instance == null) {
            TagsDatabase.instance = new TagsDatabase();
        }
        return TagsDatabase.instance;
    }

    private ReturnCode createUpdateTypeEnum() {
        return this.createEnumIfNotExists("custom_tags", "update_type", new String[] { "CREATE", "MODIFY", "DELETE" });
    }

    private ReturnCode createUpdateMethodEnum() {
        return this.createEnumIfNotExists("custom_tags", "update_method", new String[] { "COMMAND", "PLUGIN" });
    }

    private ReturnCode createOwnedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS custom_tags.owned_tags (userid TEXT, tagid TEXT, PRIMARY KEY (userid, tagid));";
        try {
            this.executeQuery(query);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnCode.FAILURE;
    }

    private ReturnCode createSelectedTagsTable() {
        String query = "CREATE TABLE IF NOT EXISTS custom_tags.selected_tags (userid TEXT PRIMARY KEY, tagid TEXT);";
        try {
            this.executeQuery(query);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnCode.FAILURE;
    }

    private ReturnCode createTagsTable() {
        String query = "CREATE TABLE IF NO EXISTS custom_tags.tags (tagid TEXT PRIMARY KEY, name TEXT, tag TEXT, description TEXT, material TEXT, obtainable BOOLEAN, added_date TIMESTAMPTZ DEFAULT NOW())";
        try {
            this.executeQuery(query);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnCode.FAILURE;
    }

    private ReturnCode createUpdateHistoryTable() {
        this.createUpdateTypeEnum();
        this.createUpdateMethodEnum();
        String query = "CREATE TABLE IF NO EXISTS custom_tags.update_history (updateId SERIAL PRIMARY KEY, type update_type, tagid TEXT, date TIMESTAMPTZ DEFAULT NOW(), method update_method, actor TEXT, old TEXT, new TEXT)";
        try {
            this.executeQuery(query);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnCode.FAILURE;
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

        List<Tag> tags = new ArrayList<>();
        try {
            while (rs != null && rs.next()) {
                String tagId = rs.getString("tagid");
                tags.add(this.getTag(tagId));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting tagId");
        }
        return tags;
    }

    public ReturnCode giveUserTag(String userId, String tagId) {
        String query = "INSERT INTO custom_tags.owned_tags (userid, tagid) VALUES (?, ?);";
        try {
            this.executeUpdate(query, userId, tagId);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnCode.FAILURE;
    }

    public ReturnCode removeUserTag(String userId, String tagId) {
        String query = "DELETE FROM custom_tags.owned_tags WHERE userid = ? AND tagid = ?;";
        try {
            this.executeUpdate(query, userId, tagId);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnCode.FAILURE;
    }

    public ReturnCode removeAllTagsForUser(String userId) {
        String query = "DELETE FROM custom_tags.owned_tags WHERE userid = ?;";
        try {
            this.executeUpdate(query, userId);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnCode.FAILURE;
    }

    public ReturnCode selectTagForUser(String userId, String tagId) {
        String query = "INSERT INTO custom_tags.selected_tags (userid, tagid) VALUES (?, ?) ON CONFLICT(userid) DO UPDATE SET tagid = excluded.tagid;";
        try {
            this.executeUpdate(query, userId, tagId);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnCode.FAILURE;
    }

    public ReturnCode unselectTagForUser(String userId) {
        String query = "DELETE FROM custom_tags.selected_tags WHERE userid = ?;";
        try {
            this.executeUpdate(query, userId);
            PrefixUtils.removeAndSetPrefix(userId);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnCode.FAILURE;
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

        try {
            if (rs != null && rs.next()) {
                String tagId = rs.getString("tagid");
                return this.getTag(tagId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting tagId");
        }
        return null;
    }

    public ReturnCode createTag(Tag newTag, TagUpdateMethod updateMethod, String actor) {
        String query = "INSERT INTO custom_tags.tags (tagid, name, tag, description, material, obtainable) VALUES (?, ?, ?, ?, ?, ?);";
        if (!this.tagExists(newTag.getId())) {
            try {
                this.executeUpdate(query, newTag.getId(), newTag.getName(), newTag.getTag(), newTag.getDescription(),
                        newTag.getMaterial().name(), newTag.getObtainable());
            } catch (Exception e) {
                e.printStackTrace();
                CustomTags.getMyLogger().sendError("ERROR Creating tag " + newTag.getId());
                return ReturnCode.FAILURE;
            }
        } else {
            return ReturnCode.ALREADY_EXISTS;
        }

        return this.addUpdateHistoryEntry(TagUpdateType.CREATE, newTag.getId(), updateMethod, actor, null, newTag);
    }

    public ReturnCode deleteTag(String tagId, TagUpdateMethod updateMethod, String actor) {
        if (this.tagExists(tagId)) {
            Tag oldTag = this.getTag(tagId);
            String query = "DELETE FROM custom_tags.tags WHERE tagid = ?;";
            try {
                this.executeUpdate(query, tagId);
            } catch (Exception e) {
                e.printStackTrace();
                CustomTags.getMyLogger().sendError("ERROR Deleting tag " + tagId);
                return ReturnCode.FAILURE;
            }

            return this.addUpdateHistoryEntry(TagUpdateType.DELETE, tagId, updateMethod, actor, oldTag, null);
        } else {
            CustomTags.getMyLogger()
                    .sendError("ERROR Cannot delete tag " + tagId + " since it does not exist");
        }
        return ReturnCode.NOT_EXIST;
    }

    public ReturnCode modifyTag(Tag newTag, TagUpdateMethod method, String actor) {
        if (this.tagExists(newTag.getId())) {
            Tag oldTag = this.getTag(newTag.getId());
            String query = "UPDATE custom_tags.tags SET name = ?, tag = ?, description = ?, material = ?, obtainable = ? WHERE tagid = ?;";
            try {
                this.executeUpdate(query, newTag.getName(), newTag.getTag(), newTag.getDescription(),
                        newTag.getMaterial().name(), newTag.getObtainable(), newTag.getId());
                ;
            } catch (Exception e) {
                e.printStackTrace();
                CustomTags.getMyLogger().sendError("ERROR Modifying tag " + newTag.getId());
                return ReturnCode.FAILURE;
            }

            return this.addUpdateHistoryEntry(TagUpdateType.MODIFY, newTag.getId(), method, actor, oldTag, newTag);
        } else {
            CustomTags.getMyLogger()
                    .sendError("ERROR Cannot modify tag " + newTag.getId() + " since it does not exist");
        }
        return ReturnCode.FAILURE;
    }

    public boolean tagExists(String tagId) {
        String query = "SELECT 1 FROM custom_tags.tags WHERE tagid = ?;";

        ResultSet rs;
        try {
            rs = this.fetchQuery(query, tagId);
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            CustomTags.getMyLogger().sendError("ERROR Checking if tag " + tagId + " exists");
        }
        return false;
    }

    public Tag getTag(String id) {
        String query = "SELECT * FROM custom_tags.tags WHERE tagid = ?;";

        ResultSet rs;
        try {
            rs = this.fetchQuery(query, id);
        } catch (Exception e) {
            e.printStackTrace();
            CustomTags.getMyLogger().sendError("ERROR Getting tag " + id);
            return null;
        }

        try {
            if (rs != null && rs.next()) {
                String tagId = rs.getString("tagid");
                String name = rs.getString("name");
                String tag = rs.getString("tag");
                String description = rs.getString("description");
                String material = rs.getString("material");
                boolean obtainable = rs.getBoolean("obtainable");
                Timestamp createDate = rs.getTimestamp("added_date");
                return new Tag(tagId, name, tag, description, material, obtainable, createDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting tagId");
        }
        return null;
    }

    public List<String> getAllTagIds() {
        String query = "SELECT tagid FROM custom_tags.tags;";

        ResultSet rs;
        try {
            rs = this.fetchQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            CustomTags.getMyLogger().sendError("ERROR Getting all tagIds");
            return new ArrayList<>();
        }

        List<String> tagIds = new ArrayList<>();
        try {
            while (rs != null && rs.next()) {
                String tagId = rs.getString("tagid");
                tagIds.add(tagId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting tagId");
        }
        return tagIds;
    }

    public List<Tag> getAllTags() {
        String query = "SELECT * FROM custom_tags.tags;";

        ResultSet rs;
        try {
            rs = this.fetchQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            CustomTags.getMyLogger().sendError("ERROR Getting all tags");
            return new ArrayList<>();
        }

        List<Tag> tags = new ArrayList<>();
        try {
            while (rs != null && rs.next()) {
                String tagId = rs.getString("tagid");
                String name = rs.getString("name");
                String tag = rs.getString("tag");
                String description = rs.getString("description");
                String material = rs.getString("material");
                boolean obtainable = rs.getBoolean("obtainable");
                Timestamp createDate = rs.getTimestamp("added_date");
                tags.add(new Tag(tagId, name, tag, description, material, obtainable, createDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting tagId");
        }
        return tags;
    }

    private ReturnCode addUpdateHistoryEntry(TagUpdateType type, String tagId, TagUpdateMethod method, String actor,
            Tag oldTag, Tag newTag) {
        final String emptyVersion = "N/A";

        String query = "INSERT INTO custom_tags.update_history (type, tagid, method, actor, old, new) VALUES (?, ?, ?, ?, ?, ?);";

        String oldTagStr = "";
        String newTagStr = "";

        List<String> oldTagChanges = new ArrayList<>();
        List<String> newTagChanges = new ArrayList<>();

        if (oldTag == null) {
            oldTagStr = emptyVersion;
            newTagStr = newTag.toString();
        } else if (newTag == null) {
            oldTagStr = oldTag.toString();
            newTagStr = emptyVersion;
        } else {
            if (!this.isEqual(oldTag.getName(), newTag.getName())) {
                oldTagChanges.add("Name: " + oldTag.getName());
                newTagChanges.add("Name: " + newTag.getName());
            }

            if (!this.isEqual(oldTag.getTag(), newTag.getTag())) {
                oldTagChanges.add("Tag: " + oldTag.getTag());
                newTagChanges.add("Tag: " + newTag.getTag());
            }

            if (!this.isEqual(oldTag.getDescription(), newTag.getDescription())) {
                oldTagChanges.add("Description: " + oldTag.getDescription());
                newTagChanges.add("Description: " + newTag.getDescription());
            }

            if (!this.isEqual(oldTag.getMaterial().name(), newTag.getMaterial().name())) {
                oldTagChanges.add("Material: " + oldTag.getMaterial().name());
                newTagChanges.add("Material: " + newTag.getMaterial().name());
            }

            if (oldTag.getObtainable() != newTag.getObtainable()) {
                oldTagChanges.add("Obtainable: " + oldTag.getObtainable());
                newTagChanges.add("Obtainable: " + newTag.getObtainable());
            }

            oldTagStr = String.join(", ", oldTagChanges);
            newTagStr = String.join(", ", newTagChanges);
        }

        try {
            this.executeUpdate(query, type.toString(), tagId, method.toString(), actor, oldTagStr, newTagStr);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            CustomTags.getMyLogger().sendError("ERROR Creating history entry for tag " + tagId);
        }
        return ReturnCode.FAILURE;
    }

    private boolean isEqual(String v1, String v2) {
        return v1.equals(v2);
    }
}
