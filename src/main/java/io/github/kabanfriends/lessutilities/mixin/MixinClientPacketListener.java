package io.github.kabanfriends.lessutilities.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.kabanfriends.lessutilities.LessUtilities;
import io.github.kabanfriends.lessutilities.screen.CPUUsageText;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class MixinClientPacketListener {

    @Inject(method = "handleChat", at = @At("HEAD"))
    public void onChatMessage(ClientboundChatPacket packet, CallbackInfo ci) {
        if (!RenderSystem.isOnRenderThread()) {
            return;
        }
        if (packet.getType() == ChatType.CHAT || packet.getType() == ChatType.SYSTEM) {
            Component component = packet.getMessage();
            String text = component.getString();

            if (text.startsWith("⏵ Click to edit variable: ")) {
                if (component.getStyle().getClickEvent().getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
                    String content = component.getStyle().getClickEvent().getValue();
                    LessUtilities.MC.setScreen(new ChatScreen(content));
                }
            }
        }
    }

    @Inject(method = "setActionBarText", at = @At("HEAD"))
    public void onActionBar(ClientboundSetActionBarTextPacket packet, CallbackInfo ci) {
        if (LessUtilities.MC.player == null) return;
        if (packet.getText().getString().matches("^CPU Usage: \\[▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮▮\\] \\(.*%\\)$")) {
            CPUUsageText.updateCPU(packet);
        }
    }
}
