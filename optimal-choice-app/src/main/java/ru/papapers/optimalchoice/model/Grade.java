package ru.papapers.optimalchoice.model;

import lombok.Getter;

@Getter
public enum Grade {
    EQUAL(1, "Равный вклад двух критериев в цель"),
    MODERATE_SUPERIORITY(3, "Лёгкое превосходство одномого критерия над другим"),
    ESSENTIAL_SUPERIORITY(5, "Сильное превосходство одномого критерия над другим"),
    SIGNIFICANT_SUPERIORITY(7, "Настолько сильное превосходство, что оно становится практически значительным"),
    HIGH_SUPERIORITY(9, "Очевидность превосходства одного критерия над другим подтверждается наиболее сильно")
    ;

    private final int value;
    private final String description;

    Grade(int value, String description) {
        this.value = value;
        this.description = description;
    }

}
