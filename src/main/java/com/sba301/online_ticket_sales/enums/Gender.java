package com.sba301.online_ticket_sales.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("Name"),
    FEMALE("NỮ");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }
}
