package com.example.bom_spring_boot.enums;

import lombok.Getter;

public enum RetryResponseEnum {

    TIMEOUT_MESSAGE("TIMEOUT_MESSAGE", "Inactivity Timeout"),
    INVALID_LENGTH("INVALID_LENGTH", "Invalid length");

    @Getter
    private String type;

    @Getter
    private String description;

    RetryResponseEnum(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public static RetryResponseEnum from(String b) throws Exception {
        for (RetryResponseEnum retryResponseEnum: values()) {
            if (retryResponseEnum.description.equalsIgnoreCase(b)) {
                return retryResponseEnum;
            }
        }

        throw new Exception("Code not found");
    }
}
