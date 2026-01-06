package org.example.exyyyl.projects;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

public class TimeController {
    private static boolean timeStopped = false;

    public static boolean isTimeStopped() {
        return timeStopped;
    }
    
    /**
     * Reset state when leaving a world
     */
    public static void restoreAndReset() {
        if (timeStopped) {
            Minecraft mc = Minecraft.getInstance();
            
            // Unfreeze the game
            MinecraftServer server = mc.getSingleplayerServer();
            if (server != null) {
                server.tickRateManager().setFrozen(false);
            }
            
            // Set timeStopped to false BEFORE resume() so Mixin doesn't block it
            timeStopped = false;
            
            // Resume sounds
            mc.getSoundManager().resume();
        } else {
            timeStopped = false;
        }
    }

    public static void toggleTimeStop() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        MinecraftServer server = mc.getSingleplayerServer();
        
        if (player == null || server == null) {
            return; // Only works in singleplayer
        }
        
        timeStopped = !timeStopped;
        
        if (timeStopped) {
            // Freeze all game ticks
            server.tickRateManager().setFrozen(true);
            
            // Pause all sounds - this will pause mob sounds but we'll allow player/block sounds through Mixin
            mc.getSoundManager().pause();
            
            player.displayClientMessage(Component.translatable("message." + Projects.MODID + ".time_stopped"), true);
        } else {
            // Unfreeze game ticks
            server.tickRateManager().setFrozen(false);
            
            // Resume all sounds - this will allow mob sounds to play again
            mc.getSoundManager().resume();
            
            player.displayClientMessage(Component.translatable("message." + Projects.MODID + ".time_resumed"), true);
        }
    }

    // Register key mappings on MOD bus
    @EventBusSubscriber(modid = Projects.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            ModKeyBindings.init();
            event.register(ModKeyBindings.timeStopKey);
        }
    }

    // Handle key presses and world events
    @EventBusSubscriber(modid = Projects.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (ModKeyBindings.timeStopKey != null && ModKeyBindings.timeStopKey.consumeClick()) {
                toggleTimeStop();
            }
        }
        
        @SubscribeEvent
        public static void onScreenOpen(ScreenEvent.Opening event) {
            // Pause all sounds when opening any screen (inventory, pause menu, etc.) during time stop
            if (timeStopped) {
                Minecraft mc = Minecraft.getInstance();
                mc.getSoundManager().pause();
            }
        }
        
        @SubscribeEvent
        public static void onScreenClose(ScreenEvent.Closing event) {
            // Pause all sounds when closing any screen during time stop
            // This ensures mob sounds stay paused after closing inventory/ESC menu
            if (timeStopped) {
                Minecraft mc = Minecraft.getInstance();
                mc.getSoundManager().pause();
            }
        }
        
        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            // Pause sounds when hitting an entity during time stop
            // This will stop any mob sounds that somehow started playing
            if (timeStopped) {
                Minecraft mc = Minecraft.getInstance();
                mc.getSoundManager().pause();
            }
        }
        
        @SubscribeEvent
        public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            restoreAndReset();
        }
        
        @SubscribeEvent
        public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
            timeStopped = false;
        }
    }
}
