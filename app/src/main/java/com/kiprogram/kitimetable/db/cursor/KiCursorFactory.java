package com.kiprogram.kitimetable.db.cursor;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class KiCursorFactory implements SQLiteDatabase.CursorFactory {

    /**
     * See {@link SQLiteCursor#SQLiteCursor(SQLiteCursorDriver, String, SQLiteQuery)}.<br>
     * 自作のCursorクラスを使用するようにしています。普段はSQLiteCursorをnewしています。
     *
     * @param db SQLiteDatabase
     * @param masterQuery SQLiteCursorDriver
     * @param editTable editTable
     * @param query query
     */
    @Override
    public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
        return new KiCursor(masterQuery, editTable, query);
    }
}
