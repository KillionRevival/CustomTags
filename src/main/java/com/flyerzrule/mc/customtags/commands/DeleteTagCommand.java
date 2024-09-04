package com.flyerzrule.mc.customtags.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.flyerzrule.mc.customtags.database.TagsDatabase;
import com.flyerzrule.mc.customtags.models.TagUpdateMethod;

import co.killionrevival.killioncommons.database.models.ReturnCode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class DeleteTagCommand implements CommandExecutor {

    public DeleteTagCommand() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tagdelete")) {
            if (args.length == 1) {
                String tagId = args[0];

                TagsDatabase db = TagsDatabase.getInstance();
                ReturnCode result = db.deleteTag(tagId, TagUpdateMethod.COMMAND, sender.getName());

                if (result == ReturnCode.SUCCESS) {
                    sender.sendMessage(Component.text().content(NamedTextColor.GREEN + "Tag " + tagId + " deleted!")
                            .color(NamedTextColor.GREEN).build());
                    return true;
                } else if (result == ReturnCode.NOT_EXIST) {
                    sender.sendMessage(
                            Component.text().content("Tag " + tagId + " does not exist!").color(NamedTextColor.RED)
                                    .build());
                    return false;
                } else {
                    Component.text().content("Error deleting tag " + tagId + "!").color(NamedTextColor.RED).build();
                    return false;
                }

            }
        }
        return false;
    }
}
