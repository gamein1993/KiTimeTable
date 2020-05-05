package com.kiprogram.kitimetable.activity;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.kiprogram.kitimetable.R;
import com.kiprogram.kitimetable.log.KiLog;

public class PeriodActivity extends AppCompatActivity {
    public static final class EXTRA_FIELD {
        private EXTRA_FIELD() {}
        public static final String PERIOD_VIEW_ID = "period_view_id";
    }

    private int periodViewId;

    private ActionBar ab;

    private TextView tvPeriod;

    private EditText etStartTime;
    private EditText etEndTime;

    private Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_period);

        // 引数取得
        periodViewId = getIntent().getIntExtra(EXTRA_FIELD.PERIOD_VIEW_ID, Integer.MIN_VALUE);

        // 引数が取得できていない場合
        if (periodViewId == Integer.MIN_VALUE) {
            // ログ出力してRuntimeExceptionを投げる
            KiLog.e("PeriodActivity:呼び出し元からの引数設定ができていません。");
            throw new IllegalArgumentException();
        }

        // メンバ変数設定
        ab = getSupportActionBar();
        tvPeriod = findViewById(R.id.tvPeriod);
        etStartTime = findViewById(R.id.tvStartTime);
        etEndTime = findViewById(R.id.tvEndTime);
        bSave = findViewById(R.id.bSave);

        // アクションバーに戻るボタンの設定
        ab.setDisplayHomeAsUpEnabled(true);

        // 表示設定
        tvPeriod.setText(MainActivity.Periods.getString(periodViewId));

        // EditTextを入力不可に変更
        etStartTime.setKeyListener(null);
        etEndTime.setKeyListener(null);

        // EditTextにクリックイベントを設定
        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpDialog = new TimePickerDialog(PeriodActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    }
                }, 0, 0, true);
                tpDialog.show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
