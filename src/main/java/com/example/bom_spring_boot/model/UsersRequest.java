package com.example.bom_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
public class UsersRequest {
    @NonNull
    @JsonProperty(value = "full_name")
    private String fullName;
    @NonNull
    private String nickname;
    @NonNull
    private int age;

    public UsersRequest(){}
}
