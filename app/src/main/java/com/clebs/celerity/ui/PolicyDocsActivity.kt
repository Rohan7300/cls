package com.clebs.celerity.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Path
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.DrawViewClass
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.OtherCompanyPolicyAdapter
import com.clebs.celerity.adapters.OtherPolicyCallbackInterface
import com.clebs.celerity.databinding.ActivityPolicyDocsBinding
import com.clebs.celerity.models.requests.CompanySignedDocX
import com.clebs.celerity.models.requests.DriverHireAgreementX
import com.clebs.celerity.models.requests.UpdateDriverAgreementSignatureRequest
import com.clebs.celerity.models.response.GetDriverSignatureInformationResponse
import com.clebs.celerity.dialogs.DownloadingDialog
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.NotificationBroadcastReciever
import com.clebs.celerity.utils.OpenMode
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.bitmapToBase64
import com.clebs.celerity.utils.checkTokenExpirationAndLogout
import com.clebs.celerity.utils.noInternetCheck
import com.clebs.celerity.utils.showToast
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.UUID


class PolicyDocsActivity : AppCompatActivity(), OtherPolicyCallbackInterface {
    lateinit var mbinding: ActivityPolicyDocsBinding
    lateinit var viewModel: MainViewModel

    private var driverSignatureInfo: GetDriverSignatureInformationResponse? = null
    private var clebuserId = 0
    var notificationID:Int = 1
    var isImage1 = true
    private var handbookID: Int = 0
    var openModeDAHandBook: OpenMode = OpenMode.VIEW
    var openModeSignedGDPRPOLICY: OpenMode = OpenMode.VIEW
    var openModeSignedServiceLevelAgreement: OpenMode = OpenMode.VIEW
    var openModeSignedPrivacyPolicy: OpenMode = OpenMode.VIEW
    var openModeSignedDAEngagement: OpenMode = OpenMode.VIEW
    var openModeTrucksServiceLevelAgreementPolicy: OpenMode = OpenMode.VIEW
    var isImage2 = true
    lateinit var loadingDialog: LoadingDialog
    lateinit var downloadingDialog: DownloadingDialog
    var REQUEST_STORAGE_PERMISSION_CODE = 101
    lateinit var currentfileName: String
    lateinit var currentFileContent: InputStream
    lateinit var currentMode: OpenMode


    companion object {
        var path = Path()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_policy_docs)
        if (!Prefs.getInstance(App.instance).getBoolean("IsamazonSign", false)) {
            mbinding.llAmazon.visibility = View.GONE
        } else {
            mbinding.llAmazon.visibility = View.VISIBLE
        }
        loadingDialog = LoadingDialog(this)
        downloadingDialog = DownloadingDialog(this)
        if (!Prefs.getInstance(App.instance).getBoolean("isother", false)) {
            mbinding.llTrucks.visibility = View.GONE
        } else {
            mbinding.llTrucks.visibility = View.VISIBLE
        }

        /*        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
                val mainRepo = MainRepo(apiService)
                viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]*/

        noInternetCheck(this,mbinding.nointernetLL,this)
        viewModel = DependencyProvider.getMainVM(this)
        clebuserId = Prefs.getInstance(this).clebUserId.toInt()
        handbookID = Prefs.getInstance(this).handbookId
        viewModel.liveDataGetDriverSignatureInformation.observe(this) {
            if (it != null) {
                driverSignatureInfo = it
                Prefs.getInstance(applicationContext)
                    .saveBoolean("IsamazonSign", it.isAmazonSignatureReq)
                Prefs.getInstance(applicationContext)
                    .saveBoolean("isother", it.IsOtherCompanySignatureReq)
            }
        }
        viewModel.getDriverSignatureInfo(clebuserId.toDouble()).observe(this) {
            if (it != null) {
                handbookID = it.handbookId
                Prefs.getInstance(this).handbookId = handbookID
                loadingDialog.dismiss()
            } else {
                handbookID = Prefs.getInstance(this).handbookId
            }
        }

