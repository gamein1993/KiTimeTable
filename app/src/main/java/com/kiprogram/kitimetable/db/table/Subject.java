package com.kiprogram.kitimetable.db.table;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    public static final KiColumn[] COLUMNS = new KiColumn[]{
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

    public void setValue(Field field, CharSequence value) {
        super.setValue(field.name, value);
    }

    private static Map<String, CharSequence> convertMap(EnumMap<Field, CharSequence> map) {
        Map<String, CharSequence> convertMap = new HashMap<>();
        for (Map.Entry<Field, CharSequence> entry : map.entrySet()) {
            convertMap.put(entry.getKey().name, entry.getValue());
        }
        return convertMap;
    }
}
