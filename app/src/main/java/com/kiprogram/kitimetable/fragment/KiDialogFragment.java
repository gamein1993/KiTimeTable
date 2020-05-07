package com.kiprogram.kitimetable.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class KiDialogFragment extends DialogFragment implements DialogInterface.OnDismissListener {
    private final CharSequence title;
    private final CharSequence message;
    private final DialogInterface.OnClickListener clickListener;
    private OnDismissListener dismissListener;

    public KiDialogFragment(CharSequence title, CharSequence message, DialogInterface.OnClickListener clickListener) {
        this.title = title;
        this.message = message;
        this.clickListener = clickListener;
    }

    public KiDialogFragment(CharSequence title, CharSequence message, DialogInterface.OnClickListener clickListener, OnDismissListener dismissListener) {
        this.title = title;
        this.message = message;
        this.clickListener = clickListener;
        this.dismissListener = dismissListener;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("はい", clickListener);
        builder.setNegativeButton("いいえ", clickListener);
        return builder.create();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) dismissListener.onDismiss();
    }
}
