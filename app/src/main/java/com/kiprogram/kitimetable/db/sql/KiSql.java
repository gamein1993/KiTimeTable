package com.kiprogram.kitimetable.db.sql;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kiprogram.kitimetable.db.cursor.KiCursor;
import com.kiprogram.kitimetable.db.cursor.KiCursorFactory;
import com.kiprogram.kitimetable.db.table.KiColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KiSql {
    /**
     * パラメータクラス<br>
     * 内部でしか使用しません。
     */
    private final class Param {

        private final KiColumn.Type type;
        private final String value;

        private Param(KiColumn.Type type, String value) {
            this.type = type;
            this.value = value;
        }
    }

    private SQLiteDatabase db;
    private StringBuilder sql;
    private ArrayList<Param> params;

    public KiSql(SQLiteOpenHelper oh) {
        this.db = oh.getWritableDatabase();
        this.sql = new StringBuilder();
        this.params = new ArrayList<>();
    }

    public KiSql(SQLiteDatabase db) {
        this.db = db;
        this.sql = new StringBuilder();
        this.params = new ArrayList<>();
    }

    public KiSql(SQLiteOpenHelper oh, CharSequence sql) {
        this.db = oh.getWritableDatabase();
        this.sql = new StringBuilder(sql);
        this.params = new ArrayList<>();
    }

    public KiSql(SQLiteDatabase db, CharSequence sql) {
        this.db = db;
        this.sql = new StringBuilder(sql);
        this.params = new ArrayList<>();
    }

    /**
     * SQLを設定します。既に設定されているものを上書きします。
     * @param sql SQL
     */
    public void setSql(CharSequence sql) {
        this.sql.setLength(0);
        this.sql.append(sql);
    }

    /**
     * SQLを付け足します。
     * @param sql 付け足すSQL
     * @return this
     */
    public KiSql append(CharSequence sql) {
        this.sql.append(sql);
        return this;
    }

    /**
     * SQL文の最後の1文字を削除します。<br>
     * カンマの付けすぎなどに対して有効です。
     */
    public void deleteLastChar() {
        this.sql.deleteCharAt(this.sql.length() - 1);
    }

    /**
     * パラメータを追加します。
     * @param type カラムタイプ
     * @param value 値
     */
    public void addParam(KiColumn.Type type, CharSequence value) {
        params.add(new Param(type, value.toString()));
    }

    /**
     * パラメータを追加します。
     * @param type カラムタイプ
     * @param value 値
     */
    public void addParam(KiColumn.Type type, int value) {
        params.add(new Param(type, String.valueOf(value)));
    }

    /**
     * パラメータを追加します。TEXTタイプ用。
     * @param value 値
     */
    public void addTextParam(CharSequence value) {
        params.add(new Param(KiColumn.Type.TEXT, value.toString()));
    }

    /**
     * パラメータを追加します。INTEGERタイプ用。
     * @param value 値
     */
    public void addIntParam(CharSequence value) {
        params.add(new Param(KiColumn.Type.INTEGER, value.toString()));
    }

    /**
     * パラメータを追加します。INTEGERタイプ用。
     * @param value 値
     */
    public void addIntParam(int value) {
        params.add(new Param(KiColumn.Type.INTEGER, String.valueOf(value)));
    }

    /**
     * SQLを実行します。
     * @return 実行結果
     */
    public KiCursor execQuery() {
        String[] arg = new String[params.size()];
        for (int i = 0; i < params.size(); i++) {
            Param param = params.get(i);
            arg[i] = param.value;
        }

        return (KiCursor) db.rawQueryWithFactory(new KiCursorFactory(), sql.toString(), arg, null);
    }

    /**
     * クエリ結果の最初の行を取得します。
     * @return クエリ結果の最初の行 1件もなければnull
     */
    public Map<String, String> getFirstRow() {
        String[] arg = new String[params.size()];
        for (int i = 0; i < params.size(); i++) {
            Param param = params.get(i);
            arg[i] = param.value;
        }

        KiCursor cursor = (KiCursor) db.rawQueryWithFactory(new KiCursorFactory(), sql.toString(), arg, null);
        try {
            if (!cursor.moveToFirst()) {
                return null;
            }

            Map<String, String> values = new HashMap<>();
            for (String columnName : cursor.getColumnNames()) {
                values.put(columnName, cursor.getValue(columnName));
            }
            return values;
        } finally {
            cursor.close();
        }
    }


}
