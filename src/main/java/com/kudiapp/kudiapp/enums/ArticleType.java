package com.kudiapp.kudiapp.enums;

import lombok.Getter;

@Getter
public enum ArticleType {
    REVIEW_ARTICLE("Review Article"),
    CASE_STUDY("Case Study"),
    SHORT_COMMUNICATION("Short Communication"),
    EDITORIAL("Editorial"),
    OTHERS("Others");

    private final String displayName;

    ArticleType(String displayName) {
        this.displayName = displayName;
    }
}
