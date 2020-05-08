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
import com.kiprogram.kitimetable.fragment.KiTimePickerFragment;
import com.kiprogram.kitimetable.log.KiLog;
import com.kiprogram.kitimetable.sp.KiSharedPreferences;
import com.kiprogram.kitimetable.sp.KiSpKey;
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
    private TextView tvErrMsg;

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
        this.tvErrMsg = findViewById(R.id.tvErrMsg);

        // アクションバー設定
        ab.setTitle("時間設定");
        ab.setDisplayHomeAsUpEnabled(true);

        // 表示設定
        tvPeriod.setText(MainActivity.Periods.getText(periodViewId));
        etStartTime.setText(sp.getString(MainActivity.Periods.getSpKeyStartTime(periodViewId)));
        etEndTime.setText(sp.getString(MainActivity.Periods.getSpKeyEndTime(periodViewId)));

        // EditTextを入力不可に変更
        etStartTime.setKeyListener(null);
        etEndTime.setKeyListener(null);

        // EditTextにクリックイベント、TimePickerDialogを設定
        EtTimeMultiListener etTimeMultiListener = new EtTimeMultiListener();
        etStartTime.setOnClickListener(etTimeMultiListener);
        etEndTime.setOnClickListener(etTimeMultiListener);

        // 保存ボタンにクリックイベントを設定
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KiSpKey spStartTime = MainActivity.Periods.getSpKeyStartTime(periodViewId);
                KiSpKey spEndTime = MainActivity.Periods.getSpKeyEndTime(periodViewId);
                sp.setValue(spStartTime, etStartTime.getText());
                sp.setValue(spEndTime, etEndTime.getText());
                sp.apply();
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

    /**
     * 時間入力用EditTextのイベント設定
     */
    private class EtTimeMultiListener implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, KiTimePickerFragment.OnDismissListener {
        private boolean isRunning = false;
        private EditText etTime;

        @Override
        public void onClick(View v) {
            // 実行中の場合 なにもしない。
            if (isRunning) {
                return;
            }
            isRunning = true;
            // クリックしたEditTextを保持
            etTime = (EditText) v;
            KiTime time = new KiTime(etTime.getText());
            KiTimePickerFragment timePickerDialog = new KiTimePickerFragment(PeriodActivity.this, this, this, time.getHourOfDay(), time.getMinute(), true);
            timePickerDialog.show(getSupportFragmentManager(), "KiTimePickerFragment");

        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            KiTime time = new KiTime(hourOfDay, minute);
            etTime.setText(time.toString());

            KiTime startTime = new KiTime(etStartTime.getText());
            KiTime endTime = new KiTime(etEndTime.getText());
            if (startTime.compare(endTime)) {
                tvErrMsg.setText(null);
                bSave.setEnabled(true);
            } else {
                tvErrMsg.setText("開始時間と終了時間が逆転しています。");
                bSave.setEnabled(false);
            }
        }

        @Override
        public void onDismiss() {
            isRunning = false;
        }
    }
}
