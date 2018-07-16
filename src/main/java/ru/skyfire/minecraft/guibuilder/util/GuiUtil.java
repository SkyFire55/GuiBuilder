package ru.skyfire.minecraft.guibuilder.util;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GuiUtil {

    public static boolean isStillViewing(Player player, Inventory inventory) {
        if(!player.getOpenInventory().isPresent()){
            return false;
        }
        final String title1 = player.getOpenInventory().get().getInventoryProperty(InventoryTitle.class).get().getValue().toPlain();
        final String title2 = inventory.getInventoryProperty(InventoryTitle.class).get().getValue().toPlain();
        if(title1.equalsIgnoreCase(title2)){
            return true;
        }
        return false;
    }

    public static ItemStack getClickedItem(ClickInventoryEvent event) {
        List<SlotTransaction> transactions = event.getTransactions();
        return transactions.stream().map(st -> st.getOriginal().createStack().copy()).findFirst().orElse(null);
    }

    public static ItemStack getRandomStub(){
        Random rand = new Random();
        ItemStack colorStub = ItemStack.of(ItemTypes.STAINED_GLASS_PANE, 1);
        ItemStack stub = colorStub.copy();
        stub = ItemStack.builder().fromContainer(stub.toContainer()
                .set(DataQuery.of("UnsafeDamage"), rand.nextInt(15))).build();
        stub.offer(Keys.DISPLAY_NAME, Text.EMPTY);
        return stub;
    }

    public static void setStackToInventory(ItemStack stack, int index, Inventory inv){
        Iterator inventoryIt = inv.slots().iterator();
        int i = 1;
        Slot slot;
        while (inventoryIt.hasNext()) {
            slot = (Slot) inventoryIt.next();
            if(i==index){
                slot.poll();
                slot.offer(stack.copy());
                return;
            }
            i++;
        }
    }

    public static boolean isSlotEmpty(int z, Inventory inv){
        Iterator inventoryIt = inv.slots().iterator();
        int i = 1;
        Slot slot;
        while (inventoryIt.hasNext()) {
            slot = (Slot) inventoryIt.next();
            if(i==z){
                return slot.peek().isPresent();
            }
            i++;
        }
        return false;
    }

    public static void setToFirstEmptySlot(ItemStack stack, Inventory inv){
        Iterator inventoryIt = inv.slots().iterator();
        Slot slot;
        while (inventoryIt.hasNext()) {
            slot = (Slot) inventoryIt.next();
            if(!slot.peek().isPresent()){
                slot.poll();
                slot.offer(stack.copy());
                return;
            }
        }
    }
}
