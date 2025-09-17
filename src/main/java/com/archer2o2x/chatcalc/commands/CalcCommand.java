package com.archer2o2x.chatcalc.commands;

import com.archer2o2x.chatcalc.Config;
import com.archer2o2x.chatcalc.rewrite.CalcFunction;
import com.archer2o2x.chatcalc.rewrite.CalcManager;
import com.archer2o2x.chatcalc.rewrite.CalcToken;
import com.archer2o2x.chatcalc.rewrite.TokenType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.Objects;

public class CalcCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(Commands.literal("calc")
                .then(Commands.argument("expression", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            CalcFunction func = new CalcFunction(StringArgumentType.getString(ctx, "expression"));
                            CalcToken result = CalcManager.getInstance().execute(func);
                            CalcManager.sendMsg(Minecraft.getInstance().player, CalcManager.composeResult(func, result), false, result.type() == TokenType.ERROR);
                            if (result.type() == TokenType.RESULT) Config.addVariable("ans", result.value());
                            return result.type() == TokenType.RESULT ? 1 : 0;
                        })
                )
                .then(Commands.literal("func")
                        .then(Commands.literal("list")
                                .executes(ctx -> {
                                    Config.refreshDynamicContent();
                                    CalcManager.sendMsg(Minecraft.getInstance().player, "Available Functions", true, false);
                                    Config.dynamicFunctions.forEach((key, value) -> {
                                        CalcManager.sendMsg(Minecraft.getInstance().player, "- " + key + " ( " + value.condense(" ") + ")");
                                    });
                                    return 1;
                                })
                        )
                        .then(Commands.literal("get")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            String value = Config.getFunction(name);
                                            if (value == null) {
                                                CalcManager.sendMsg(Minecraft.getInstance().player, "Function '" + name + "' does not exist.", false, true);
                                            } else {
                                                CalcManager.sendMsg(Minecraft.getInstance().player, "Got Function => " + name + "( " + value + " )");
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .then(Commands.argument("expression", StringArgumentType.greedyString())
                                                .executes(ctx -> {
                                                    String name = StringArgumentType.getString(ctx, "name");
                                                    String func = StringArgumentType.getString(ctx, "expression");
                                                    Config.addFunction(name, func);
                                                    CalcManager.sendMsg(Minecraft.getInstance().player, "Set Function <= " + name + "( " + func + " )");
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            boolean success = Config.removeFunction(name);
                                            if (success) {
                                                CalcManager.sendMsg(Minecraft.getInstance().player, "Removed Function -- " + name);
                                            } else {
                                                CalcManager.sendMsg(Minecraft.getInstance().player, "Function '" + name + "' does not exist.", false, true);
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("var")
                        .then(Commands.literal("list")
                                .executes(ctx -> {
                                    Config.refreshDynamicContent();
                                    CalcManager.sendMsg(Minecraft.getInstance().player, "Available Variables", true, false);
                                    Config.dynamicVariables.forEach((key, value) -> {
                                        CalcManager.sendMsg(Minecraft.getInstance().player, "- " + key + " ( " + value + " )");
                                    });
                                    return 1;
                                })
                        )
                        .then(Commands.literal("get")
                                .then(Commands.argument("name", StringArgumentType.string())
                                    .executes(ctx -> {
                                        String name = StringArgumentType.getString(ctx, "name");
                                        String value = Config.getVariable(name);
                                        if (value == null) {
                                            CalcManager.sendMsg(Minecraft.getInstance().player, "Variable '" + name + "' does not exist.", false, true);
                                        } else {
                                            CalcManager.sendMsg(Minecraft.getInstance().player, "Got Variable => " + name + " [ " + value + " ]");
                                        }
                                        return 1;
                                    })
                                )
                        )
                        .then(Commands.literal("set")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .then(Commands.argument("expression", StringArgumentType.greedyString())
                                                .executes(ctx -> {
                                                    String name = StringArgumentType.getString(ctx, "name");
                                                    CalcFunction func = new CalcFunction(StringArgumentType.getString(ctx, "expression"));
                                                    CalcToken result = CalcManager.getInstance().execute(func);
                                                    if (result.type() != TokenType.RESULT) {
                                                        CalcManager.sendMsg(Minecraft.getInstance().player, CalcManager.composeResult(func, result), false, true);
                                                        return 0;
                                                    }
                                                    Config.addVariable(name, result.value());
                                                    CalcManager.sendMsg(Minecraft.getInstance().player, "Set Variable <= " + name + " [ " + result.value() + " ]");
                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("remove")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            boolean success = Config.removeVariable(name);
                                            if (success) {
                                                CalcManager.sendMsg(Minecraft.getInstance().player, "Removed Variable -- " + name);
                                            } else {
                                                CalcManager.sendMsg(Minecraft.getInstance().player, "Variable '" + name + "' does not exist.", false, true);
                                            }
                                            return 1;
                                        })
                                )
                        )
                )
                .then(Commands.literal("help")
                        .executes(ctx -> {
                            // Add some explanation
                            return 1;
                        })
                )
        );

    }

}
