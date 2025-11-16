package com.book.together.gatherings.entity;

import com.book.together.gatherings.exception.GatheringErrorCode;
import com.book.together.gatherings.exception.GatheringException;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum GatheringLocation {
    KONKUK_UNIVERSITY_STATION("건대입구"),
    EULJIRO_3GA("을지로3가"),
    SILLIM("신림"),
    HONGIK_UNIVERSITY_STATION("홍대입구");

    private final String name;

    public static GatheringLocation from(String value) {
        return Arrays.stream(values())
                .filter(loc -> loc.name.equals(value))
                .findFirst()
                .orElseThrow(() -> new GatheringException(
                        GatheringErrorCode.INVALID_LOCATION,
                        value
                ));
    }
}
