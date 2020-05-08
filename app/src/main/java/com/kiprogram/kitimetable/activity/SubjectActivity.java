package com.kiprogram.kitimetable.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.kiprogram.kitimetable.R;
import com.kiprogram.kitimetable.db.helper.KiSQLiteOpenHelper;
import com.kiprogram.kitimetable.db.table.Subject;
import com.kiprogram.kitimetable.fragment.KiDialogFragment;
import com.kiprogram.kitimetable.sp.KiSharedPreferences;

import java.util.EnumMap;

public class SubjectActivity extends AppCompatActivity {
    public static final class EXTRA_FIELD {
        private EXTRA_FIELD() {}
        public static final String SUBJECT_ID = "subject_id";
    }

    private int subjectId;

    private ActionBar ab;
    private KiSQLiteOpenHelper oh;
    private KiSharedPreferences sp;

    private EditText etName;
    private Button bSave;
    private Subject subject;

    private boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        // 引数取得
        this.subjectId = getIntent().getIntExtra(EXTRA_FIELD.SUBJECT_ID, Integer.MIN_VALUE);

        // メンバ変数設定
        this.ab = getSupportActionBar();
        this.oh = new KiSQLiteOpenHelper(this);
        this.sp = new KiSharedPreferences(this);
        this.etName = findViewById(R.id.etName);
        this.bSave = findViewById(R.id.bSave);
        if (subjectId == Integer.MIN_VALUE) {
            // 引数が取得できていない場合 登録画面
            subject = new Subject(oh);
            // アクションバーの表示タイトル変更
            ab.setTitle("教科登録");
        } else {
            // 引数が取得できている場合 更新画面
            EnumMap<Subject.Field, CharSequence> param = new EnumMap<>(Subject.Field.class);
            param.put(Subject.Field.ID, String.valueOf(subjectId));
            subject = new Subject(oh, param);
            etName.setText(subject.getValue(Subject.Field.NAME));
            ab.setTitle("教科更新");
        }

        // アクションバーに戻るボタンの設定
        ab.setDisplayHomeAsUpEnabled(true);

        // 保存ボタンのクリックイベント
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 新規の場合 IDを取得
                if (subject.isNew()) {
                    subject.setValue(Subject.Field.ID, Subject.nextIdStr(oh));
                }
                subject.setValue(Subject.Field.NAME, etName.getText());

                if (!subject.insertUpdate()) {
                    Toast.makeText(getApplicationContext(), "なんか保存に失敗したよ。", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), "保存しました！", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 引数が取得できている場合
        if (subjectId != Integer.MIN_VALUE) {
            // アクションバーに削除メニューを表示する
            getMenuInflater().inflate(R.menu.activity_subject_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menuDelete:
                if (isRunning) {
                    break;
                }
                isRunning = true;

                // 削除確認のダイアログを表示
                KiDialogFragment dialogFragment = new KiDialogFragment("削除確認", "削除しますか？", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (!subject.delete()) {
                                    Toast.makeText(getApplicationContext(), "なんか削除に失敗したよ。", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                Toast.makeText(getApplicationContext(), "削除しました。", Toast.LENGTH_LONG).show();
                                MainActivity.Classes.delete(sp, subject.getValueInt(Subject.Field.ID));
                                finish();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                }, new KiDialogFragment.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        isRunning = false;
                    }
                });
                dialogFragment.show(getSupportFragmentManager(), "KiDialogFragment");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oh.close();
    }
}
