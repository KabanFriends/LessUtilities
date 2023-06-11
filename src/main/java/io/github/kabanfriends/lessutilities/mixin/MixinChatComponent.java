package io.github.kabanfriends.lessutilities.mixin;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.*;

import io.github.kabanfriends.lessutilities.config.Config;
import io.github.kabanfriends.lessutilities.sidedchat.ChatRule;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
public abstract class MixinChatComponent {
    @Shadow
    @Final
    private static Logger LOGGER;

    private final List<GuiMessage.Line> sideVisibleMessages = Lists.newArrayList();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private int chatScrollbarPos;

    @Shadow
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Shadow
    private boolean newMessageSinceScroll;

    private int oldSideChatWidth = -1;
    private int sideScrolledLines;

    @Shadow
    private static double getTimeFactor(int age) {
        return 0;
    }

    @Shadow
    protected abstract boolean isChatFocused();

    @Shadow
    protected abstract boolean isChatHidden();

    @Shadow
    public abstract double getScale();

    @Shadow
    public abstract int getLinesPerPage();

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract void rescaleChat();

    @Shadow
    public abstract int getLineHeight();

    @Shadow
    public abstract int getTagIconLeft(GuiMessage.Line line);

    @Shadow
    public abstract int getMessageEndIndexAt(double d, double e);

    @Shadow
    public abstract double screenToChatX(double d);

    @Shadow
    public abstract double screenToChatY(double d);

    @Shadow
    public abstract void drawTagIcon(GuiGraphics guiGraphics, int i, int j, GuiMessageTag.Icon icon);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(GuiGraphics graphics, int tickDelta, int j, int k, CallbackInfo ci) {
        if (!this.isChatHidden()) {
            int renderedLines = renderChat(graphics, tickDelta, j, k, trimmedMessages, 0, getWidth(),
                    chatScrollbarPos, true);
            renderChat(graphics, tickDelta, j, k, sideVisibleMessages, getSideChatStartX(),
                    getSideChatWidth(), sideScrolledLines, false);
            renderOthers(graphics, renderedLines);
        }
        ci.cancel();
    }

    /**
     * Renders a chat box, drawn into its own function so I don't repeat code for side chat Most
     * params are just stuff the code needs and I don't have the confidence to change
     *
     * @param displayX      X to display at
     * @param width         Width of the chat to display
     * @param scrolledLines The amount of lines scrolled on this chat
     * @return The amount of lines actually rendered. Other parts of rendering need to know this
     */
    @SuppressWarnings("deprecation")
    private int renderChat(GuiGraphics graphics, int i, int j, int k, List<GuiMessage.Line> visibleMessages, int displayX, int width, int scrolledLines, boolean renderTags) {
        // reset chat (re-allign all text) whenever calculated size for side chat changes
        int newSideChatWidth = getSideChatWidth();
        if (newSideChatWidth != oldSideChatWidth) {
            oldSideChatWidth = newSideChatWidth;
            rescaleChat();
        }

        int l = this.getLinesPerPage();
        int m = visibleMessages.size();
        int renderedLines = 0;
        if (m > 0) {
            boolean bl = this.isChatFocused();
            float f = (float)this.getScale();
            int n = Mth.ceil((float)width / f);
            int o = this.minecraft.getWindow().getGuiScaledHeight();
            graphics.pose().pushPose();
            graphics.pose().scale(f, f, 1.0F);
            graphics.pose().translate(4.0F, 0.0F, 0.0F);
            int p = Mth.floor((float)(o - 40) / f);
            int q = this.getMessageEndIndexAt(this.screenToChatX(j), this.screenToChatY(k));
            double d = this.minecraft.options.chatOpacity().get();
            double e = this.minecraft.options.textBackgroundOpacity().get();
            double g = this.minecraft.options.chatLineSpacing().get();
            int r = this.getLineHeight();
            int s = (int)Math.round(-8.0 * (g + 1.0) + 4.0 * g);

            int w;
            int x;
            int y;
            int aa;
            for(int u = 0; u + scrolledLines < visibleMessages.size() && u < l; ++u) {
                int v = u + scrolledLines;
                GuiMessage.Line line = visibleMessages.get(v);
                if (line != null) {
                    w = i - line.addedTime();
                    if (w < 200 || bl) {
                        double h = bl ? 1.0 : getTimeFactor(w);
                        x = (int)(255.0 * h * d);
                        y = (int)(255.0 * h * e);
                        ++renderedLines;
                        if (x > 3) {
                            boolean z = false;
                            aa = p - u * r;
                            int ab = aa + s;
                            graphics.pose().pushPose();
                            graphics.pose().translate(displayX, 0.0F, 50.0F);

                            int fillX = renderTags ? -4 : -2;
                            graphics.fill(fillX, aa - r, 0 + n + 4 + 4, aa, y << 24);
                            if (renderTags) {
                                GuiMessageTag guiMessageTag = line.tag();
                                if (guiMessageTag != null) {
                                    int ac = guiMessageTag.indicatorColor() | x << 24;
                                    graphics.fill(-4, aa - r, -2, aa, ac);
                                    if (v == q && guiMessageTag.icon() != null) {
                                        int ad = this.getTagIconLeft(line);
                                        Objects.requireNonNull(this.minecraft.font);
                                        int ae = ab + 9;
                                        this.drawTagIcon(graphics, ad, ae, guiMessageTag.icon());
                                    }
                                }
                            }

                            RenderSystem.enableBlend();
                            graphics.pose().translate(0.0F, 0.0F, 50.0F);
                            graphics.drawString(this.minecraft.font, line.content(), 0, ab, 16777215 + (x << 24));
                            RenderSystem.disableBlend();
                            graphics.pose().popPose();
                        }
                    }
                }
            }
            graphics.pose().popPose();
        }
        return renderedLines;
    }

