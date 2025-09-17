package com.archer2o2x.chatcalc;

import com.archer2o2x.chatcalc.commands.CalcCommand;
import com.mojang.logging.LogUtils;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ChatCalc.MODID)
public class ChatCalc
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "chatcalc";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public ChatCalc(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.CLIENT, Config.SPEC, "chat-calc.toml");

    }

    // TODO LIST
    // - Test in a server context - Just server should be able to use the command, whilst client only should be only on client.

    // Plan for features
    // '=' key by default opens a chat window with an equals sign that can be used for quick calculation.
    // The /calc command can also be used with no arguments for a similar effect.
    // the /calc command has two main subcommands, being "func" and "var" respectively.
    // The /calc func command is used for controlling functions declared in scope.
    // The /calc var command is used for controlling variables declared in scope.
    // Each of these subcommands have the options "list", "set" and "get"
    // The list subcommand will list all functions with a page system that allows for browsing a large amount of declared functions.
    // The set subcommand will simply set the function name and the expression or value.
    // The get subcommand will show the expression or value of the selected function.

    // If an expression has a static value, the value will be calculated and stored.
    // If the expression features any variable elements, e.g. other variables or function arguments, the expression will be stored normally.

    // =<expr> - Evaluates the expression
    // /calc <expr> - Evaluates the expression
    // /calc func list - Lists all known functions
    // /calc func set <name> <expr> - Sets the function to the expression value.
    // /calc func get <name> - Gets the currently stored expression value for the function.
    // /calc var list - Lists all known variables
    // /calc var set <name> <expr> - Sets the variable to the expression value.
    // /calc var get <name> - Gets the currently stored variable value.
    // /calc score set <score_name> <expr> - Sets the scoreboard value to the expression, rounding when needed.
    // /calc score get <score_name> <var_name> - Sets the variable to the scoreboard value.

    // COMMAND FEEDBACK
    // ## /calc func list ##
    // Available Functions
    // - pearls( a * 16 )
    // - stacks( a * 64 )
    // ## /calc func get ##
    // Got Function > pearls( a * 16 )
    // ## /calc func set ##
    // Set Function < pearls( a * 24 )
    // ## /calc func remove ##
    // Remove Function - pearls
    // Got Variable > stack [ 64 ]
}
