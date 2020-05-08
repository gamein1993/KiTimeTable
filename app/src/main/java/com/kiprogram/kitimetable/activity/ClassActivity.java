package com.kiprogram.kitimetable.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.kiprogram.kitimetable.R;
import com.kiprogram.kitimetable.db.cursor.KiCursor;
import com.kiprogram.kitimetable.db.helper.KiSQLiteOpenHelper;
import com.kiprogram.kitimetable.db.sql.KiSql;
import com.kiprogram.kitimetable.log.KiLog;
import com.kiprogram.kitimetable.sp.KiSharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class ClassActivity extends AppCompatActivity {
    public static final class EXTRA_FIELD {
        private EXTRA_FIELD() {}
        public static final String CLASS_VIEW_ID = "class_view_id";
    }

    private int classViewId;

    private ActionBar ab;
    private KiSQLiteOpenHelper oh;
    private KiSharedPreferences sp;

    private TextView tvDOW;
    private TextView tvPeriod;
    private Spinner spinnerSubject;
    private Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        // 引数取得
        this.classViewId = getIntent().getIntExtra(EXTRA_FIELD.CLASS_VIEW_ID, Integer.MIN_VALUE);
        // 引数が取得できていない場合
        if (classViewId == Integer.MIN_VALUE) {
            // ログ出力してRuntimeExceptionを投げる
            KiLog.e("ClassActivity:呼び出し元からの引数設定ができていません。");
            throw new IllegalArgumentException();
        }

        // メンバ変数設定
        this.ab = getSupportActionBar();
        this.oh = new KiSQLiteOpenHelper(this);
        this.sp = new KiSharedPreferences(this);
        this.tvDOW = findViewById(R.id.tvDOW);
        this.tvPeriod = findViewById(R.id.tvPeriod);
        this.spinnerSubject = findViewById(R.id.spinnerSubject);
        this.bSave = findViewById(R.id.bSave);

        // アクションバー設定
        ab.setTitle("コマ設定");
        ab.setDisplayHomeAsUpEnabled(true);

        // 表示設定
        tvDOW.setText(MainActivity.Classes.getTextDOW(classViewId));
        tvPeriod.setText(MainActivity.Classes.getTextPeriod(classViewId));

        // スピナー設定
        SubjectsListAdapter sla = new SubjectsListAdapter();
        spinnerSubject.setAdapter(sla);
        int subjectId = sp.getInt(MainActivity.Classes.getSpKeySubjectId(classViewId));
        spinnerSubject.setSelection(sla.getPosition(subjectId));

        // 保存ボタンのクリックイベント設定
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubjectsListAdapter.SubjectRow subjectRow = (SubjectsListAdapter.SubjectRow) spinnerSubject.getSelectedItem();
                if (subjectRow.id == Integer.MIN_VALUE) {
                    sp.remove(MainActivity.Classes.getSpKeySubjectId(classViewId));
                } else {
                    sp.setValue(MainActivity.Classes.getSpKeySubjectId(classViewId), subjectRow.id);
                }
                sp.apply();
                Toast.makeText(getApplicationContext(), "保存しました。", Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oh.close();
    }

    private class SubjectsListAdapter extends ArrayAdapter<SubjectsListAdapter.SubjectRow> {
        private class SubjectRow {
            private final int id;
            private final String name;
            private SubjectRow(int id, String name) {
                this.id = id;
                this.name = name;
            }
        }

        private final List<SubjectRow> subjectsList;

        public SubjectsListAdapter() {
            super(ClassActivity.this, android.R.layout.simple_spinner_item);
            this.subjectsList = getSubjects();
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            addAll(subjectsList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView tv = (TextView) super.getView(position, convertView, parent);
            tv.setText(getItem(position).name);
            return tv;
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
            tv.setText(getItem(position).name);
            return tv;
        }

        public int getPosition(int subjectId) {
            for (int i = 0; i < subjectsList.size(); i++) {
                if (subjectId == subjectsList.get(i).id) {
                    return i;
                }
            }
            return Integer.MIN_VALUE;
        }



        private List<SubjectRow> getSubjects() {
            List<SubjectRow> subjectList = new ArrayList<>();
            subjectList.add(new SubjectRow(Integer.MIN_VALUE, ""));

            KiSql sql = new KiSql(oh);
            sql.append("SELECT id, name FROM subjects ORDER BY id");
            KiCursor cursor = sql.execQuery();
            try {
                while (cursor.moveToNext()) {
                    subjectList.add(new SubjectRow(cursor.getIntValue("id"), cursor.getValue("name")));
                }
            } finally {
                cursor.close();
            }
            return subjectList;
        }
    }


}
