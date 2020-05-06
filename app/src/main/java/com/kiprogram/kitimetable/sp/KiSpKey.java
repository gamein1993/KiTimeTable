package com.kiprogram.kitimetable.sp;

public enum KiSpKey {
    FIRST_START_TIME("first_start_time"),
    FIRST_END_TIME("first_end_time"),
    SECOND_START_TIME("second_start_time"),
    SECOND_END_TIME("second_end_time"),
    THIRD_START_TIME("third_start_time"),
    THIRD_END_TIME("third_end_time"),
    FOURTH_START_TIME("fourth_start_time"),
    FOURTH_END_TIME("fourth_end_time");
    public final String text;
    KiSpKey(String text) {
        this.text = text;
    }
}
