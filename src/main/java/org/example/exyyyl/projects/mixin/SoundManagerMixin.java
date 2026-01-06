package org.example.exyyyl.projects.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import org.example.exyyyl.projects.TimeController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    
    @Inject(method = "resume", at = @At("HEAD"), cancellable = true)
    private void onResume(CallbackInfo ci) {
        // Block sound resume while time is stopped (except when we explicitly resume)
        // This prevents sounds from resuming when opening inventory/ESC menu
        if (TimeController.isTimeStopped()) {
            ci.cancel();
        }
    }
    
    /**
     * Helper method to check if a sound should be blocked
     */
    private boolean shouldBlockSound(SoundSource source) {
        if (!TimeController.isTimeStopped()) {
            return false;
        }
        // Allow player sounds, block sounds (breaking blocks), and UI sounds
        return source != SoundSource.PLAYERS && source != SoundSource.BLOCKS && source != SoundSource.MASTER;
    }
    
    @Inject(method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V", at = @At("HEAD"), cancellable = true)
    private void onPlay(SoundInstance sound, CallbackInfo ci) {
        // Block new mob sounds while time is stopped, but allow player and block sounds
        if (shouldBlockSound(sound.getSource())) {
            ci.cancel();
        }
    }
    
    @Inject(method = "playDelayed", at = @At("HEAD"), cancellable = true)
    private void onPlayDelayed(SoundInstance sound, int delay, CallbackInfo ci) {
        // Block delayed mob sounds while time is stopped
        if (shouldBlockSound(sound.getSource())) {
            ci.cancel();
        }
    }
    
    @Inject(method = "queueTickingSound", at = @At("HEAD"), cancellable = true)
    private void onQueueTickingSound(net.minecraft.client.resources.sounds.TickableSoundInstance sound, CallbackInfo ci) {
        // Block ticking sounds (like bee buzzing) while time is stopped
        if (shouldBlockSound(sound.getSource())) {
            ci.cancel();
        }
    }
}

