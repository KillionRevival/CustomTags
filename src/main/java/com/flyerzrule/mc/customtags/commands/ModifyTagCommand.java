package com.flyerzrule.mc.customtags.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.Tag;
import com.flyerzrule.mc.customtags.models.TagUpdateMethod;

import co.killionrevival.killioncommons.database.models.ReturnCode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ModifyTagCommand implements CommandExecutor {
    public ModifyTagCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tagmodify")) {
            if (args.length < 6) {
                return false;
            }

            // Combine args back into a single command string to handle quotes
            String input = String.join(" ", args);

            // Use a regex pattern to match the arguments (taking quotes into account)
            Pattern pattern = Pattern.compile("([^\"]\\S*|\"[^\"]+\")\\s*");
            Matcher matcher = pattern.matcher(input);
            List<String> parsedArgs = new ArrayList<>();

            while (matcher.find()) {
                parsedArgs.add(matcher.group(1).replace("\"", "")); // Remove quotes
            }

            // Check if we have enough arguments after parsing
            if (parsedArgs.size() < 6) {
                return false;
            }

            // Extract the parsed arguments
            String tagId = parsedArgs.get(0);
            String tag = parsedArgs.get(1);
            String material = parsedArgs.get(2);
            boolean obtainable = Boolean.parseBoolean(parsedArgs.get(3));
            String name = parsedArgs.get(4);
            String description = parsedArgs.get(5);

            Tag newTag = new Tag(tagId, name, tag, description, material, obtainable);

            TagsDatabase db = TagsDatabase.getInstance();
            ReturnCode result = db.modifyTag(newTag, TagUpdateMethod.COMMAND, sender.getName());

            if (result == ReturnCode.SUCCESS) {
                sender.sendMessage(Component.text().content("Tag " + tagId + " modified!").color(NamedTextColor.GREEN)
                        .build());
                return true;
            } else if (result == ReturnCode.NOT_EXIST) {
                sender.sendMessage(Component.text().content("Tag " + tagId + " does not exist!")
                        .color(NamedTextColor.RED).build());
                return false;
            } else {
                sender.sendMessage(Component.text().content("Error modifying tag " + tagId + "!")
                        .color(NamedTextColor.RED).build());
                return false;
            }
        }
        return false;
    }
}
