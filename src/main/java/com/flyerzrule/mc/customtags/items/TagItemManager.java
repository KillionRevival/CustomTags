package com.flyerzrule.mc.customtags.items;

import java.util.List;
import java.util.stream.Collectors;
import com.flyerzrule.mc.customtags.CustomTags;
import xyz.xenondevs.invui.item.Item;

public class TagItemManager {
    private List<TagItem> items;

    public TagItemManager() {
    }

    public TagItemManager(List<TagItem> items) {
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
            CustomTags.getPlugin().getLogger().warning("No item is selected!");
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
