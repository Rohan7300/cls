package com.clebs.celerity_admin.ui

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.RequestTypeListAdapter
import com.clebs.celerity_admin.databinding.ActivitySubmitCollectionBinding
import com.clebs.celerity_admin.databinding.CollectVehicleOptionsBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.viewModels.MainViewModel
import io.clearquote.assessment.cq_sdk.CQSDKInitializer

class CollectVehicleFromSupplier : AppCompatActivity() {
    private lateinit var binding:ActivitySubmitCollectionBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mainViewModel: MainViewModel

    lateinit var prefs: Prefs
    private var isRbRoadWorthySelected: Boolean = false
    private var isRbNotRoadWorthy: Boolean = false
    private var vehicleValid: Boolean = false
    var imageUploadLevel: Int = 0
    lateinit var listAdapter: RequestTypeListAdapter

    private val REQUIRED_PERMISSIONS =
        mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }.toTypedArray()
    private var crrRegNo: String = ""
    private var cqOpened = false
    private lateinit var cqSDKInitializer: CQSDKInitializer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmitCollectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        if(DependencyClass.requestTypeList.size>0){
            DependencyClass.requestTypeList.clear()
        }
        window.statusBarColor = resources.getColor(R.color.commentbg, null)
        prefs = Prefs.getInstance(this)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        cqSDKInitializer = CQSDKInitializer(this)
        loadingDialog = LoadingDialog(this)
        //clickListeners()
        //mainViewModel.GetAllVehicleInspectionList()
        loadingDialog.show()
        mainViewModel.GetReturnVehicleList()
        /*listAdapter = RequestTypeListAdapter(this@CollectVehicleFromSupplier)
        binding.layoutReturnVehicle.selectRequestTypeRV.adapter = listAdapter*/
        binding.layoutReturnVehicle.selectRequestTypeRV.layoutManager = LinearLayoutManager(this)
        /*observers()
        updateCardLayout(-1)*/
    }
}