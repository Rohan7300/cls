package com.clebs.celerity.fragments.interior

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.ImageViewModel
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImageEntity
import com.clebs.celerity.databinding.FragmentWindScreenBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.DBImages
import com.clebs.celerity.utils.DBNames
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertBitmapToBase64
import com.clebs.celerity.utils.setImageView
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions


class WindScreenFragment : Fragment() {

    lateinit var mbinding: FragmentWindScreenBinding
    private var isupload: Boolean = false
    private var isuploadtwo: Boolean = false
    private lateinit var viewModel: MainViewModel
    private val CAMERA_REQUEST_CODE = 101
    var base64: String? = null
    var defectName: String? = null
    lateinit var imageViewModel: ImageViewModel
    var imageEntity = ImageEntity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentWindScreenBinding.inflate(inflater, container, false)
        }
        val vm_id = arguments?.get("vm_mileage")
        Log.e("vmvmvmv", "onCreateView: $vm_id")

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.windScreenFragment)

        imageViewModel = (activity as HomeActivity).imageViewModel
        imageEntity = imageViewModel.images.value!!
        imageViewModel.images.value.let {
            if (it != null) {
                setImageView(mbinding.windScreenIV, it.inWindScreen.toString())
                if (it.dfNameWindScreen!!.isNotEmpty() && it.dfNameWindScreen != "f") {
                    mbinding.edtDefect.setText(it.dfNameWindScreen.toString())
                }
            }
        }



        Log.d("Check ", imageViewModel.images.toString())

        mbinding.edtMil.setOnClickListener {
            isuploadtwo = false
            isupload = !isupload
            selectLayout1()
        }

        mbinding.windScreenIV.setOnClickListener {
            pictureDialogBase64()
        }


        mbinding.edtMilTwo.setOnClickListener {
            isupload = false
            isuploadtwo = !isuploadtwo
            if (isuploadtwo) {
                mbinding.rlUploadDefect.visibility = View.GONE
                mbinding.tvNext.isEnabled = true
                tvNextColorUpdate()
                selectLayout2()
            }
        }

        mbinding.run {
            edtDefect.doAfterTextChanged {
                mbinding.tvNext.isEnabled = (edtDefect.text?.length!! > 0)
                if (edtDefect.text?.length!! > 0)
                    defectName = edtDefect.text?.toString()
                if (tvNext.isEnabled) {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                }
            }
            mbinding.tvNext.setOnClickListener {
                if (isupload) {
                    if (!base64.isNullOrEmpty()) {
                        imageEntity.inWindScreen = base64
                        imageViewModel.insertImage(imageEntity)
                    }
                    if (defectName!!.toString().isNotEmpty()) {
                        imageEntity.dfNameWindScreen = defectName!!.toString()
                        imageViewModel.insertDefectName(imageEntity)
                    }
                }
                else{
                    imageEntity.inWindScreen = "empty"
                    imageViewModel.insertImage(imageEntity)
                    imageEntity.dfNameWindScreen = "f"
                    imageViewModel.insertDefectName(imageEntity)
                }
                navigateTo(R.id.windowsGlassFragment)
                //findNavController().navigate(R.id.windowsGlassFragment)
            }
        }
        mbinding.headings.setOnClickListener {
            //findNavController().navigate(R.id.profileFragment)
            navigateTo(R.id.profileFragment)
        }
        return mbinding.root
    }

    private fun selectLayout1() {
        if (isupload) {
            mbinding.rlUploadDefect.visibility = View.VISIBLE
            mbinding.edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_filled_textview
                )
            )
            mbinding.imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_circle_orange
                )
            )

            mbinding.edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_textview_button
                )
            )
            mbinding.imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_circle_white
                )
            )
        } else {
            mbinding.rlUploadDefect.visibility = View.GONE
        }
    }

    private fun selectLayout2() {
        if (isuploadtwo) {
            mbinding.rlUploadDefect.visibility = View.GONE
            mbinding.edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_filled_textview
                )
            )
            mbinding.imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_circle_orange
                )
            )

            mbinding.edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_textview_button
                )
            )
            mbinding.imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_circle_white
                )
            )
        }
    }

    fun pictureDialogBase64() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO

            ) {
                showPictureDialog()

            }
        } else {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE

            ) {
                showPictureDialog()
            }
        }
    }

    fun showPictureDialog() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            base64 = convertBitmapToBase64(imageBitmap)
            setImageView(mbinding.windScreenIV, base64.toString())
            Log.d("WindScreenFragment", "Base64 : ${base64!!.take(50)}")
        }
    }

    private fun tvNextColorUpdate() {
        if (mbinding.tvNext.isEnabled) {
            mbinding.tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

        } else {
            mbinding.tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orange
                )
            )
        }
    }

    fun navigateTo(fragmentId: Int) {
        var prefs = Prefs.getInstance(requireContext())
        val fragmentStack = prefs.getNavigationHistory()
        fragmentStack.push(fragmentId)
        findNavController().navigate(fragmentId)
        prefs.saveNavigationHistory(fragmentStack)
    }


}