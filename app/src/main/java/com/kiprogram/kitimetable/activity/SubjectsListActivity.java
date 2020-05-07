package com.kiprogram.kitimetable.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiprogram.kitimetable.R;
import com.kiprogram.kitimetable.db.cursor.KiCursor;
import com.kiprogram.kitimetable.db.helper.KiSQLiteOpenHelper;
import com.kiprogram.kitimetable.db.sql.KiSql;

import java.util.ArrayList;
import java.util.List;

public class SubjectsListActivity extends AppCompatActivity {

    private KiSQLiteOpenHelper oh;
    private ActionBar ab;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects_list);

        // メンバ変数設定
        this.oh = new KiSQLiteOpenHelper(this);
        this.ab = getSupportActionBar();
        this.rv = findViewById(R.id.rvSubjectsList);

        // アクションバーに戻るボタンの設定
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // リサイクラービューの設定
        SubjectsListAdapter sla = new SubjectsListAdapter(getSubjects());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.setAdapter(sla);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_subjects_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuAddSubject:
                // Intentの作成
                Intent intent = new Intent(this, SubjectActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oh.close();
    }

    private class SubjectRow {
        private final int subjectId;
        private final String subjectName;
        private SubjectRow(int subjectId, String subjectName) {
            this.subjectId = subjectId;
            this.subjectName = subjectName;
        }
    }

    private class SubjectHolder extends RecyclerView.ViewHolder {
        private int subjectId;
        private final TextView tvSubjectName;
        public SubjectHolder(@NonNull View itemView) {
            super(itemView);
            subjectId = Integer.MIN_VALUE;
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
        }
    }

    private class SubjectsListAdapter extends RecyclerView.Adapter<SubjectHolder> {
        List<SubjectRow> subjectsList;

        private SubjectsListAdapter(List<SubjectRow> subjectNameList) {
            this.subjectsList = subjectNameList;
        }

        @NonNull
        @Override
        public SubjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_subjects_list_row, parent, false);
            SubjectHolder sh = new SubjectHolder(inflate);
            return sh;
        }

        @Override
        public void onBindViewHolder(@NonNull final SubjectHolder holder, int position) {
            holder.subjectId = subjectsList.get(position).subjectId;
            holder.tvSubjectName.setText(subjectsList.get(position).subjectName);
            holder.tvSubjectName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SubjectActivity.class);
                    intent.putExtra(SubjectActivity.EXTRA_FIELD.SUBJECT_ID, holder.subjectId);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return subjectsList.size();
        }
    }

    private List<SubjectRow> getSubjects() {
        List<SubjectRow> subjectList = new ArrayList<>();

        KiSql sql = new KiSql(oh);
        sql.append("SELECT id, name FROM subjects");
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
