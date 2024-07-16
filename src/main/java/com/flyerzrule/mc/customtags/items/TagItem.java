package com.flyerzrule.mc.customtags.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;
import com.google.common.base.Objects;

import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public class TagItem extends AbstractItem {
    private Player sender;
    private Player player;
    private Tag tag;
    private boolean selected;
    private boolean locked;
    private TagItemManager itemManager;

    public TagItem(TagItemManager itemManager, Player sender, Player player, Tag tag, boolean selected) {
        this.sender = sender;
        this.player = player;
        this.itemManager = itemManager;
        this.tag = tag;
        this.selected = selected;
        this.locked = false;
    }

    public TagItem(TagItemManager itemManager, Player sender, Player player, Tag tag, boolean selected,
            boolean locked) {
        this.sender = sender;
        this.player = player;
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
        TagsDatabase db = TagsDatabase.getInstance();
        if (clickType.isLeftClick() && this.selected && !this.locked) {
            // Tag clicked that was selected and not locked
            boolean result = db.unselectTagForUser(this.player.getUniqueId().toString());
            if (result) {
                this.selected = false;

                if (Objects.equal(this.sender, this.player)) {
                    this.sender.sendMessage(String.format("§rYou have unselected the %s§r tag!", this.tag.getTag()));
                } else {
                    this.sender.sendMessage(String.format("§rYou have unselected the %s§r tag for %s!",
                            this.tag.getTag(), this.player.getName()));
                }
                notifyWindows();
            }
        } else if (clickType.isLeftClick() && !this.selected && !this.locked) {
            // Tag clicked that was unselected and not locked
            this.itemManager.unselectAll();
            this.selected = true;

            db.selectTagForUser(this.player.getUniqueId().toString(), this.tag.getId());

            if (Objects.equal(this.sender, this.player)) {
                this.sender.sendMessage(String.format("§rYou selected the %s§r tag!", this.tag.getTag()));
            } else {
                this.sender.sendMessage(
                        String.format("§rYou selected the %s§r tag for %s!", this.tag.getTag(), this.player.getName()));
            }
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
