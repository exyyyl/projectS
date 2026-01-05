package org.example.exyyyl.projects.mixin;

import net.minecraft.client.sounds.SoundManager;
import org.example.exyyyl.projects.TimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    
    @Inject(method = "resume", at = @At("HEAD"), cancellable = true)
    private void onResume(CallbackInfo ci) {
        // Block sound resume while time is stopped
        if (TimeController.isTimeStopped()) {
            ci.cancel();
        }
    }
}

