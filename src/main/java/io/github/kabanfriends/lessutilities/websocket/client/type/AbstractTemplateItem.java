package io.github.kabanfriends.lessutilities.websocket.client.type;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.kabanfriends.lessutilities.LessUtilities;
import io.github.kabanfriends.lessutilities.utils.templates.TemplateUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.IOException;

public abstract class AbstractTemplateItem extends SocketItem {

    @Override
    public ItemStack getItem(String data) throws Exception {
        JsonObject templateObject = JsonParser.parseString(data).getAsJsonObject();

        String author;
        Component name;
        String templateData = parseJsonData(templateObject.get("data").getAsString());

        ItemStack stack = new ItemStack(Items.ENDER_CHEST);
        if (templateObject.has("author")) {
            author = templateObject.get("author").getAsString();
        } else {
            author = LessUtilities.MC.player.getGameProfile().getName();
        }
        if (templateObject.has("name")) {
            name = Component.literal(templateObject.get("name").getAsString());
        } else {
            name = Component.literal("Imported Code Template").withStyle((style) -> style.withColor(TextColor.fromRgb(11163135)));
        }
        int version;
        if (templateObject.has("version")) {
            version = templateObject.get("version").getAsInt();
        } else {
            version = TemplateUtils.VERSION;
        }

        stack.setHoverName(name);
        TemplateUtils.applyRawTemplateNBT(stack, name, author, templateData, version);
        return stack;
    }

    public abstract String parseJsonData(String templateData) throws IOException;

}