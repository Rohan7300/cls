package com.clebs.celerity.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.util.Base64
import android.util.Base64OutputStream
import android.util.DisplayMetrics
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.KeyEventDispatcher.dispatchKeyEvent
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImageEntity
import com.clebs.celerity.database.OfflineSyncEntity
import com.clebs.celerity.databinding.DialogvehicleadvancepaymentBinding
import com.clebs.celerity.databinding.TokenExpiredDialogBinding
import com.clebs.celerity.dialogs.BreakDownDialog
import com.clebs.celerity.dialogs.ErrorDialog
import com.clebs.celerity.dialogs.ScanErrorDialog
import com.clebs.celerity.fragments.DailyWorkFragment
import com.clebs.celerity.models.requests.SaveVehicleInspectionInfo
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.App.Companion.showToastX
import com.clebs.celerity.ui.LoginActivity
import com.clebs.celerity.utils.DependencyProvider.brkEnd
import com.clebs.celerity.utils.DependencyProvider.brkEndTime
import com.clebs.celerity.utils.DependencyProvider.brkStart
import com.clebs.celerity.utils.DependencyProvider.brkStartTime
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.ParseException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@SuppressLint("HardwareIds")
fun Context.getDeviceID(): String {
    return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)
}

fun getRandomString(): String {
    var random = UUID.randomUUID().toString()
    while (random.isEmpty()) {
        random = UUID.randomUUID().toString()
    }
    return random
}

fun generatorRandom(): String {
    val generator = Random()
    val randomStringBuilder = StringBuilder()
    val randomLength = generator.nextInt(30)
    var tempChar: Char
    for (i in 0 until randomLength) {
        tempChar = (generator.nextInt(96) + 32).toChar()
        randomStringBuilder.append(tempChar)
    }
    return randomStringBuilder.toString()
}

fun String.isEmailValid(): Boolean {
    return Pattern.compile(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    ).matcher(this).matches();
}

fun convertImageFileToBase64(imageFile: File): String {
    println("FIlePAthhh${imageFile.path}")
    return ByteArrayOutputStream().use { outputStream ->
        Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
            imageFile.inputStream().use { inputStream ->
                inputStream.copyTo(base64FilterStream)
            }
        }
        return@use outputStream.toString()
    }
}

fun Context.convertImageUriToBase64(uri: Uri): String? {
    return try {
        val `in`: InputStream? = contentResolver.openInputStream(uri)
        val bytes: ByteArray? = `in`?.let { getBytes(it) }
        Log.d("data", "onActivityResult: bytes size=" + (bytes?.size ?: 0))
        Log.d(
            "data",
            "onActivityResult: Base64string=" + Base64.encodeToString(bytes, Base64.DEFAULT)
        )
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: java.lang.Exception) {
        // TODO: handle exception
        e.printStackTrace()
        Log.d("error", "onActivityResult: $e")
        null
    }
}

@Throws(IOException::class)
fun getBytes(inputStream: InputStream): ByteArray? {
    val byteBuffer = ByteArrayOutputStream()
    val bufferSize = 1024
    val buffer = ByteArray(bufferSize)
    var len = 0
    while (inputStream.read(buffer).also { len = it } != -1) {
        byteBuffer.write(buffer, 0, len)
    }
    return byteBuffer.toByteArray()
}

fun Context.getRealPathFromURI(contentUri: Uri): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = contentResolver.query(contentUri, proj, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(column_index)
    } catch (e: java.lang.Exception) {
        Log.e("TAG", "getRealPathFromURI Exception : $e")
        ""
    } finally {
        cursor?.close()
    }
}

fun Context.handleRemoteFile(uri: Uri): String? {
    val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
    val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    cursor?.moveToFirst()
    val filename = nameIndex?.let { cursor.getString(it) }
    val downloadedCloudFile = File(externalCacheDir, filename)
    return downloadedCloudFile.absolutePath
}

