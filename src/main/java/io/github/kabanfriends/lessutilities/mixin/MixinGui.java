package io.github.kabanfriends.lessutilities.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kabanfriends.lessutilities.LessUtilities;
import io.github.kabanfriends.lessutilities.utils.ItemUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(Gui.class)
public class MixinGui {

    private static final Map<String, MutableComponent> scopes = new HashMap<>();

    static {
        scopes.put("unsaved",
                new TextComponent("GAME").withStyle((style) -> style.withColor(ChatFormatting.GRAY)));
        scopes.put("saved",
                new TextComponent("SAVE").withStyle((style) -> style.withColor(ChatFormatting.YELLOW)));
        scopes.put("local",
                new TextComponent("LOCAL").withStyle((style) -> style.withColor(ChatFormatting.GREEN)));
    }

    private final Minecraft mc = LessUtilities.MC;
    private ItemStack variableStack;
    private JsonObject varItemNbt;

    @Inject(method = "renderSelectedItemName", at = @At("HEAD"), cancellable = true)
    public void renderHeldItemTooltip(PoseStack stack, CallbackInfo callbackInfo) {
        try {
            ItemStack itemStack = mc.player.getMainHandItem();

            if (variableStack != itemStack) {
                if (ItemUtils.isVar(itemStack, "var")) {
                    variableStack = itemStack;

                    CompoundTag tag = itemStack.getTag();
                    if (tag == null) {
                        return;
                    }

                    CompoundTag publicBukkitNBT = tag.getCompound("PublicBukkitValues");
                    if (publicBukkitNBT == null) {
                        return;
                    }

                    varItemNbt = JsonParser.parseString(
                            publicBukkitNBT.getString("hypercube:varitem")).getAsJsonObject()
                            .getAsJsonObject("data");
                } else {
                    variableStack = null;
                }
            }

            if (variableStack != null) {
                callbackInfo.cancel();

                String name = varItemNbt.get("name").getAsString();
                MutableComponent scope = scopes.get(varItemNbt.get("scope").getAsString());

                int x1 = (mc.getWindow().getGuiScaledWidth() - mc.font.width(
                        new TextComponent(name))) / 2;
                int y1 = mc.getWindow().getGuiScaledHeight() - 45;

                int x2 = (mc.getWindow().getGuiScaledWidth() - mc.font.width(
                        scope.getString())) / 2;
                int y2 = mc.getWindow().getGuiScaledHeight() - 35;

                mc.font.drawShadow(stack, new TextComponent(name), (float) x1,
                        (float) y1, 16777215);
                mc.font.drawShadow(stack, scope, (float) x2, (float) y2,
                        16777215);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
