package com.kiprogram.kitimetable.db.table;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kiprogram.kitimetable.db.cursor.KiCursor;
import com.kiprogram.kitimetable.db.sql.KiSql;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Subject extends KiTable {
    public static final String NAME = "subjects";

    public enum Field {
        ID("id"),
        NAME("name");

        private final String name;

        Field(String name) {
            this.name = name;
        }
    }

    private static final KiColumn[] COLUMNS = new KiColumn[]{
            new KiColumn(Field.ID.name, KiColumn.Type.INTEGER, true),
            new KiColumn(Field.NAME.name, KiColumn.Type.TEXT, false)
    };

    public Subject(SQLiteOpenHelper oh) {
        super(oh, NAME, COLUMNS);
    }

    public Subject(SQLiteDatabase db) {
        super(db, NAME, COLUMNS);
    }

    public Subject(SQLiteOpenHelper oh, EnumMap<Field, CharSequence> keys) {
        super(oh, NAME, COLUMNS, convertMap(keys));
    }

    public Subject(SQLiteDatabase db, EnumMap<Field, CharSequence> keys) {
        super(db, NAME, COLUMNS, convertMap(keys));
    }

    public static void create(SQLiteDatabase db) {
        create(db, NAME, COLUMNS);
    }

    public String getValue(Field field) {
        return super.getValue(field.name);
    }

    public int getValueInt(Field field) {
        return super.getValueInt(field.name);
    }

    public void setValue(Field field, CharSequence value) {
        super.setValue(field.name, value);
    }

    public static int nextId(SQLiteOpenHelper oh) {
        KiSql sql = new KiSql(oh, "SELECT MAX(id) AS id FROM " + NAME);
        KiCursor cursor = sql.execQuery();
        try {
            cursor.moveToFirst();
            return cursor.getIntValue("id") + 1;
        } finally {
            cursor.close();
        }
    }

    public static String nextIdStr(SQLiteOpenHelper oh) {
        return String.valueOf(nextId(oh));
    }

    private static Map<String, CharSequence> convertMap(EnumMap<Field, CharSequence> map) {
        Map<String, CharSequence> convertMap = new HashMap<>();
        for (Map.Entry<Field, CharSequence> entry : map.entrySet()) {
            convertMap.put(entry.getKey().name, entry.getValue());
        }
        return convertMap;
    }
}
