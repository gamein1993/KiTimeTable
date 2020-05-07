package com.kiprogram.kitimetable.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class KiTimePickerFragment extends DialogFragment implements DialogInterface.OnDismissListener {
    private final Context context;
    private final TimePickerDialog.OnTimeSetListener timeSetListener;
    private final OnDismissListener dismissListener;
    private int hourOfDay;
    private int minute;
    private final boolean is24HourView;

    public KiTimePickerFragment(Context context, TimePickerDialog.OnTimeSetListener timeSetListener, OnDismissListener dismissListener, int hourOfDay, int minute, boolean is24HourView) {
        this.context = context;
        this.timeSetListener = timeSetListener;
        this.dismissListener = dismissListener;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.is24HourView = is24HourView;
    }

    public KiTimePickerFragment(Context context, TimePickerDialog.OnTimeSetListener timeSetListener, OnDismissListener dismissListener, boolean is24HourView) {
        this.context = context;
        this.timeSetListener = timeSetListener;
        this.dismissListener = dismissListener;
        this.is24HourView = is24HourView;
        Calendar calendar = Calendar.getInstance();
        this.hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        this.minute = calendar.get(Calendar.MINUTE);
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        return new TimePickerDialog(context, timeSetListener, hourOfDay, minute, is24HourView);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) dismissListener.onDismiss();
    }
}
