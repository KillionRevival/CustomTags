package com.flyerzrule.mc.customtags.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.items.TagItem;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.panels.AllTagsPanel;
import com.flyerzrule.mc.customtags.panels.OwnedTagsPanel;

import xyz.xenondevs.invui.item.Item;

public class TagsCommand implements CommandExecutor {
    private JavaPlugin plugin;

    public TagsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tags")) {
            // Check if the sender is a player
            if (sender instanceof Player) {
                Player player = (Player) sender;

                TagsConfig tagsConfig = TagsConfig.getInstance();
                List<Tag> tags = tagsConfig.getTags();

                List<Item> items = tags.stream().map(ele -> new TagItem(ele, false)).collect(Collectors.toList());

                if (args.length > 0) {
                    this.plugin.getLogger().info(args[0]);
                    if (args[0].equals("all")) {
                        new AllTagsPanel(player, items).open();
                    } else if (args[0].equals("reload")) {
                        tagsConfig.parseFile();
                    } else {
                        return false;
                    }
                } else {
                    new OwnedTagsPanel(player, items).open();
                }
                return true;
            } else {
                sender.sendMessage("This command can only be used by players!");
            }
        }
        return false;
    }
}
