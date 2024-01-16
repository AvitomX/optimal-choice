package ru.papapers.optimalchoice.domain.math;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum MatrixMiddleConsistency {
    ONE(1, BigDecimal.valueOf(0.0000001)),
    TWO(2, BigDecimal.valueOf(0.0000001)),
    TREE(3, BigDecimal.valueOf(0.58)),
    FOUR(4, BigDecimal.valueOf(0.90)),
    FIVE(5, BigDecimal.valueOf(1.12)),
    SIX(6, BigDecimal.valueOf(1.24)),
    SEVEN(7, BigDecimal.valueOf(1.32)),
    EIGHT(8, BigDecimal.valueOf(1.41)),
    NINE(9, BigDecimal.valueOf(1.45)),
    TEN(10, BigDecimal.valueOf(1.49)),
    ELEVEN(11, BigDecimal.valueOf(1.51)),
    TWELVE(12, BigDecimal.valueOf(1.53)),
    THIRTEEN(13, BigDecimal.valueOf(1.56)),
    FOURTEEN(14, BigDecimal.valueOf(1.57)),
    FIFTEEN(15, BigDecimal.valueOf(1.59));

    private final int matrixSize;
    private final BigDecimal consistency;

    MatrixMiddleConsistency(int matrixSize, BigDecimal consistency) {
        this.matrixSize = matrixSize;
        this.consistency = consistency;
    }

    public static MatrixMiddleConsistency findByMatrixSize(int matrixSize) {
        for(MatrixMiddleConsistency value : values()) {
            if (value.matrixSize == matrixSize) {
                return value;
            }
        }

        return null;
    }

}
