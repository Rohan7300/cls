package com.clebs.celerity.fragments.interior

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentWindScreenBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo

import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 * Use the [WindScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WindScreenFragment : Fragment() {

    lateinit var mbinding: FragmentWindScreenBinding
    private var isupload: Boolean = false
    private var isuploadtwo: Boolean = false
    private lateinit var viewModel: MainViewModel

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
        Log.e("vmvmvmv", "onCreateView: " + vm_id)

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.windScreenFragment)


        mbinding.icUp.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                runWithPermissions(
//                    android.Manifest.permission.CAMERA,
//                    android.Manifest.permission.READ_MEDIA_IMAGES,
//                    android.Manifest.permission.READ_MEDIA_VIDEO,
//                    android.Manifest.permission.READ_MEDIA_AUDIO
//
//                ) {
////                    showPictureDialog()
//                }
//            } else {
//                runWithPermissions(
//                    android.Manifest.permission.CAMERA,
//                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
//                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
//
//                ) {
//
////                    showPictureDialog()
//                }
//            }
        }

        mbinding.edtMil.setOnClickListener {

            if (isupload) {
                selectLayout1()
            }
            isupload = !isupload
        }
        mbinding.edtMilTwo.setOnClickListener {


            if (isuploadtwo) {
                mbinding.rlUploadDefect.visibility = View.GONE
                mbinding.tvNext.isEnabled = true
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
                selectLayout2()
            }
            isuploadtwo = !isuploadtwo


        }
        mbinding.run {
            edtDefect.doAfterTextChanged {

                mbinding.tvNext.isEnabled = (edtDefect.text?.length!! > 0)

                if (tvNext.isEnabled) {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                }

            }
            mbinding.tvNext.setOnClickListener {
                findNavController().navigate(R.id.windowsGlassFragment)
            }

        }
        mbinding.headings.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
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
        isupload = !isupload

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
        } else {

        }
        isuploadtwo = !isuploadtwo
    }




}