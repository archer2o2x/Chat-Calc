package com.archer2o2x.chatcalc.rewrite;

import com.archer2o2x.chatcalc.Token;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayDeque;
import java.util.Queue;

public class CalcFunction {

    // This class manages a singular function that can be executed.
    // It can be created from a string input

    private final CalcToken[] tokens;

    public CalcFunction(String input) {
        tokens = tokenise(input);
    }

    public CalcToken[] getTokens() { return tokens; }

    public boolean equals(CalcToken base, TokenType type, String value) {
        return base.type() == type && base.value() == value;
    }

    public String condense(String separator) {
        return condense(separator, tokens);
    }

    public String condense(String separator, CalcToken[] input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length; i++) {
            if (i + 1 < input.length && equals(input[i], TokenType.LITERAL, "-1") && equals(input[i+1], TokenType.OPERAND, "*")) {
                result.append("-");
                i++;
                continue;
            }
            switch (input[i].type()) {
                case LITERAL:
                case VARIABLE:
                case OPERAND:
                case FUNCTION:
                    result.append(input[i].value());
                    break;
                case LEFT_BRACKET:
                    result.append("(");
                    break;
                case RIGHT_BRACKET:
                    result.append(")");
                    break;
                case COMMA:
                    result.append(",");
                    break;
            }
            result.append(separator);
        }
        return result.toString();
    }

    private static CalcToken[] tokenise(String input) {

        input = input.replaceAll("\\s", "");

        ArrayDeque<CalcToken> result = new ArrayDeque<>();
        ArrayDeque<String> letterBuffer = new ArrayDeque<>();
        ArrayDeque<String> numberBuffer = new ArrayDeque<>();

        for (int i = 0; i < input.length(); i++) {

            String chr = String.valueOf(input.charAt(i));

            if (chr.matches("[\\d.]")) { // Any Digit
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new CalcToken(TokenType.VARIABLE, chrLiteral));
                    result.add(new CalcToken(TokenType.OPERAND, "*"));
                }
                numberBuffer.add(chr);
                continue;
            }

            if (chr.matches("\\w")) { // Any Letter
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new CalcToken(TokenType.LITERAL, numLiteral));
                    result.add(new CalcToken(TokenType.OPERAND, "*"));
                }
                letterBuffer.add(chr);
                continue;
            }

            if (chr.matches("-")) { // Unary Negation (Negative Numbers Fix)
                if (i + 1 >= input.length()) { continue; }
                if (numberBuffer.isEmpty() && letterBuffer.isEmpty()) {
                    if (String.valueOf(input.charAt(i + 1)).matches("[\\w.]")) {
                        result.add(new CalcToken(TokenType.LITERAL, "-1"));
                        result.add(new CalcToken(TokenType.OPERAND, "*"));
                        continue;
                    }
                }
            }

            if (chr.matches("[/*^%+-]")) { // Any Operand ( +, -, *, /, ^, % )
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new CalcToken(TokenType.LITERAL, numLiteral));
                }
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new CalcToken(TokenType.VARIABLE, chrLiteral));
                }
                result.add(new CalcToken(TokenType.OPERAND, chr));
                continue;
            }

            if (chr.matches("\\(")) { // Left Parenthesis
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new CalcToken(TokenType.FUNCTION, chrLiteral));
                }
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new CalcToken(TokenType.LITERAL, numLiteral));
                    result.add(new CalcToken(TokenType.OPERAND, "*"));
                }
                result.add(new CalcToken(TokenType.LEFT_BRACKET, ""));
                continue;
            }

            if (chr.matches("\\)")) { // Right Parenthesis
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new CalcToken(TokenType.VARIABLE, chrLiteral));
                }
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new CalcToken(TokenType.LITERAL, numLiteral));
                }
                result.add(new CalcToken(TokenType.RIGHT_BRACKET, ""));
                continue;
            }

            if (chr.matches(",")) { // Comma
                if (!letterBuffer.isEmpty()) {
                    String chrLiteral = collectBuffer(letterBuffer);
                    letterBuffer.clear();
                    result.add(new CalcToken(TokenType.VARIABLE, chrLiteral));
                }
                if (!numberBuffer.isEmpty()) {
                    String numLiteral = collectBuffer(numberBuffer);
                    numberBuffer.clear();
                    result.add(new CalcToken(TokenType.LITERAL, numLiteral));
                }
                result.add(new CalcToken(TokenType.COMMA, ""));
            }
        }

        if (!letterBuffer.isEmpty()) {
            String chrLiteral = collectBuffer(letterBuffer);
            letterBuffer.clear();
            result.add(new CalcToken(TokenType.VARIABLE, chrLiteral));
        }
        if (!numberBuffer.isEmpty()) {
            String numLiteral = collectBuffer(numberBuffer);
            numberBuffer.clear();
            result.add(new CalcToken(TokenType.LITERAL, numLiteral));
        }

        return result.toArray(new CalcToken[0]);
    }

    private static String collectBuffer(Queue<String> buffer) {
        StringBuilder result = new StringBuilder();
        String[] input = buffer.toArray(new String[0]);
        for (int i = 0; i < buffer.size(); i++) {
            result.append(input[i]);
        }
        return result.toString();
    }

}
