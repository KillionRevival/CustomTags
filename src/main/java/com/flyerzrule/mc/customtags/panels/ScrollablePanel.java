package com.flyerzrule.mc.customtags.panels;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.items.ScrollDownItem;
import com.flyerzrule.mc.customtags.items.ScrollUpItem;

import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class ScrollablePanel {
    protected final String title;
    protected final Player player;
    protected final List<Item> items;

    protected final Gui gui;
    protected final Window window;

    public ScrollablePanel(Player player, String title, List<Item> items) {
        this.title = title;
        this.player = player;
        this.items = items;

        this.gui = ScrollGui.items()
                .setStructure(
                        "x x x x x x x x u",
                        "x x x x x x x x #",
                        "x x x x x x x x #",
                        "x x x x x x x x #",
                        "x x x x x x x x d")
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('u', new ScrollUpItem())
                .addIngredient('d', new ScrollDownItem())
                .setContent(this.items)
                .build();
        this.window = Window.single().setViewer(player).setTitle(this.title).setGui(this.gui).build();
    }

    public void open() {
        this.window.open();
    }
}
