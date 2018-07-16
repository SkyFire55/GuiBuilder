package ru.skyfire.minecraft.guibuilder;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import ru.skyfire.minecraft.guibuilder.util.ItemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.skyfire.minecraft.guibuilder.GuiBuilder.logger;

public class GuiIcon {
    private String itemId;
    private boolean closeOnClick;
    private ItemStack itemStack;
    private int slot;
    private List<Integer> slots;
    private List<String> cmdPlayerList;
    private List<String> cmdConsoleList;
    private String pageLink;

    public GuiIcon(String itemId, boolean closeOnClick, ItemStack itemStack, int slot, List<Integer> slots, List<String> cmdPlayerList, List<String> cmdConsoleList, String pageLink) {
        this.itemId = itemId;
        this.closeOnClick = closeOnClick;
        this.itemStack = itemStack;
        this.slot = slot;
        this.slots = slots;
        this.cmdPlayerList = cmdPlayerList;
        this.cmdConsoleList = cmdConsoleList;
        this.pageLink = pageLink;
    }

    public static GuiIcon fromConfig(ConfigurationNode node, Map<String, ItemStack> itemsMap){
        String itemId = node.getNode("item").getString("default");
        boolean closeOnClick = node.getNode("closeOnClick").getBoolean(true);
        ItemStack bufStack = itemsMap.get(itemId);
        if(bufStack==null){
            bufStack=ItemStack.of(ItemTypes.DIRT, 1);
            logger.error("ERROR IN itemId: "+itemId);
        }
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("guiId", itemId);
        ItemStack itemStack = ItemUtil.applyExtraToItemStack(bufStack.copy(), extraMap);
        int slot = node.getNode("slot").getInt(1);
        List<Integer> slots = new ArrayList<>();
        if(!node.getNode("slots").isVirtual()){
            try {
                slots = node.getNode("slots").getList(TypeToken.of(Integer.class));
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        }
        List<String> cmdPlayerList = new ArrayList<>();
        int x=1;
        for(Object o : node.getNode("cmdPlayer").getChildrenMap().keySet()){
            while(node.getNode("cmdPlayer").getNode(o).isVirtual()){
                x++;
                if(x==20){
                    break;
                }
            }
            cmdPlayerList.add(node.getNode("cmdPlayer",o).getString());
        }
        List<String> cmdConsoleList = new ArrayList<>();
        int y=1;
        for(Object o : node.getNode("cmdConsole").getChildrenMap().keySet()){
            while(node.getNode("cmdConsole").getNode(o).isVirtual()){
                y++;
                if(y==20){
                    break;
                }
            }
            cmdPlayerList.add(node.getNode("cmdConsole",o).getString());
        }
        String iconLink = node.getNode("openPage").getString("");
        return new GuiIcon(itemId, closeOnClick, itemStack, slot, slots, cmdPlayerList, cmdConsoleList, iconLink);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public List<String> getCmdPlayerList() {
        return cmdPlayerList;
    }

    public List<String> getCmdConsoleList() {
        return cmdConsoleList;
    }

    public String getItemId() {
        return itemId;
    }

    public boolean isCloseOnClick() {
        return closeOnClick;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public String getPageLink() {
        return pageLink;
    }
}
