package com.clebs.celerity.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.ImageViewModel
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImageEntity
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertBitmapToBase64
import com.clebs.celerity.utils.setImageView
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import java.util.Stack

abstract class BaseInteriorFragment : Fragment() {
    lateinit var viewModel: MainViewModel
    lateinit var imageViewModel: ImageViewModel
    private val CAMERA_REQUEST_CODE = 101
    var base64: String? = null
    private lateinit var imageView: ImageView
    private var nextEnabled: Boolean = false
    var functionalView: Boolean = false
    var defectView: Boolean = false
    var defectName: String? = null
    var imageEntity = ImageEntity()
    lateinit var prefs: Prefs
    lateinit var fragmentStack: Stack<Int>
    lateinit var dxLoc: TextView
    lateinit var dxReg: TextView
    lateinit var dxm5: TextView
    lateinit var ana_carolin: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HomeActivity).viewModel
        imageViewModel = (activity as HomeActivity).imageViewModel
        dxLoc = view.findViewById(R.id.dxLoc)
        dxReg = view.findViewById(R.id.dxReg)
        dxm5 = view.findViewById(R.id.dxm5)
        ana_carolin = view.findViewById(R.id.ana_carolin)

        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
            dxLoc.text = it?.locationName ?: ""
            dxReg.text = it?.vmRegNo ?: ""
            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}"
                .also { name -> ana_carolin.text = name }
            dxm5.text = (activity as HomeActivity).date
        }


        prefs = Prefs.getInstance(requireContext())
        fragmentStack = prefs.getNavigationHistory()
    }

    abstract fun clickListeners()
    abstract fun saveNnext()
    private fun saveNnextVisibility(tvNext: TextView, nextEnabled: Boolean) {
        if (nextEnabled) {
            tvNext.isEnabled = true
            //tvNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
            tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

        } else {
            tvNext.isEnabled = false
            //tvNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orange
                )
            )
        }
    }

    protected fun pictureDialogBase64(iv: ImageView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO

            ) {
                showPictureDialog(iv)

            }
        } else {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE

            ) {
                showPictureDialog(iv)
            }
        }
    }

    private fun showPictureDialog(iv: ImageView) {
        imageView = iv
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            base64 = convertBitmapToBase64(imageBitmap)
            setImageView(imageView, base64.toString())
        }
    }

    fun editMil1Visibilty(
        tvNext: TextView,
        rlUploadDefect: RelativeLayout,
        edtMil: TextView,
        edtMilTwo: TextView,
        imageRadioTwo: ImageButton,
        imageRadio: ImageButton
    ) {
        functionalView = false
        defectView = !defectView
        if (defectView) {
            rlUploadDefect.visibility = View.VISIBLE
            edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_filled_textview
                )
            )
            imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_orange
                )
            )
            edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )
        } else {
            rlUploadDefect.visibility = View.GONE
            edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )
        }
    }


    fun editMil2Visibilty(
        tvNext: TextView,
        rlUploadDefect: RelativeLayout,
        edtMil: TextView,
        edtMilTwo: TextView,
        imageRadioTwo: ImageButton,
        imageRadio: ImageButton
    ) {
        defectView = false
        functionalView = !functionalView
        if (functionalView) {

            nextEnabled = true
            saveNnextVisibility(tvNext, nextEnabled)

            rlUploadDefect.visibility = View.VISIBLE
            edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_filled_textview
                )
            )
            imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_orange
                )
            )
            rlUploadDefect.visibility = View.GONE
            edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )

        } else {
            nextEnabled = false
            saveNnextVisibility(tvNext, nextEnabled)
            rlUploadDefect.visibility = View.GONE
            edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )
        }
    }

    fun editMil2VisibilityNew(
        rlUploadDefect: RelativeLayout,
        edtMil: TextView,
        edtMilTwo: TextView,
        imageRadioTwo: ImageButton,
        imageRadio: ImageButton
    ) {
        defectView = false
        rlUploadDefect.visibility = View.VISIBLE
        edtMilTwo.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.shape_filled_textview
            )
        )
        imageRadioTwo.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.round_circle_orange
            )
        )
        rlUploadDefect.visibility = View.GONE
        edtMil.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.shape_textview_button
            )
        )
        imageRadio.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.round_circle_white
            )
        )
    }

    fun doAfterTextChanged(tvNext: TextView, edtDefect: EditText) {
        tvNext.isEnabled = (edtDefect.text?.length!! > 0)
        if (edtDefect.text?.length!! > 0)
            defectName = edtDefect.text?.toString()
        if (tvNext.isEnabled) {
            saveNnextVisibility(tvNext, true)
        } else {
            saveNnextVisibility(tvNext, false)
        }
    }

    fun navigateTo(fragmentId: Int) {
        fragmentStack.push(fragmentId)
        findNavController().navigate(fragmentId)
        prefs.saveNavigationHistory(fragmentStack)
    }

}