fun EditText.checkEnabled() {
    isEnabled = this.text.isEmpty()
    isClickable = this.text.isEmpty()
    isFocusable = this.text.isEmpty()
    isFocusableInTouchMode = this.text.isEmpty()
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.isVisibleView(): Boolean {
    return this.visibility == View.VISIBLE
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun EditText.setAutoCaps() {
    this.doAfterTextChanged {
        if (it?.toString()?.matches(Regex(".*[a-z].*")) == true) {
            this.setText(it.toString().uppercase())
            this.setSelection(it.length)
            Log.d("TAG", "setAutoCaps: ====>")
        }
    }
}

fun EditText.hideErrorOnTextSet(error: TextView) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.isNotEmpty()) {
                if (error.isVisible) {
                    error.gone()
                }
            }
        }

    })
}

fun NavController.isFragmentInBackStack(destinationId: Int) =
    try {
        getBackStackEntry(destinationId)
        true
    } catch (e: Exception) {
        false
    }


fun getWindowHeight(context: Context): Int {
    // Calculate window height for fullscreen use
    val displayMetrics = DisplayMetrics()
    (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.heightPixels
}


fun selectDate(
    context: Context?,
    tvDate: TextView,
) {
    var datePickerDialog: DatePickerDialog? = null

    datePickerDialog?.let {
        if (it.isShowing) {
            return
        }
    }
    val calendar = Calendar.getInstance()

    val mYear = calendar[Calendar.YEAR]
    val mMonth = calendar[Calendar.MONTH]
    val mDay = calendar[Calendar.DAY_OF_MONTH]
    datePickerDialog = DatePickerDialog(
        context!!,
        { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->

            Log.e("year", year.toString())
            Log.e("month", month.toString())
            Log.e("day", dayOfMonth.toString())

            val day =
                if (dayOfMonth.toString().length < 2) "0$dayOfMonth" else dayOfMonth.toString()

            val monthh =
                if ((month + 1).toString().length < 2) "0${(month + 1)}" else (month + 1).toString()

            tvDate.setText("${year}-${monthh}-${day}")


        },
        mYear,
        mMonth,
        mDay
    )

    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    datePickerDialog.show()

}


fun getUtcTime(date: String): String? {
    val sdf1 = SimpleDateFormat("yyyy-MM-dd")
    sdf1.timeZone = TimeZone.getTimeZone("UTC")
    val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    sdf2.timeZone = TimeZone.getTimeZone("UTC")
    return sdf1.parse(date)?.let { sdf2.format(it) }
}

fun log(str: String) {
    Log.d("TAG", str)
}

fun String.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // FROM_HTML_MODE_LEGACY is the behaviour that was used for versions below android N
        // we are using this flag to give a consistent behaviour
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

fun isValidPassword(password: String?): Boolean {
    val pattern: Pattern
    val matcher: Matcher
    val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$"
    pattern = Pattern.compile(PASSWORD_PATTERN)
    matcher = pattern.matcher(password)
    return matcher.matches()
}

fun String.contains(
    strList: List<String>,
    isAll: Boolean = false,
    ignoreCase: Boolean = true
): Boolean {
    if (isAll) {
        var isAllMatch = false
        var i = 0
        kotlin.run end@{
            strList.forEach {
                if (!this.contains(it, ignoreCase = ignoreCase)) {
                    isAllMatch = false
                    return@end
                } else {
                    if (strList.size - 1 == i) {
                        isAllMatch = true
                    }
                }
                ++i
            }
        }
        return isAllMatch
    } else {
        strList.forEach {
            return this.contains(it, ignoreCase = ignoreCase)
        }
        return false
    }
}

fun isTesting(): Boolean {
    return true
}

fun decodeBase64Image(base64String: String): Bitmap {
    val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun convertBitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun dbLog(it: ImageEntity) {

    /*val TAG = "DB_TEST"
         Log.d(TAG, "\n\n-+-----------------------------------------------------------------+-\n\n")
         Log.d(TAG, it.toString())
         Log.d(TAG, "\n\n-+-----------------------------------------------------------------+-\n\n")*/

}

fun setImageView(im: ImageView, value: String, context: Context) {
    try {
        if (value.isNullOrEmpty() || value == "empty")
            im.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.upload_bt))
        else
            im.setImageURI(value.toUri())


    } catch (_: Exception) {
        im.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.upload_bt))
    }
}

