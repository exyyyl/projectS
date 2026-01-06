package org.example.exyyyl.projects.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.example.exyyyl.projects.TimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin {
    
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(LivingEntity entity, Runnable runnable, CallbackInfoReturnable<Boolean> cir) {
        // Prevent potion effects from ticking (applying their effects) while time is stopped
        // The effect remains active but doesn't apply regeneration/poison/etc.
        if (TimeController.isTimeStopped()) {
            // Return true to keep the effect active, but cancel the tick to prevent effects from applying
            cir.setReturnValue(true);
        }
    }
}

