package com.flyerzrule.mc.customtags;

import java.io.File;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.customtags.api.API;
import com.flyerzrule.mc.customtags.api.CustomTagsAPI;
import com.flyerzrule.mc.customtags.commands.AddAllTagsCommand;
import com.flyerzrule.mc.customtags.commands.AddTagCommand;
import com.flyerzrule.mc.customtags.commands.CreateTagCommand;
import com.flyerzrule.mc.customtags.commands.DeleteTagCommand;
import com.flyerzrule.mc.customtags.commands.ModifyTagCommand;
import com.flyerzrule.mc.customtags.commands.RemoveAllTagsCommand;
import com.flyerzrule.mc.customtags.commands.RemoveTagCommand;
import com.flyerzrule.mc.customtags.commands.TagsCommand;
import com.flyerzrule.mc.customtags.commands.TagsUserCommand;
import com.flyerzrule.mc.customtags.commands.tabcompleters.AddTagTabComplete;
import com.flyerzrule.mc.customtags.commands.tabcompleters.RemoveTagTabComplete;
import com.flyerzrule.mc.customtags.commands.tabcompleters.TagCreateTabComplete;
import com.flyerzrule.mc.customtags.commands.tabcompleters.TagIdTabComplete;
import com.flyerzrule.mc.customtags.commands.tabcompleters.UsersTabComplete;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.listeners.ChatListener;
import com.flyerzrule.mc.customtags.listeners.GroupListener;
import com.flyerzrule.mc.customtags.models.Tag;

import co.killionrevival.killioncommons.KillionUtilities;
import co.killionrevival.killioncommons.database.models.ReturnCode;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import xyz.xenondevs.invui.gui.structure.Structure;
import xyz.xenondevs.invui.item.builder.ItemBuilder;

public class CustomTags extends JavaPlugin implements CustomTagsAPI {
    private static JavaPlugin plugin = null;
    private static Permission perms = null;
    private static Chat chat = null;
    private static LuckPerms luckPerms;
    private static KillionUtilities killionUtilities;
    private static ConsoleUtil logger;
    private static SimpleClans sc;

    @Override
    public void onEnable() {
        plugin = this;

        killionUtilities = new KillionUtilities(plugin);
        logger = killionUtilities.getConsoleUtil();

        sc = (SimpleClans) Objects.requireNonNull(getServer().getPluginManager().getPlugin("SimpleClans"));

        ensureDataFolderExists();

        saveDefaultConfig();

        TagsDatabase.getInstance();

        setupPermissions();
        luckPerms = LuckPermsProvider.get();
        setupChat();

        registerGlobalIngredients();
        registerCommands();
        registerListeners();
        registerAPI();

        logger.sendSuccess("CustomTags has been enabled!");
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
        logger.sendSuccess("CustomTags has been disabled!");
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

    public static KillionUtilities getKillionUtilities() {
        return killionUtilities;
    }

    public static ConsoleUtil getMyLogger() {
        return logger;
    }

    public static SimpleClans getSimpleClans() {
        return sc;
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

        this.getCommand("tagcreate").setExecutor(new CreateTagCommand());
        this.getCommand("tagcreate").setTabCompleter(new TagCreateTabComplete());

        this.getCommand("tagmodify").setExecutor(new ModifyTagCommand());
        // this.getCommand("tagmodify").setTabCompleter(new TagIdTabComplete());

        this.getCommand("tagdelete").setExecutor(new DeleteTagCommand());
        // this.getCommand("tagdelete").setTabCompleter(new TagIdTabComplete());
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        new GroupListener();
    }

    private void registerAPI() {
        this.getServer().getServicesManager().register(CustomTagsAPI.class, this, this, ServicePriority.Normal);
    }

    // API Methods
    public ReturnCode giveUserTag(Player player, String tagId) {
        return API.giveUserTag(player, tagId);
    }

    public ReturnCode removeUserTag(Player player, String tagId) {
        return API.removeUserTag(player, tagId);
    }

    public ReturnCode removeAllUserTags(Player player) {
        return API.removeAllUserTags(player);
    }

    public List<String> getUserTagIds(Player player) {
        return API.getUserTagIds(player);
    }

    public ReturnCode setUserSelectedTag(Player player, String tagId) {
        return API.setUserSelectedTag(player, tagId);
    }

    public ReturnCode removeUserSelectedTag(Player player) {
        return API.removeUserSelectedTag(player);
    }

    public String getUserSelectedTagId(Player player) {
        return API.getUserSelectedTagId(player);
    }

    public List<String> getAvailableTagIds() {
        return API.getAvailableTagIds();
    }

    public ReturnCode createTag(String pluginIdentifier, Tag newTag) {
        return API.createTag(pluginIdentifier, newTag);
    }

    public ReturnCode deleteTag(String pluginIdentifier, String tagId) {
        return API.deleteTag(pluginIdentifier, tagId);
    }

    public ReturnCode modifyTag(String pluginIdentifier, Tag newTag) {
        return API.modifyTag(pluginIdentifier, newTag);
    }

    public ReturnCode ensureTag(String pluginIdentifier, Tag newTag) {
        return API.ensureTag(pluginIdentifier, newTag);
    }

    public boolean tagExists(String tagId) {
        return API.tagExists(tagId);
    }

    public Tag getTag(String tagId) {
        return API.getTag(tagId);
    }
}