fun navigateTo(fragmentId: Int, context: Context, navController: NavController) {
    val prefs = Prefs.getInstance(context)
    val fragmentStack = prefs.getNavigationHistory()
    fragmentStack.push(fragmentId)
    navController.navigate(fragmentId)
    prefs.saveNavigationHistory(fragmentStack)
}

fun showToast(msg: String, context: Context) {
    try {
        showToastX!!.show(context, msg)
        //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.d("ToastException", e.message.toString())
    }
}

fun showSnackBar(msg: String, view: View) {
    Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
}

fun Bitmap.toRequestBody(): okhttp3.RequestBody {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
}

fun Context.getFileFromUri(uri: Uri): File {

    val file = File(this.filesDir, UUID.randomUUID().toString() + ".jpg")
    try {
        val inputStream =
            this.contentResolver.openInputStream(uri)
                ?: throw NullPointerException("file was null")
        val outputStream = FileOutputStream(file)
        inputStream.use { i ->
            outputStream.use { o ->
                i.copyTo(o, 1024)
            }
        }
    } catch (e: Exception) {
        Log.e(">>>>>>>>", e.message.toString())
    }
    return file
}

fun bitmapToBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

/*fun progressBarVisibility(show: Boolean,pb:ProgressBar, overlayView:View) {
    if (show) {
        pb.bringToFront()
        pb.visibility = View.VISIBLE
        overlayView.visibility = View.VISIBLE
        overlayView.bringToFront()
        overlayView.isClickable = true
        overlayView.isFocusable = true
    } else {
        pb.visibility = View.GONE
        overlayView.visibility = View.GONE
        overlayView.isClickable = false
        overlayView.isFocusable = false
    }
}*/

fun showTimePickerDialog(context: Context, tv: TextView, mode: Int) {

    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            if (mode == 1)
                brkStartTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            else
                brkEndTime = String.format("%02d:%02d", selectedHour, selectedMinute)

            val formattedTime: String = when {
                selectedHour == 0 -> {
                    "${(selectedHour + 12).toString().padStart(2, '0')}:${
                        selectedMinute.toString().padStart(2, '0')
                    } am"
                }

                selectedHour > 12 -> {
                    "${(selectedHour - 12).toString().padStart(2, '0')}:${
                        selectedMinute.toString().padStart(2, '0')
                    } pm"
                }

                selectedHour == 12 -> {
                    "${selectedHour.toString().padStart(2, '0')}:${
                        selectedMinute.toString().padStart(2, '0')
                    } pm"
                }

                else -> {
                    "${selectedHour.toString().padStart(2, '0')}:${
                        selectedMinute.toString().padStart(2, '0')
                    } am"
                }
            }

            tv.text = formattedTime

        },
        hour,
        minute,
        false

    )
//want to disable keyboard mode in above time picker dialog
//    timePickerDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    //there is a input keyboard icon show on timepicker dialog at bottom left i want to remove that icon
//    hideKeyboardInputInTimePicker(context.resources.configuration.orientation, timePickerDialog)
    //timePickerDialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.orange))
    //timePickerDialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(context.resources.getColor(R.color.orange))
    timePickerDialog.show()
}

