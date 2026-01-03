package org.example.exyyyl.projects;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final String KEY_CATEGORY = "key.category." + Projects.MODID;
    public static final String KEY_TIME_STOP = "key." + Projects.MODID + ".time_stop";

    public static KeyMapping timeStopKey;

    public static void init() {
        timeStopKey = new KeyMapping(
                KEY_TIME_STOP,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT, // ~ key
                KEY_CATEGORY
        );
    }
}

