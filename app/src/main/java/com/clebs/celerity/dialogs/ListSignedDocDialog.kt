package com.clebs.celerity.dialogs

import android.Manifest
import android.app.Dialog
import android.app.Notification
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityListSignedDocsBinding
import com.clebs.celerity.models.response.GetDriverSignatureInformationResponse
import com.clebs.celerity.ui.SignedDocActivity
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.getCompanySignedDocsClicked
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

class ListSignedDocDialog : DialogFragment() {
    lateinit var viewModel: MainViewModel
    lateinit var mbinding: ActivityListSignedDocsBinding
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
    lateinit var context: SignedDocActivity

    companion object {
        var path = Path()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context, R.style.CustomDialog)
        mbinding = ActivityListSignedDocsBinding.inflate(LayoutInflater.from(context))
        viewModel = context.vm
        dialog.setContentView(mbinding.root)
        dialog.window?.apply {
            //   setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(R.color.transparent)
        }
        init()
        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = (context as SignedDocActivity)
    }

    fun init() {
        loadingDialog = LoadingDialog(context)
        downloadingDialog = DownloadingDialog(context)
        clebuserId = Prefs.getInstance(context).clebUserId.toInt()
        handbookID = DependencyProvider.getCompanySignedDocs?.HandBookId ?: 0

        if (DependencyProvider.getCompanySignedDocs != null) {
            if (DependencyProvider.getCompanySignedDocs!!.HBSLAFileName != null || DependencyProvider.getCompanySignedDocs!!.HBSLAFileName != "null")
                mbinding.llones.visibility = View.VISIBLE
            if (DependencyProvider.getCompanySignedDocs!!.HBGDRPFileName != null || DependencyProvider.getCompanySignedDocs!!.HBGDRPFileName != "null")
                mbinding.ll3.visibility = View.VISIBLE
            if (DependencyProvider.getCompanySignedDocs!!.HBSLAFileName != null || DependencyProvider.getCompanySignedDocs!!.HBSLAFileName != "null")
                mbinding.llPP.visibility = View.VISIBLE
            if (DependencyProvider.getCompanySignedDocs!!.HBEngagementFileName != null || DependencyProvider.getCompanySignedDocs!!.HBEngagementFileName != "null")
                mbinding.ll4.visibility = View.VISIBLE
            if (DependencyProvider.getCompanySignedDocs!!.HBSignFileName != null || DependencyProvider.getCompanySignedDocs!!.HBSignFileName != "null")
                mbinding.llsix.visibility = View.VISIBLE
        }

        observers()
        clickListeners()
    }

    private fun observers() {
        /*        viewModel.liveDataGetDriverSignatureInformation.observe(context) {
                    if (it != null) {
                        driverSignatureInfo = it
                        Prefs.getInstance(context)
                            .saveBoolean("IsamazonSign", it.isAmazonSignatureReq)
                        Prefs.getInstance(context)
                            .saveBoolean("isother", it.IsOtherCompanySignatureReq)
                    }
                }

                viewModel.getDriverSignatureInfo(clebuserId.toDouble()).observe(context) {
                    if (it != null) {
                        handbookID = it.handbookId
                        Prefs.getInstance(context).handbookId = handbookID
                        loadingDialog.dismiss()

                    } else {
                        handbookID = Prefs.getInstance(context).handbookId
                    }
                }*/

        /*
                viewModel.liveDataDownloadDriverOtherCompaniesPolicy.observe(context) {
                    downloadingDialog.dismiss()
                    if (it != null) {
                        if(getCompanySignedDocsClicked){
                            downloadPDF(
                                it.CompanyDocuments[0].FileName,
                                ByteArrayInputStream(
                                    Base64.getDecoder().decode(it.CompanyDocuments[0].FileContent)
                                ),
                                currentMode
                            )
                            getCompanySignedDocsClicked = false
                        }

                    }
                }
        */

        viewModel.liveDataDownloadSignedServiceLevelAgreement.observe(context) {
            downloadingDialog.dismiss()
            if (it != null) {
                if (getCompanySignedDocsClicked) {
                    downloadPDF(
                        "SignedServiceLevelAgreement",
                        it.byteStream(),
                        openModeSignedServiceLevelAgreement
                    )
                    getCompanySignedDocsClicked = false
                }

            }
        }

        viewModel.liveDataDownloadSignedDAHandbook.observe(context) {
            downloadingDialog.dismiss()
            if (it != null) {
                if (getCompanySignedDocsClicked) {
                    downloadPDF("DAHandbook", it.byteStream(), openModeDAHandBook)

                    getCompanySignedDocsClicked = false
                }
            }
        }

        viewModel.liveDataDownloadSignedGDPRPOLICY.observe(context) {
            downloadingDialog.dismiss()
            if (it != null) {
                if (getCompanySignedDocsClicked) {
                    downloadPDF("GDPRPOLICY", it.byteStream(), openModeSignedGDPRPOLICY)
                    getCompanySignedDocsClicked = false
                }

            }
        }

        viewModel.liveDataDownloadSignedPrivacyPolicy.observe(context) {
            downloadingDialog.dismiss()
            if (it != null) {
                if (getCompanySignedDocsClicked) {
                    downloadPDF("SignedPrivacyPolicy", it.byteStream(), openModeSignedPrivacyPolicy)
                    getCompanySignedDocsClicked = false
                }

            }
        }

        viewModel.liveDataDownloadSignedDAEngagement.observe(context) {
            downloadingDialog.dismiss()
            if (it != null) {
                if (getCompanySignedDocsClicked) {
                    downloadPDF("DAEngagementPolicy", it.byteStream(), openModeSignedDAEngagement)
                    getCompanySignedDocsClicked = false
                }

            }
        }

        /*        viewModel.liveDataDownloadTrucksServiceLevelAgreementPolicy.observe(context) {
                    downloadingDialog.dismiss()
                    if (it != null) {
                        if(getCompanySignedDocsClicked){
                            downloadPDF(
                                "TruckSLAPolicy",
                                it.byteStream(),
                                openModeTrucksServiceLevelAgreementPolicy
                            )
                            getCompanySignedDocsClicked = false
                        }

                    }
                }*/
    }

    private fun clickListeners() {

        mbinding.downloadHandBookPolicy1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeDAHandBook = OpenMode.DOWNLOAD
                viewModel.DownloadSignedDAHandbook(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadDAHandbookPolicy()
        }
        mbinding.imgHandBookPolicy1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeDAHandBook = OpenMode.VIEW
                viewModel.DownloadSignedDAHandbook(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadDAHandbookPolicy()
        }

        mbinding.downloadSLA1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeSignedServiceLevelAgreement = OpenMode.DOWNLOAD
                viewModel.DownloadSignedServiceLevelAgreement(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadServiceLevelAgreementPolicy()
        }
        mbinding.imgSLA1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeSignedServiceLevelAgreement = OpenMode.VIEW
                viewModel.DownloadSignedServiceLevelAgreement(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadServiceLevelAgreementPolicy()
        }

        mbinding.downloadPrivacyPolicy1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeSignedPrivacyPolicy = OpenMode.DOWNLOAD
                viewModel.DownloadSignedPrivacyPolicy(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadPrivacyPolicy()
        }
        mbinding.imgPrivacyPolicy1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeSignedPrivacyPolicy = OpenMode.VIEW
                viewModel.DownloadSignedPrivacyPolicy(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadPrivacyPolicy()
        }

        mbinding.downloadDAEngagement1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeSignedDAEngagement = OpenMode.DOWNLOAD
                viewModel.DownloadSignedDAEngagement(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadDAEngagementPolicy()
        }
        mbinding.imgDAEngagement1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeSignedDAEngagement = OpenMode.VIEW
                viewModel.DownloadSignedDAEngagement(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadDAEngagementPolicy()
        }

        mbinding.downloadGDPR1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeSignedGDPRPOLICY = OpenMode.DOWNLOAD
                viewModel.DownloadSignedGDPRPOLICY(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadGDPRPolicy()
        }
        mbinding.imgGDPR1.setOnClickListener {
            if (checkForStoragePermission()) {
                getCompanySignedDocsClicked = true
                downloadingDialog.show()
                openModeSignedGDPRPOLICY = OpenMode.VIEW
                viewModel.DownloadSignedGDPRPOLICY(handbookID)
            } else {
                showToast("Storage Permission Required", context)
            }
            //viewModel.DownloadGDPRPolicy()
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
                    showToast("PDF Downloaded!", context)
                } else {
                    showNotification(
                        "PDF Loaded",
                        "Your PDF is ready to view.",
                        uri
                    )
                    showToast("Your PDF is ready to view.", context)
                }

                openPDF(file, mode)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to download PDF", context)
            }
        } else {
            showToast("Storage Permission Required", context)
        }
    }

    private fun getFileUri(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
    }

    private fun showNotification(title: String, content: String, uri: Uri) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val viewPendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, "Download Complete")
            .setSmallIcon(R.drawable.logo_new)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(viewPendingIntent) // Set the pending intent for when notification is clicked
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_PROGRESS)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
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
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    context,
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
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }

        if (mode == OpenMode.VIEW) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("No PDF viewer found", context)
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
                showToast("Storage Permission Required", context)

            }
        }
    }

    fun showDialog(fragmentManager: FragmentManager) {
        val fragment = fragmentManager.findFragmentByTag(ExpiredDocDialog.TAG)
        if (!isVisible && fragment == null) {
            show(fragmentManager, ExpiredDocDialog.TAG)
        }
    }

    fun hideDialog() {
        if (dialog != null)
            if (dialog!!.isShowing)
                dismiss()
    }


}