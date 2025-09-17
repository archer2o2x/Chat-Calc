package com.archer2o2x.chatcalc.rewrite;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.*;

public class CalcProcessor {

    // This class describes an environment for CalcFunctions to execute inside.
    // It handles the management of variables and functions, both static and dynamic.

    private static final String ARGMAP = "abcdefghijklmnopqrstuvwxyz"; // Allows up to 26 variables, if that's needed for some reason.

    private HashMap<String, CalcFunction> dynamicFunctions;
    private HashMap<String, Double> dynamicVariables;

    public CalcProcessor(HashMap<String, Double> variables, HashMap<String, CalcFunction> functions) {
        this.dynamicVariables = variables;
        this.dynamicFunctions = functions;
    }

    public CalcToken execute(CalcFunction expression) {
        return execute(expression, dynamicVariables, dynamicFunctions);
    }

    public HashMap<String, Double> getVariables() {
        return dynamicVariables;
    }

    public HashMap<String, CalcFunction> getFunctions() {
        return dynamicFunctions;
    }

    public void setVariables(HashMap<String, Double> variables) {
        dynamicVariables = variables;
    }

    public void setFunctions(HashMap<String, CalcFunction> functions) {
        dynamicFunctions = functions;
    }

    public static CalcToken execute(CalcFunction expression, HashMap<String, Double> variables, HashMap<String, CalcFunction> functions) {
        CalcToken[] instructions = getPolishNotation(expression.getTokens());
        // The following code line is extremely helpful for debugging, please keep.
        //Minecraft.getInstance().player.sendSystemMessage(Component.literal(expression.condense(" ", instructions)).withStyle(ChatFormatting.DARK_RED));
        return process(instructions, variables, functions);
    }

    private static CalcToken[] getPolishNotation(CalcToken[] input) {

        Stack<CalcToken> operators = new Stack<>();
        Queue<CalcToken> output = new ArrayDeque<>();

        for (int i = 0; i < input.length; i++) {

            CalcToken t = input[i];
            if (Objects.equals(t.type(), TokenType.LITERAL) || Objects.equals(t.type(), TokenType.VARIABLE)) {
                output.add(t);
            }
            else if (Objects.equals(t.type(), TokenType.FUNCTION)) {
                operators.push(t);
            }
            else if (Objects.equals(t.type(), TokenType.COMMA)) {
                output.add(new CalcToken(TokenType.COMMA, ""));
                while (!operators.isEmpty() && !Objects.equals(operators.peek().type(), TokenType.LEFT_BRACKET)) {
                    output.add(operators.pop());
                }
            }
            else if (Objects.equals(t.type(), TokenType.OPERAND)) {
                while (!operators.isEmpty() &&
                        Objects.equals(operators.peek().type(), TokenType.OPERAND) &&
                        (t.getLeftAssoc() && t.getPrec() <= operators.peek().getPrec() ||
                                !t.getLeftAssoc() && t.getPrec() < operators.peek().getPrec())) {
                    output.add(operators.pop());
                }
                operators.push(t);
            }
            else if (Objects.equals(t.type(), TokenType.LEFT_BRACKET)) {
                operators.push(t);
            }
            else if (Objects.equals(t.type(), TokenType.RIGHT_BRACKET)) {
                while (!operators.isEmpty() && !Objects.equals(operators.peek().type(), TokenType.LEFT_BRACKET)) {
                    output.add(operators.pop());
                }
                operators.pop();
                if (!operators.isEmpty() && Objects.equals(operators.peek().type(), TokenType.FUNCTION)) {
                    output.add(operators.pop());
                }
            }
        }

        for (int i = operators.size() - 1; i >= 0; i--) {
            output.add(operators.get(i));
        }
        return output.toArray(new CalcToken[0]);

    }

    private static CalcToken process(CalcToken[] input, HashMap<String, Double> variables, HashMap<String, CalcFunction> functions) {

        Stack<Double> operators = new Stack<>();
        int commaCount = 0;

        for (int i = 0; i < input.length; i++) {

            var token = input[i];

            switch (token.type()) {
                case LITERAL:
                    operators.push(Double.valueOf(token.value()));
                    break;
                case VARIABLE:
                    if (!Double.isNaN(StaticVariables.getValue(token.value()))) {
                        operators.push(StaticVariables.getValue(token.value()));
                    } else if (variables.containsKey(token.value())) {
                        operators.push(variables.get(token.value()));
                    } else if (token.value().length() == 1 && ARGMAP.contains(token.value())) { // If a function tries to use an argument it doesn't have, default to 0.
                        operators.push(0d);
                    } else {
                        return new CalcToken(TokenType.ERROR, token.value() + " variable does not exist.");
                    }
                    break;
                case COMMA:
                    commaCount++;
                    break;
                case FUNCTION:
                    if (StaticFunctions.getValue(token.value()) != null) {
                        operators.push(Objects.requireNonNull(StaticFunctions.getValue(token.value())).apply(operators.pop()));
                    } else if (functions.containsKey(token.value())) {
                        if (operators.isEmpty()) return new CalcToken(TokenType.ERROR, token.value() + " - functions needs at least one argument.");
                        if (commaCount > 25) return new CalcToken(TokenType.ERROR, token.value() + " - functions can only use up to 26 arguments.");
                        Double[] previous = new Double[commaCount + 1];
                        for (int j = commaCount; j >= 0; j--) {
                            previous[j] = variables.put(String.valueOf(ARGMAP.charAt(j)), operators.pop());
                        }
                        CalcToken result = execute(functions.get(token.value()), variables, functions);
                        for (int j = commaCount; j >= 0; j--) {
                            variables.put(String.valueOf(ARGMAP.charAt(j)), previous[j]);
                        }
                        commaCount = 0;
                        if (result.type() == TokenType.ERROR) { return result; }
                        operators.push(Double.valueOf(result.value()));
                    } else {
                        return new CalcToken(TokenType.ERROR, token.value() + " function does not exist.");
                    }
                    break;
                case OPERAND:
                    if (operators.size() < 2) {
                        return new CalcToken(TokenType.ERROR, "not enough operators left. " + token.value());
                    }
                    var operand = token.value();
                    double b = operators.pop();
                    double a = operators.pop();
                    switch (operand) {
                        case "+":
                            operators.push(a + b);
                            break;
                        case "-":
                            operators.push(a - b);
                            break;
                        case "*":
                            operators.push(a * b);
                            break;
                        case "/":
                            operators.push(a / b);
                            break;
                        case "^":
                            operators.push(Math.pow(a, b));
                            break;
                        case "%":
                            operators.push(a % b);
                            break;
                    }
            }

        }

        return new CalcToken(TokenType.RESULT, String.valueOf(operators.pop()));

    }

}