//fun hideKeyboardInputInTimePicker(orientation: Int, timePickerDialog: TimePickerDialog) {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        try {
//            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
//                // Hide the keyboard icon in portrait mode
//                ((timePickerDialog as LinearLayout).getChildAt(4) as LinearLayout)
//                    .getChildAt(0).visibility = View.GONE
//            } else {
//                // Hide the keyboard icon in landscape mode
//                (((timePickerDialog as LinearLayout).getChildAt(2) as LinearLayout)
//                    .getChildAt(2) as LinearLayout).getChildAt(0).visibility = View.GONE
//            }
//        } catch (e: Exception) {
//            // Handle any exceptions here
//        }
//    }
//}
private val timePickerDialogListener: TimePickerDialog.OnTimeSetListener =
    object : TimePickerDialog.OnTimeSetListener {
        override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

            // logic to properly handle
            // the picked timings by user
            val formattedTime: String = when {
                hourOfDay == 0 -> {
                    if (minute < 10) {
                        "${hourOfDay + 12}:0${minute} am"
                    } else {
                        "${hourOfDay + 12}:${minute} am"
                    }
                }

                hourOfDay > 12 -> {
                    if (minute < 10) {
                        "${hourOfDay - 12}:0${minute} pm"
                    } else {
                        "${hourOfDay - 12}:${minute} pm"
                    }
                }

                hourOfDay == 12 -> {
                    if (minute < 10) {
                        "${hourOfDay}:0${minute} pm"
                    } else {
                        "${hourOfDay}:${minute} pm"
                    }
                }

                else -> {
                    if (minute < 10) {
                        "${hourOfDay}:${minute} am"
                    } else {
                        "${hourOfDay}:${minute} am"
                    }
                }
            }

        }
    }

fun showDatePickerDialog(context: Context, tv1: TextView, tv2: TextView, tvNext: TextView, i: Int) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDayOfMonth ->

            val selectedCalendar = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDayOfMonth, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (i != 0) {
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 23)
                selectedCalendar.set(Calendar.MINUTE, 59)
                selectedCalendar.set(Calendar.SECOND, 59)
            }

            val date = String.format(
                Locale.getDefault(),
                "%04d-%02d-%02dT%02d:%02d:%02d",
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH) + 1,
                selectedCalendar.get(Calendar.DAY_OF_MONTH),
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                selectedCalendar.get(Calendar.SECOND)
            )


            if (i == 0) {
                brkStart = date
                tv1.text = convertDateFormat(date)
            } else {
                brkEnd = date
                tv2.text = convertDateFormat(date)
            }



            if (tv1.text != "DD-MM-YYYY" && tv2.text != "DD-MM-YYYY" && isEndDateGreaterThanStartDate(
                    tv1.text.toString(),
                    tv2.text.toString()
                )
            ) {
                tvNext.isClickable = true
                tvNext.isEnabled = true
                tvNext.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                tvNext.isClickable = false
                tvNext.isEnabled = false
                tvNext.setTextColor(ContextCompat.getColor(context, R.color.orange))
            }
        },
        year,
        month,
        dayOfMonth
    )
    //datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.orange))
    //datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(context.resources.getColor(R.color.orange))
    datePickerDialog.show()
}


