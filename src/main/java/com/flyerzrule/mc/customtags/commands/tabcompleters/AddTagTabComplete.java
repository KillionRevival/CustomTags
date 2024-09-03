package com.flyerzrule.mc.customtags.commands.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.Utils;

public class AddTagTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("tagadd")) {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                suggestions = CustomTags.getPlugin().getServer().getOnlinePlayers().stream().map(ele -> ele.getName())
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                TagsDatabase db = TagsDatabase.getInstance();
                List<Tag> ownedTags = db.getUserOwnedTags(Bukkit.getPlayer(args[0]).getUniqueId().toString());

                List<Tag> allTags = db.getAllTags();

                suggestions = Utils.getDifferenceTags(allTags, ownedTags).stream().map(ele -> ele.getId())
                        .collect(Collectors.toList());
            }
            return suggestions;
        }
        return null;
    }
}
