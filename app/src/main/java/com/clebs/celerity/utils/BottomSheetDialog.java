package com.clebs.celerity.utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.clebs.celerity.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
    ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(com.clebs.celerity.R.layout.bottom_sheet_dialog,
                 container, false);

        Button algo_button = v.findViewById(R.id.ss);


        algo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                     "Algorithm Shared", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        return v;
    }
}