fun isEndDateGreaterThanStartDate(startDate: String, endDate: String): Boolean {
    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    try {
        val startDateObj = sdf.parse(startDate)
        val endDateObj = sdf.parse(endDate)
        if (endDateObj != null) {
            return !endDateObj.before(startDateObj)
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return false
}


fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun showErrorDialog(fragmentManager: FragmentManager, code: String, msg: String) {
    val errorDialog: ErrorDialog = ErrorDialog.newInstance(msg, code)

    errorDialog.show(fragmentManager, ErrorDialog.TAG)
}

fun showScanErrorDialog(
    dailyWorkFragment: DailyWorkFragment,
    fragmentManager: FragmentManager,
    code: String,
    msg: String,
    context: Context
) {
    try {
        val scanDialog: ScanErrorDialog = ScanErrorDialog.newInstance(msg, code)
        scanDialog.setListener(dailyWorkFragment)
        scanDialog.show(fragmentManager, ScanErrorDialog.TAG)
    } catch (_: Exception) {
        showToast(msg, context)
    }
}


fun getCurrentDateTime(): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    // dateFormat.timeZone = TimeZone.getTimeZone("UTC")
    return dateFormat.format(Date())
}

fun convertDateFormat(inputDate: String, inputFormat: String, outputFormat: String): String {
    val inputDateFormat = SimpleDateFormat(inputFormat, Locale.getDefault())
    val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())

    return try {
        val date = inputDateFormat.parse(inputDate)
        outputDateFormat.format(date)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun getVRegNo(prefs: Prefs): String {
    return if (prefs.scannedVmRegNo.isEmpty()) {
        prefs.vmRegNo
    } else
        prefs.scannedVmRegNo
}

fun getLoc(prefs: Prefs): String {
    return if (prefs.currLocationName.isNotEmpty())
        prefs.currLocationName ?: ""
    else
        prefs.workLocationName ?: ""
}

fun getLocID(prefs: Prefs): Int {
    return if (prefs.currLocationId != 0)
        prefs.currLocationId
    else
        prefs.workLocationId
}

fun checkIfInspectionFailed(osData: OfflineSyncEntity): Boolean {
    return osData.isoillevelImageFailed || osData.isaddblueImageFailed
}

fun checkIfInspectionFailed2(osData: OfflineSyncEntity): Boolean {
    return osData.isoillevelImageFailed || osData.isaddblueImageFailed
}

fun logOSEntity(base: String, osData: OfflineSyncEntity) {
    Log.d("$base", "OS DATA LOG + --------------------")
    Log.d("OSData DashFailureStat", osData.isdashboardUploadedFailed.toString())
    Log.d("OSData FrontImageFailed", osData.isfrontImageFailed.toString())
    Log.d("OSData NearSideFailed", osData.isnearSideFailed.toString())
    Log.d("OSData RearSideFailed", osData.isrearSideFailed.toString())
    Log.d("OSData OffSideFailed", osData.isoffSideFailed.toString())
    Log.d("OSData AddblueFailed", osData.isaddblueImageFailed.toString())
    Log.d("OSData OilFailed", osData.isoillevelImageFailed.toString())

    osData.dashboardImage?.take(10)
        ?.let { it1 -> Log.d("OSData DashboardImageFirst10", it1) }
        ?: Log.d("OSData DashboardImage", "null")

    osData.frontImage?.take(10)
        ?.let { it1 -> Log.d("OSData frontImageFirst10", it1) }
        ?: Log.d("OSData frontImage", "null")

    osData.rearSideImage?.take(10)
        ?.let { it1 -> Log.d("OSData rearSideImageFirst10", it1) }
        ?: Log.d("OSData rearSideImage", "null")

    osData.nearSideImage?.take(10)
        ?.let { it1 -> Log.d("OSData nearSideImageFirst10", it1) }
        ?: Log.d("OSData nearSideImage", "null")

    osData.offSideImage?.take(10)
        ?.let { it1 -> Log.d("OSData offSideImageFirst10", it1) }
        ?: Log.d("OSData offSideImage", "null")

    osData.addblueImage?.take(10)
        ?.let { it1 -> Log.d("OSData addblueImageFirst10", it1) }
        ?: Log.d("OSData addblueImage", "null")

    osData.oillevelImage?.take(10)
        ?.let { it1 -> Log.d("OSData oillevelImageFirst10", it1 + "\n") }
        ?: Log.d("OSData oillevelImage", "null")



    Log.d("$base", "OS DATA LOG + --------------------")
}

fun startUploadWithWorkManager(
    uploadType: Int,
    prefs: Prefs,
    context: Context,
    lmID: Int = 0,
    vmID: Int = 0
) {

    val userId = prefs.clebUserId.toInt()

    val inputData = Data.Builder()
        .putInt("clebUserId", userId)
        .putInt("uploadtype", uploadType)
        .build()

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val uploadWorkRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
        .setInputData(inputData)
        .setConstraints(constraints)
        .build()

    WorkManager.getInstance(context).enqueue(uploadWorkRequest)
}

fun SaveVehicleInspection(viewModel: MainViewModel) {
    val currentDate =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).format(
            Date()
        )

    val currentloction = Prefs.getInstance(App.instance).currLocationId
    val workinglocation = Prefs.getInstance(App.instance).workLocationId
    val locationID: Int = if (workinglocation != 0) {
        workinglocation
    } else {
        currentloction
    }
    viewModel.SaveVehicleInspectionInfo(
        SaveVehicleInspectionInfo(
            Prefs.getInstance(App.instance).clebUserId.toInt(),
            currentDate,
            Prefs.getInstance(App.instance).inspectionID.replace(" ", ""),
            locationID,
            Prefs.getInstance(App.instance).vmId
        )
    )

    Log.e(
        "sdkskffjdapiisnpection",
        "SaveVehicleInspection: " + Prefs.getInstance(App.instance).inspectionID
    )
}


fun getCurrentWeek(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.WEEK_OF_YEAR)
}

