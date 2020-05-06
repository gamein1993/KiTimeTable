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
import com.kiprogram.kitimetable.sp.KiSharedPreferences;
import com.kiprogram.kitimetable.util.KiTime;

public class PeriodActivity extends AppCompatActivity {
    public static final class EXTRA_FIELD {
        private EXTRA_FIELD() {}
        public static final String PERIOD_VIEW_ID = "period_view_id";
    }

    private int periodViewId;

    private ActionBar ab;
    private KiSharedPreferences sp;

    private TextView tvPeriod;

    private EditText etStartTime;
    private EditText etEndTime;

    private Button bSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_period);

        // 引数取得
        this.periodViewId = getIntent().getIntExtra(EXTRA_FIELD.PERIOD_VIEW_ID, Integer.MIN_VALUE);

        // 引数が取得できていない場合
        if (periodViewId == Integer.MIN_VALUE) {
            // ログ出力してRuntimeExceptionを投げる
            KiLog.e("PeriodActivity:呼び出し元からの引数設定ができていません。");
            throw new IllegalArgumentException();
        }

        // メンバ変数設定
        this.ab = getSupportActionBar();
        this.sp = new KiSharedPreferences(this);
        this.tvPeriod = findViewById(R.id.tvPeriod);
        this.etStartTime = findViewById(R.id.etStartTime);
        this.etEndTime = findViewById(R.id.etEndTime);
        this.bSave = findViewById(R.id.bSave);

        // アクションバーに戻るボタンの設定
        ab.setDisplayHomeAsUpEnabled(true);

        // 表示設定
        tvPeriod.setText(MainActivity.Periods.getString(periodViewId));
        etStartTime.setText(sp.getString(MainActivity.Periods.getSpKeyStartTime(periodViewId)));
        etEndTime.setText(sp.getString(MainActivity.Periods.getSpKeyEndTime(periodViewId)));

        // EditTextを入力不可に変更
        etStartTime.setKeyListener(null);
        etEndTime.setKeyListener(null);

        // EditTextにクリックイベント、TimePickerDialogを設定
        EtTimeMultiListener etTimeMultiListener = new EtTimeMultiListener();
        etStartTime.setOnClickListener(etTimeMultiListener);
        etEndTime.setOnClickListener(etTimeMultiListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 戻るしかアクションバーに設定していないのでとりあえずfinish
        finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * 時間入力用EditTextのイベント設定
     */
    private class EtTimeMultiListener implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {
        private EditText etTime;

        @Override
        public void onClick(View v) {
            // クリックしたEditTextを保持
            etTime = (EditText) v;
            KiTime time = new KiTime(etTime.getText());
            TimePickerDialog tpDialog = new TimePickerDialog(PeriodActivity.this,this, time.getHourOfDay(), time.getMinute(), true);
            tpDialog.show();
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            KiTime time = new KiTime(hourOfDay, minute);
            etTime.setText(time.toString());
        }
    }
}
