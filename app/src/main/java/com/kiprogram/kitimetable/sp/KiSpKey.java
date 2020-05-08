package com.kiprogram.kitimetable.sp;

public enum KiSpKey {
    // 毎日の各コマ時間設定
    FIRST_START_TIME("first_start_time"),
    FIRST_END_TIME("first_end_time"),
    SECOND_START_TIME("second_start_time"),
    SECOND_END_TIME("second_end_time"),
    THIRD_START_TIME("third_start_time"),
    THIRD_END_TIME("third_end_time"),
    FOURTH_START_TIME("fourth_start_time"),
    FOURTH_END_TIME("fourth_end_time"),

    // 各コマの教科設定
    MON_FIRST_SUBJECT_ID("mon_first_subject_id"),
    MON_SECOND_SUBJECT_ID("mon_second_subject_id"),
    MON_THIRD_SUBJECT_ID("mon_third_subject_id"),
    MON_FOURTH_SUBJECT_ID("mon_fourth_subject_id"),
    TUE_FIRST_SUBJECT_ID("tue_first_subject_id"),
    TUE_SECOND_SUBJECT_ID("tue_second_subject_id"),
    TUE_THIRD_SUBJECT_ID("tue_third_subject_id"),
    TUE_FOURTH_SUBJECT_ID("tue_fourth_subject_id"),
    WED_FIRST_SUBJECT_ID("wed_first_subject_id"),
    WED_SECOND_SUBJECT_ID("wed_second_subject_id"),
    WED_THIRD_SUBJECT_ID("wed_third_subject_id"),
    WED_FOURTH_SUBJECT_ID("wed_fourth_subject_id"),
    THU_FIRST_SUBJECT_ID("thu_first_subject_id"),
    THU_SECOND_SUBJECT_ID("thu_second_subject_id"),
    THU_THIRD_SUBJECT_ID("thu_third_subject_id"),
    THU_FOURTH_SUBJECT_ID("thu_fourth_subject_id"),
    FRI_FIRST_SUBJECT_ID("fri_first_subject_id"),
    FRI_SECOND_SUBJECT_ID("fri_second_subject_id"),
    FRI_THIRD_SUBJECT_ID("fri_third_subject_id"),
    FRI_FOURTH_SUBJECT_ID("fri_fourth_subject_id");

    public final String text;
    KiSpKey(String text) {
        this.text = text;
    }
}
