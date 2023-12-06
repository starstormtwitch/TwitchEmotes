package me.falu.twitchemotes.emote;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Builder;
import lombok.ToString;
import me.falu.twitchemotes.TwitchEmotes;
import me.falu.twitchemotes.emote.texture.EmoteTextureHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.math.Matrix4f;

@Builder
@ToString
public class Emote {
    public final String name;
    public final String id;
    public final String url;
    public final ImageType imageType;
    public final EmoteTextureHandler textureHandler = new EmoteTextureHandler(this);

    public boolean draw(float x, float y, Matrix4f matrix, float alpha) {
        NativeImage img = this.textureHandler.getImage();
        if (img != null) {
            this.createTextureBuffer(matrix, x - 1.0F, y - 1.0F, alpha);
            this.textureHandler.postRender();
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private void createTextureBuffer(Matrix4f matrix, float x, float y, float alpha) {
        int glId = this.textureHandler.getGlId();
        if (glId == -1) { return; }
        float size = TwitchEmotes.EMOTE_SIZE;
        float width = this.textureHandler.getWidth();
        RenderSystem.activeTexture(33984);
        RenderSystem.bindTexture(glId);
        RenderSystem.enableBlend();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder
                .vertex(matrix, x, y, 100.0F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .texture(0.0F, 0.0F)
                .next();
        bufferBuilder
                .vertex(matrix, x, y + size, 100.0F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .texture(0.0F, 1.0F)
                .next();
        bufferBuilder
                .vertex(matrix, x + width, y + size, 100.0F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .texture(1.0F, 1.0F)
                .next();
        bufferBuilder
                .vertex(matrix, x + width, y, 100.0F)
                .color(1.0F, 1.0F, 1.0F, alpha)
                .texture(1.0F, 0.0F)
                .next();
        RenderSystem.enableAlphaTest();
        Tessellator.getInstance().draw();
        RenderSystem.disableBlend();
    }

    public enum ImageType {
        GIF("gif"),
        WEBP("webp"),
        STATIC("png");

        public final String suffix;

        ImageType(String suffix) {
            this.suffix = suffix;
        }

        public static ImageType fromSuffix(String suffix) {
            for (ImageType type : ImageType.values()) {
                if (type.suffix.equals(suffix)) {
                    return type;
                }
            }
            return STATIC;
        }
    }
}