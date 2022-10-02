package com.zerogift.backend.common.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum GiftBoxErrorCode {
    GIFT_BOX_NOT_FOUND("선물함에 존재 하지 않습니다."),
    CODE_NOT_MATCH("코드가 일치 하지 않습니다.");

    private final String description;
}
