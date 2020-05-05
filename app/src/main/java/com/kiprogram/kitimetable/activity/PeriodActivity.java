package com.kiprogram.kitimetable.activity;

import android.os.Bundle;
import android.view.MenuItem;

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

        // アクションバーに戻るボタンの追加
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