    @SuppressWarnings("deprecation")
    private void renderOthers(GuiGraphics graphics, int renderedLines) {
        int visibleMessagesSize = this.trimmedMessages.size();
        if (visibleMessagesSize == 0) {
            return;
        }
        boolean chatFocused = this.isChatFocused();

        float chatScale = (float) this.getScale();
        int k = Mth.ceil((double) this.getWidth() / chatScale);
        graphics.pose().pushPose();
        graphics.pose().scale(chatScale, chatScale, 1.0f);
        graphics.pose().translate(4.0F, 0.0F, 0.0F);
        double opacity = this.minecraft.options.chatOpacity().get();
        double backgroundOpacity = this.minecraft.options.textBackgroundOpacity().get();

        long queueSize = this.minecraft.getChatListener().queueSize();
        if (queueSize > 0L) {
            int m = (int) (128.0D * opacity);
            int w = (int) (255.0D * backgroundOpacity);
            graphics.pose().pushPose();
            graphics.pose().translate(0.0F, 0.0F, 50.0F);
            graphics.fill(-2, 0, k + 4, 9, w << 24);
            RenderSystem.enableBlend();
            graphics.pose().translate(0.0F, 0.0F, 50.0F);
            graphics.drawString(this.minecraft.font,
                    Component.translatable("chat.queue", queueSize), 0, 1,
                    16777215 + (m << 24));
            graphics.pose().popPose();
            RenderSystem.disableBlend();
        }

        if (chatFocused) {
            int v = 9;
            int w = visibleMessagesSize * v + visibleMessagesSize;
            int x = renderedLines * v + renderedLines;
            int y = chatScrollbarPos * x / visibleMessagesSize;
            int z = x * x / w;
            if (w != x) {
                int aa = y > 0 ? 170 : 96;
                int ab = this.newMessageSinceScroll ? 13382451 : 3355562;
                int ac = k + 4;
                graphics.fill(ac, -y, ac + 2, -y - z, ab + (aa << 24));
                graphics.fill(ac, -y, ac + 1, -y - z, 13421772 + (aa << 24));
            }
        }

        graphics.pose().popPose();
    }

    private int getSideChatStartX() {
        return (int) ((this.minecraft.getWindow().getGuiScaledWidth() - getSideChatWidth())
                / getSideChatScale()) - 4;
    }

    private int getSideChatWidth() {
        int configWidth = Config.getConfig().sideChatWidth;

        // if the width in config is valid
        if (configWidth > 0) {
            return configWidth;
        } else { // else if 0 or less, auto size the side chat
            int rawWidth = Math.min(
                    (this.minecraft.getWindow().getGuiScaledWidth() - getWidth() - 14),
                    getWidth()
            );
            // if the calculated width <= 0 (window really small), have 1 as a failsafe value
            return rawWidth > 0 ? rawWidth : 1;
        }
    }

    // just incase i want to re-add the option to change side chat scale
    private double getSideChatScale() {
        return getScale();
    }

