package com.flyerzrule.mc.customtags.listeners;

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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class ChatListener implements Listener {

    public ChatListener() {
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String prefix = PrefixUtils.removeTagFromPrefix(player);

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
            // Create hover text component
            String hoverContent = String.format("%s\n%s\n%s", selectedTag.getName(), selectedTag.getDescription(),
                    (selectedTag.getObtainable() == true) ? "§aObtainable" : "§4Not-Obtainable");

            TextComponent hoverComponent = new TextComponent(selectedTag.getTag());
            hoverComponent
                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverContent)));

            // Create the prefix component
            TextComponent prefixComponent = new TextComponent(prefix.replace('&', '§'));
            prefixComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§r")));

            // Create the username component
            TextComponent usernameComponent = new TextComponent(premiumColor + player.getName() + "§r");
            usernameComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§r")));

            // Create the message component
            TextComponent messageComponent = new TextComponent(MiniMessage.miniMessage().serialize(event.message()));
            messageComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§r")));

            // Combine prefix, hoverable tag, and message
            BaseComponent[] baseComponents = new ComponentBuilder("")
                    .append(prefixComponent)
                    .append(" ")
                    .append(hoverComponent)
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
