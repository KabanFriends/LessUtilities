package io.github.kabanfriends.lessutilities.screen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.Window;
import io.github.kabanfriends.lessutilities.LessUtilities;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;

public class CPUUsageText {

    private static final Font font = LessUtilities.MC.font;
    private static final Window mainWindow = LessUtilities.MC.getWindow();
    public static boolean hasLagSlayer;
    public static boolean lagSlayerEnabled;
    private static Component barsText;
    private static Component numberText;
    private static long lastUpdate;

    public CPUUsageText() {
        throw new RuntimeException("CPUUsageText is a static class!");
    }

    public static void updateCPU(ClientboundSetActionBarTextPacket packet) {
        JsonArray msgArray = Component.Serializer.toJsonTree(packet.getText()).getAsJsonObject().getAsJsonArray("extra");
        JsonObject msgPart = msgArray.get(2).getAsJsonObject();

        barsText = packet.getText();

        int sibs = barsText.getSiblings().size();

        Component pText = barsText.getSiblings().get(sibs - 2);
        pText.getSiblings().add(barsText.getSiblings().get(sibs - 1));

        barsText.getSiblings().remove(sibs - 1);
        barsText.getSiblings().remove(sibs - 2);
        barsText.getSiblings().remove(0);

        String numberStr = pText.getString().replaceAll("\\(", "").replaceAll("\\)", "");
        String numberColor = msgPart.get("color").getAsString();

        if (numberColor.equals("dark_gray")) numberColor = "white";

        numberText = Component.Serializer.fromJson("{\"extra\":[{\"italic\":false,\"color\":\"white\",\"text\":\"(\"}," +
                "{\"italic\":false,\"color\":\"" + numberColor + "\",\"text\":\"" + numberStr + "%\"}," +
                "{\"italic\":false,\"color\":\"white\",\"text\":\")\"}],\"text\":\"\"}");

        lastUpdate = System.currentTimeMillis();
    }

    public static void onRender(GuiGraphics graphics) {

        if (barsText == null || numberText == null) return;
        if ((System.currentTimeMillis() - lastUpdate) > 1200) {
            barsText = null;
            numberText = null;
            hasLagSlayer = false;
            return;
        }

        hasLagSlayer = true;

        try {
            renderText(graphics, ChatFormatting.GOLD.getColor());
            renderText(graphics, barsText, 2);
            renderText(graphics, numberText, 1);
        } catch (Exception e) {
            LessUtilities.LOGGER.error("Error while trying to render LagSlayer HUD!");
            e.printStackTrace();
        }
    }

    private static void renderText(GuiGraphics graphics, Component text, int line) {
        graphics.drawString(LessUtilities.MC.font, text, 5, mainWindow.getGuiScaledHeight() - (font.lineHeight * line), 0xffffff);
    }

    private static void renderText(GuiGraphics graphics, int color) {
        graphics.drawString(LessUtilities.MC.font, "CPU Usage:", 5, mainWindow.getGuiScaledHeight() - (font.lineHeight * 3), color);
    }
}