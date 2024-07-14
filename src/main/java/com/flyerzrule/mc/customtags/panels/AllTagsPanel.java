package com.flyerzrule.mc.customtags.panels;

import java.util.List;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.item.Item;

public class AllTagsPanel extends ScrollablePanel {
    public AllTagsPanel(Player player, List<Item> items) {
        super(player, "All Tags", items);
    }
}