package com.clebs.celerity.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.custDialog
import com.clebs.celerity.databinding.ActivityPolicyDocsBinding
import com.clebs.celerity.utils.CustDialog
import com.clebs.celerity.utils.Prefs


class PolicyDocsActivity : AppCompatActivity() {
    lateinit var mbinding:ActivityPolicyDocsBinding

    companion object{
            var path = Path()
            var brush = Paint()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_policy_docs)
        if (Prefs.getInstance(App.instance).getBoolean("IsamazonSign", false).equals(false)) {
            mbinding.llAmazon.visibility = View.GONE
        } else {
            mbinding.llAmazon.visibility = View.VISIBLE
        }

        if (Prefs.getInstance(App.instance).getBoolean("isother", false).equals(false)) {
            mbinding.llTrucks.visibility = View.GONE
        } else {
            mbinding.llAmazon.visibility = View.VISIBLE
        }

        mbinding.checkbox.addOnCheckedStateChangedListener { checkBox, state ->
            if (checkBox.isChecked) {
                if (mbinding.llTrucks.visibility == View.GONE) {
                    showAlert()
                } else {
                    if (!mbinding.checkbox2.isChecked) {
                        Toast.makeText(
                          this,
                            "Please check the trucks agreement to proceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        showAlert()
                    }
                }
            } else {

            }


        }
        mbinding.checkbox2.addOnCheckedStateChangedListener { checkBox, state ->

            if (checkBox.isChecked) {
                if (mbinding.llAmazon.visibility == View.GONE) {
                    showAlert()

                } else {
                    if (!mbinding.checkbox.isChecked) {
                        Toast.makeText(
                           this
                            ,
                            "Please check the amazon agreement to proceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        showAlert()
                    }

                }

            } else {

            }
        }
    }

    override fun onBackPressed() {
        Toast.makeText(applicationContext, "Please sign the policy documents to continue", Toast.LENGTH_SHORT).show()
    }

    fun showAlert() {

       /* mbinding.checkbox.getDrawable().mutate()
                .setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
*/
            CustDialog().show(supportFragmentManager, "sign")



/*
        val factory = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.dialog_signature, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(this).create()
        val SignatureView = view.findViewById<SignatureView>(R.id.signatureView)
        val LinearLayout = view.findViewById<LinearLayout>(R.id.llclear)
        val sign = view.findViewById<TextView>(R.id.bt_sign)
        sign.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            deleteDialog.dismiss()
        }
        LinearLayout.setOnClickListener {
            SignatureView.signatureClear()
        }

        deleteDialog.setView(view)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();
*/

    }


}