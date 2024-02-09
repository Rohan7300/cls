package com.clebs.celerity.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentDailyWorkBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.ui.HomeActivity.Companion.showLog
import com.clebs.celerity.utils.Prefs
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * A simple [Fragment] subclass.
 * Use the [DailyWorkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyWorkFragment : Fragment() {
    lateinit var mbinding: FragmentDailyWorkBinding
    lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentDailyWorkBinding.inflate(inflater, container, false)
        }
        HomeActivity.Boolean = true
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)
        mainViewModel.setLastVisitedScreenId(requireContext(), R.id.dailyWorkFragment)





        mbinding.rectangle4.setOnClickListener {


            getVichleinformation()
        }

        return mbinding.root
    }






    override fun onDestroy() {
        super.onDestroy()

    }

    fun getVichleinformation() {
        mainViewModel.getVichelinformationResponse(196991.toDouble(), 0.toDouble(), "YE23MUU")
            .observe(requireActivity(),
                Observer {
                    if (it != null) {
                        Prefs.getInstance(App.instance)
                            .save("vehicleLastMillage", it.vehicleLastMillage.toString())
                        showLog(
                            "TAG------->",
                            "mymileage" + it.vehicleLastMillage.toString() + Prefs.getInstance(App.instance)
                                .get("vehicleLastMillage")
                        )
                        showAlert()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Something went wrong",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                })


    }

    fun showAlert() {
        val factory = LayoutInflater.from(requireActivity())
        val view: View = factory.inflate(R.layout.acknowledgement, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        val checkBox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        val image: ImageView = view.findViewById(R.id.img_Acknowledege)
        image.setOnClickListener {

            if (checkBox.isChecked) {
                findNavController().navigate(R.id.vechileMileageFragment)
                deleteDialog.dismiss()

            } else {
                Toast.makeText(
                    requireActivity(),
                    "Please check the acknowledgment check",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        deleteDialog.setView(view)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }


}