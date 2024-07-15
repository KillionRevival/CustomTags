package com.flyerzrule.mc.customtags.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import com.flyerzrule.mc.customtags.models.Tag;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class TagItem extends AbstractItem {
    private final Tag tag;
    private boolean selected;

    public TagItem(Tag tag, boolean selected) {
        this.tag = tag;
        this.selected = selected;
    }

    @Override
    public ItemProvider getItemProvider() {
        String nameLore = String.format("Name: %s", this.tag.getName());
        String descriptionLore = String.format("Description: %s", this.tag.getDescription());
        String obtainableLore = (this.tag.getObtainable() == true) ? "Obtainable" : "Not-Obtainable";

        ItemBuilder item = new ItemBuilder(this.tag.getItem()).setDisplayName(this.tag.getTag()).addLoreLines(nameLore,
                descriptionLore, obtainableLore);

        if (this.selected) {
            item.addEnchantment(Enchantment.CHANNELING, 1, true);
        }
        return item;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isLeftClick() && !this.selected) {

            this.selected = true;
            notifyWindows();
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
