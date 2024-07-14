package com.flyerzrule.mc.customtags.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.models.JsonTag;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.models.TagContainer;

public class TagsConfig {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Tag> parseFile(CustomTags that, File filePath) {
        try {
            TagContainer tagContainer = objectMapper.readValue(filePath, TagContainer.class);

            if (tagContainer != null && tagContainer.getTags() != null) {
                List<JsonTag> jsonTags = tagContainer.getTags();

                return jsonTags.stream().map(ele -> {
                    Tag tag = new Tag();
                    tag.fromJsonTag(ele);
                    return tag;
                }).collect(Collectors.toList());

            } else {
                that.getLogger().severe("Failed to parse tags.json");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<Tag>();
    }
}
