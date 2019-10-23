package com.bank;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateTime {
    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(2);
    private static final ZoneId ZONE_ID = ZoneId.ofOffset("UTC", ZONE_OFFSET);

    private DateTime() {
    }

    private static ZonedDateTime mockNow;

    public static LocalDateTime now() {
        return zoneNow().toLocalDateTime();
    }

    public static void setMockNow(LocalDateTime localDateTime) {
        DateTime.mockNow = localDateTime.atZone(ZONE_ID);
    }

    private static ZonedDateTime zoneNow() {
        return mockNow == null ? ZonedDateTime.now(ZONE_ID) : mockNow;
    }

}
