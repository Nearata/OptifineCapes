package com.github.nearata.optifinecapes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import com.google.common.hash.Hashing;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;

public final class CapeManager
{
    private static final ResourceLocation NO_CAPE = new ResourceLocation("optifinecapes", "textures/no_cape.png");
    private static final Minecraft mc = Minecraft.getInstance();

    public static final ResourceLocation loadCape(String uuid, String username)
    {
        final String url = String.format("http://s.optifine.net/capes/%s.png", username);
        final String s = Hashing.sha1().hashString(uuid, Charset.defaultCharset()).toString();
        ResourceLocation resourcelocation = new ResourceLocation("optifinecapes/" + s);

        File file1 = new File(capeDir().toFile(), s.length() > 2 ? s.substring(0, 2) : "xx");
        File file2 = new File(file1, s);
        DownloadingTexture downloadingtexture = new DownloadingTexture(file2, url, NO_CAPE, new IImageBuffer() {
            public NativeImage parseUserSkin(NativeImage nativeImageIn)
            {
                int imageWidth = 64;
                int imageHeight = 32;
                int imageSrcWidth = nativeImageIn.getWidth();

                for (int imageSrcHeight = nativeImageIn.getHeight(); imageWidth < imageSrcWidth || imageHeight < imageSrcHeight; imageHeight *= 2)
                {
                    imageWidth *= 2;
                }

                NativeImage nativeImage = new NativeImage(imageWidth, imageHeight, true);
                nativeImage.copyImageData(nativeImageIn);
                nativeImageIn.close();

                return nativeImage;
            }

            public void skinAvailable()
            {
            }
        });

        mc.textureManager.loadTexture(resourcelocation, downloadingtexture);

        return resourcelocation;
    }

    public static final void clearCapes()
    {
        try
        {
            Files.walk(capeDir())
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static final Path capeDir()
    {
        return Paths.get(mc.gameDir.getPath(), "assets", "optifinecapes");
    }
}
