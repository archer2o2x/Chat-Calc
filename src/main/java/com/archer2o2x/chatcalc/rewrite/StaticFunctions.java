package com.archer2o2x.chatcalc.rewrite;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum StaticFunctions {

    sin(Math::sin),
    cos(Math::cos),
    tan(Math::tan),

    asin(Math::asin),
    acos(Math::acos),
    atan(Math::atan),

    deg(Math::toDegrees),
    rad(Math::toRadians),

    logn(Math::log),
    logx(Math::log10),

    abs(Math::abs),
    sqrt(Math::sqrt),

    floor(Math::floor),
    ceil(Math::ceil),
    round((inp) -> (double) Math.round(inp))
    ;

    final Function<Double, Double> value;

    StaticFunctions(Function<Double, Double> value) {
        this.value = value;
    }

    public static Function<Double, Double> getValue(String name) {
        try {
            return valueOf(name).value;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
