package org.example.exyyyl.projects.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.example.exyyyl.projects.TimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class PlayerAirMixin {
    
    @Inject(method = "decreaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void onDecreaseAirSupply(int air, CallbackInfoReturnable<Integer> cir) {
        // Don't decrease air while time is stopped
        if (TimeController.isTimeStopped()) {
            // Return the same air value (no decrease)
            cir.setReturnValue(air);
        }
    }
}

