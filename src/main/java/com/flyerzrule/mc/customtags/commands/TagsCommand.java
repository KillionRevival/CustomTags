package com.flyerzrule.mc.customtags.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.flyerzrule.mc.customtags.config.TagsConfig;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.panels.TagsPanel;

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

                if (args.length > 0) {
                    if (args[0].equals("reload")) {
                        tagsConfig.parseFile();
                    } else {
                        return false;
                    }
                } else {
                    new TagsPanel(plugin, player, tags, tags).openOwned();
                }
                return true;
            } else {
                sender.sendMessage("This command can only be used by players!");
            }
        }
        return false;
    }
}
