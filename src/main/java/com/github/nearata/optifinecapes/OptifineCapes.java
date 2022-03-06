package com.github.nearata.optifinecapes;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("optifinecapes")
public final class OptifineCapes
{
    private final Minecraft mc = Minecraft.getInstance();
    private List<String> players = new ArrayList<>();

    public OptifineCapes()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public final void renderPlayer(final RenderPlayerEvent.Pre event)
    {
        final AbstractClientPlayerEntity acp = (AbstractClientPlayerEntity) event.getPlayer();
        final String name = acp.getName().getString();

        if (acp.isCapeLoaded() && !this.players.contains(name))
        {
            this.players.add(name);

            final NetworkPlayerInfo playerInfo = acp.getPlayerInfo();

            Util.backgroundExecutor().execute(() -> {
                try
                {
                    final URL url = new URL("http://s.optifine.net/capes/" + name + ".png");
                    final NativeImage nativeImage = NativeImage.read(url.openStream());
                    final DynamicTexture dynamicTexture = new DynamicTexture(this.parseCape(nativeImage));
                    final ResourceLocation resourceLocation = mc.getTextureManager().register("optifinecapes/", dynamicTexture);

                    playerInfo.textureLocations.put(Type.CAPE, resourceLocation);
                    playerInfo.textureLocations.put(Type.ELYTRA, resourceLocation);
                }
                catch (final IOException e)
                {
                    // no cape
                }
            });
        }
    }

    @SubscribeEvent
    public final void clientTick(final ClientTickEvent event)
    {
        if (mc.player == null && !this.players.isEmpty())
        {
            this.players.clear();
        }
    }

    private final NativeImage parseCape(final NativeImage nativeImageIn)
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

        NativeImage nativeImage = new NativeImage(imageWidth, imageHeight, true);

        for (int x = 0; x < imageSrcWidth; x++)
        {
            for (int y = 0; y < imageSrcHeight; y++)
            {
                nativeImage.setPixelRGBA(x, y, nativeImageIn.getPixelRGBA(x, y));
            }
        }

        nativeImageIn.close();

        return nativeImage;
    }
}
