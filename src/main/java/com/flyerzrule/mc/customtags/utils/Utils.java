package com.flyerzrule.mc.customtags.utils;

import java.util.ArrayList;
import java.util.List;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.flyerzrule.mc.customtags.CustomTags;
import com.flyerzrule.mc.customtags.models.Tag;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class Utils {
    public static List<Tag> getDifferenceTags(List<Tag> list1, List<Tag> list2) {
        List<Tag> uniqueToList1 = new ArrayList<>(list1);
        List<Tag> uniqueToList2 = new ArrayList<>(list2);

        uniqueToList1.removeAll(list2);
        uniqueToList2.removeAll(list1);

        List<Tag> result = new ArrayList<>(uniqueToList1);
        result.addAll(uniqueToList2);

        return result;
    }

    public static NamedTextColor getColorFromTag(String tag) {
        char tagChar;
        if (tag.isEmpty()) {
            tagChar = 'z';
        } else if (tag.length() == 1) {
            tagChar = tag.charAt(0);
        } else {
            tagChar = tag.charAt(1);
        }

        return switch (Character.toLowerCase(tagChar)) {
            case '0' -> NamedTextColor.BLACK;
            case '1' -> NamedTextColor.DARK_BLUE;
            case '2' -> NamedTextColor.DARK_GREEN;
            case '3' -> NamedTextColor.DARK_AQUA;
            case '4' -> NamedTextColor.DARK_RED;
            case '5' -> NamedTextColor.DARK_PURPLE;
            case '6' -> NamedTextColor.GOLD;
            case '7' -> NamedTextColor.GRAY;
            case '8' -> NamedTextColor.DARK_GRAY;
            case '9' -> NamedTextColor.BLUE;
            case 'a' -> NamedTextColor.GREEN;
            case 'b' -> NamedTextColor.AQUA;
            case 'c' -> NamedTextColor.RED;
            case 'd' -> NamedTextColor.LIGHT_PURPLE;
            case 'e' -> NamedTextColor.YELLOW;
            case 'f' -> NamedTextColor.WHITE;
            default -> NamedTextColor.WHITE; // Fallback to white
        };
    }

    public static boolean isPlayerIgnored(Player sender, Player receiver) {
        Essentials essentials = CustomTags.getEssentials();

        User senderUser = essentials.getUser(sender);
        User receiverUser = essentials.getUser(receiver);

        return receiverUser.isIgnoredPlayer(senderUser);
    }
}
