package com.github.nearata.optifinecapes;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class Utils
{
    private static final String optifineCapesUrl = "http://s.optifine.net/capes/%s.png";
    private static final ResourceLocation noCapeImage = new ResourceLocation(OptifineCapes.MODID, "textures/no_cape.png");

    public static ThreadDownloadImageData getDownloadImageCape(ResourceLocation resourceLocation, String username)
    {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);

        iTextureObject = new ThreadDownloadImageData((File) null, String.format(Utils.optifineCapesUrl, username), Utils.noCapeImage, new ParseCape());
        textureManager.loadTexture(resourceLocation, iTextureObject);

        return (ThreadDownloadImageData) iTextureObject;
    }
}
