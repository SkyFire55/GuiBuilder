package ru.skyfire.minecraft.guibuilder.cmd;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

public class CmdBuilder {
    public static void initCmds(PluginContainer plugin){

        CommandSpec reload = CommandSpec.builder()
                .permission("guibuilder.admin")
                .description(Text.of("Reloads config"))
                .executor(new CmdReload())
                .build();
        CommandSpec showGui = CommandSpec.builder()
                .permission("guibuilder.basic")
                .description(Text.of("Shows gui page to player"))
                .executor(new CmdShowGui())
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("player"))),
                        GenericArguments.onlyOne(GenericArguments.string(Text.of("page"))))
                .build();

        CommandSpec gui = CommandSpec.builder()
                .description(Text.of("Building your own GUI!"))
                .executor(new CmdShowGui())
                .child(showGui, "show")
                .child(reload, "reload")
                .build();

        Sponge.getCommandManager().register(plugin, gui, "guibuilder", "guib");
    }
}
