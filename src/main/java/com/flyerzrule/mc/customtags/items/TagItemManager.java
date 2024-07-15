package com.flyerzrule.mc.customtags.items;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.xenondevs.invui.item.Item;

public class TagItemManager {
    private List<TagItem> items;
    private JavaPlugin plugin;

    public TagItemManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public TagItemManager(JavaPlugin plugin, List<TagItem> items) {
        this.plugin = plugin;
        this.items = items;
    }

    public void unselectAll() {
        items.stream().forEach(ele -> {
            ele.setSelected(false);
        });
    }

    public TagItem getSelected() {
        try {
            return items.stream().filter(ele -> ele.isSelected() == true).findFirst().get();
        } catch (Exception e) {
            plugin.getLogger().warning("No item is selected!");
        }
        return null;
    }

    public List<Item> getItems() {
        return this.items.stream().map(ele -> ele).collect(Collectors.toList());
    }

    public void setItems(List<TagItem> items) {
        this.items = items;
    }
}
