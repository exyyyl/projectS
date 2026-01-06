package org.example.exyyyl.projects.mixin;

import net.minecraft.world.entity.player.Player;
import org.example.exyyyl.projects.TimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerFoodMixin {
    
    @Inject(method = "causeFoodExhaustion", at = @At("HEAD"), cancellable = true)
    private void onCauseFoodExhaustion(float exhaustion, CallbackInfo ci) {
        // Don't cause food exhaustion while time is stopped
        if (TimeController.isTimeStopped()) {
            ci.cancel();
        }
    }
}