    @Inject(method = "clearMessages", at = @At("TAIL"))
    private void clearMessages(boolean clearHistory, CallbackInfo ci) {
        sideVisibleMessages.clear();
    }


    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;ILnet/minecraft/client/GuiMessageTag;Z)V", at = @At("TAIL"))
    private void addMessage(Component component, MessageSignature messageSignature, int messageId, GuiMessageTag guiMessageTag, boolean refresh, CallbackInfo ci) {
        boolean matchedARule = false;
        if (Config.getConfig().useSideChat) {
            for (ChatRule chatRule : ChatRule.getChatRules()) {
                // compare against all rules
                if (chatRule.matches(component)) {
                    if (!matchedARule) {
                        addToChat(component, messageId, guiMessageTag);
                        matchedARule = true;
                    }
                }
            }
        }

        // if matched rule, remove last from
        if (matchedARule) {
            int i = Mth.floor((double) this.getWidth() / this.getScale());
            int addedMessageCount = ComponentRenderUtils.wrapComponents(component, i,
                    this.minecraft.font).size();
            // remove the last addedMessageCount messages from the visible messages
            // this has the effect of removing last message sent to main (to go to side instead)
            trimmedMessages.subList(0, addedMessageCount).clear();
        }
    }

    private void addToChat(Component message, int chatLineId,
                           GuiMessageTag guiMessageTag) {
        int i = Mth.floor((double) this.getSideChatWidth() / this.getSideChatScale());

        List<FormattedCharSequence> list = ComponentRenderUtils.wrapComponents(message, i, this.minecraft.font);
        for(int k = 0; k < list.size(); ++k) {
            FormattedCharSequence formattedCharSequence = list.get(k);

            boolean bl3 = k == list.size() - 1;
            sideVisibleMessages.add(0, new GuiMessage.Line(chatLineId, formattedCharSequence, guiMessageTag, bl3));

        }
    }

    @Inject(method = "rescaleChat", at = @At("HEAD"))
    private void rescaleChat(CallbackInfo ci) {
        sideVisibleMessages.clear();
    }

    // another copy from minecraft decompiled code
    // the main difference is switching references from main to side chat
    // and subtracting getSideChatStartX() from the adjusted x
    @Inject(method = "getClickedComponentStyleAt", at = @At("HEAD"), cancellable = true)
    private void getClickedComponentStyleAt(double x, double y, CallbackInfoReturnable<Style> cir) {
        if (this.isChatFocused() && !this.minecraft.options.hideGui && !this.isChatHidden()) {
            double scale = this.getSideChatScale();
            double adjustedX = (x - 2.0D) - getSideChatStartX();
            double adjustedY = (double) this.minecraft.getWindow().getGuiScaledHeight() - y - 40.0D;
            adjustedX = Mth.floor(adjustedX / scale);
            adjustedY = Mth.floor(
                    adjustedY / (scale * (this.minecraft.options.chatLineSpacing().get() + 1.0D)));
            if (!(adjustedX < 0.0D) && !(adjustedY < 0.0D)) {
                int size = Math.min(this.getLinesPerPage(), this.sideVisibleMessages.size());
                if (adjustedX <= (double) Mth.floor(
                        (double) this.getSideChatWidth() / scale)) {
                    if (adjustedY < (double) (9 * size + size)) {
                        int line = (int) (adjustedY / 9.0D + (double) sideScrolledLines);
                        if (line >= 0 && line < this.sideVisibleMessages.size()) {
                            GuiMessage.Line chatHudLine = this.sideVisibleMessages.get(line);
                            cir.setReturnValue(this.minecraft.font.getSplitter()
                                    .componentStyleAtWidth(chatHudLine.content(), (int) adjustedX));
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "resetChatScroll", at = @At("TAIL"))
    private void resetChatScroll(CallbackInfo ci) {
        sideScrolledLines = 0;
    }

    @Inject(method = "scrollChat", at = @At("TAIL"))
    private void scrollChat(int amount, CallbackInfo ci) {
        sideScrolledLines = (int) ((double) this.chatScrollbarPos + amount);
        int i = this.sideVisibleMessages.size();
        if (sideScrolledLines > i - this.getLinesPerPage()) {
            sideScrolledLines = i - this.getLinesPerPage();
        }

        if (sideScrolledLines <= 0) {
            sideScrolledLines = 0;
        }
    }
}