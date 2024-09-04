package com.flyerzrule.mc.customtags.commands.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TagCreateTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("tagcreate")) {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 3) {
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
