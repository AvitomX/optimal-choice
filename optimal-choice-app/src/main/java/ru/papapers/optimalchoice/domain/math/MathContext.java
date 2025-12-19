package ru.papapers.optimalchoice.domain.math;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.math.RoundingMode.UP;

public class MathContext {
    public static final double ONE = 1.0;
    public static final int SCALE = 6;

    private final int objectCount;
    private final List<BigDecimal> row = new ArrayList<>();
    private final List<BigDecimal> column = new ArrayList<>();
    private BigDecimal nonNormalizeValue;
    private BigDecimal normalizeValue;

    public MathContext(int objectCount) {
        this.objectCount = objectCount;
    }

    public boolean addRowValue(BigDecimal value) {
        return row.add(value);
    }

    public boolean addColumnValue(BigDecimal value) {
        return column.add(value);
    }

    public BigDecimal getNonNormalizeValue() {
        if (nonNormalizeValue == null) {
            BigDecimal multiplication = row.stream().reduce(BigDecimal::multiply).orElseThrow(RuntimeException::new);
            this.nonNormalizeValue = BigDecimal.valueOf(Math.pow(multiplication.doubleValue(), ONE / objectCount));
        }

        return this.nonNormalizeValue;
    }

    public BigDecimal computeNormalizeValue(BigDecimal nonNormalizeVectorSum) {
        if (normalizeValue == null) {
            this.normalizeValue = getNonNormalizeValue().divide(nonNormalizeVectorSum, SCALE, UP);
        }

        return this.normalizeValue;
    }

    public BigDecimal getNormalizeValue() {
        if (this.normalizeValue == null) {
            throw new NullPointerException("NormalizeValue is NULL");
        }

        return this.normalizeValue;
    }

    public BigDecimal getColumnValueSum() {
        return column.stream()
                .reduce(BigDecimal::add)
                .orElseThrow(ArithmeticException::new)
                .add(BigDecimal.ONE);
    }
}
