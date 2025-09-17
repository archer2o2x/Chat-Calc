package com.archer2o2x.chatcalc;

import com.archer2o2x.chatcalc.rewrite.CalcFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Config
{
    public static final ForgeConfigSpec SPEC;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        SPEC = configBuilder.build();
    }

    // VARIABLE DECLARATIONS GO HERE
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> variables;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> functions;

    public static HashMap<String, Double> dynamicVariables;
    public static HashMap<String, CalcFunction> dynamicFunctions;

    private static void setupConfig(ForgeConfigSpec.Builder builder) {

        variables = builder
                .comment("These are the defined variables available for your use.")
                .comment("Avoid single character variables, as these can be temporarily overridden by command arguments inside functions.")
                .defineListAllowEmpty("variables", List.of("stack = 64", "pearl = 16"), entry -> entry instanceof String);

        functions = builder
                .comment("These are the defined functions available for your use.")
                .comment("The function can receive arguments, starting with 'a' then 'b', all the way through to 'z'.")
                .comment("Please note that this argument ordering cannot be changed, and can interfere with variables of the same name.")
                .defineListAllowEmpty("functions", List.of("stacks = a * stack", "pearls = a * pearl", "sum = a + b + c + d + e"), entry -> entry instanceof String);

    }

    public static void refreshDynamicContent() {

        dynamicVariables = new HashMap<>();
        for (String row: variables.get()) {
            if (!row.contains("=")) continue;
            String[] rowData = row.split("=", 2);
            if (!rowData[1].trim().matches("^[\\d.]+$")) continue;
            dynamicVariables.put(rowData[0].trim(), Double.valueOf(rowData[1].trim()));
        }

        dynamicFunctions = new HashMap<>();
        for (String row: functions.get()) {
            if (!row.contains("=")) continue;
            String[] rowData = row.split("=", 2);
            try {
                dynamicFunctions.put(rowData[0].trim(), new CalcFunction(rowData[1].trim()));
            } catch (Exception ignored) {}
        }

    }

    public static void addFunction(String name, String expr) {
        String entry = name + " = " + expr;
        List<String> copy = new ArrayList<>(functions.get());
        boolean modified = false;
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).split("=")[0].trim().equals(name)) {
                copy.set(i, entry);
                modified = true;
            }
        }
        if (!modified) copy.add(entry);
        functions.set(copy);
        functions.save();
    }

    public static void addVariable(String name, String expr) {
        String entry = name + " = " + expr;
        List<String> copy = new ArrayList<>(variables.get());
        boolean modified = false;
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).split("=")[0].trim().equals(name)) {
                copy.set(i, entry);
                modified = true;
            }
        }
        if (!modified) copy.add(entry);
        variables.set(copy);
        variables.save();
    }

    public static boolean removeFunction(String name) {
        List<String> copy = new ArrayList<>(functions.get());
        int remove = -1;
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).split("=")[0].trim().equals(name)) {
                remove = i;
                break;
            }
        }
        if (remove >= 0) copy.remove(remove);
        functions.set(copy);
        functions.save();
        return remove >= 0;
    }

    public static boolean removeVariable(String name) {
        List<String> copy = new ArrayList<>(variables.get());
        int remove = -1;
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).split("=")[0].trim().equals(name)) {
                remove = i;
                break;
            }
        }
        if (remove >= 0) copy.remove(remove);
        variables.set(copy);
        variables.save();
        return remove >= 0;
    }

    public static String getFunction(String name) {
        for (String s : functions.get()) {
            String[] rowData = s.split("=");
            if (rowData[0].trim().equals(name)) {
                return rowData[1].trim();
            }
        }
        return null;
    }

    public static String getVariable(String name) {
        for (String s : variables.get()) {
            String[] rowData = s.split("=");
            if (rowData[0].trim().equals(name)) {
                return rowData[1].trim();
            }
        }
        return null;
    }

}
