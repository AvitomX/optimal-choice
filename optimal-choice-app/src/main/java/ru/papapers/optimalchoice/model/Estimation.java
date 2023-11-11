package ru.papapers.optimalchoice.model;

import java.math.BigDecimal;

public enum Estimation {

    ONE(BigDecimal.valueOf(1), BigDecimal.valueOf(1)),
    TWO(BigDecimal.valueOf(2), BigDecimal.valueOf(1.0 / 2)),
    THREE(BigDecimal.valueOf(3), BigDecimal.valueOf(1.0 / 3)),
    FOUR(BigDecimal.valueOf(4), BigDecimal.valueOf(1.0 / 4)),
    FIVE(BigDecimal.valueOf(5), BigDecimal.valueOf(1.0 / 5)),
    SIX(BigDecimal.valueOf(6), BigDecimal.valueOf(1.0 / 6)),
    SEVEN(BigDecimal.valueOf(7), BigDecimal.valueOf(1.0 / 7)),
    EIGHT(BigDecimal.valueOf(8), BigDecimal.valueOf(1.0 / 8)),
    NINE(BigDecimal.valueOf(9), BigDecimal.valueOf(1.0 / 9));

    private final BigDecimal directValue;
    private final BigDecimal reverseValue;

    Estimation(BigDecimal directValue, BigDecimal reverseValue) {
        this.directValue = directValue;
        this.reverseValue = reverseValue;
    }


    public BigDecimal getDirectValue() {
        return directValue;
    }

    public BigDecimal getReverseValue() {
        return reverseValue;
    }
}
