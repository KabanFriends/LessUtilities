package io.github.kabanfriends.lessutilities.utils;

import com.google.gson.JsonParser;
import io.github.kabanfriends.lessutilities.LessUtilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ItemUtils {

    public static void giveCreativeItem(ItemStack item, boolean preferHand) {
        Minecraft mc = LessUtilities.MC;
        NonNullList<ItemStack> mainInventory = mc.player.getInventory().items;

        if (preferHand) {
            if (mc.player.getMainHandItem().isEmpty()) {
                mc.gameMode.handleCreativeModeItemAdd(item, mc.player.getInventory().selected + 36);
                return;
            }
        }

        for (int index = 0; index < mainInventory.size(); index++) {
            ItemStack i = mainInventory.get(index);
            System.out.println(index);
            System.out.println(i.getItem());
            if (i.getItem() == Items.AIR) {
                if (index < 9)
                    LessUtilities.MC.gameMode.handleCreativeModeItemAdd(item, index + 36);
                mainInventory.set(index, item);
                return;
            }
        }
    }

    public static boolean isVar(ItemStack stack, String type) {
        try {
            CompoundTag tag = stack.getTag();
            if (tag == null) {
                return false;
            }

            CompoundTag publicBukkitNBT = tag.getCompound("PublicBukkitValues");
            if (publicBukkitNBT == null) {
                return false;
            }

            if (publicBukkitNBT.getString("hypercube:varitem").length() > 0) {
                return JsonParser.parseString(publicBukkitNBT.getString("hypercube:varitem")).getAsJsonObject().get("id").getAsString().equalsIgnoreCase(type);
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
