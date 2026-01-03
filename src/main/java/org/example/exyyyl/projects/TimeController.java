package org.example.exyyyl.projects;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TimeController {
    private static boolean timeStopped = false;
    private static final Set<UUID> frozenEntities = new HashSet<>();

    public static boolean isTimeStopped() {
        return timeStopped;
    }

    public static void toggleTimeStop() {
        timeStopped = !timeStopped;
        
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (timeStopped) {
                player.displayClientMessage(Component.translatable("message." + Projects.MODID + ".time_stopped"), true);
            } else {
                // Don't clear frozenEntities here - let onEntityTick restore AI first
                player.displayClientMessage(Component.translatable("message." + Projects.MODID + ".time_resumed"), true);
            }
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

    // Handle key presses on GAME bus (default)
    @EventBusSubscriber(modid = Projects.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (ModKeyBindings.timeStopKey != null && ModKeyBindings.timeStopKey.consumeClick()) {
                toggleTimeStop();
            }
        }
    }

    // Handle mob freezing on GAME bus (default)
    @EventBusSubscriber(modid = Projects.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onEntityTick(EntityTickEvent.Pre event) {
            // Don't freeze players
            if (event.getEntity() instanceof Player) {
                return;
            }

            if (!timeStopped) {
                // Restore AI and sounds when time resumes
                if (event.getEntity() instanceof Mob mob) {
                    if (frozenEntities.contains(mob.getUUID())) {
                        mob.setNoAi(false);
                        mob.setSilent(false);
                        frozenEntities.remove(mob.getUUID());
                    }
                }
                return;
            }

            // Only freeze mobs
            if (event.getEntity() instanceof Mob mob) {
                // Stop the mob's movement
                mob.setDeltaMovement(0, 0, 0);
                
                // Stop the mob's AI
                mob.getNavigation().stop();
                
                // Reset attack target
                mob.setTarget(null);

                // Keep the mob in place - prevent all movement
                mob.setNoAi(true);
                
                // Mute the mob
                mob.setSilent(true);
                
                frozenEntities.add(mob.getUUID());
            }
        }
    }
}
