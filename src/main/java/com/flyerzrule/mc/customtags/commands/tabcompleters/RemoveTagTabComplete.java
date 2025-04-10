package com.flyerzrule.mc.customtags.commands.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.flyerzrule.mc.customtags.database.OwnedTagsDao;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.models.Tag;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RemoveTagTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command command, @NotNull String alias, String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("tagremove")) {
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                suggestions = CustomTags.getPlugin().getServer().getOnlinePlayers().stream().map(Player::getName)
                        .collect(Collectors.toList());
            } else if (args.length == 2) {
                OwnedTagsDao db = OwnedTagsDao.getInstance();
                List<Tag> ownedTags = db.getUserOwnedTags(Objects.requireNonNull(Bukkit.getPlayer(args[0])).getUniqueId().toString());

                suggestions = ownedTags.stream().map(Tag::getId)
                        .collect(Collectors.toList());
            }
            return suggestions;
        }
        return null;
    }
}
