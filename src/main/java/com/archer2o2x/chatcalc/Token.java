package com.archer2o2x.chatcalc;

import java.util.Dictionary;
import java.util.Objects;

public record Token(String type, String value) {

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
