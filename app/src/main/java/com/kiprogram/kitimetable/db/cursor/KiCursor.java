package com.kiprogram.kitimetable.db.cursor;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteQuery;

public class KiCursor extends SQLiteCursor {
    /**
     * 自作のCursorクラスです。
     *
     * @param driver
     * @param editTable the name of the table used for this query
     * @param query     the {@link SQLiteQuery} object associated with this cursor object.
     */
    public KiCursor(SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
        super(driver, editTable, query);
    }

    public String getValue(String name) {
        return getString(getColumnIndex(name));
    }
}
