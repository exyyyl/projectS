package org.example.exyyyl.projects.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.example.exyyyl.projects.Projects;

@EventBusSubscriber(modid = Projects.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    
    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        // Register the time stop overlay above the hotbar
        event.registerAbove(
            VanillaGuiLayers.HOTBAR,
            ResourceLocation.fromNamespaceAndPath(Projects.MODID, "time_stop_overlay"),
            new TimeStopOverlay()
        );
    }
}

