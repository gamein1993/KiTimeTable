package com.kiprogram.kitimetable.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kiprogram.kitimetable.db.table.Subject;


public class KiSQLiteOpenHelper extends SQLiteOpenHelper {
    /**
     * データベース名
     */
    private static final String NAME = "TimeTable";
    /**
     * バージョン
     */
    private static final int VERSION = 1;

    /**
     * コンストラクタ<br>
     * Activityクラス内では <b>this</b> を引数とすれば問題ありません。
     *
     * @param context Context (this)
     */
    public KiSQLiteOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Subject.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}


}
