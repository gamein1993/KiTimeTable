package com.kiprogram.kitimetable.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kiprogram.kitimetable.R;

import java.util.ArrayList;
import java.util.List;

public class SubjectsListActivity extends AppCompatActivity {

    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects_list);

        // メンバ変数設定
        rv = findViewById(R.id.rvSubjectsList);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // リサイクラービューの設定
        SubjectsListAdapter sla = new SubjectsListAdapter(createData());
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(llm);
        rv.setAdapter(sla);
    }

    private class Subject {
        private final int subjectId;
        private final String subjectName;
        private Subject(int subjectId, String subjectName) {
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
        List<Subject> subjectsList;

        private SubjectsListAdapter(List<Subject> subjectNameList) {
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
                    Toast.makeText(SubjectsListActivity.this, "押した教科は" + holder.subjectId, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return subjectsList.size();
        }
    }

    private List<Subject> createData() {
        List<Subject> list = new ArrayList<>();
        list.add(new Subject(1, "国語"));
        list.add(new Subject(2, "算数"));
        list.add(new Subject(3, "理科"));
        list.add(new Subject(4, "社会"));
        list.add(new Subject(5, "地理"));
        list.add(new Subject(6, "政治"));
        list.add(new Subject(7, "数学"));
        list.add(new Subject(8, "体育"));
        list.add(new Subject(9, "家庭科"));
        list.add(new Subject(10, "科学"));
        return list;
    }
}
