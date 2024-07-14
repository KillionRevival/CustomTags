package com.flyerzrule.mc.customtags.panels;

import java.util.List;
import org.bukkit.entity.Player;
import xyz.xenondevs.invui.item.Item;

public class OwnedTagsPanel extends ScrollablePanel {
    public OwnedTagsPanel(Player player, List<Item> items) {
        super(player, "Owned Tags", items);
    }
}