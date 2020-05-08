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
import com.kiprogram.kitimetable.db.helper.KiSQLiteOpenHelper;
import com.kiprogram.kitimetable.db.table.Subject;
import com.kiprogram.kitimetable.sp.KiSharedPreferences;
import com.kiprogram.kitimetable.sp.KiSpKey;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private KiSQLiteOpenHelper oh;
    private KiSharedPreferences sp;
    private Periods periods;
    private Classes classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // メンバ変数設定
        oh = new KiSQLiteOpenHelper(this);
        sp = new KiSharedPreferences(this);
        periods = new Periods(this);
        classes = new Classes(this);

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
            sb.append(Periods.getText(viewId)).append("\n");
            sb.append(sp.getString(Periods.getSpKeyStartTime(viewId))).append("\n");
            sb.append("|").append("\n");
            sb.append(sp.getString(Periods.getSpKeyEndTime(viewId))).append("\n");
            tv.setText(sb);
            sb.setLength(0);
        }

        // コマ表示設定
        for (int viewId : Classes.VIEW_ID_LIST) {
            // コマに設定されている教科を取得
            String subjectId = sp.getString(Classes.getSpKeySubjectId(viewId));
            if (subjectId == null) {
                // 設定されていない場合 何も表示しない。
                continue;
            }

            // 教科の情報を取得
            EnumMap<Subject.Field, CharSequence> param = new EnumMap<>(Subject.Field.class);
            param.put(Subject.Field.ID, subjectId);
            Subject subject = new Subject(oh, param);

            // テキストビュー取得
            TextView tv = classes.getTextView(viewId);
            tv.setText(subject.getValue(Subject.Field.NAME));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        oh.close();
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
        private static final Map<Integer, String> VIEW_ID_TO_TEXT = new HashMap<Integer, String>() {
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

        public static String getText(int viewId) {
            return VIEW_ID_TO_TEXT.get(viewId);
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

    public static class Classes implements View.OnClickListener {
        private static final List<Integer> VIEW_ID_LIST = new ArrayList<Integer>() {
            {
                add(R.id.tvMonFirst);
                add(R.id.tvMonSecond);
                add(R.id.tvMonThird);
                add(R.id.tvMonFourth);
                add(R.id.tvTueFirst);
                add(R.id.tvTueSecond);
                add(R.id.tvTueThird);
                add(R.id.tvTueFourth);
                add(R.id.tvWedFirst);
                add(R.id.tvWedSecond);
                add(R.id.tvWedThird);
                add(R.id.tvWedFourth);
                add(R.id.tvThuFirst);
                add(R.id.tvThuSecond);
                add(R.id.tvThuThird);
                add(R.id.tvThuFourth);
                add(R.id.tvFriFirst);
                add(R.id.tvFriSecond);
                add(R.id.tvFriThird);
                add(R.id.tvFriFourth);
            }
        };

        private static final Map<Integer, KiSpKey> VIEW_ID_TO_SP_KEY_SUBJECT = new HashMap<Integer, KiSpKey>() {
            {
                put(R.id.tvMonFirst, KiSpKey.MON_FIRST_SUBJECT_ID);
                put(R.id.tvMonSecond, KiSpKey.MON_SECOND_SUBJECT_ID);
                put(R.id.tvMonThird, KiSpKey.MON_THIRD_SUBJECT_ID);
                put(R.id.tvMonFourth, KiSpKey.MON_FOURTH_SUBJECT_ID);
                put(R.id.tvTueFirst, KiSpKey.TUE_FIRST_SUBJECT_ID);
                put(R.id.tvTueSecond, KiSpKey.TUE_SECOND_SUBJECT_ID);
                put(R.id.tvTueThird, KiSpKey.TUE_THIRD_SUBJECT_ID);
                put(R.id.tvTueFourth, KiSpKey.TUE_FOURTH_SUBJECT_ID);
                put(R.id.tvWedFirst, KiSpKey.WED_FIRST_SUBJECT_ID);
                put(R.id.tvWedSecond, KiSpKey.WED_SECOND_SUBJECT_ID);
                put(R.id.tvWedThird, KiSpKey.WED_THIRD_SUBJECT_ID);
                put(R.id.tvWedFourth, KiSpKey.WED_FOURTH_SUBJECT_ID);
                put(R.id.tvThuFirst, KiSpKey.THU_FIRST_SUBJECT_ID);
                put(R.id.tvThuSecond, KiSpKey.THU_SECOND_SUBJECT_ID);
                put(R.id.tvThuThird, KiSpKey.THU_THIRD_SUBJECT_ID);
                put(R.id.tvThuFourth, KiSpKey.THU_FOURTH_SUBJECT_ID);
                put(R.id.tvFriFirst, KiSpKey.FRI_FIRST_SUBJECT_ID);
                put(R.id.tvFriSecond, KiSpKey.FRI_SECOND_SUBJECT_ID);
                put(R.id.tvFriThird, KiSpKey.FRI_THIRD_SUBJECT_ID);
                put(R.id.tvFriFourth, KiSpKey.FRI_FOURTH_SUBJECT_ID);

            }
        };

        private static final String TEXT_MAP_KEY_DOW = "day_of_the_week";
        private static final String TEXT_MAP_KEY_PERIOD = "period";
        private static final Map<Integer, Map<String, String>> VIEW_ID_TO_TEXT = new HashMap<Integer, Map<String, String>>() {
            {
                put(R.id.tvMonFirst, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "月曜日");
                        put(TEXT_MAP_KEY_PERIOD, "1限目");
                    }
                });
                put(R.id.tvMonSecond, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "月曜日");
                        put(TEXT_MAP_KEY_PERIOD, "2限目");
                    }
                });
                put(R.id.tvMonThird, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "月曜日");
                        put(TEXT_MAP_KEY_PERIOD, "3限目");
                    }
                });
                put(R.id.tvMonFourth, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "月曜日");
                        put(TEXT_MAP_KEY_PERIOD, "4限目");
                    }
                });
                put(R.id.tvTueFirst, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "火曜日");
                        put(TEXT_MAP_KEY_PERIOD, "1限目");
                    }
                });
                put(R.id.tvTueSecond, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "火曜日");
                        put(TEXT_MAP_KEY_PERIOD, "2限目");
                    }
                });
                put(R.id.tvTueThird, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "火曜日");
                        put(TEXT_MAP_KEY_PERIOD, "3限目");
                    }
                });
                put(R.id.tvTueFourth, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "火曜日");
                        put(TEXT_MAP_KEY_PERIOD, "4限目");
                    }
                });
                put(R.id.tvWedFirst, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "水曜日");
                        put(TEXT_MAP_KEY_PERIOD, "1限目");
                    }
                });
                put(R.id.tvWedSecond, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "水曜日");
                        put(TEXT_MAP_KEY_PERIOD, "2限目");
                    }
                });
                put(R.id.tvWedThird, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "水曜日");
                        put(TEXT_MAP_KEY_PERIOD, "3限目");
                    }
                });
                put(R.id.tvWedFourth, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "水曜日");
                        put(TEXT_MAP_KEY_PERIOD, "4限目");
                    }
                });
                put(R.id.tvThuFirst, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "木曜日");
                        put(TEXT_MAP_KEY_PERIOD, "1限目");
                    }
                });
                put(R.id.tvThuSecond, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "木曜日");
                        put(TEXT_MAP_KEY_PERIOD, "2限目");
                    }
                });
                put(R.id.tvThuThird, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "木曜日");
                        put(TEXT_MAP_KEY_PERIOD, "3限目");
                    }
                });
                put(R.id.tvThuFourth, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "木曜日");
                        put(TEXT_MAP_KEY_PERIOD, "4限目");
                    }
                });
                put(R.id.tvFriFirst, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "金曜日");
                        put(TEXT_MAP_KEY_PERIOD, "1限目");
                    }
                });
                put(R.id.tvFriSecond, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "金曜日");
                        put(TEXT_MAP_KEY_PERIOD, "2限目");
                    }
                });
                put(R.id.tvFriThird, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "金曜日");
                        put(TEXT_MAP_KEY_PERIOD, "3限目");
                    }
                });
                put(R.id.tvFriFourth, new HashMap<String, String>() {
                    {
                        put(TEXT_MAP_KEY_DOW, "金曜日");
                        put(TEXT_MAP_KEY_PERIOD, "4限目");
                    }
                });

            }
        };

        private final Map<Integer, TextView> viewIdToTextView = new HashMap<>();

        private static int counter = 0;
        private final AppCompatActivity appCompatActivity;

        /**
         * コンストラクタ
         * インスタンス生成時にTextViewにクリックイベントを設定します。
         */
        Classes(AppCompatActivity appCompatActivity) {
            if (counter != 0) {
                throw new RuntimeException();
            }
            this.appCompatActivity = appCompatActivity;
            setOnClickListener();
            counter++;
        }

        private void setOnClickListener() {
            for (int viewId : VIEW_ID_LIST) {
                TextView tv = appCompatActivity.findViewById(viewId);
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
            Intent intent = new Intent(appCompatActivity, ClassActivity.class);
            intent.putExtra(ClassActivity.EXTRA_FIELD.CLASS_VIEW_ID, v.getId());
            appCompatActivity.startActivity(intent);
        }

        public TextView getTextView(int viewId) {
            return viewIdToTextView.get(viewId);
        }

        public static KiSpKey getSpKeySubjectId(int viewId) {
            return VIEW_ID_TO_SP_KEY_SUBJECT.get(viewId);
        }

        public static String getTextDOW(int viewId) {
            return VIEW_ID_TO_TEXT.get(viewId).get(TEXT_MAP_KEY_DOW);
        }

        public static String getTextPeriod(int viewId) {
            return VIEW_ID_TO_TEXT.get(viewId).get(TEXT_MAP_KEY_PERIOD);
        }
    }
}
