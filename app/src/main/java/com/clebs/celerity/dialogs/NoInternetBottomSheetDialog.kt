package com.clebs.celerity.dialogs

import android.app.Dialog
import android.os.Bundle
import com.clebs.celerity.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NoInternetBottomSheetDialog:BottomSheetDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setContentView(R.layout.dialog_no_internet_bottomsheetdialog)
        }

    }
}