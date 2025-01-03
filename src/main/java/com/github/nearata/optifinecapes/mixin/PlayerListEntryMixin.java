package com.github.nearata.optifinecapes.mixin;

import com.github.nearata.optifinecapes.OptifineCapes;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.URI;
import java.net.URL;

@Mixin(PlayerListEntry.class)
public class PlayerListEntryMixin
{
    @Shadow
    @Final
    private GameProfile profile;

    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    public void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir)
    {
        final SkinTextures skinTextures = cir.getReturnValue();

        if (skinTextures.capeTexture() != null)
        {
            return;
        }

        if (!OptifineCapes.ofc.containsKey(this.profile.getId()))
        {
            OptifineCapes.ofc.put(this.profile.getId(), null);
            Util.getMainWorkerExecutor().execute(() -> {
                try
                {
                    final URL url = new URI("http://s.optifine.net/capes/" + this.profile.getName() + ".png").toURL();
                    final NativeImage nativeImage = NativeImage.read(url.openStream());
                    final NativeImageBackedTexture dynamicTexture = new NativeImageBackedTexture(this.parseCape(nativeImage));
                    final Identifier identifier1 = MinecraftClient.getInstance().getTextureManager().registerDynamicTexture("optifinecapes/", dynamicTexture);

                    OptifineCapes.ofc.put(this.profile.getId(), identifier1);
                }
                catch (Exception ignored)
                {
                }
            });
        }

        final Identifier identifier = OptifineCapes.ofc.get(this.profile.getId());

        if (identifier != null)
        {
            final SkinTextures skinTextures1 = new SkinTextures(skinTextures.texture(), skinTextures.textureUrl(), identifier, identifier, skinTextures.model(), skinTextures.secure());
            cir.setReturnValue(skinTextures1);
        }
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
