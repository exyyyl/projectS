package org.example.exyyyl.projects.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import org.example.exyyyl.projects.TimeController;

public class TimeStopOverlay implements LayeredDraw.Layer {
    
    private static final ResourceLocation CLOCK_ICON = ResourceLocation.withDefaultNamespace("textures/item/clock_00.png");
    
    // Icon size
    private static final int ICON_SIZE = 24;
    // Padding from screen edge
    private static final int PADDING = 10;
    
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        
        // Don't render if UI is hidden (F1) or time is not stopped
        if (mc.options.hideGui || !TimeController.isTimeStopped()) {
            return;
        }
        
        // Get screen dimensions
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        
        // Position: top-center of screen
        int x = (screenWidth - ICON_SIZE) / 2;
        int y = PADDING;
        
        // Render the clock icon with slight transparency
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        
        // Draw clock icon
        guiGraphics.blit(CLOCK_ICON, x, y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        
        RenderSystem.disableBlend();
    }
}

