package com.github.nearata.optifinecapes.mixin;

import com.github.nearata.optifinecapes.OptifineCapes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;
import java.net.URL;
import java.util.UUID;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin
{
    @Inject(method = "render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci)
    {
        if (abstractClientPlayerEntity.getSkinTextures().capeTexture() != null)
        {
            return;
        }

        final UUID uuid = abstractClientPlayerEntity.getUuid();

        if (OptifineCapes.ofc.containsKey(uuid))
        {
            return;
        }

        OptifineCapes.ofc.put(uuid, null);

        Util.getMainWorkerExecutor().execute(() -> {
            try
            {
                final URL url = new URI("http://s.optifine.net/capes/" + abstractClientPlayerEntity.getGameProfile().getName() + ".png").toURL();
                final NativeImage nativeImage = NativeImage.read(url.openStream());
                final NativeImageBackedTexture dynamicTexture = new NativeImageBackedTexture(this.parseCape(nativeImage));
                final Identifier identifier = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("optifinecapes/", dynamicTexture);

                OptifineCapes.ofc.put(uuid, identifier);
            }
            catch (Exception ignored)
            {
            }
        });
    }

    @Unique
    private NativeImage parseCape(final NativeImage nativeImageIn)
    {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = nativeImageIn.getWidth();
        int imageSrcHeight = nativeImageIn.getHeight();

        while (imageWidth < imageSrcWidth || imageHeight < imageSrcHeight)
        {
            imageWidth *= 2;
            imageHeight *= 2;
        }

        final NativeImage nativeImage = new NativeImage(imageWidth, imageHeight, true);

        for (int x = 0; x < imageSrcWidth; x++)
        {
            for (int y = 0; y < imageSrcHeight; y++)
            {
                nativeImage.setColor(x, y, nativeImageIn.getColor(x, y));
            }
        }

        nativeImageIn.close();

        return nativeImage;
    }
}
