package com.archer2o2x.chatcalc.rewrite;

public enum StaticVariables {

    pi(Math.PI),
    tau(Math.PI * 2),
    euler(Math.E)

    ;

    final double value;

    StaticVariables(double value) {
        this.value = value;
    }

    public static double getValue(String name) {
        try {
            return valueOf(name).value;
        } catch (IllegalArgumentException e) {
            return Float.NaN;
        }
    }
}
