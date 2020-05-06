package com.kiprogram.kitimetable.util;

public class KiTime {
    public static final String TIME_DELIMITER = ":";
    private int hourOfDay;
    private int minute;

    /**
     * コンストラクタ
     * @param time [時間]:[分]の組み合わせ
     */
    public KiTime(CharSequence time) {
        String[] splitTime = time.toString().split(TIME_DELIMITER);
        this.hourOfDay = Integer.parseInt(splitTime[0]);
        this.minute = Integer.parseInt(splitTime[1]);
    }

    /**
     * コンストラクタ
     * @param hourOfDay 時間
     * @param minute 分
     */
    public KiTime(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    /**
     * [時間]:[分]の形式
     * @return [時間]:[分]
     */
    public String toString() {
        return String.format("%02d", hourOfDay) + TIME_DELIMITER + String.format("%02d", minute);
    }

    /**
     * [時間]を取得します。
     * @return [時間]
     */
    public int getHourOfDay() {
        return hourOfDay;
    }

    /**
     * [時間]を取得します。
     * @return [時間]
     */
    public String getHourOfDayStr() {
        return String.format("%02d", hourOfDay);
    }

    /**
     * [分]を取得します。
     * @return [分]
     */
    public int getMinute() {
        return minute;
    }

    /**
     * [分]を取得します。
     * @return [分]
     */
    public String getMinuteStr() {
        return String.format("%02d", minute);
    }

    /**
     * 時間を比較します。
     * @param time 比較される時間
     * @return 自分が早い場合はtrue 同じ場合はfalse
     */
    public boolean compare(KiTime time) {
        int hourOfDay = time.getHourOfDay();
        if (this.hourOfDay > hourOfDay) {
            return false;
        }
        if (this.hourOfDay < hourOfDay) {
            return true;
        }

        if (minute > time.getMinute()) {
            return false;
        }

        return true;
    }
}
