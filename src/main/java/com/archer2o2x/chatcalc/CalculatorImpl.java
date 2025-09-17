package com.archer2o2x.chatcalc;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.time.chrono.MinguoEra;
import java.util.*;
import java.util.function.Function;

public class CalculatorImpl {

    public static String collectBuffer(Queue<String> buffer) {
        StringBuilder result = new StringBuilder();
        String[] input = buffer.toArray(new String[0]);
        for (int i = 0; i < buffer.size(); i++) {
            result.append(input[i]);
        }
        return result.toString();
    }

    public static Token[] tokenize(String exp) {
        Queue<Token> result = new ArrayDeque<>();
        exp = exp.replaceAll("\\s", "");

        Queue<String> letterBuffer = new ArrayDeque<>();
        Queue<String> numberBuffer = new ArrayDeque<>();
        for (int i = 0; i < exp.length(); i++) {

            String chr = String.valueOf(exp.charAt(i));

            if (chr.matches("[\\d.]")) { // Any Digit
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new Token("VARIABLE", chrLiteral));
                    result.add(new Token("OPERAND", "*"));
                }
                numberBuffer.add(chr);
            }
            else if (chr.matches("\\w")) { // Any Letter
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new Token("LITERAL", numLiteral));
                    result.add(new Token("OPERAND", "*"));
                }
                letterBuffer.add(chr);
            }
            else if (chr.matches("[/*^%+-]")) { // Any Operand ( +, -, *, /, ^, % )
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new Token("LITERAL", numLiteral));
                }
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new Token("VARIABLE", chrLiteral));
                }
                result.add(new Token("OPERAND", chr));
            }
            else if (chr.matches("\\(")) { // Left Parenthesis
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new Token("FUNCTION", chrLiteral));
                }
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new Token("LITERAL", numLiteral));
                    result.add(new Token("OPERAND", "*"));
                }
                result.add(new Token("LEFT_BRACKET", ""));
            }
            else if (chr.matches("\\)")) { // Right Parenthesis
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new Token("VARIABLE", chrLiteral));
                }
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new Token("LITERAL", numLiteral));
                }
                result.add(new Token("RIGHT_BRACKET", ""));
            }
            else if (chr.matches(",")) { // Comma
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new Token("VARIABLE", chrLiteral));
                }
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new Token("LITERAL", numLiteral));
                }
                result.add(new Token("COMMA", ""));
            }
        }

        if (!letterBuffer.isEmpty()) {
            String chrLiteral = collectBuffer(letterBuffer);
            letterBuffer.clear();
            result.add(new Token("VARIABLE", chrLiteral));
        }
        if (!numberBuffer.isEmpty()) {
            String numLiteral = collectBuffer(numberBuffer);
            numberBuffer.clear();
            result.add(new Token("LITERAL", numLiteral));
        }

        return result.toArray(new Token[0]);
    }

    public static Token[] getRPN(Token[] input) {
        Stack<Token> operators = new Stack<>();
        Queue<Token> output = new ArrayDeque<>();

        for (int i = 0; i < input.length; i++) {

            Token t = input[i];
            if (Objects.equals(t.type(), "LITERAL") || Objects.equals(t.type(), "VARIABLE")) {
                output.add(t);
            }
            else if (Objects.equals(t.type(), "FUNCTION")) {
                operators.push(t);
            }
            else if (Objects.equals(t.type(), "COMMA")) {
                while (!operators.isEmpty() && !Objects.equals(operators.peek().type(), "LEFT_BRACKET")) {
                    output.add(operators.pop());
                }
            }
            else if (Objects.equals(t.type(), "OPERAND")) {
                while (!operators.isEmpty() &&
                        Objects.equals(operators.peek().type(), "OPERAND") &&
                        (t.getLeftAssoc() && t.getPrec() <= operators.peek().getPrec() ||
                        !t.getLeftAssoc() && t.getPrec() < operators.peek().getPrec())) {
                    output.add(operators.pop());
                }
                operators.push(t);
            }
            else if (Objects.equals(t.type(), "LEFT_BRACKET")) {
                operators.push(t);
            }
            else if (Objects.equals(t.type(), "RIGHT_BRACKET")) {
                while (!operators.isEmpty() && !Objects.equals(operators.peek().type(), "LEFT_BRACKET")) {
                    output.add(operators.pop());
                }
                operators.pop();
                if (!operators.isEmpty() && Objects.equals(operators.peek().type(), "FUNCTION")) {
                    output.add(operators.pop());
                }
            }
        }

        for (int i = operators.size() - 1; i >= 0; i--) {
            output.add(operators.get(i));
        }
        return output.toArray(new Token[0]);
    }

    public static String condense(Token[] tokens) {
        StringBuilder result = new StringBuilder();
        for (Token token : tokens) {
            switch (token.type()) {
                case "LITERAL":
                case "VARIABLE":
                case "OPERAND":
                case "FUNCTION":
                    result.append(token.value());
                    result.append(" ");
                    break;
                case "LEFT_BRACKET":
                    result.append("( ");
                    break;
                case "RIGHT_BRACKET":
                    result.append(") ");
                    break;
                case "COMMA":
                    result.append(", ");
                    break;
            }
        }
        return result.toString();
    }

    public static Token process(Token[] tokens, HashMap<String, Float> variables, HashMap<String, Function<Float, Float>> functions) {

        Stack<Float> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {

            var token = tokens[i];

            switch (token.type()) {
                case "LITERAL":
                    operators.push(Float.valueOf(token.value()));
                    break;
                case "VARIABLE":
                    if (!variables.containsKey(token.value())) {
                        return new Token("ERROR", token.value());
                    }
                    operators.push(variables.get(token.value()));
                    break;
                case "FUNCTION":
                    if (!functions.containsKey(token.value())) {
                        return new Token("ERROR", token.value());
                    }
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal(operators.toString()));
                    operators.push(functions.get(token.value()).apply(operators.pop()));
                    break;
                case "OPERAND":
                    Minecraft.getInstance().player.sendSystemMessage(Component.literal(operators.toString()));
                    var operand = token.value();
                    float b = operators.pop();
                    float a = operators.pop();
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
                            operators.push((float) Math.pow(a, b));
                            break;
                        case "%":
                            operators.push(a % b);
                            break;
                    }
            }

        }

        return new Token("RESULT", String.valueOf(operators.pop()));



    }

}
