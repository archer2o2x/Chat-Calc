package com.archer2o2x.chatcalc;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ChatCalc.MODID)
public class ModEvents {

    public static KeyMapping OPEN_CALC = new KeyMapping(
            "key.chatcalc.open",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_EQUAL,
            "key.categories.misc"
    );

    @SubscribeEvent
    public static void onRegisterKeybindsEvent(RegisterKeyMappingsEvent event) {

        event.register(OPEN_CALC);

    }

}
