package com.archer2o2x.chatcalc;

import com.archer2o2x.chatcalc.rewrite.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ChatCalc.MODID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onClientChatEvent(ClientChatEvent event) {

        if (event.getMessage().startsWith(ModEvents.OPEN_CALC.getKey().getDisplayName().getString())) {

            event.setCanceled(true);
            assert Minecraft.getInstance().player != null;
            String exp = event.getMessage().substring(1);
            CalcFunction func = new CalcFunction(exp);   ;
            CalcToken result = CalcManager.getInstance().execute(func);
            CalcManager.sendMsg(Minecraft.getInstance().player, CalcManager.composeResult(func, result), false, result.type() == TokenType.ERROR);
            if (result.type() == TokenType.RESULT) Config.addVariable("ans", result.value());

        }

    }

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.ClientTickEvent event) {

        if (ModEvents.OPEN_CALC.isDown()) {
            Minecraft.getInstance().setScreen(new ChatScreen(ModEvents.OPEN_CALC.getKey().getDisplayName().getString()));
        }

    }

}
