package com.flyerzrule.mc.customtags.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.Database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class TagItem extends AbstractItem {
    private Tag tag;
    private boolean selected;
    private boolean locked;
    private TagItemManager itemManager;

    public TagItem(TagItemManager itemManager, Tag tag, boolean selected) {
        this.itemManager = itemManager;
        this.tag = tag;
        this.selected = selected;
        this.locked = false;
    }

    public TagItem(TagItemManager itemManager, Tag tag, boolean selected, boolean locked) {
        this.itemManager = itemManager;
        this.tag = tag;
        this.selected = selected;
        this.locked = locked;
    }

    @Override
    public ItemProvider getItemProvider() {
        String tag = String.format("§l%s", this.tag.getTag());
        String nameLore = String.format("Name: %s", this.tag.getName());
        String descriptionLore = String.format("Description: %s", this.tag.getDescription());
        String obtainableLore = (this.tag.getObtainable() == true) ? "§aObtainable" : "§4Not-Obtainable";

        ItemBuilder item = new ItemBuilder(this.tag.getMaterial()).setDisplayName(tag).addLoreLines(
                nameLore,
                descriptionLore, obtainableLore);

        if (this.selected) {
            item.addEnchantment(Enchantment.CHANNELING, 1, true);
        }
        return item;
    }

    @Override
    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
        if (clickType.isLeftClick() && !this.selected && !this.locked) {
            this.itemManager.unselectAll();
            this.selected = true;

            TagsDatabase db = TagsDatabase.getInstance();
            // Tag oldTag = db.getSelectedForUser(player.getUniqueId().toString());
            // String oldTagTag = "";
            // if (oldTag != null) {
            // oldTagTag = oldTag.getTag();
            // }

            db.selectTagForUser(player.getUniqueId().toString(), this.tag.getId());

            // CustomTags.setPlayerPrefix(player, oldTagTag, this.tag.getTag());
            player.sendMessage(String.format("§fYou selected the %s§f tag!", this.tag.getTag()));
            notifyWindows();
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.notifyWindows();
    }

    public boolean isSelected() {
        return this.selected;
    }

    public Tag getTag() {
        return this.tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}
