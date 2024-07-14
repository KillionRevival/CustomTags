package com.flyerzrule.mc.customtags;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.panels.OwnedTagsPanel;

import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class CustomTags extends JavaPlugin {
    File configFile = new File(getDataFolder(), "tags.json");

    @Override
    public void onEnable() {
        ensureDataFolderExists();
        registerGlobalIngredients();

        ensureTagsConfigExists();
        TagsConfig.parseFile(this, configFile);

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
            try (InputStream in = getResource("config.yml")) {
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                } else {
                    getLogger().severe("Default config.yml not found in resources!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}