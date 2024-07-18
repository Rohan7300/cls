package com.clebs.celerity_admin.utils


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
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
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


fun <T> LiveData<T>.observeOnce(
    lifecycleOwner: LifecycleOwner,
    observer: androidx.lifecycle.Observer<T>
) {
    observe(lifecycleOwner, object : androidx.lifecycle.Observer<T> {


        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}


open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */

//
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

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

fun setupFullHeight(bottomSheetDialog: BottomSheetDialog, context: Context) {
    val bottomSheet =
        bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
    val behavior = BottomSheetBehavior.from(bottomSheet as View)
    val layoutParams = bottomSheet.layoutParams

    val windowHeight: Int = getWindowHeight(context)
    if (layoutParams != null) {
        layoutParams.height = windowHeight
    }
    bottomSheet.layoutParams = layoutParams
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    behavior.isDraggable = false
}

fun setupHalfHeight(bottomSheetDialog: BottomSheetDialog, context: Context) {
    val bottomSheet =
        bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
    val behavior = BottomSheetBehavior.from(bottomSheet as View)
    val layoutParams = bottomSheet.layoutParams

    val windowHeight: Int = getWindowHeight(context)
    if (layoutParams != null) {
        layoutParams.height = windowHeight
    }
    behavior.peekHeight = 500 // Set the desired maximum height in pixels

// Set the bottom sheet to be expanded by default
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    bottomSheet.layoutParams = layoutParams

    behavior.isDraggable = true
    behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    // Bottom sheet is expanded, adjust the height as needed
                    behavior.peekHeight = 800 // Set the desired maximum height in pixels
                }

                BottomSheetBehavior.STATE_COLLAPSED -> {
                    // Bottom sheet is collapsed, adjust the height as needed
                    behavior.peekHeight = 400 // Set the desired minimum height in pixels
                }
                // Handle other state changes as needed
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Handle bottom sheet sliding
        }
    })
}

fun setupHalfHeightForlisting(bottomSheetDialog: BottomSheetDialog, context: Context) {
    val bottomSheet =
        bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
    val behavior = BottomSheetBehavior.from(bottomSheet as View)
    val layoutParams = bottomSheet.layoutParams

    val windowHeight: Int = getWindowHeight(context)
    if (layoutParams != null) {
        layoutParams.height = windowHeight
    }
    behavior.peekHeight = 500 // Set the desired maximum height in pixels

// Set the bottom sheet to be expanded by default
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    bottomSheet.layoutParams = layoutParams

    behavior.isDraggable = false
    behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    // Bottom sheet is expanded, adjust the height as needed
                    behavior.peekHeight = 800 // Set the desired maximum height in pixels
                }

                BottomSheetBehavior.STATE_COLLAPSED -> {
                    // Bottom sheet is collapsed, adjust the height as needed
                    behavior.peekHeight = 400 // Set the desired minimum height in pixels
                }
                // Handle other state changes as needed
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Handle bottom sheet sliding
        }
    })
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

fun showToast(msg: String, context: Context) {
    try {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.d("ToastException", e.message.toString())
    }
}

fun getMimeType(uri: Uri): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
}

fun radioButtonState(rbFull: RadioButton, rbBelow: RadioButton): Int {
    if (rbFull.isChecked)
        return 1
    else if (rbBelow.isChecked)
        return 2
    else
        return 0
}

fun getRadioButtonState(rbState: Int): Boolean {
    return rbState == 1
}


fun uriToFileName(uriString: String): String {
    return Uri.parse(uriString).lastPathSegment ?: "Unknown"
}

fun dateToday():String{
    return SimpleDateFormat("yyyy-MM-dd").format(Date())
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

fun Bitmap.toRequestBody(): okhttp3.RequestBody {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
}

fun getFilePathFromURI(context: Context?, contentUri: Uri?): String? {
    //copy file and send new file path
    val wallpaperDirectory: File = File(
        Environment.getExternalStorageDirectory().parent!!
    )
    // have the object build the directory structure, if needed.
    if (!wallpaperDirectory.exists()) {
        wallpaperDirectory.mkdirs()
    }
    val copyFile = File(
        wallpaperDirectory.toString() + File.separator + Calendar.getInstance()
            .timeInMillis + ".mp4"
    )
    // create folder if not exists

    return copyFile.absolutePath
}