fun getCurrentYear(): Int {
    val calendar = Calendar.getInstance()
    return calendar.get(Calendar.YEAR)
}

fun convertDateFormat(inputDate: String): String {
    try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    } catch (_: Exception) {
        return " "
    }
}

fun convertToDate(inputDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        outputFormat.format(date!!)
    } catch (_: Exception) {
        " "
    }
}


fun convertToTime(inputDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val date = inputFormat.parse(inputDate)
        outputFormat.format(date!!)
    } catch (_: Exception) {
        try {
            val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date!!)
        } catch (_: Exception) {
            " -- : -- "
        }
    }
}

private fun checkNullorEmpty(value: String?): Boolean {
    return !(value.isNullOrEmpty() || value == "empty")
}

fun getMimeType(uri: Uri): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

fun getCurrentAppVersion(context: Context): String {
    try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        Log.e("VersionError", "Package name not found", e)
    }
    return "1.0.0"
}

fun addLeadingZeroIfNeeded(text: Editable): String {
    val time = text.toString().trim()
    val parts = time.split(":")
    if (parts.size == 2) {
        val hour = parts[0].toIntOrNull()
        val minute = parts[1].toIntOrNull()
        if (hour != null && minute != null) {
            return String.format("%02d:%02d", hour, minute)
        }
    }
    return time
}

fun printBitmapSize(bitmap: Bitmap) {
    val width = bitmap.width
    val height = bitmap.height
    val byteCount = bitmap.byteCount
    val kilobyteCount = byteCount / 1024.0
    val mb = kilobyteCount / 1024.0

    Log.d(
        "BitmapSize",
        "Width: $width, Height: $height, Size: $byteCount bytes\n ($kilobyteCount KB)\n ($mb MB"
    )
}

fun scaleBitmapToWidth(bitmap: Bitmap, newWidth: Int): Bitmap {

    val originalWidth = bitmap.width
    val originalHeight = bitmap.height

    val aspectRatio = originalHeight.toFloat() / originalWidth.toFloat()
    val newHeight = (newWidth * aspectRatio).toInt()

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

fun getImageBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        val contentResolver: ContentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return bitmap
}

fun getCameraURI(context: Context): Uri? {
    var savedUri: Uri? = null
    val imageCapture = ImageCapture.Builder().build() ?: throw IOException("Camera not connected")
    val name = SimpleDateFormat(DailyWorkFragment.FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
    }
    val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()
    else
        ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                contentValues
            ).build()
    imageCapture.takePicture(
        outputOptions, ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.d(ContentValues.TAG, "Photo capture failed")
                println("Photo capture failed ext ex: $exc")
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d(ContentValues.TAG, "Photo capture succeeded processing photo")

                showToast("Photo capture succeeded processing photo", context)

                if (getImageBitmapFromUri(context, output.savedUri!!) != null) {
                    savedUri = output.savedUri!!
                }
            }
        }
    )
    return savedUri
}


fun getFileUri(file: File, context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    } else {
        Uri.fromFile(file)
    }
}

fun showBirthdayCard(dateString: String, prefs: Prefs): Boolean {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    return try {
        val inputDateTime = LocalDateTime.parse(dateString, formatter)
        val inputDate = inputDateTime.toLocalDate()
        prefs.dob = dateString
        val isCardShown = prefs.isBirthdayCardShown

        val today = LocalDate.now()
        val isToday = (inputDate.month == today.month) && (inputDate.dayOfMonth == today.dayOfMonth)

        Log.d("BirthdayDialog", " $isToday\n ${inputDate}\n ${today}\n ${isCardShown}")
        if (!isCardShown!! && isToday) {
            Log.d("BirthdayDialog", "in if")
            true
        } else {
            Log.d("BirthdayDialog", "in else")
            false
        }
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        false
    }
}

