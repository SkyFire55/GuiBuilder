package ru.skyfire.minecraft.guibuilder.util;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.skyfire.minecraft.guibuilder.GuiBuilder;

public class TextUtil {

    public static Text colorString(String rawInput) {
        return TextSerializers.FORMATTING_CODE.deserialize(rawInput);
    }

    public static Text trans(String nodeName){
        ConfigurationNode rootNode = GuiBuilder.getInstance().getTransNode();
        if (rootNode==null){
            return Text.of("Problems with translation config!");
        }
        ConfigurationNode node = rootNode.getNode(nodeName);
        if (node.getString()==null){
            return Text.of("§4Translation error! Check node: "+nodeName);
        }
        return colorString(node.getString());
    }
}
