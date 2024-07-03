package com.clebs.celerity.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Path
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.OtherPolicyCallbackInterface
import com.clebs.celerity.databinding.ActivityListSignedDocsBinding
import com.clebs.celerity.databinding.ActivityPolicyDocsBinding
import com.clebs.celerity.models.response.GetDriverSignatureInformationResponse
import com.clebs.celerity.dialogs.DownloadingDialog
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.getCompanySignedDocs
import com.clebs.celerity.utils.NotificationBroadcastReciever
import com.clebs.celerity.utils.OpenMode
import com.clebs.celerity.utils.Prefs
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


class ListSignedDocsActivity : AppCompatActivity() {
    lateinit var mbinding: ActivityListSignedDocsBinding
    lateinit var viewModel: MainViewModel
    private var driverSignatureInfo: GetDriverSignatureInformationResponse? = null
    private var clebuserId = 0
    var notificationID: Int = 1
    private var handbookID: Int = 0
    var openModeDAHandBook: OpenMode = OpenMode.VIEW
    var openModeSignedGDPRPOLICY: OpenMode = OpenMode.VIEW
    var openModeSignedServiceLevelAgreement: OpenMode = OpenMode.VIEW
    var openModeSignedPrivacyPolicy: OpenMode = OpenMode.VIEW
    var openModeSignedDAEngagement: OpenMode = OpenMode.VIEW
    var openModeTrucksServiceLevelAgreementPolicy: OpenMode = OpenMode.VIEW
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
        mbinding = ActivityListSignedDocsBinding.inflate(layoutInflater)
        setContentView(mbinding.root)
        loadingDialog = LoadingDialog(this)
        downloadingDialog = DownloadingDialog(this)

        viewModel = DependencyProvider.getMainVM(this)
        clebuserId = Prefs.getInstance(this).clebUserId.toInt()
        handbookID = getCompanySignedDocs?.HandBookId ?: 0

        if(getCompanySignedDocs!=null){
            if (getCompanySignedDocs!!.HBSLAFileName!=null|| getCompanySignedDocs!!.HBSLAFileName!="null")
                mbinding.llones.visibility = View.VISIBLE
            if (getCompanySignedDocs!!.HBGDRPFileName!=null|| getCompanySignedDocs!!.HBGDRPFileName!="null")
                mbinding.ll3.visibility = View.VISIBLE
            if (getCompanySignedDocs!!.HBSLAFileName!=null|| getCompanySignedDocs!!.HBSLAFileName!="null")
                mbinding.llPP.visibility = View.VISIBLE
            if (getCompanySignedDocs!!.HBEngagementFileName!=null|| getCompanySignedDocs!!.HBEngagementFileName!="null")
                mbinding.ll4.visibility = View.VISIBLE
            if (getCompanySignedDocs!!.HBSignFileName!=null|| getCompanySignedDocs!!.HBSignFileName!="null")
                mbinding.llsix.visibility = View.VISIBLE
        }

            loadingDialog.show()
        observers()
        clickListeners()
    }

    private fun observers() {
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

        viewModel.liveDataDownloadDriverOtherCompaniesPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF(
                    it.CompanyDocuments[0].FileName,
                    ByteArrayInputStream(
                        Base64.getDecoder().decode(it.CompanyDocuments[0].FileContent)
                    ),
                    currentMode
                )
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

        viewModel.liveDataDownloadDAHandbookPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF("DAHandbook", it.byteStream(), openModeDAHandBook)
            }
        }

        viewModel.liveDataDownloadGDPRPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF("GDPRPOLICY", it.byteStream(), openModeSignedGDPRPOLICY)
            }
        }

        viewModel.liveDataDownloadPrivacyPolicy.observe(this) {
            downloadingDialog.dismiss()
            if (it != null) {
                downloadPDF("PrivacyPolicy", it.byteStream(), openModeSignedPrivacyPolicy)
            }
        }

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

        mbinding.downloadGDPR1.setOnClickListener {
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

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)

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
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(viewPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "View PDF", toastPendingIntent)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@ListSignedDocsActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notificationID, notificationBuilder.build())
            notificationID += 1
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

}