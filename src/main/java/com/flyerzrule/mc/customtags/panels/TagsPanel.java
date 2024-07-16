package com.flyerzrule.mc.customtags.panels;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.items.ScrollDownItem;
import com.flyerzrule.mc.customtags.items.ScrollUpItem;
import com.flyerzrule.mc.customtags.items.TagItem;
import com.flyerzrule.mc.customtags.items.TagItemManager;
import com.flyerzrule.mc.customtags.models.Tag;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class TagsPanel {
    private final Player sender;
    private final Player player;

    private Gui ownedGui;
    private Gui allGui;
    private Window window;

    public TagsPanel(Player sender, Player player, List<Tag> allTags, List<Tag> ownedTags) {
        this.player = player;
        this.sender = sender;

        TagsDatabase db = TagsDatabase.getInstance();
        Tag selectedTag = db.getSelectedForUser(this.player.getUniqueId().toString());
        final String selectedTagId = (selectedTag == null) ? "" : selectedTag.getId();

        TagItemManager ownedItemManager = new TagItemManager();
        List<TagItem> ownedTagItems = ownedTags.stream()
                .map(ele -> {
                    boolean selected = false;
                    if (!selectedTagId.isEmpty() && ele.getId().equals(selectedTagId)) {
                        selected = true;
                    }
                    return new TagItem(ownedItemManager, sender, player, ele, selected);
                })
                .collect(Collectors.toList());
        ownedItemManager.setItems(ownedTagItems);
        List<Item> ownedItems = ownedTagItems.stream().collect(Collectors.toList());

        TagItemManager allItemManager = new TagItemManager();
        List<TagItem> allTagItems = allTags.stream()
                .map(ele -> new TagItem(allItemManager, sender, player, ele, false, true))
                .collect(Collectors.toList());
        allItemManager.setItems(allTagItems);
        List<Item> allItems = allTagItems.stream().collect(Collectors.toList());

        this.ownedGui = ScrollGui.items().setStructure(
                "x x x x x x x x u",
                "x x x x x x x x #",
                "x x x x x x x x a",
                "x x x x x x x x #",
                "x x x x x x x x d")
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('u', new ScrollUpItem())
                .addIngredient('d', new ScrollDownItem())
                .addIngredient('a',
                        new SimpleItem(new ItemBuilder(Material.DIAMOND)
                                .setDisplayName("View All Available Tags"),
                                event -> openAll()))
                .setContent(ownedItems)
                .build();

        this.allGui = ScrollGui.items().setStructure(
                "x x x x x x x x u",
                "x x x x x x x x #",
                "x x x x x x x x o",
                "x x x x x x x x #",
                "x x x x x x x x d")
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('u', new ScrollUpItem())
                .addIngredient('d', new ScrollDownItem())
                .addIngredient('o',
                        new SimpleItem(new ItemBuilder(Material.EMERALD)
                                .setDisplayName("View Your Tags"),
                                event -> openOwned()))
                .setContent(allItems)
                .build();
    }

    public void openOwned() {
        this.window = Window.single().setViewer(this.sender).setTitle("§2Your Tags§r").setGui(this.ownedGui).build();
        this.window.open();
    }

    public void openAll() {
        this.window = Window.single().setViewer(this.sender).setTitle("§eAll Tags§r").setGui(this.allGui).build();
        this.window.open();
    }
}
