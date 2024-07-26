package com.flyerzrule.mc.customtags;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.customtags.api.API;
import com.flyerzrule.mc.customtags.api.CustomTagsAPI;
import com.flyerzrule.mc.customtags.commands.AddAllTagsCommand;
import com.flyerzrule.mc.customtags.commands.AddTagCommand;
import com.flyerzrule.mc.customtags.commands.RemoveAllTagsCommand;
import com.flyerzrule.mc.customtags.commands.RemoveTagCommand;
import com.flyerzrule.mc.customtags.commands.TagsCommand;
import com.flyerzrule.mc.customtags.commands.TagsReloadCommand;
import com.flyerzrule.mc.customtags.commands.TagsUserCommand;
import com.flyerzrule.mc.customtags.commands.tabcompleters.AddTagTabComplete;
import com.flyerzrule.mc.customtags.commands.tabcompleters.RemoveTagTabComplete;
import com.flyerzrule.mc.customtags.commands.tabcompleters.UsersTabComplete;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.listeners.ChatListener;
import com.flyerzrule.mc.customtags.listeners.GroupListener;

import co.killionrevival.killioncommons.KillionCommonsApi;
import co.killionrevival.killioncommons.util.ConsoleUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class CustomTags extends JavaPlugin implements CustomTagsAPI {
    private File configFile = new File(getDataFolder(), "tags.json");
    private static JavaPlugin plugin = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private static LuckPerms luckPerms;
    private static KillionCommonsApi api;
    private static ConsoleUtil logger;

    @Override
    public void onEnable() {
        plugin = this;

        api = new KillionCommonsApi(plugin);
        logger = api.getConsoleUtil();

        ensureDataFolderExists();
        ensureTagsConfigExists();

        saveDefaultConfig();

        TagsConfig tagConfig = TagsConfig.getInstance();
        tagConfig.setFile(configFile);
        tagConfig.parseFile();

        TagsDatabase.getInstance();

        setupPermissions();
        luckPerms = LuckPermsProvider.get();
        setupChat();

        registerGlobalIngredients();
        registerCommands();
        registerListeners();
        registerAPI();

        logger.sendInfo("CustomTags has been enabled!");
    }

    @Override
    public void onDisable() {
        TagsDatabase db = TagsDatabase.getInstance();
        if (db != null) {
            try {
                db.closeConnection();
            } catch (Exception e) {
                logger.sendError("Failed to close connection to Database!");
            }
        }
        logger.sendInfo("CustomTags has been disabled!");
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static Chat getChat() {
        return chat;
    }

    public static Permission getPermission() {
        return perms;
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public static KillionCommonsApi getApi() {
        return api;
    }

    public static ConsoleUtil getMyLogger() {
        return logger;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    public static void setPlayerPrefix(Player player, String oldTag, String newTag) {
        if (chat != null) {
            String oldPrefix = chat.getPlayerPrefix(player);

            // Remove the old tag from the prefix
            String rankPrefix = oldPrefix.replace(oldTag, "").trim();

            String newPrefix = String.format("%s %s", rankPrefix, newTag);

            chat.setPlayerPrefix(player, newPrefix);
        }
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
                    logger.sendError("Default tags.json not found in resources!");
                }
            } catch (IOException e) {
                logger.sendError("Error creating tags.json file!");
                // e.printStackTrace();
            }
        }
    }

    private void registerCommands() {
        this.getCommand("tags").setExecutor(new TagsCommand());

        this.getCommand("taguser").setExecutor(new TagsUserCommand());
        this.getCommand("taguser").setTabCompleter(new UsersTabComplete());

        this.getCommand("tagadd").setExecutor(new AddTagCommand());
        this.getCommand("tagadd").setTabCompleter(new AddTagTabComplete());

        this.getCommand("tagaddall").setExecutor(new AddAllTagsCommand());
        this.getCommand("tagaddall").setTabCompleter(new UsersTabComplete());

        this.getCommand("tagremove").setExecutor(new RemoveTagCommand());
        this.getCommand("tagremove").setTabCompleter(new RemoveTagTabComplete());

        this.getCommand("tagremoveall").setExecutor(new RemoveAllTagsCommand());
        this.getCommand("tagremoveall").setTabCompleter(new UsersTabComplete());

        this.getCommand("tagreload").setExecutor(new TagsReloadCommand());

    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        new GroupListener();
    }

    private void registerAPI() {
        this.getServer().getServicesManager().register(CustomTagsAPI.class, this, this, ServicePriority.Normal);
    }

    // API Methods
    public boolean giveUserTag(Player player, String tagId) {
        return API.giveUserTag(player, tagId);
    }

    public boolean removeUserTag(Player player, String tagId) {
        return API.removeUserTag(player, tagId);
    }

    public boolean removeAllUserTags(Player player) {
        return API.removeAllUserTags(player);
    }

    public List<String> getUserTagIds(Player player) {
        return API.getUserTagIds(player);
    }

    public boolean setUserSelectedTag(Player player, String tagId) {
        return API.setUserSelectedTag(player, tagId);
    }

    public boolean removeUserSelectedTag(Player player) {
        return API.removeUserSelectedTag(player);
    }

    public String getUserSelectedTagId(Player player) {
        return API.getUserSelectedTagId(player);
    }

    public List<String> getAvailableTagIds() {
        return API.getAvailableTagIds();
    }
}