        loadingDialog.show()
        viewModel.GetDriverSignatureInformation(clebuserId)
        viewModel.GetDriverOtherCompaniesPolicy(clebuserId)
        observers()
        clickListeners()
        checkTokenExpirationAndLogout(this, Prefs.getInstance(this))
        mbinding.amazonHeader.setOnClickListener {
            if (isImage1) {
                mbinding.views1.visibility = View.GONE
            } else {
                mbinding.views1.visibility = View.VISIBLE
            }
            isImage1 = !isImage1

            setVisibility(mbinding.amazonLayout, !mbinding.amazonLayout.isVisible)
        }

        mbinding.truckHeaderLL.setOnClickListener {
            if (isImage2) {
                mbinding.viewss2.visibility = View.GONE
            } else {
                mbinding.viewss2.visibility = View.VISIBLE
            }
            isImage2 = !isImage2
            setVisibility(mbinding.truckLayout, !mbinding.truckLayout.isVisible)
        }

        mbinding.checkbox.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked) {

                mbinding.amazonHeader.isClickable = false
                viewGoneAnimator(mbinding.amazonLayout)
                viewGoneAnimator(mbinding.views1)
                mbinding.amazonArrow.setImageDrawable(resources.getDrawable(R.drawable.checkin))

                if (mbinding.llTrucks.visibility == View.GONE) {
                    showAlert()
                } else {
                    if (!mbinding.checkbox2.isChecked) {
                        showToast("Please check the trucks agreement to proceed", this)
                    } else {
                        showAlert()
                    }
                }
            } else {
                mbinding.amazonLayout.visibility = View.VISIBLE
                mbinding.views1.visibility = View.VISIBLE
                mbinding.amazonArrow.setImageDrawable(resources.getDrawable(R.drawable.chevron))
            }
        }
        mbinding.checkbox2.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked) {
                viewGoneAnimator(mbinding.truckLayout)
                viewGoneAnimator(mbinding.viewss2)
                mbinding.truckHeaderLL.isClickable = false
                mbinding.truckArrow.setImageDrawable(resources.getDrawable(R.drawable.checkin))
                mbinding.viewss2.visibility = View.GONE
                if (mbinding.llAmazon.visibility == View.GONE) {
                    showAlert()
                } else {
                    if (!mbinding.checkbox.isChecked) {
                        showToast("Please check the amazon agreement to proceed", this)
                    } else {
                        showAlert()
                    }
                }
            } else {
                mbinding.truckLayout.visibility = View.VISIBLE
                mbinding.viewss2.visibility = View.VISIBLE
                mbinding.truckArrow.setImageDrawable(resources.getDrawable(R.drawable.chevron))
            }
        }
    }

    private fun observers() {

        viewModel.liveDataDownloadDriverOtherCompaniesPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
              //  try {

                    downloadPDF(
                        it.CompanyDocuments[0].FileName,
                        ByteArrayInputStream(
                            Base64.getDecoder().decode(it.CompanyDocuments[0].FileContent)
                        ),
                        currentMode
                    )
                /*} catch (_: Exception) {

                }*/
            }
        }

        /*        viewModel.liveDataDownloadSignedServiceLevelAgreement.observe(this) {
                    downloadingDialog.dismiss()
                    if (it != null) {
                        downloadPDF(
                            "SignedServiceLevelAgreement",
                            it.byteStream(),
                            openModeSignedServiceLevelAgreement
                        )
                    }
                }*/
        var otherDocAdapter = OtherCompanyPolicyAdapter(this@PolicyDocsActivity)
        mbinding.otherDocsRV.adapter = otherDocAdapter
        mbinding.otherDocsRV.layoutManager = LinearLayoutManager(this)

        viewModel.liveDataGetDriverOtherCompaniesPolicy.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                otherDocAdapter.saveData(it)
            }
        }

        viewModel.liveDataDownloadServiceLevelAgreementPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF(
                    "SignedServiceLevelAgreement",
                    it.byteStream(),
                    openModeSignedServiceLevelAgreement
                )
            }
        }


        /*        viewModel.liveDataDownloadSignedDAHandbook.observe(this) {
                    downloadingDialog.dismiss()
                    if (it != null) {
                        downloadPDF("DAHandbook", it.byteStream(), openModeDAHandBook)
                    }
                }*/

        viewModel.liveDataDownloadDAHandbookPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF("DAHandbook", it.byteStream(), openModeDAHandBook)
            }
        }

        /*       viewModel.liveDataDownloadSignedGDPRPOLICY.observe(this) {
                   downloadingDialog.dismiss()
                   if (it != null) {
                       downloadPDF("GDPRPOLICY", it.byteStream(), openModeSignedGDPRPOLICY)
                   }
               }*/

        viewModel.liveDataDownloadGDPRPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF("GDPRPOLICY", it.byteStream(), openModeSignedGDPRPOLICY)
            }
        }

        /*        viewModel.liveDataDownloadSignedPrivacyPolicy.observe(this) {
                    downloadingDialog.dismiss()
                    if (it != null) {
                        downloadPDF("PrivacyPolicy", it.byteStream(), openModeSignedPrivacyPolicy)
                    }
                }*/

        viewModel.liveDataDownloadPrivacyPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF("PrivacyPolicy", it.byteStream(), openModeSignedPrivacyPolicy)
            }
        }

        /*        viewModel.liveDataDownloadSignedDAEngagement.observe(this) {
                    downloadingDialog.dismiss()
                    if (it != null) {
                        downloadPDF("DAEngagement", it.byteStream(), openModeSignedDAEngagement)
                    }
                }*/

        viewModel.liveDataDownloadDAEngagementPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF("PrivacyPolicy", it.byteStream(), openModeSignedDAEngagement)
            }
        }


        viewModel.liveDataDownloadTrucksServiceLevelAgreementPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF(
                    "TruckSLAPolicy",
                    it.byteStream(),
                    openModeTrucksServiceLevelAgreementPolicy
                )
            }
        }
    }

    private fun clickListeners() {

        mbinding.downloadHandBookPolicy1.setOnClickListener {
            downloadingDialog.show()
            openModeDAHandBook = OpenMode.DOWNLOAD
            //viewModel.DownloadSignedDAHandbook(handbookID)
            viewModel.DownloadDAHandbookPolicy()
        }
        mbinding.imgHandBookPolicy1.setOnClickListener {
            downloadingDialog.show()
            openModeDAHandBook = OpenMode.VIEW
            //viewModel.DownloadSignedDAHandbook(handbookID)
            viewModel.DownloadDAHandbookPolicy()
        }
        mbinding.downloadHandBookPolicy2.setOnClickListener {
            downloadingDialog.show()
            openModeDAHandBook = OpenMode.DOWNLOAD
            //viewModel.DownloadSignedDAHandbook(handbookID)
            viewModel.DownloadDAHandbookPolicy()
        }
        mbinding.imgHandBookPolicy2.setOnClickListener {
            downloadingDialog.show()
            openModeDAHandBook = OpenMode.VIEW
            //viewModel.DownloadSignedDAHandbook(handbookID)
            viewModel.DownloadDAHandbookPolicy()
        }


        mbinding.downloadSLA1.setOnClickListener {
            downloadingDialog.show()
            openModeSignedServiceLevelAgreement = OpenMode.DOWNLOAD
            //  viewModel.DownloadSignedServiceLevelAgreement(handbookID)
            viewModel.DownloadServiceLevelAgreementPolicy()
        }
        mbinding.imgSLA1.setOnClickListener {
            downloadingDialog.show()
            openModeSignedServiceLevelAgreement = OpenMode.VIEW
            //viewModel.DownloadSignedServiceLevelAgreement(handbookID)
            viewModel.DownloadServiceLevelAgreementPolicy()
        }
        mbinding.downloadSLA2.setOnClickListener {
            downloadingDialog.show()
            openModeTrucksServiceLevelAgreementPolicy = OpenMode.DOWNLOAD
            // viewModel.DownloadSignedServiceLevelAgreement(handbookID)
            // viewModel.DownloadServiceLevelAgreementPolicy()
            viewModel.DownloadTrucksServiceLevelAgreementPolicy()
        }
        mbinding.imgSLA2.setOnClickListener {
            downloadingDialog.show()
            openModeTrucksServiceLevelAgreementPolicy = OpenMode.VIEW
            // viewModel.DownloadSignedServiceLevelAgreement(handbookID)
            //viewModel.DownloadServiceLevelAgreementPolicy()
            viewModel.DownloadTrucksServiceLevelAgreementPolicy()
        }


        mbinding.downloadPrivacyPolicy1.setOnClickListener {
            downloadingDialog.show()
            openModeSignedPrivacyPolicy = OpenMode.DOWNLOAD
            //viewModel.DownloadSignedPrivacyPolicy(handbookID)
            viewModel.DownloadPrivacyPolicy()
        }
        mbinding.imgPrivacyPolicy1.setOnClickListener {
            downloadingDialog.show()
            openModeSignedPrivacyPolicy = OpenMode.VIEW
            // viewModel.DownloadSignedPrivacyPolicy(handbookID)
            viewModel.DownloadPrivacyPolicy()
        }
        mbinding.downloadPrivacyPolicy2.setOnClickListener {
            downloadingDialog.show()
            openModeSignedPrivacyPolicy = OpenMode.DOWNLOAD
            //viewModel.DownloadSignedPrivacyPolicy(handbookID)
            viewModel.DownloadPrivacyPolicy()
        }
        mbinding.imgPrivacyPolicy2.setOnClickListener {
            downloadingDialog.show()
            openModeSignedPrivacyPolicy = OpenMode.VIEW
            //viewModel.DownloadSignedPrivacyPolicy(handbookID)
            viewModel.DownloadPrivacyPolicy()
        }


        mbinding.downloadDAEngagement1.setOnClickListener {
            downloadingDialog.show()
            openModeSignedDAEngagement = OpenMode.DOWNLOAD
            //viewModel.DownloadSignedDAEngagement(handbookID)
            viewModel.DownloadDAEngagementPolicy()
        }
        mbinding.imgDAEngagement1.setOnClickListener {
            downloadingDialog.show()
            openModeSignedDAEngagement = OpenMode.VIEW
            //viewModel.DownloadSignedDAEngagement(handbookID)
            viewModel.DownloadDAEngagementPolicy()
        }
        mbinding.downloadDAEngagement2.setOnClickListener {
            downloadingDialog.show()
            openModeSignedDAEngagement = OpenMode.DOWNLOAD
            //viewModel.DownloadSignedDAEngagement(handbookID)
            viewModel.DownloadDAEngagementPolicy()
        }
        mbinding.imgDAEngagement2.setOnClickListener {
            downloadingDialog.show()
            openModeSignedDAEngagement = OpenMode.DOWNLOAD
            //viewModel.DownloadSignedDAEngagement(handbookID)
            viewModel.DownloadDAEngagementPolicy()
        }


        mbinding.downloadGDPR1.setOnClickListener {
            downloadingDialog.show()
            openModeSignedGDPRPOLICY = OpenMode.DOWNLOAD
            //viewModel.DownloadSignedGDPRPOLICY(handbookID)
            viewModel.DownloadGDPRPolicy()
        }
        mbinding.downloadGDPR2.setOnClickListener {
            downloadingDialog.show()
            openModeSignedGDPRPOLICY = OpenMode.DOWNLOAD
            //viewModel.DownloadSignedGDPRPOLICY(handbookID)
            viewModel.DownloadGDPRPolicy()
        }
        mbinding.imgGDPR1.setOnClickListener {
            downloadingDialog.show()
            openModeSignedGDPRPOLICY = OpenMode.VIEW
            //  viewModel.DownloadSignedGDPRPOLICY(handbookID)
            viewModel.DownloadGDPRPolicy()
        }
        mbinding.imgGDPR2.setOnClickListener {
            downloadingDialog.show()
            openModeSignedGDPRPOLICY = OpenMode.VIEW
            // viewModel.DownloadSignedGDPRPOLICY(handbookID)
            viewModel.DownloadGDPRPolicy()
        }
    }

    private fun showAlert() {
        mbinding.signLayoutll.visibility = View.VISIBLE


        val retry = mbinding.signLayout.RetryLay
        val save = mbinding.signLayout.sv
        val testIV = mbinding.signLayout.textIV
        val drawView = mbinding.signLayout.paintView.drawView

        save.setOnClickListener {
            Log.d("DrawViewSize", DrawViewClass.pathList.size.toString())
            if (DrawViewClass.pathList.isEmpty()) {
                showToast("Please add valid signature before saving", this)
            } else {
                // showToast("Signature length ${DrawViewClass.pathList.size}", this)
                val signatureBitmap: Bitmap = drawView.getBitmap()
                testIV.setImageBitmap(signatureBitmap)

                loadingDialog.show()
                updateSignatureInfoApi(signatureBitmap)
            }
        }

        retry.setOnClickListener {
            drawView.clearSignature()
        }

        /*        val dialog = CustDialog()
                dialog.setSignatureListener(object : SignatureListener {
                    override fun onSignatureSaved(bitmap: Bitmap) {
                        Log.d("Sign", "Bitmap $bitmap")
                        *//*  progressBarVisibility(true,mbinding.policyDocPB,mbinding.overlayViewPolicyActivity)*//*
                loadingDialog.show()
                updateSignatureInfoApi(bitmap)
            }
        })
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "sign")*/


    }

    private fun updateSignatureInfoApi(bitmap: Bitmap) {
        viewModel.livedataupdateDriverAgreementSignature.observe(this) {
            loadingDialog.cancel()
            if (it != null) {
                if (it.Status == "200") {
                    Prefs.getInstance(applicationContext)
                        .saveBoolean("isSignatureReq", false)
                }
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("destinationFragment", "HomeFragment")
                intent.putExtra("actionToperform", "undef")
                intent.putExtra("actionID", "0")
                intent.putExtra("tokenUrl", "undef")
                intent.putExtra("notificationId", "0")

                startActivity(intent)
            }
        }
        if (driverSignatureInfo != null) {
            val companyDocIds = driverSignatureInfo!!.OtherCompanyDocuments?.flatMap { company ->
                company.DocumentList.map { document ->
                    document.CompanyDocId
                }
            } ?: emptyList<Int>()
            val companyIDS = arrayListOf<CompanySignedDocX>()
            driverSignatureInfo!!.OtherCompanyDocuments?.map {
                companyIDS.add(
                    CompanySignedDocX(
                        it.CompanyID,
                        it.DocumentList.map { docs -> docs.CompanyDocId })
                )
            } ?: emptyArray<CompanySignedDocX>()

            val driverVanHire = DriverHireAgreementX(
                Accidents = false,
                HireAgrDOB = "2024-02-27T11:58:30.668Z",
                VehType = "HR",
                AgrLicenceNo = "HR9897896",
                AgrLicenceStartDate = "2024-02-27T11:58:30.668Z",
                DaHireLicenceEndDate = "2024-02-27T11:58:30.668Z",
                Conviction = true,
                Comments = "",
                Address = "",
                Signature = "",
                UserID = 0,
            )
            val bse64 = "data:image/png;base64," + bitmapToBase64(bitmap)
            Log.d("Base64", bse64)
            viewModel.UpdateDriverAgreementSignature(
                UpdateDriverAgreementSignatureRequest(
                    Address = driverSignatureInfo!!.PreviousAddress,//GRTTOwer
                    CompanyDocId = companyDocIds,//[]
                    CompanySignedDocs = companyIDS,//[]
                    DriverHireAgreement = driverVanHire,
                    HasAgreement = true,
                    IsDAVanHireChecked = driverSignatureInfo!!.DAVanHireSectionReq,
                    IsDAHandbookChecked = driverSignatureInfo!!.DAHandbookSectionReq,
                    IsGDPRChecked = driverSignatureInfo!!.GDPRSectionReq,
                    IsSLAChecked = driverSignatureInfo!!.SLASectionReq,
                    Signature = bse64,
                    UserID = clebuserId,
                    IsAmazonSignatureUpdated = driverSignatureInfo!!.isAmazonSignatureReq,
                    IsDAEngagementChecked = driverSignatureInfo!!.DAEngagementSectionReq
                )
            )

        } else {
            showToast("Pls Wait!!", this)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Toast.makeText(
            applicationContext,
            "Please sign the policy documents to continue",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun setVisibility(ll: LinearLayout, visibility: Boolean) {

        if (visibility) {
            ll.visibility = View.VISIBLE

        } else {
            ll.visibility = View.GONE

        }

    }

    private fun downloadPDF(fileName: String, fileContent: InputStream, mode: OpenMode) {
        currentfileName = fileName
        currentMode = mode
        currentFileContent = fileContent
        if (checkForStoragePermission()) {
            try {
                val currentDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                    Date()
                )
                val uniqueId = UUID.randomUUID().toString()
                val uniqueFileName = "$fileName-$currentDate-$uniqueId.pdf"
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    uniqueFileName
                )
                FileOutputStream(file).use { outputStream ->
                    fileContent.use { input ->
                        input.copyTo(outputStream)
                    }
                }
                val uri = getFileUri(file)
                if (mode == OpenMode.DOWNLOAD) {
                    showNotification(
                        "PDF Downloaded",
                        "Your PDF has been downloaded successfully.",
                        uri
                    )
                    showToast("PDF Downloaded!", this)
                } else {
                    showNotification(
                        "PDF Loaded",
                        "Your PDF is ready to view.",
                        uri
                    )
                    showToast("Your PDF is ready to view.", this)
                }

                openPDF(file, mode)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to download PDF", this)
            }
        } else {
            showToast("Storage Permission Required", this)
        }
    }

    private fun getFileUri(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "${this.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    private fun showNotification(title: String, content: String, uri: Uri) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Download Complete",
                "PDF Download Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        DependencyProvider.isComingFromPolicyNotification = true
        DependencyProvider.policyDocPDFURI = uri

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val viewPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val toastIntent = Intent(this, NotificationBroadcastReciever::class.java).apply {
            putExtra("notification_id", notificationID)
        }
        val toastPendingIntent = PendingIntent.getBroadcast(
            this, 0, toastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, "Download Complete")
            .setSmallIcon(R.drawable.logo_new)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(viewPendingIntent)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@PolicyDocsActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationID, notificationBuilder.build())
            notificationID+=1
        }
    }


    private fun checkForStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION_CODE
                )
            } else {
                return true
            }
        } else {
            return true
        }
        return false
    }


    private fun openPDF(file: File, mode: OpenMode) {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "${this.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }

        if (mode == OpenMode.VIEW) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            try {
                this.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("No PDF viewer found", this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION_CODE) {
            val granted = grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (granted) {
                downloadPDF(currentfileName, currentFileContent, currentMode)
            } else {
                showToast("Storage Permission Required", this)

            }
        }
    }

    private fun viewGoneAnimator(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
    }

    private fun viewVisibleAnimator(view: View) {
        view.animate()
            .alpha(1f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.VISIBLE
                }
            })
    }

    override fun docClick(companyDocID: Int, companyID: Int, viewMode: OpenMode) {
        currentMode = viewMode
        downloadingDialog.show()
        viewModel.DownloadDriverOtherCompaniesPolicy(
            Prefs.getInstance(this).clebUserId.toInt(),
            companyID,
            companyDocID
        )
    }
}