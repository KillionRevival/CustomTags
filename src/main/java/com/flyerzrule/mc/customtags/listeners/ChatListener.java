package com.flyerzrule.mc.customtags.listeners;

import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.utils.PrefixUtils;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.kif.reincarceration.api.IReincarcerationAPI;

public class ChatListener implements Listener {

    public ChatListener() {
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String prefix = PrefixUtils.removeTagFromPrefix(player);

        IReincarcerationAPI rcApi = Objects
                .requireNonNull(CustomTags.getPlugin().getServer().getServicesManager()
                        .getRegistration(IReincarcerationAPI.class))
                .getProvider();

        // Get Premium name color
        String premiumColor = "";
        CompletableFuture<User> userFuture = CustomTags.getLuckPerms().getUserManager().loadUser(player.getUniqueId());
        User user = userFuture.join();
        Optional<Node> metaOptional = user.getNodes().stream()
                .filter(node -> node instanceof MetaNode).findFirst();
        if (metaOptional.isPresent()) {
            MetaNode metaNode = (MetaNode) metaOptional.get();
            Pattern premiumColorRegex = Pattern.compile("meta\\.username-color\\.(&\\S)");
            String metadata = metaNode.getKey();

            Matcher match = premiumColorRegex.matcher(metadata);

            if (match.find()) {
                premiumColor = match.group(1).replace('&', '§');
            }
        }

        TagsDatabase db = TagsDatabase.getInstance();
        Tag selectedTag = db.getSelectedForUser(player.getUniqueId().toString());

        if (selectedTag != null) {
            // Create the prefix component
            TextComponent prefixComponent = new TextComponent(" " + prefix.replace('&', '§'));
            String prefixHoverContent = "§r";
            if (rcApi.isPlayerInCycle(player)) {
                CustomTags.getMyLogger().sendDebug("Player " + player.getDisplayName() + " is in cycle");
                try {
                    prefixHoverContent = String.format("Current modifier: §a%s§r",
                            rcApi.getPlayerModifier(player).getName());
                } catch (SQLException e) {
                    CustomTags.getMyLogger()
                            .sendError("Failed to get player: " + player.getDisplayName() + " modifier");
                }
            } else {
                CustomTags.getMyLogger().sendDebug("Player " + player.getDisplayName() + " is not in cycle");
            }
            prefixComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(prefixHoverContent)));

            // Create tag hover text component
            String tagHoverContent = String.format("%s\n%s\n%s", selectedTag.getName(), selectedTag.getDescription(),
                    (selectedTag.getObtainable() == true) ? "§aObtainable" : "§4Not-Obtainable");

            TextComponent tagComponent = new TextComponent(selectedTag.getTag());
            tagComponent
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(tagHoverContent)));

            // Create the username component
            String nameWithNickname = LegacyComponentSerializer.legacySection().serialize(player.displayName());
            String username;
            if (nameWithNickname.contains("~")) {
                username = nameWithNickname.split("~")[1];
            } else {
                username = player.getName();
            }
            TextComponent usernameComponent = new TextComponent(premiumColor + username + "§r");
            usernameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§r")));

            // Create the message component
            TextComponent messageComponent = new TextComponent(
                    LegacyComponentSerializer.legacySection().serialize(event.message()));
            messageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§r")));

            // Combine prefix, hoverable tag, and message
            BaseComponent[] baseComponents = new ComponentBuilder("")
                    .append(prefixComponent)
                    .append(" ")
                    .append(tagComponent)
                    .append(" ")
                    .append(usernameComponent)
                    .append("§8: §r")
                    .append(messageComponent)
                    .create();

            // Cancel the original event and send the new formatted message
            event.setCancelled(true);
            Bukkit.getServer().getConsoleSender().sendMessage(baseComponents);
            for (Player p : CustomTags.getPlugin().getServer().getOnlinePlayers()) {
                p.sendMessage(baseComponents);
            }
        }
    }
}
