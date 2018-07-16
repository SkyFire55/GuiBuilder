package ru.skyfire.minecraft.guibuilder.util;

import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.*;

public class ItemUtil {
    public static int giveItemToInventory(Inventory inv, ItemStack itemStack, int amount) {
        int bufamount = amount;
        for (Inventory s : inv.slots()) {
            Slot slot = (Slot) s;
            ItemStack slotStack = slot.peek().orElse(null);
            if (slotStack == null) {
                if (bufamount > itemStack.getMaxStackQuantity()) {
                    ItemStack newitem = itemStack.copy();
                    newitem.setQuantity(newitem.getMaxStackQuantity());
                    slot.offer(newitem);
                    bufamount = bufamount - itemStack.getMaxStackQuantity();
                    continue;
                } else {
                    ItemStack newitem = itemStack.copy();
                    newitem.setQuantity(bufamount);
                    slot.offer(newitem);
                    bufamount = 0;
                    break;
                }
            }
            int itemMeta = (int) itemStack.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0);
            int slotMeta = (int) slotStack.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0);
            if (!(itemStack.getType() == slotStack.getType() && itemMeta==slotMeta)) {
                continue;
            }
            int add = slotStack.getMaxStackQuantity() - slotStack.getQuantity();
            if (bufamount > add) {
                bufamount = bufamount - add;
                slotStack.setQuantity(slotStack.getMaxStackQuantity());
                slot.poll();
                slot.offer(slotStack);
            } else {
                int bufquant = slotStack.getQuantity() + bufamount;
                slotStack.setQuantity(bufquant);
                slot.poll();
                slot.offer(slotStack.copy());
                bufamount = 0 ;
                break;
            }
        }
        return bufamount;
    }

    public static int giveItemToPlayer(Player player, ItemStack itemStack, int amount){
        PlayerInventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(PlayerInventory.class));
        int buf = ItemUtil.giveItemToInventory(inv.getHotbar(), itemStack, amount);
        if (buf > 0) {
            ItemUtil.giveItemToInventory(inv.getMain(), itemStack, buf);
        }
        return buf;
    }


    public static int getFreeSlots(Inventory inv) {
        int res = 0;
        for (Inventory s : inv.slots()) {
            Slot slot = (Slot) s;
            if (!slot.peek().isPresent()) {
                res = res + 1;
            }
        }
        return res;
    }

    public static int getFreeSlotsPlayer(Player player){
        PlayerInventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(PlayerInventory.class));
        return ItemUtil.getFreeSlots(inv.getHotbar()) + ItemUtil.getFreeSlots(inv.getMain());
    }

    public static int takeItemFromInventory(Inventory inventory, ItemStack itemStack, int amount) {
        int bufamount = amount;
        for (Inventory s : inventory.slots()) {
            Slot slot = (Slot) s;
            if (!slot.peek().isPresent()) {
                continue;
            }
            int itemMeta = (int) itemStack.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0);
            int slotMeta = (int) slot.peek().get().toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0);
            if (!(itemStack.getType() == slot.peek().get().getType() && itemMeta==slotMeta)) {
                continue;
            }
            if (bufamount >= slot.peek().get().getQuantity()) {
                bufamount -= slot.peek().get().getQuantity();
                slot.poll();
            } else {
                int bufquant = slot.peek().get().getQuantity() - bufamount;
                slot.poll();
                ItemStack newitem = itemStack.copy();
                newitem.setQuantity(bufquant);
                slot.offer(newitem);
                bufamount = 0;
                break;
            }
        }
        return bufamount;
    }

    public static int takeItemFromPlayer(Player player, ItemStack itemStack, int amount){
        PlayerInventory inv = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(PlayerInventory.class));
        int buf = ItemUtil.takeItemFromInventory(inv.getHotbar(), itemStack, amount);
        if (buf > 0) {
            return ItemUtil.takeItemFromInventory(inv.getMain(), itemStack, buf);
        }
        return buf;
    }

    public static boolean hasItemToTake(Inventory inventory, ItemStack itemStack, int amount) {
        Objects.requireNonNull(itemStack, "Input item is null");
        int bufamount = amount;
        for (Inventory s : inventory.slots()) {
            Slot slot = (Slot) s;
            if (!slot.peek().isPresent()) {
                continue;
            }
            int itemMeta = (int) itemStack.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0);
            int slotMeta = (int) slot.peek().get().toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0);
            if (!(slot.peek().get().getType() == itemStack.getType() && slotMeta==itemMeta)) {
                continue;
            }
            int slotAmount = slot.peek().get().getQuantity();

            if (bufamount >= slotAmount) {
                bufamount -= slotAmount;
                if (bufamount == 0) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    public static ItemStack parseItem(String text) {
        final ItemStack error = ItemStack.builder().add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "ERROR")).itemType(ItemTypes.BARRIER).build();
        String[] parts = text.split(":");
        Optional<ItemType> itemType;
        int metadata = 0;
        switch (parts.length) {
            case 1:
                itemType = Sponge.getGame().getRegistry().getType(ItemType.class, text);
                break;
            case 2:
                itemType = Sponge.getGame().getRegistry().getType(ItemType.class, text);
                if (!itemType.isPresent()) {
                    itemType = Sponge.getRegistry().getType(ItemType.class, parts[0]);
                    try {
                        metadata = Integer.parseInt(parts[1]);
                    } catch (NumberFormatException exc) {
                    }
                }
                break;
            case 3:
                itemType = Sponge.getRegistry().getType(ItemType.class, parts[0] + ":" + parts[1]);
                if (itemType.isPresent()) {
                    metadata = Integer.parseInt(parts[2]);
                }
                break;
            default:
                return error;
        }
        if (itemType.isPresent()) {
            ItemStack stack = ItemStack.of(itemType.get(), 1);
            DataContainer container = stack.toContainer();
            container.set(DataQuery.of("UnsafeDamage"), metadata);
            stack = ItemStack.builder().fromContainer(container).build();
            return stack;
        } else {
            return error;
        }
    }

    public static boolean isItemStacksSimilar(ItemStack stack1, ItemStack stack2){
        return stack1.getType().equals(stack2.getType()) &&
                stack1.get(Keys.DISPLAY_NAME).get().equals(stack2.get(Keys.DISPLAY_NAME).get());
    }

    public static ItemStack fromNode(ConfigurationNode node){
        ItemStack stack = parseItem(node.getNode("material").getString());
        stack.offer(Keys.DISPLAY_NAME, TextUtil.colorString(node.getNode("name").getString("Default Name")));
        List<Text> lore = new ArrayList<>();
        int i=0;
        for (Object b : node.getNode("lore").getChildrenMap().keySet()){
            lore.add(i, TextUtil.colorString(node.getNode("lore", String.valueOf(i+1)).getString()));
            i++;
        }
        if(node.getNode("enchanted").getBoolean(false)){
            List<Enchantment> ench = new ArrayList<>();
            ench.add(Enchantment.builder().type(EnchantmentTypes.INFINITY).level(10).build());
            stack.offer(Keys.ITEM_ENCHANTMENTS, ench);
            stack.offer(Keys.HIDE_ENCHANTMENTS, true);
        }
        stack.offer(Keys.ITEM_LORE, lore);
        Map<String, Object> extraMap = new HashMap<>();
        if(!node.getNode("nbt").getChildrenMap().isEmpty()){
            for(Object b : node.getNode("nbt").getChildrenMap().keySet()){
                extraMap.put(b.toString(), node.getNode("nbt", b).getString());
            }
            stack = applyExtraToItemStack(stack, extraMap);
        }

        return stack;
    }

    public static ItemStack applyExtraToItemStack(ItemStack itemStack, Map<String, Object> extraMap) {
        if(extraMap == null) {
            return itemStack;
        }
        DataContainer container = itemStack.toContainer();
        for(String key : extraMap.keySet()) {
            container.set(DataQuery.of('.', "UnsafeData."+key), extraMap.get(key));
        }
        return ItemStack.builder().fromContainer(container).build();
    }
}
