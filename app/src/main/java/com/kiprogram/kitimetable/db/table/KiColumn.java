package com.kiprogram.kitimetable.db.table;

public class KiColumn {
    public enum Type {
        TEXT("TEXT"),
        NUMERIC("NUMERIC"),
        INTEGER("INTEGER"),
        REAL("REAL"),
        NONE("NONE");

        private final String type;

        Type(String type) {
            this.type = type;
        }

        public String getString() {
            return this.type;
        }
    }

    public final String name;
    public final Type type;
    public final boolean primary;

    public KiColumn(String name, Type type, boolean primary) {
        this.name = name;
        this.type = type;
        this.primary = primary;
    }
}
