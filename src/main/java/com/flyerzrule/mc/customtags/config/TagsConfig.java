package com.flyerzrule.mc.customtags.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.models.TagContainer;

public class TagsConfig {
    private static TagsConfig instance;

    private final ObjectMapper objectMapper;
    private File file;

    private List<Tag> tags;

    private TagsConfig() {
        this.objectMapper = new ObjectMapper();
        this.tags = new ArrayList<>();
    }

    public static TagsConfig getInstance() {
        if (instance == null) {
            instance = new TagsConfig();
        }
        return instance;
    }

    public List<Tag> parseFile() {
        try {
            TagContainer tagContainer = objectMapper.readValue(this.file, TagContainer.class);

            if (tagContainer != null && tagContainer.getTags() != null) {
                this.tags = tagContainer.getTags();
                return this.tags;
            } else {
                CustomTags.getPlugin().getLogger().severe("Failed to parse tags.json");
            }
        } catch (IOException e) {
            CustomTags.getPlugin().getLogger().severe("Failed to parse tags.json");
            // e.printStackTrace();
        }
        this.tags = new ArrayList<Tag>();
        return this.tags;
    }

    public void setFile(File file) {
        this.file = file;
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
}
