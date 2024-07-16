package com.flyerzrule.mc.customtags.commands.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.flyerzrule.mc.customtags.CustomTags;

public class UsersTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("tagremoveall") || command.getName().equalsIgnoreCase("taguser")
                || command.getName().equalsIgnoreCase("tagaddall")) {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                suggestions = CustomTags.getPlugin().getServer().getOnlinePlayers().stream().map(ele -> ele.getName())
                        .collect(Collectors.toList());
            }
            return suggestions;
        }
        return null;
    }
}
