package com.archer2o2x.chatcalc.rewrite;

public enum TokenType {
    FUNCTION,           // sin()
    VARIABLE,           // x
    LITERAL,            // 5
    LEFT_BRACKET,       // (
    RIGHT_BRACKET,      // )
    COMMA,              // ,
    OPERAND,            // +

    RESULT,             // ===
    ERROR,              // !!!
}
