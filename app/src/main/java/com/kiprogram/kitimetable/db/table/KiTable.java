package com.kiprogram.kitimetable.db.table;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.kiprogram.kitimetable.db.sql.KiSql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class KiTable {

    private SQLiteOpenHelper oh;
    private SQLiteDatabase db;
    private String name;
    private KiColumn[] columns;
    private Map<String, String> values;
    private boolean isNew;

    /**
     * コンストラクタ(登録用)<br>
     * 自分でコミットロールバックを制御する場合は<br>
     * KiTable(SQLiteDatabase, String, KiColumn[])を使用してください。
     *
     * @param oh SQLiteOpenHelper
     * @param name テーブル名
     * @param columns カラム
     */
    public KiTable(SQLiteOpenHelper oh, String name, KiColumn[] columns) {
        this.oh = oh;
        this.db = oh.getWritableDatabase();
        this.name = name;
        this.columns = columns;
        this.values = new HashMap<>();
        this.isNew = true;
    }

    /**
     * コンストラクタ(登録用)<br>
     * 自分でコミットロールバックを制御しない場合は<br>
     * KiTable(SQLiteOpenHelper, String, KiColumn[])を使用してください。
     *
     * @param db SQLiteDatabase
     * @param name テーブル名
     * @param columns カラム
     */
    public KiTable(SQLiteDatabase db, String name, KiColumn[] columns) {
        this.db = db;
        this.name = name;
        this.columns = columns;
        this.values = new HashMap<>();
        this.isNew = true;
    }

    /**
     * コンストラクタ(更新用)<br>
     * 自分でコミットロールバックを制御する場合は<br>
     * KiTable(SQLiteDatabase, String, KiColumn[], Map<String, CharSequence>)を使用してください。
     *
     * @param oh SQLiteOpenHelper
     * @param name テーブル名
     * @param columns カラム
     * @param keys キー
     */
    public KiTable(SQLiteOpenHelper oh, String name, KiColumn[] columns, Map<String, CharSequence> keys) {
        this.oh = oh;
        this.db = oh.getWritableDatabase();
        this.name = name;
        this.columns = columns;
        this.isNew = false;
        checkKeys(keys);
        setValues(keys);
    }

    /**
     * コンストラクタ(更新用)<br>
     * 自分でコミットロールバックを制御しない場合は<br>
     * KiTable(SQLiteOpenHelper, String, KiColumn[], Map<String, CharSequence>)を使用してください。
     *
     * @param db SQLiteDatabase
     * @param name テーブル名
     * @param columns カラム
     * @param keys キー
     */
    public KiTable(SQLiteDatabase db, String name, KiColumn[] columns, Map<String, CharSequence> keys) {
        this.db = db;
        this.name = name;
        this.columns = columns;
        this.isNew = false;
        checkKeys(keys);
        setValues(keys);
    }

    public boolean insertUpdate() {
        if (isNew) {
            return insert();
        }
        return update();
    }

    /**
     * 登録処理<br>
     * 設定した値が登録されます。
     * @return 登録できた場合 true
     */
    public boolean insert() {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(name).append("(");
        for (KiColumn column : columns) {
            sql.append(column.name).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(") VALUES(");
        for (int i = 0; i < columns.length; i++) {
            sql.append("?,");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");

        SQLiteStatement statement = db.compileStatement(sql.toString());
        for (int i = 0; i < columns.length; i++) {
            KiColumn column = columns[i];
            String value = values.get(column.name);
            bindStatement(statement, i + 1, column, value);
        }

        return statement.executeInsert() >= 0;
    }

    /**
     * 更新処理<br>
     * 設定した値が更新されます。
     * @return 更新できた場合 true
     */
    public boolean update() {
        StringBuilder sql = new StringBuilder();
        List<KiColumn> list = new ArrayList<>();
        sql.append("UPDATE ").append(name).append(" SET ");
        for (KiColumn column : columns) {
            if (!column.primary) {
                sql.append(column.name).append(" = ?,");
                list.add(column);
            }
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE 0 = 0");
        for (KiColumn column : columns) {
            if (column.primary) {
                sql.append(" AND ").append(column.name).append(" = ?");
                list.add(column);
            }
        }

        SQLiteStatement statement = db.compileStatement(sql.toString());
        for (int i = 0; i < list.size(); i++) {
            KiColumn column = list.get(i);
            String value = values.get(column.name);
            bindStatement(statement, i + 1, column, value);
        }
        return statement.executeUpdateDelete() == 1;
    }

    /**
     * 更新処理を行います。中途半端です。完全一致でしか抽出できないようになっています。
     * @param db SQLiteDatabase
     * @param name テーブル名
     * @param columns カラム
     * @param keys 抽出条件に使用するキーの値
     * @param values 更新に使用する値
     * @return update件数
     */
    public static int update(SQLiteDatabase db, String name, KiColumn[] columns, Map<String, CharSequence> keys, Map<String, CharSequence> values) {
        StringBuilder sql = new StringBuilder();
        List<KiColumn.Type> typeList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        sql.append("UPDATE ").append(name).append(" SET ");
        for (Map.Entry<String, CharSequence> entry : values.entrySet()) {
            sql.append(entry.getKey()).append(" = ?,");
            typeList.add(searchColumn(columns, entry.getKey()).type);
            valueList.add(entry.getValue().toString());
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE 0 = 0");
        for (Map.Entry<String, CharSequence> entry : keys.entrySet()) {
            sql.append(" AND ").append(entry.getKey()).append(" = ?");
            typeList.add(searchColumn(columns, entry.getKey()).type);
            valueList.add(entry.getValue().toString());
        }

        SQLiteStatement statement = db.compileStatement(sql.toString());
        for (int i = 0; i < typeList.size(); i++) {
            KiColumn.Type type = typeList.get(i);
            String value = valueList.get(i);
            bindStatement(statement, i + 1, type, value);
        }
        return statement.executeUpdateDelete();
    }

    /**
     * 削除処理<br>
     * 設定した値が削除されます。
     * @return 削除できた場合 true
     */
    public boolean delete() {
        StringBuilder sql = new StringBuilder();
        List<KiColumn> list = new ArrayList<>();

        sql.append("DELETE FROM ").append(name);
        sql.append(" WHERE 0 = 0");
        for (KiColumn column : columns) {
            if (column.primary) {
                sql.append(" AND ").append(column.name).append(" = ?");
                list.add(column);
            }
        }

        SQLiteStatement statement = db.compileStatement(sql.toString());
        for (int i = 0; i < list.size(); i++) {
            KiColumn column = list.get(i);
            String value = values.get(column.name);
            bindStatement(statement, i + 1, column, value);
        }
        return statement.executeUpdateDelete() == 1;
    }

    public boolean isNew() {
        return isNew;
    }

    /**
     * 値を取得します。
     * @param name カラム名
     * @return 値
     */
    protected String getValue(String name) {
        return values.get(name);
    }

    /**
     * 値を取得します。
     * @param name カラム名
     * @return 値
     */
    protected int getValueInt(String name) {
        return Integer.parseInt(getValue(name));
    }

    /**
     * 値を設定します。
     * @param name カラム名
     */
    protected void setValue(String name, CharSequence value) {
        KiColumn column = searchColumn(columns, name);
        if (column.primary) {
            if (!isNew) {
                return;
            }
        }
        values.put(name, value.toString());
    }

    /**
     * テーブル作成
     * @param db SQLiteDatabase
     * @param name テーブル名
     * @param columns カラム
     */
    protected static void create(SQLiteDatabase db, String name, KiColumn[] columns) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(name).append("(");
        StringBuilder primaryColumns = new StringBuilder();
        for (KiColumn column : columns) {
            sql.append(column.name).append(" ").append(column.type.getString()).append(",");
            if (column.primary) {
                primaryColumns.append(column.name).append(",");
            }
        }
        primaryColumns.deleteCharAt(primaryColumns.length() - 1);
        sql.append("PRIMARY KEY(").append(primaryColumns).append("))");
        db.execSQL(sql.toString());
    }

    /**
     * 値を設定します。<br>
     * 引数Mapはテーブルのキーの数と必ず一致するようにしてください。
     *
     * @param keys 検索したいキーとなるcolumn名と値の組み合わせ
     */
    private void setValues(Map<String, CharSequence> keys) {
        values = getTableData(keys);
        if (values == null) {
            values = new HashMap<>();
        }
    }

    /**
     * キーチェックを行います。異常がある場合はIllegalArgumentExceptionがthrowされます。
     *
     * @param keys
     */
    private void checkKeys(Map<String, CharSequence> keys) {
        for (String key : keys.keySet()) {
            KiColumn column = searchColumn(columns, key);
            if (column == null) {
                Log.e("kilog", "設定された項目は存在しません。");
                throw new IllegalArgumentException();
            }
            if (!column.primary) {
                Log.e("kilog", "設定された項目はキーではありません。");
                throw new IllegalArgumentException();
            }
        }
    }

    /**
     * データを取得します。
     *
     * @param keys キーと値
     * @return データ
     */
    private Map<String, String> getTableData(Map<String, CharSequence> keys) {
        KiSql sql = new KiSql(db);
        sql.append("SELECT ");
        for (KiColumn column : columns) {
            sql.append(column.name).append(",");
        }
        sql.deleteLastChar();

        sql.append(" FROM ").append(this.name);

        sql.append(" WHERE 0 = 0");
        for (Map.Entry<String, CharSequence> entry : keys.entrySet()) {
            KiColumn column = searchColumn(columns, entry.getKey());
            sql.append(" AND ");
            sql.append(column.name).append(" = ?");
            sql.addParam(column.type, entry.getValue().toString());
        }
        return sql.getFirstRow();
    }

    /**
     * カラム名でカラム情報を取得します。
     *
     * @param name カラム名
     * @return カラム
     */
    private static KiColumn searchColumn(KiColumn[] columns, String name) {
        for (KiColumn column : columns) {
            if (name.equals(column.name)) {
                return column;
            }
        }
        return null;
    }

    /**
     * バインドします。
     * @param statement SQLiteStatement
     * @param index index
     * @param column カラム
     * @param value 値
     */
    private static void bindStatement(SQLiteStatement statement, int index, KiColumn column, String value) {
        if (value == null || value.trim().length() == 0) {
            statement.bindNull(index);
            return;
        }

        switch (column.type) {
            case TEXT:
                statement.bindString(index, value);
                break;
            case INTEGER:
                statement.bindLong(index, Long.parseLong(value));
                break;
            case NUMERIC:
            case REAL:
            case NONE:
                throw new RuntimeException("すみませんがまだ利用できません。");
        }
    }

    /**
     * バインドします。
     * @param statement SQLiteStatement
     * @param index index
     * @param type カラムタイプ
     * @param value 値
     */
    private static void bindStatement(SQLiteStatement statement, int index, KiColumn.Type type, String value) {
        if (value == null || value.trim().length() == 0) {
            statement.bindNull(index);
            return;
        }

        switch (type) {
            case TEXT:
                statement.bindString(index, value);
                break;
            case INTEGER:
                statement.bindLong(index, Long.parseLong(value));
                break;
            case NUMERIC:
            case REAL:
            case NONE:
                throw new RuntimeException("すみませんがまだ利用できません。");
        }
    }
}
