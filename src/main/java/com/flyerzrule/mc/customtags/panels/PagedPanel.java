package com.flyerzrule.mc.customtags.panels;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
// import mc.obliviate.inventory.pagination.PaginationManager;

public class PagedPanel extends Gui {
    // private final PaginationManager paginationManager = new PaginationManager(this);
    private final Icon border = new Icon(Material.BLACK_STAINED_GLASS_PANE).hideFlags();


    public PagedPanel(Player player, String id, String name) {
        super(player, id, name, 6);
        // paginationManager.registerPageSlotsBetween(10, 16);
        // paginationManager.registerPageSlotsBetween(19, 25);
        // paginationManager.registerPageSlotsBetween(28, 34);
        // paginationManager.registerPageSlotsBetween(37, 43);
    }

    public void setup() {
        fillRow(this.border, 0);
        fillRow(this.border, 5);
        fillColumn(this.border, 0);
        fillColumn(this.border, 8);
        Icon nextPage = new Icon(Material.GREEN_STAINED_GLASS_PANE).setName("Next Page");
        Icon currentPage = new Icon(Material.YELLOW_STAINED_GLASS_PANE).setName("Current Page: 2").setAmount(2);
        Icon prevPage = new Icon(Material.GREEN_STAINED_GLASS_PANE).setName("Previous Page");
        addItem(prevPage, 47);
        addItem(currentPage, 49);
        addItem(nextPage, 51);

    }
}
