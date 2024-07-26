package com.flyerzrule.mc.customtags.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.PermissionHolder.Identifier;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;

public class GroupListener {

    public GroupListener() {
        registerListener();
    }

    private void registerListener() {
        EventBus eventBus = CustomTags.getLuckPerms().getEventBus();
        eventBus.subscribe(CustomTags.getPlugin(), NodeAddEvent.class, this::onNodeAddEvent);
    }

    private void onNodeAddEvent(NodeAddEvent event) {
        if (event.getNode() instanceof InheritanceNode
                && event.getTarget().getIdentifier().getType() == Identifier.USER_TYPE) {

            User user = (User) event.getTarget();
            Player player = Bukkit.getPlayer(user.getUniqueId());

            CustomTags.getMyLogger().sendDebug("Group changed for " + player.getName());

            PrefixUtils.removePrefix(player);

            TagsDatabase db = TagsDatabase.getInstance();
            Tag selectedTag = db.getSelectedForUser(player.getUniqueId().toString());
            if (selectedTag != null) {
                PrefixUtils.selectPrefix(player, selectedTag.getTag());
            }

        }
    }
}
