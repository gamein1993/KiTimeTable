package com.kiprogram.kitimetable.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kiprogram.kitimetable.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Periods periods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        periods = new Periods(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (int viewId : Periods.VIEW_ID_LIST) {
            TextView tv = periods.getTextView(viewId);
            tv.setText(Periods.getString(viewId));
        }
    }

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

        private final Map<Integer, TextView> viewIdToTextView = new HashMap<>();

        private static int counter;
        private final AppCompatActivity appCompatActivity;

        /**
         * コンストラクタ
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
    }
}
