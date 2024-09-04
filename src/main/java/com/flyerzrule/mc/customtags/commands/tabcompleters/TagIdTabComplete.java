package com.flyerzrule.mc.customtags.commands.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.flyerzrule.mc.customtags.database.TagsDatabase;

public class TagIdTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("tagmodify") || command.getName().equalsIgnoreCase("tagdelete")) {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                TagsDatabase db = TagsDatabase.getInstance();
                suggestions = db.getAllTagIds();
            } else if (args.length == 3) {
                String prefix = args[2].toUpperCase();
                for (Material material : Material.values()) {
                    if (material.name().startsWith(prefix)) {
                        suggestions.add(material.name());
                    }
                }
            } else if (args.length == 4) {
                suggestions.add("true");
                suggestions.add("false");
            }
            return suggestions;
        }
        return null;
    }
}
