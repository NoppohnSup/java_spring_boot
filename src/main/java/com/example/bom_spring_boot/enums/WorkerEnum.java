package com.example.bom_spring_boot.enums;

import lombok.Getter;

public enum WorkerEnum
{
    CASE_ONE("CASE_ONE", "Case one"),
    CASE_TWO("CASE_TWO", "Case two"),
    CASE_THREE("CASE_THREE", "Case three"),
    RETRY("RETRY", "Retry");

    @Getter
    private String type;

    @Getter
    private String description;

    WorkerEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public static WorkerEnum from(String type) {
        try {
            return WorkerEnum.valueOf(type);
        } catch (Exception e) {
            return WorkerEnum.RETRY;
        }
    }
}
