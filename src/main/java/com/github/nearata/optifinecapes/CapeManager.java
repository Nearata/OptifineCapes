package com.github.nearata.optifinecapes;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public final class CapeManager
{
    private static final Minecraft mc = Minecraft.getInstance();

    public static final void loadCape(AbstractClientPlayerEntity acp)
    {
        final String username = acp.getDisplayName().getString();
        
        final NetworkPlayerInfo playerInfo = ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayerEntity.class, acp, "field_175157_a");
        final Map<Type, ResourceLocation> playerTextures = ObfuscationReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, playerInfo, "field_187107_a");
        
        Runnable runnable = () -> {
            try
            {
                final URL url = new URL(String.format("http://s.optifine.net/capes/%s.png", username));
                
                final NativeImage nativeImage = NativeImage.read(url.openStream());
                
                final DynamicTexture texture = new DynamicTexture(parseCape(nativeImage));
                final ResourceLocation resourcelocation = mc.getTextureManager().getDynamicTextureLocation("optifinecapes/", texture);

                playerTextures.put(Type.CAPE, resourcelocation);
                playerTextures.put(Type.ELYTRA, resourcelocation);
            }
            catch (IOException e)
            {
                // no cape
            }
        };
        
        Util.getServerExecutor().execute(runnable);
    }
    
    private static final NativeImage parseCape(NativeImage nativeImageIn)
    {
        int imageWidth = 64;
        int imageHeight = 32;
        int imageSrcWidth = nativeImageIn.getWidth();

        for (int srcHeight = nativeImageIn.getHeight(); imageWidth < imageSrcWidth || imageHeight < srcHeight; imageHeight *= 2)
        {
            imageWidth *= 2;
        }

        NativeImage nativeImage = new NativeImage(imageWidth, imageHeight, true);
        nativeImage.copyImageData(nativeImageIn);
        nativeImageIn.close();
        
        return nativeImage;
    }
}
