package ru.skyfire.minecraft.guibuilder.cmd;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import ru.skyfire.minecraft.guibuilder.GuiBuilder;
import ru.skyfire.minecraft.guibuilder.GuiPage;
import ru.skyfire.minecraft.guibuilder.GuiShow;

public class CmdShowGui implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player target = args.<Player>getOne("player").orElse(null);
        String pageStr = args.<String>getOne("page").orElse("default");
        if(target==null){
            src.sendMessage(Text.of("There is no such a player!"));
            return CommandResult.success();
        }
        GuiPage page = GuiBuilder.getInstance().getGuiPageMap().get(pageStr);
        if(page==null){
            src.sendMessage(Text.of("There is no such a page!"));
            return CommandResult.success();
        }
        GuiShow.showGui(page, target);
        return CommandResult.success();
    }
}
