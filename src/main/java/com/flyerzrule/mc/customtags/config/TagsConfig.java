package com.flyerzrule.mc.customtags.config;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.models.TagContainer;
import com.google.gson.Gson;

public class TagsConfig {
    private static TagsConfig instance;

    private String filePath;

    private List<Tag> tags;

    private TagsConfig() {
        this.tags = new ArrayList<>();
    }

    public static TagsConfig getInstance() {
        if (instance == null) {
            instance = new TagsConfig();
        }
        return instance;
    }

    public List<Tag> parseJson() {
        String json = this.readFile();
        Gson gson = new Gson();
        TagContainer tagContainer = gson.fromJson(json, TagContainer.class);

        if (tagContainer != null && tagContainer.getTags() != null) {
            this.tags = tagContainer.getTags();
            return this.tags;
        } else {
            CustomTags.getMyLogger().sendError("Failed to parse tags.json");
        }

        this.tags = new ArrayList<Tag>();
        return this.tags;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public List<String> getTagIds() {
        return this.tags.stream().map(ele -> ele.getId()).collect(Collectors.toList());
    }

    public Tag getTagById(String id) {
        return this.tags.stream().filter(ele -> ele.getId().equals(id)).findFirst().get();
    }

    private String readFile() {
        if (this.filePath == null) {
            return null;
        }

        StringBuilder jsonContent = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(this.filePath));
            String line;
            while ((line = br.readLine()) != null) {
                jsonContent.append(line);
            }
            br.close();
        } catch (Exception e) {
            CustomTags.getMyLogger().sendError("Failed to read tags.json");
        }
        return jsonContent.toString();
    }
}
