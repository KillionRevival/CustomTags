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
            InheritanceNode node = (InheritanceNode) event.getNode();
            CustomTags.getPlugin().getLogger()
                    .info(event.getTarget().getFriendlyName() + " was added to group " + node.getGroupName());
            // Group parentGroup = event.getTarget().getInheritedGroups(event.getTarget().getQueryOptions()).stream()
            //         .findFirst().get();

            // String parentGroupPrefix = parentGroup.data().toCollection().stream().filter(NodeType.PREFIX::matches)
            //         .map(NodeType.PREFIX::cast).findFirst().get().getMetaValue();

            PrefixUtils.removePrefix(player);

            TagsDatabase db = TagsDatabase.getInstance();
            Tag selectedTag = db.getSelectedForUser(player.getUniqueId().toString());
            if (selectedTag != null) {
                PrefixUtils.selectPrefix(player, selectedTag.getTag());
            }

        }
    }
}
