package com.sba301.online_ticket_sales.enums;

import lombok.Getter;

@Getter
public enum Country {
    VIET_NAM("Việt Nam"),
    THAI_LAN("Thái Lan"),
    INDONESIA("Indonesia"),
    MY("Mỹ"),
    HAN_QUOC("Hàn Quốc"),
    NHAT_BAN("Nhật Bản"),
    TRUNG_QUOC("Trung Quốc"),
    AN_DO("Ấn Độ"),
    PHAP("Pháp"),
    ANH("Anh"),
    HONG_KONG("Hồng Kông"),
    DAI_LOAN("Đài Loan"),
    MALAYSIA("Malaysia"),
    SINGAPORE("Singapore"),
    UC("Úc");

    private final String displayName;

    Country(String displayName) {
        this.displayName = displayName;
    }
}
