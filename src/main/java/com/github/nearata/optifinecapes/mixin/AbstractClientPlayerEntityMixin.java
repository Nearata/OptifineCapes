package com.github.nearata.optifinecapes.mixin;

import com.github.nearata.optifinecapes.OptifineCapes;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity
{
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile)
    {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "getSkinTextures", at = @At("RETURN"), cancellable = true)
    public void getSkinTextures(CallbackInfoReturnable<SkinTextures> cir)
    {
        if (!this.isPartVisible(PlayerModelPart.CAPE))
        {
            return;
        }

        final SkinTextures skinTextures = cir.getReturnValue();
        final Identifier identifier = OptifineCapes.ofc.get(this.getUuid());

        if (identifier != null)
        {
            final SkinTextures skinTextures1 = new SkinTextures(skinTextures.texture(), skinTextures.textureUrl(), identifier, identifier, skinTextures.model(), skinTextures.secure());
            cir.setReturnValue(skinTextures1);
        }
    }
}