fun isTokenExpired(prefs: Prefs): Boolean {

    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")

    val expirationDateTime = sdf.parse(prefs.tokenExpiredOn)
    val currentDateTime = Date()

    return currentDateTime >= expirationDateTime
}

fun dateToday(): String {
    return SimpleDateFormat("yyyy-MM-dd").format(Date())
}
fun dateOnFullFormat():String{
    return ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT)
}
fun checkTokenExpirationAndLogout(context: Activity, prefs: Prefs) {
    if (isTokenExpired(prefs)) {
        val tokenExpiredDialog = AlertDialog.Builder(context).create()
        val tokenExpiredDialogBinding =
            TokenExpiredDialogBinding.inflate(LayoutInflater.from(context))
        tokenExpiredDialog.setView(tokenExpiredDialogBinding.root)
        tokenExpiredDialog.setCanceledOnTouchOutside(false)
        tokenExpiredDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        tokenExpiredDialogBinding.logoutbtn.setOnClickListener {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("logout", "0")
            intent.putExtra("downloadCQ", Prefs.getInstance(App.instance).isFirst)
            Prefs.getInstance(context).clearPreferences()
            context.finish()
            context.startActivity(intent)
            prefs.saveBoolean("isLoggedIn", false)
        }
        tokenExpiredDialog.show()

    }
}

fun roundOffValues(value: String): String {
    var roundOffValue = "0"
    try {
        val number = value.toDoubleOrNull() ?: 0.0
        val roundedNumber = kotlin.math.round(number).toInt()
        roundOffValue = String.format("%.2f", number)
    } catch (_: Exception) {
        return value
    }
    return roundOffValue
}

fun noInternetCheck(context: Context, ll: LinearLayout, viewLifecycleOwner: LifecycleOwner) {
    val networkManager = NetworkManager(context)
    networkManager.observe(viewLifecycleOwner) {
        if (it) {
            ll.visibility = View.GONE
        } else {
            ll.visibility = View.VISIBLE
        }
    }
}

fun showUpdateDialog(context: Context, playStoreUrl: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Update Available")
    builder.setMessage("A new version of the app is available. Please update to the latest version.")
    builder.setPositiveButton("Update") { dialog, _ ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl))
        context.startActivity(intent)
        //dialog.dismiss()
    }
    builder.setCancelable(false)
    val dialog = builder.create()
    dialog.setCanceledOnTouchOutside(false)
    dialog.show()
}

fun isVersionNewer(currentVersion: String, latestVersion: String): Boolean {
    val currentParts = currentVersion.split('.').map { it.toIntOrNull() ?: 0 }
    val latestParts = latestVersion.split('.').map { it.toIntOrNull() ?: 0 }

    for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
        val currentPart = currentParts.getOrElse(i) { 0 }
        val latestPart = latestParts.getOrElse(i) { 0 }

        if (currentPart < latestPart) {
            return true
        } else if (currentPart > latestPart) {
            return false
        }
    }
    return false
}

fun showBreakDownDialog(fragmentManager: FragmentManager):BreakDownDialog {
    val breakDownDialog = BreakDownDialog()
    breakDownDialog.showDialog(fragmentManager)
    breakDownDialog.isCancelable = false
    return breakDownDialog
}
fun hideBreakDownDialog(breakDownDialog: BreakDownDialog?){
    if(breakDownDialog!=null){
        if(breakDownDialog.isVisible)
            breakDownDialog.dismiss()
    }
}


fun clientUniqueIDForBreakDown() {
    val x = "123456"
    val y = "123456"
    // example string
    val currentDate = LocalDateTime.now()
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))

    val regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")


    val  inspectionID = regexPattern.toString()
    Prefs.getInstance(App.instance).inspectionIDForBreakDown = inspectionID
    Log.e("resistrationvrnpatterhn", "clientUniqueID: " + inspectionID)
}