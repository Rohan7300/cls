package com.clebs.celerity.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavController
import com.clebs.celerity.database.ImageEntity
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


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

   /* val TAG = "DB_TEST"
    Log.d(TAG, "\n\n-+-----------------------------------------------------------------+-\n\n")
    Log.d(TAG, it.toString())
    Log.d(TAG, "\n\n-+-----------------------------------------------------------------+-\n\n")
*/
}

fun setImageView(im: ImageView, value: String) {
    try {
        val bitmap: Bitmap = decodeBase64Image(value)
        im.setImageBitmap(bitmap)
    } catch (_: Exception) {
    }
}

fun navigateTo(fragmentId: Int,context: Context,navController: NavController) {
    val prefs = Prefs.getInstance(context)
    val fragmentStack = prefs.getNavigationHistory()
    fragmentStack.push(fragmentId)
    navController.navigate(fragmentId)
    prefs.saveNavigationHistory(fragmentStack)
}
fun showToast(msg:String,context: Context){
    try {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
    }catch (_:Exception){

    }
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

fun showTimePickerDialog(context: Context, editText: EditText) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
            editText.setText(time)
        },
        hour,
        minute,
        true
    )

    timePickerDialog.show()
}

