package ru.skyfire.minecraft.guibuilder;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import ru.skyfire.minecraft.guibuilder.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GuiPage {
    private int size;
    private Text pageName;
    private List<GuiIcon> iconList;

    public GuiPage(int size, Text pageName, List<GuiIcon> iconList) {
        this.size = size;
        this.pageName = pageName;
        this.iconList = iconList;
    }

    public static GuiPage fromConfig(ConfigurationNode node, Map<String, ItemStack> itemsMap){
        int size = node.getNode("size").getInt();
        Text pageName = TextUtil.colorString(node.getNode("name").getString());
        List<GuiIcon> iconList = new ArrayList<>();
        for(ConfigurationNode n : node.getNode("icons").getChildrenList()){
            iconList.add(GuiIcon.fromConfig(n, itemsMap));
        }
        return new GuiPage(size, pageName, iconList);
    }

    public int getSize() {
        return size;
    }

    public List<GuiIcon> getIconList() {
        return iconList;
    }

    public Text getPageName() {
        return pageName;
    }
}
