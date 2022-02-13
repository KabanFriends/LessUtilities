package io.github.kabanfriends.lessutilities.utils;

import io.github.kabanfriends.lessutilities.LessUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemUtils {
    public static void giveCreativeItem(ItemStack item, boolean preferHand) {
        Minecraft mc = LessUtilities.MC;
        NonNullList<ItemStack> mainInventory = mc.player.inventoryMenu.getItems();

        if (preferHand) {
            if (mc.player.getMainHandItem().isEmpty()) {
                mc.gameMode.handleCreativeModeItemAdd(item, mc.player.getInventory().selected + 36);
                return;
            }
        }

        for (int index = 0; index < mainInventory.size(); index++) {
            ItemStack i = mainInventory.get(index);
            ItemStack compareItem = i.copy();
            compareItem.setCount(item.getCount());
            if (item == compareItem) {
                while (i.getCount() < i.getMaxStackSize() && item.getCount() > 0) {
                    i.setCount(i.getCount() + 1);
                    item.setCount(item.getCount() - 1);
                }
            } else {
                if (i.getItem() == Items.AIR) {
                    if (index < 9)
                        LessUtilities.MC.gameMode.handleCreativeModeItemAdd(item, index + 36);
                    mainInventory.set(index, item);
                    return;
                }
            }
        }
    }
}
