package ru.skyfire.minecraft.guibuilder.cmd;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import ru.skyfire.minecraft.guibuilder.GuiBuilder;

public class CmdReload implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        GuiBuilder.getInstance().initConfigs();
        GuiBuilder.getInstance().initGui();
        src.sendMessage(Text.of("GuiBuilder reloaded!"));
        return CommandResult.success();
    }
}
