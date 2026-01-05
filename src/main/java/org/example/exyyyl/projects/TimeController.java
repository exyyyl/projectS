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
            
            // Pause all sounds (like ESC pause does)
            mc.getSoundManager().pause();
            
            player.displayClientMessage(Component.translatable("message." + Projects.MODID + ".time_stopped"), true);
        } else {
            // Unfreeze game ticks
            server.tickRateManager().setFrozen(false);
            
            // Resume all sounds
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
        public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
            restoreAndReset();
        }
        
        @SubscribeEvent
        public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
            timeStopped = false;
        }
    }
}
