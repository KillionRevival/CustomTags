package com.flyerzrule.mc.customtags.panels;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;


public class CustomTagsPanel extends PagedPanel {
    public CustomTagsPanel(Player player) {
        super(player, "user-tags", "Owned Tags");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        setup();
    }
}