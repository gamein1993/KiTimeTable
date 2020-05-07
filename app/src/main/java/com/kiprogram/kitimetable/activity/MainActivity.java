package com.kiprogram.kitimetable.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kiprogram.kitimetable.R;
import com.kiprogram.kitimetable.sp.KiSharedPreferences;
import com.kiprogram.kitimetable.sp.KiSpKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Periods periods;
    private KiSharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // メンバ変数設定
        sp = new KiSharedPreferences(this);
        periods = new Periods(this);

        // 初動設定
        if (isFirstToUse()) {
            initialSetUp();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 時間表示設定
        StringBuilder sb = new StringBuilder();
        for (int viewId : Periods.VIEW_ID_LIST) {
            TextView tv = periods.getTextView(viewId);
            sb.append(Periods.getString(viewId)).append("\n");
            sb.append(sp.getString(Periods.getSpKeyStartTime(viewId))).append("\n");
            sb.append("|").append("\n");
            sb.append(sp.getString(Periods.getSpKeyEndTime(viewId))).append("\n");
            tv.setText(sb);
            sb.setLength(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSubjectsList:
                // Intentの作成
                Intent intent = new Intent(this, SubjectsListActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初回起動かどうか。
     * @return 初回起動の場合 true
     */
    private boolean isFirstToUse() {
        String firstStartTime = sp.getString(KiSpKey.FIRST_START_TIME);
        return firstStartTime == null;
    }

    /**
     * 初期設定を行う。
     */
    private void initialSetUp() {
        sp.setValue(KiSpKey.FIRST_START_TIME, "09:00");
        sp.setValue(KiSpKey.FIRST_END_TIME, "10:30");

        sp.setValue(KiSpKey.SECOND_START_TIME, "11:00");
        sp.setValue(KiSpKey.SECOND_END_TIME, "12:30");

        sp.setValue(KiSpKey.THIRD_START_TIME, "13:00");
        sp.setValue(KiSpKey.THIRD_END_TIME, "14:30");

        sp.setValue(KiSpKey.FOURTH_START_TIME, "15:00");
        sp.setValue(KiSpKey.FOURTH_END_TIME, "16:30");

        sp.apply();
    }

    /**
     * 時間割の時間部分のクラス
     */
    public static class Periods implements View.OnClickListener {
        private static final List<Integer> VIEW_ID_LIST = new ArrayList<Integer>() {
            {
                add(R.id.tvFirst);
                add(R.id.tvSecond);
                add(R.id.tvThird);
                add(R.id.tvFourth);
            }
        };
        private static final Map<Integer, String> VIEW_ID_TO_STRING = new HashMap<Integer, String>() {
            {
                put(R.id.tvFirst, "1限目");
                put(R.id.tvSecond, "2限目");
                put(R.id.tvThird, "3限目");
                put(R.id.tvFourth, "4限目");
            }
        };
        private static final Map<Integer, KiSpKey> VIEW_ID_TO_SP_KEY_START_TIME = new HashMap<Integer, KiSpKey>() {
            {
                put(R.id.tvFirst, KiSpKey.FIRST_START_TIME);
                put(R.id.tvSecond, KiSpKey.SECOND_START_TIME);
                put(R.id.tvThird, KiSpKey.THIRD_START_TIME);
                put(R.id.tvFourth, KiSpKey.FOURTH_START_TIME);
            }
        };
        private static final Map<Integer, KiSpKey> VIEW_ID_TO_SP_KEY_END_TIME = new HashMap<Integer, KiSpKey>() {
            {
                put(R.id.tvFirst, KiSpKey.FIRST_END_TIME);
                put(R.id.tvSecond, KiSpKey.SECOND_END_TIME);
                put(R.id.tvThird, KiSpKey.THIRD_END_TIME);
                put(R.id.tvFourth, KiSpKey.FOURTH_END_TIME);
            }
        };

        private final Map<Integer, TextView> viewIdToTextView = new HashMap<>();

        private static int counter;
        private final AppCompatActivity appCompatActivity;

        /**
         * コンストラクタ
         * インスタンス生成時にTextViewにクリックイベントを設定します。
         */
        Periods(AppCompatActivity appCompatActivity) {
            if (counter != 0) {
                throw new RuntimeException();
            }
            this.appCompatActivity = appCompatActivity;
            setOnClickListener();
            counter++;
        }

        private void setOnClickListener() {
            TextView tv;
            for (int viewId : VIEW_ID_LIST) {
                tv = appCompatActivity.findViewById(viewId);
                tv.setOnClickListener(this);
                viewIdToTextView.put(viewId, tv);
            }
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            counter--;
        }

        @Override
        public void onClick(android.view.View v) {
            Intent intent = new Intent(appCompatActivity, PeriodActivity.class);
            intent.putExtra(PeriodActivity.EXTRA_FIELD.PERIOD_VIEW_ID, v.getId());
            appCompatActivity.startActivity(intent);
        }

        public static String getString(int viewId) {
            return VIEW_ID_TO_STRING.get(viewId);
        }

        public TextView getTextView(int viewId) {
            return viewIdToTextView.get(viewId);
        }

        public static KiSpKey getSpKeyStartTime(int viewId) {
            return VIEW_ID_TO_SP_KEY_START_TIME.get(viewId);
        }

        public static KiSpKey getSpKeyEndTime(int viewId) {
            return VIEW_ID_TO_SP_KEY_END_TIME.get(viewId);
        }
    }
}
