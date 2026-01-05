package org.example.exyyyl.projects.mixin;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.example.exyyyl.projects.TimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public class MobEffectInstanceMixin {
    
    @Shadow private int duration;
    
    @Unique
    private int projects$savedDuration = -1;
    
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTickHead(LivingEntity entity, Runnable runnable, CallbackInfoReturnable<Boolean> cir) {
        // Save duration before tick if time is stopped
        if (TimeController.isTimeStopped()) {
            projects$savedDuration = this.duration;
        }
    }
    
    @Inject(method = "tick", at = @At("RETURN"), cancellable = true)
    private void onTickReturn(LivingEntity entity, Runnable runnable, CallbackInfoReturnable<Boolean> cir) {
        // If time is stopped, restore the saved duration to prevent it from decreasing
        if (TimeController.isTimeStopped() && projects$savedDuration >= 0) {
            // Restore the duration to what it was before the tick
            this.duration = projects$savedDuration;
            // Ensure the effect remains active
            cir.setReturnValue(true);
            projects$savedDuration = -1;
        }
    }
}

