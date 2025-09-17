package com.archer2o2x.chatcalc.rewrite;

import java.util.Objects;

public record CalcToken(TokenType type, String value) {

    public int getPrec() {
        return switch (value) {
            case "+", "-" -> 1;
            case "*", "/", "%" -> 2;
            case "^" -> 3;
            default -> 0;
        };
    }

    public boolean getLeftAssoc() {
        return !(Objects.equals(value, "^"));
    }



}
