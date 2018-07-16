package ru.skyfire.minecraft.guibuilder;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import ru.skyfire.minecraft.guibuilder.util.GuiUtil;
import ru.skyfire.minecraft.guibuilder.util.PlaceholderUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiShow {
    public static void showGui(GuiPage page, Player player){
        int inventorySize = page.getSize();

        InventoryArchetype archetype = InventoryArchetype.builder()
                .property(InventoryCapacity.of(inventorySize))
                .build("shop-inv", page.getPageName().toPlain());

        Inventory inventory = Inventory.builder()
                .of(archetype)
                .property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(page.getPageName()))
                .listener(
                        ClickInventoryEvent.class,
                        e -> {
                            e.setCancelled(true);
                            ItemStack pressedItem = GuiUtil.getClickedItem(e);
                            handleButton(pressedItem, player, page);
                        }
                )
                .build(GuiBuilder.getInstance());

        fillInventory(inventory, page);

        player.openInventory(inventory);
    }

    private static void fillInventory(Inventory inv, GuiPage page){
        if(page.getIconList().size()<=0){
            System.out.println("ERROR");
        }
        for(GuiIcon icon : page.getIconList()){
            if(icon.getSlots().isEmpty()){
                GuiUtil.setStackToInventory(icon.getItemStack().copy(), icon.getSlot(), inv);
            } else {
                for(Integer i : icon.getSlots()){
                    GuiUtil.setStackToInventory(icon.getItemStack().copy(), i, inv);
                }
            }
        }
    }

    private static void handleButton(ItemStack pressedItem, Player player, GuiPage page){
        DataContainer container = pressedItem.toContainer();
        String id = container.get(DataQuery.of('.', "UnsafeData.guiId")).orElse(0).toString();
        if(id.equalsIgnoreCase("0")){
            return;
        }
        GuiIcon icon = null;
        for(GuiIcon i : page.getIconList()){
            if(i.getItemId().equalsIgnoreCase(id)){
                icon = i;
                break;
            }
        }
        if(icon == null){
            return;
        }
        List<String> cmdPlayerList  = icon.getCmdPlayerList();
        List<String> cmdConsoleList = icon.getCmdConsoleList();
        String pageLink = icon.getPageLink();

        if(cmdPlayerList.size()>0){
            AtomicInteger x = new AtomicInteger(0);
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(5)
                    .intervalTicks(10)
                    .execute(t-> {
                        String cmd = PlaceholderUtil.replace(cmdPlayerList.get(x.get()), player);
                        Sponge.getCommandManager().process(player, PlaceholderUtil.replace(cmd, player));
                        if(x.get()>=cmdPlayerList.size()-1){
                            t.cancel();
                            return;
                        }
                        x.incrementAndGet();
                    })
                    .submit(GuiBuilder.getInstance());
        }
        if(cmdConsoleList.size()>0){
            AtomicInteger y = new AtomicInteger(0);
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(5)
                    .intervalTicks(10)
                    .execute(t-> {
                        String cmd = PlaceholderUtil.replace(cmdConsoleList.get(y.get()), player);
                        Sponge.getCommandManager().process(Sponge.getServer().getConsole(), PlaceholderUtil.replace(cmd, player));
                        if(y.get()>=cmdConsoleList.size()-1){
                            t.cancel();
                            return;
                        }
                        y.incrementAndGet();
                    })
                    .submit(GuiBuilder.getInstance());
        }
        if(!pageLink.equals("")){
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(4)
                    .execute(t-> {
                        GuiShow.showGui(GuiBuilder.getInstance().getGuiPageMap().get(pageLink), player);
                        t.cancel();
                    })
                    .submit(GuiBuilder.getInstance());
        }
        if(icon.isCloseOnClick()){
            Sponge.getScheduler().createTaskBuilder()
                    .delayTicks(3)
                    .execute(t-> {
                        player.closeInventory();
                        t.cancel();
                    })
                    .submit(GuiBuilder.getInstance());
        }
    }
}
