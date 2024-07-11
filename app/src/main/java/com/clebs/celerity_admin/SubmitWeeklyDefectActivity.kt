package com.clebs.celerity_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.clebs.celerity_admin.database.CheckInspection
import com.clebs.celerity_admin.database.IsInspectionDone
import com.clebs.celerity_admin.databinding.ActivityMainTwoBinding
import com.clebs.celerity_admin.databinding.ActivitySubmitWeeklyDefectBinding
import com.clebs.celerity_admin.ui.App
import com.clebs.celerity_admin.utils.Prefs
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SubmitWeeklyDefectActivity : AppCompatActivity() {
    lateinit var binding: ActivitySubmitWeeklyDefectBinding
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var regexPattern: Regex
    private lateinit var inspectionID: String
    private var startonetime: Boolean? = false
    private var inspectionreg: String? = null


    private var isfirst: Boolean? = false
    override fun onResume() {
        super.onResume()
        val message = intent?.getStringExtra(PublicConstants.quoteCreationFlowStatusMsgKeyInIntent)
            ?: "Could not identify status message"
        val tempCode =
            intent?.getIntExtra(PublicConstants.quoteCreationFlowStatusCodeKeyInIntent, -1)

        Log.e("tempcode", "onNewIntent: " + tempCode)
        if (tempCode == 200) {
            App.offlineSyncDB!!.insertinspectionInfo(
                IsInspectionDone(
                    InspectionDoneRegNo = Prefs.getInstance(App.instance).vehinspection.replace(
                        " ",
                        ""
                    ),
                    InspectionClientUniqueID = Prefs.getInstance(App.instance).vehinspectionUniqueID.replace(
                        " ",
                        ""
                    )
                )
            )

        } else {

            Log.d("hdhsdshdsdjshhsds", "else $tempCode $message")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmitWeeklyDefectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cqSDKInitializer = CQSDKInitializer(this)

        if (intent.hasExtra("regno")) {

            inspectionreg = intent?.getStringExtra("regno")?.replace(" ", "")
            Prefs.getInstance(App.instance).vehinspection = inspectionreg.toString()
        }
        val inspectionInfo = App.offlineSyncDB!!.getInspectionInfo()
        Log.e("result4", "onCreate: " + inspectionInfo)
        if (!App.offlineSyncDB!!.isInspectionTableEmpty()) {


            inspectionInfo.forEach {
                if (Prefs.getInstance(App.instance).vehinspection == it.InspectionDoneRegNo) {
                    binding.llmain.visibility = View.VISIBLE
                    binding.llstart.visibility = View.VISIBLE
                    binding.tvInspection.setText("OSM Vehicle Inspection Completed")
                    binding.btStart.visibility = View.GONE
                    binding.done.visibility = View.VISIBLE
                    Glide.with(this).load(R.raw.dones).into(binding.done)

                } else {
                    Log.e("result3", "onCreate: ")
                    binding.llmain.visibility = View.GONE
                    binding.btStart.visibility = View.VISIBLE
                    binding.tvInspection.setText("Start OSM Inspection *")
                    binding.done.visibility = View.GONE
                    binding.llstart.visibility = View.VISIBLE
                }


//                Log.e("result2", "onCreate: ")
//                binding.llmain.visibility = View.VISIBLE
//                binding.llstart.visibility = View.GONE
            }
        }




        isfirst = Prefs.getInstance(this).Isfirst
        startonetime = isfirst

        Log.e("newinspection", "onCreate: " + Prefs.getInstance(App.instance).vehinspection)


        binding.btStart.setOnClickListener {

            startInspection()
        }


    }

    private fun startInspection() {
        clientUniqueID()



        if (cqSDKInitializer.isCQSDKInitialized()) {

            Log.e("sdkskdkdkskdkskd", "onCreateView " + inspectionreg)

            try {
                cqSDKInitializer.startInspection(activity = this, clientAttrs = ClientAttrs(
                    userName = " ",
                    dealer = " ",
                    dealerIdentifier = " ",
                    client_unique_id = inspectionID

                    //drivers ID +vechile iD + TOdays date dd// mm //yy::tt,mm
                ), inputDetails = InputDetails(
                    vehicleDetails = VehicleDetails(
                        regNumber = inspectionreg?.replace(" ", ""), //if sent, user can't edit
                        make = "Van", //if sent, user can't edit
                        model = "Any Model", //if sent, user can't edit
                        bodyStyle = "Van"  // if sent, user can't edit - Van, Boxvan, Sedan, SUV, Hatch, Pickup [case sensitive]
                    ), customerDetails = CustomerDetails(
                        name = "", //if sent, user can't edit
                        email = "", //if sent, user can't edit
                        dialCode = "", //if sent, user can't edit
                        phoneNumber = "", //if sent, user can't edit
                    )
                ), userFlowParams = UserFlowParams(
                    isOffline = !startonetime!!, // true, Offline quote will be created | false, online quote will be created | null, online

                    skipInputPage = true, // true, Inspection will be started with camera page | false, Inspection will be started

                ),

                    result = { isStarted, msg, code ->

                        Log.e("messsagesss", "startInspection: " + msg + code)
                        if (isStarted) {
                            Prefs.getInstance(App.instance).Isfirst = false
                            startonetime = Prefs.getInstance(App.instance).Isfirst
                            Log.d("CQSDKXX", "isStarted " + msg)
                        } else {
                            Prefs.getInstance(App.instance).Isfirst = true
                            startonetime = Prefs.getInstance(App.instance).Isfirst
                            if (msg.equals("Online quote can not be created without internet")) {
                                Toast.makeText(
                                    this, "Please Turn on the internet", Toast.LENGTH_SHORT
                                ).show()
                                Log.d("CQSDKXX", "Not isStarted1  " + msg)
                            } else if (msg.equals("Sufficient data not available to create an offline quote")) {
                                Toast.makeText(
                                    this, "Please Turn on the internet", Toast.LENGTH_SHORT
                                ).show()
                                Log.d("CQSDKXX", "Not isStarted2  " + msg)
                            } else if (msg.equals("Unable to download setting updates, Please check internet")) {
                                Toast.makeText(
                                    this, "Please Turn on the internet", Toast.LENGTH_SHORT
                                ).show()
                                Log.d("CQSDKXX", "Not isStarted3  " + msg)
                            }

                            Log.d("CQSDKXX", "Not isStarted4  " + msg)
                        }
                        if (msg == "Success") {
                            Log.d("CQSDKXX", "Success " + msg)
                        } else {

                            Log.d("CQSDKXX", "Not Success " + msg)
                        }
                        if (!isStarted) {
                            Log.e("startedinspection", "onCreateView: $msg$isStarted")
                        }
                    })
            } catch (_: Exception) {


            }
        }

    }


    fun clientUniqueID(): String {
        val x = "123456"
        val y = "123456"
        // example string
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))

        regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")


        inspectionID = regexPattern.toString()
        Prefs.getInstance(App.instance).vehinspectionUniqueID = inspectionID
        return regexPattern.toString()
        Log.e("resistrationvrnpatterhn", "clientUniqueID: " + inspectionID)
    }
}