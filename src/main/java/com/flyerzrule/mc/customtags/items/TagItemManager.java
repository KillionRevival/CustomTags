package com.flyerzrule.mc.customtags.items;

import java.util.List;

public class TagItemManager {
    private List<TagItem> items;

    public TagItemManager(List<TagItem> items) {
        this.items = items;
    }

    public void unselectAll() {
        items.stream().forEach(ele -> ele.setSelected(false));
    }
}
