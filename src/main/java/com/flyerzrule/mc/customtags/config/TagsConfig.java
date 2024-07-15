package com.flyerzrule.mc.customtags.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.plugin.java.JavaPlugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.models.TagContainer;

public class TagsConfig {
    private static TagsConfig instance;

    private final ObjectMapper objectMapper;
    private File file;

    private List<Tag> tags;
    private JavaPlugin plugin;

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
                plugin.getLogger().severe("Failed to parse tags.json");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.tags = new ArrayList<Tag>();
        return this.tags;
    }

    public void setThat(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public List<Tag> getTags() {
        return this.tags;
    }
}
