package com.flyerzrule.mc.customtags.commands;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.panels.OwnedTagsPanel;

import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public class MyTagsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("mytags")) {
            // Check if the sender is a player
            if (sender instanceof Player) {
                Player player = (Player) sender;

                List<Item> items = Arrays.stream(Material.values())
                        .filter(material -> !material.isAir() && material.isItem())
                        .map(material -> new SimpleItem(new ItemBuilder(material)))
                        .collect(Collectors.toList());

                new OwnedTagsPanel(player, items).open();
                return true;
            }
        }
        return false;
    }
}
