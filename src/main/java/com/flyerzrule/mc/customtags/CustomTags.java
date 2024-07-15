package com.flyerzrule.mc.customtags;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.customtags.commands.AddTagCommand;
import com.flyerzrule.mc.customtags.commands.TagsCommand;
import com.flyerzrule.mc.customtags.config.TagsConfig;

import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class CustomTags extends JavaPlugin {
    File configFile = new File(getDataFolder(), "tags.json");

    @Override
    public void onEnable() {
        ensureDataFolderExists();
        ensureTagsConfigExists();

        TagsConfig tagConfig = TagsConfig.getInstance();
        tagConfig.setThat(this);
        tagConfig.setFile(configFile);
        tagConfig.parseFile();

        registerGlobalIngredients();
        registerCommands();

        getLogger().info("CustomTags has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("CustomTags has been disabled!");
    }

    private void registerGlobalIngredients() {
        Structure.addGlobalIngredient('#', new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r"));
    }

    private void ensureDataFolderExists() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

    }

    private void ensureTagsConfigExists() {
        if (!configFile.exists()) {
            try (InputStream in = getResource("tags.json")) {
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                } else {
                    getLogger().severe("Default tags.json not found in resources!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerCommands() {
        this.getCommand("tags").setExecutor(new TagsCommand(this));
        this.getCommand("addtag").setExecutor(new AddTagCommand(this));
    }
}