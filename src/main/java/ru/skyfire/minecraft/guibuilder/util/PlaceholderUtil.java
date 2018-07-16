package ru.skyfire.minecraft.guibuilder.util;

import org.spongepowered.api.entity.living.player.Player;

public class PlaceholderUtil {
    public static String replace(String input, Player player){
        return input.replace("%%player", player.getName())
                .replace("%%balance", EcoUtil.getBalance(player).toString());

    }
}
