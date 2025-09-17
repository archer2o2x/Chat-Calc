package com.archer2o2x.chatcalc.rewrite;

import com.archer2o2x.chatcalc.Config;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Objects;

public final class CalcManager {

    public static void sendMsg(Player player, String message) {
        sendMsg(player, message, false, false);
    }
    public static void sendMsg(Player player, String message, boolean bold, boolean error) {
        Objects.requireNonNull(player).sendSystemMessage(Component.literal(message).withStyle(bold ? ChatFormatting.BOLD : ChatFormatting.RESET).withStyle(error ? ChatFormatting.DARK_GRAY : ChatFormatting.DARK_AQUA));
    }

    public static String truncate(String input, String pattern) {
        if (input == null || !input.contains(".")) {
            return input;
        }
        int decimalIndex = input.indexOf('.');
        String afterDecimal = input.substring(decimalIndex + 1);
        int patternIndex = afterDecimal.indexOf(pattern);
        if (patternIndex + 1 == decimalIndex) {
            return input.substring(0, decimalIndex);
        }
        if (patternIndex >= 0) {
            return input.substring(0, patternIndex);
        } else {
            return input;
        }
    }

    public static String composeResult(CalcFunction func, CalcToken token) {
        if (token.type() == TokenType.ERROR) {
            return "ERROR: " + token.value() + " (" + func.condense(" ") + "= ???)";
        }
        String result = token.value();
        if (result.endsWith(".0")) {
            result = result.substring(0, result.length() - 2);
        }
        result = truncate(result, "000");
        return func.condense(" ") + "= " + result;
    }

    private static CalcManager INSTANCE;

    public static CalcManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CalcManager();
        }
        return INSTANCE;
    }

    private final CalcProcessor processor;

    private CalcManager() {

        Config.refreshDynamicContent();
        processor = new CalcProcessor(Config.dynamicVariables, Config.dynamicFunctions);
    }

    public CalcProcessor getProcessor() {
        return processor;
    }

    public CalcToken execute(CalcFunction func) {
        Config.refreshDynamicContent();
        processor.setVariables(Config.dynamicVariables);
        processor.setFunctions(Config.dynamicFunctions);
        return processor.execute(func);
    }



}
