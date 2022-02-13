package io.github.kabanfriends.lessutilities.websocket.client.type;

import io.github.kabanfriends.lessutilities.utils.templates.CompressionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RawTemplateItem extends AbstractTemplateItem {

    @Override
    public String getIdentifier() {
        return "raw_template";
    }

    @Override
    public String parseJsonData(String templateData) throws IOException {
        byte[] bytes = CompressionUtils.toBase64(CompressionUtils.toGZIP(templateData.getBytes(StandardCharsets.UTF_8)));

        return new String(bytes);
    }
}