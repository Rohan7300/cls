package com.clebs.celerity_admin.ui.CollectionModule

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.DeleteCallback
import com.clebs.celerity_admin.adapters.RequestTypeListAdapter
import com.clebs.celerity_admin.databinding.ActivitySubmitCollectionBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponseItem
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import com.clebs.celerity_admin.ui.ImageCaptureActivity
import com.clebs.celerity_admin.ui.VanHireCollectionActivity
import com.clebs.celerity_admin.ui.VanHireReturnAgreementActivity
import com.clebs.celerity_admin.utils.DependencyClass
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.clientUniqueID
import com.clebs.celerity_admin.utils.observeOnce
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.viewModels.MainViewModel
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails

class CollectVehicleFromSupplier : AppCompatActivity(), DeleteCallback {
    private lateinit var binding: ActivitySubmitCollectionBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mainViewModel: MainViewModel
    private var crrVmId = 0
    private var crrMileage = 0
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
        if (DependencyClass.requestTypeList.size > 0) {
            DependencyClass.requestTypeList.clear()
        }
        window.statusBarColor = resources.getColor(R.color.commentbg, null)
        prefs = Prefs.getInstance(this)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        cqSDKInitializer = CQSDKInitializer(this)
        loadingDialog = LoadingDialog(this)
        clickListeners()
        mainViewModel.GetAllVehicleInspectionList()
        loadingDialog.show()

        listAdapter = RequestTypeListAdapter(this@CollectVehicleFromSupplier)
        //binding.layoutReturnVehicle.selectRequestTypeRV.adapter = listAdapter
        //binding.layoutReturnVehicle.selectRequestTypeRV.layoutManager = LinearLayoutManager(this)
        observers()
        updateCardLayout(-1)
    }

    private fun observers() {

        mainViewModel.mileageApiLiveData.observe(this) {
            if (it != null) {
                crrMileage = it.vehicleInfo.vehLastMillage
                binding.layoutSelectVehicleInformation.previousMileage.text = crrMileage.toString()
            }
        }
        mainViewModel.GetCompanyListing().observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                val companyNames = arrayListOf<String>()
                val companyIds = arrayListOf<Int>()
                it.map { company ->
                    if (company.name != null && company.id != null) {
                        companyNames.add(company.name)
                        companyIds.add(company.id)
                    }
                }
                setSpinner(
                    binding.layoutSelectVehicleOptions.spinnerSelectCompany,
                    companyNames,
                    companyIds
                )
            }
        }
        /*mainViewModel.VehicleInspectionListLiveData.observe(this) {
            if (it != null) {
                val vehicleNameList = arrayListOf<String>()
                val vehicleIdList = arrayListOf<Int>()
                val vehicleRegNoList = arrayListOf<String>()
                it.map { vehicleList ->
                    if (vehicleList.VehicleName != null && vehicleList.VehicleId != null && vehicleList.VehicleRegNo != null) {
                        vehicleNameList.add(vehicleList.VehicleRegNo)
                        vehicleIdList.add(vehicleList.VehicleId)
                        vehicleRegNoList.add(vehicleList.VehicleRegNo)
                    }
                }

                setSpinner(
                    binding.layoutSelectVehicleOptions.spinnerSelectVehicle,
                    vehicleNameList,
                    vehicleIdList,
                    vehicleRegNoList
                )
            }
        }*/
        mainViewModel.GetVehicleLocationListing().observe(this) {
            if (it != null) {
                val locationIds = arrayListOf<Int>()
                val locationNames = arrayListOf<String>()
                it.map { locations ->
                    if (locations.locId != null && locations.locationName != null) {
                        locationIds.add(locations.locId)
                        locationNames.add(locations.locationName)
                    }
                }
                setSpinner(
                    binding.layoutSelectVehicleInformation.spinnerSelectVehicleLocation,
                    locationNames,
                    locationIds
                )
            }
        }
        mainViewModel.GetVehiclefuelListing().observeOnce(this) {
            if (it != null) {
                val fuelIds = arrayListOf<Int>()
                val fuelTypes = arrayListOf<String>()
                it.map { fuel ->
                    if (fuel != null) {
                        if (fuel.vehFuelLevelId != null && fuel.vehFuelLevelName != null) {
                            fuelIds.add(fuel.vehFuelLevelId)
                            fuelTypes.add(fuel.vehFuelLevelName)
                        }
                    }
                }
                setSpinner(
                    binding.layoutSelectVehicleInformation.spinnerVehicleFuelLevel,
                    fuelTypes,
                    fuelIds
                )
            }
        }

        mainViewModel.GetVehicleOilListing().observe(this) {
            if (it != null) {
                val oilIds = arrayListOf<Int>()
                val oilNames = arrayListOf<String>()
                it.map { oils ->
                    if (oils.vehOilLevelId != null && oils.vehOilLevelName != null) {
                        oilIds.add(oils.vehOilLevelId)
                        oilNames.add(oils.vehOilLevelName)
                    }
                }
                setSpinner(
                    binding.layoutSelectVehicleInformation.spinnerVehicleOilLevel, oilNames, oilIds
                )
            }
        }  /*
        mainViewModel.GetVehicleDamageWorkingStatus()
        mainViewModel.VehicleDamageWorkingStatusLD.observe(this) {
            if (it != null) {
                val workingStatusIds = arrayListOf<Int>()
                val workingStatusNames = arrayListOf<String>()
                it.map { requestTypes ->
                    if (requestTypes.Id != null && !requestTypes.Name.isNullOrEmpty()) {
                        workingStatusIds.add(requestTypes.Id)
                        workingStatusNames.add(requestTypes.Name)
                    }
                }
                setSpinner(
                    binding.layoutReturnVehicle.spinnerRequestType,
                    workingStatusNames, workingStatusIds
                )
            }
        }
         mainViewModel.GetCurrentAllocatedDaLD.observe(this) {
                    loadingDialog.dismiss()
                    vehicleValid = false
                    if (it != null) {
                        if (it.VehicleInfo.AllowReturnSupplier != null) {
                            binding.layoutSelectVehicleOptions.errorText.text =
                                it.VehicleInfo.AllowReturnSupplier!!
                            updateCardLayout(3)
                        } else {

                        }
                    }
                }*/
    }

    private fun setSpinner(
        spinner: AutoCompleteTextView,
        items: List<String>,
        ids: List<Int>,
        regNos: List<String>? = listOf(),
        locationIds: List<Int>? = listOf(),
        locationNames: List<String>? = listOf()
    ) {
        val itemsList = mutableListOf<String>()
        Log.d("ID", "$ids")
        itemsList.addAll(items)
        val adapter = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.setAdapter(adapter)
        spinner.setOnItemClickListener { parent, _, position, _ ->
            run {
                parent?.let { nonNullParent ->

                    val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                    selectedItem.let {
                        when (spinner) {
                            binding.layoutSelectVehicleOptions.spinnerSelectCompany -> {
                                DependencyClass.selectedCompanyId = ids[position]
                                if (vehicleValid) updateCardLayout(4)
                            }

                            /*                   binding.layoutSelectVehicleOptions.spinnerSelectVehicle -> {
                                                   DependencyClass.selectedVehicleId = ids[position]
                                                   loadingDialog.show()
                                                   mainViewModel.GetCurrentAllocatedDa(
                                                       DependencyClass.selectedVehicleId.toString(), true
                                                   )
                                                   DependencyClass.selectedVehicleLocId = locationIds!![position]
                                                   DependencyClass.selectedVehicleLocationName = locationNames!![position]
                                                   binding.layoutSelectVehicleInformation.vehicleLocation.text =
                                                       locationNames[position]
                                                   crrRegNo = items[position]
                                                   if (!regNos.isNullOrEmpty())
                                                       DependencyClass.crrSelectedVehicleType = regNos[position]
                                               }*/

                                        binding.layoutSelectVehicleInformation.spinnerSelectVehicleLocation -> {
                                            //selectedVehicleLocId = ids[position]
                                            DependencyClass.collectionSelectedVehicleLocId = ids[position]
                                            card2Update()
                                        }

                            binding.layoutSelectVehicleInformation.spinnerVehicleFuelLevel -> {
                                DependencyClass.collectionSelectedVehicleFuelId = ids[position]
                                card2Update()
                            }

                            binding.layoutSelectVehicleInformation.spinnerVehicleOilLevel -> {
                                DependencyClass.collectionSelectedVehicleOilLevelListId = ids[position]
                                card2Update()
                            }

       /*                     binding.layoutReturnVehicle.spinnerRequestType -> {
                                DependencyClass.selectedRequestTypeId = ids[position]
                                DependencyClass.requestTypeList.add(
                                    GetVehicleDamageWorkingStatusResponseItem(
                                        ids[position],
                                        items[position]
                                    )
                                )
                                Log.d("RequestTypeList", "${DependencyClass.requestTypeList}")
                                listAdapter.saveData(DependencyClass.requestTypeList)
                                listAdapter.notifyItemInserted(listAdapter.itemCount)

                            }*/
                        }
                    }
                }
            }
        }
    }


    private fun card2Update() {
        if (DependencyClass.collectionSelectedVehicleLocId != -1 && DependencyClass.collectionSelectedVehicleFuelId != -1
            && DependencyClass.collectionSelectedVehicleOilLevelListId != -1) {
            updateCardLayout(6)
        }
    }

    private fun clickListeners() {
        binding.back.setOnClickListener {
            finish()
        }
        binding.layoutSelectVehicleOptions.headerSelectVehicleOptions.setOnClickListener {
            updateCardLayout(0)
        }
        binding.layoutSelectVehicleInformation.headerVehicleInformation.setOnClickListener {
            updateCardLayout(1)
        }
        binding.layoutAddImages.headerAddInspectionImages.setOnClickListener {
            updateCardLayout(5)
        }
        binding.layoutAddImages.addImagesBtn.setOnClickListener {
            startInspection()
        }
        binding.layoutAddImages.nextBtn.setOnClickListener {
            startActivity(Intent(this,VanHireCollectionActivity::class.java))
        }
        /*        binding.layoutSelectVehicleOptions.aTvVehicleRegNo.doOnTextChanged { text, start, before, count ->
                    //prefs.saveCurrentVehicleInfo(text)
                    vehicleValid = true
                    if (DependencyClass.selectedCompanyId != -1) {
                        updateCardLayout(4)
                    }
                }*/
        binding.layoutSelectVehicleOptions.addNewVehicleBtn.setOnClickListener {
            vehicleValid = true
            val regNo = binding.layoutSelectVehicleOptions.aTvVehicleRegNo.text.toString()
            loadingDialog.show()
            mainViewModel.GetVehicleIdOnCollectVehicleOption(regNo)
                .observe(this@CollectVehicleFromSupplier) {
                    loadingDialog.dismiss()
                    if (it != null) {
                        crrVmId = it.VehicleId
                    }
                    if (DependencyClass.selectedCompanyId != -1) {
                        updateCardLayout(4)
                    }
                    mainViewModel.GetVehicleLastMileageInfo(crrVmId.toString())
                }
        }
/*        binding.layoutSupplierImages.checkboxUploadAccidentImages.setOnClickListener {
            if(binding.layoutSupplierImages.checkboxUploadAccidentImages.isChecked){

            }
        }*/

/*        binding.layoutReturnVehicle.rbRoadWorthy.setOnClickListener {
            if (binding.layoutReturnVehicle.rbRoadWorthy.isChecked) {
                updateCardLayout(9)
                isRbRoadWorthySelected = true
                isRbNotRoadWorthy = false
            }
        }
        binding.layoutReturnVehicle.rbNotRoadWorthy.setOnClickListener {
            if (binding.layoutReturnVehicle.rbNotRoadWorthy.isChecked) {
                updateCardLayout(10)
                isRbNotRoadWorthy = true
                isRbRoadWorthySelected = false
            }
        }
        binding.layoutReturnVehicle.returnVehicleBtn.setOnClickListener {
            if (isRbRoadWorthySelected) {
                returnVehicle()
            } else if (isRbNotRoadWorthy) {
                if (DependencyClass.selectedRequestTypeId == -1) {
                    showToast("Please select request type first!!", this@CollectVehicleFromSupplier)
                } else {
                    returnVehicle()
                }
            } else {
                showToast("Please select road worthiness first!!", this@CollectVehicleFromSupplier)
            }
        }*/

        /*        binding.layoutAddImages.addSpareWheelImageBtn.setOnClickListener {
                    imageUploadLevel = 1
                    openCamera()
                }
                binding.layoutAddImages.addVehicleInteriorImageBtn.setOnClickListener {
                    imageUploadLevel = 2
                    openCamera()
                }
                binding.layoutAddImages.addLoadingInteriorImageBtn.setOnClickListener {
                    imageUploadLevel = 3
                    openCamera()
                }
                binding.layoutAddImages.addToolsPictureImageBtn.setOnClickListener {
                    imageUploadLevel = 4
                    openCamera()
                }
                binding.layoutAddImages.addVinNumberImageBtn.setOnClickListener {
                    imageUploadLevel = 5
                    openCamera()
                }*/
        binding.layoutAddImages.uploadImageBtn.setOnClickListener {
            openCamera()
        }
        binding.layoutSelectVehicleOptions.checkRegNo.setOnClickListener {
            val regNo = binding.layoutSelectVehicleOptions.aTvVehicleRegNo.text
            if (regNo.isBlank())
                showToast("Please add Vehicle RegNo first!!", this@CollectVehicleFromSupplier)
            else {
                loadingDialog.show()
                mainViewModel.GetExistingRegIds(regNo.toString())
                    .observe(this@CollectVehicleFromSupplier) {
                        binding.layoutSelectVehicleOptions.regNoResText.visibility = View.VISIBLE
                        loadingDialog.dismiss()
                        if (it != null) {
                            binding.layoutSelectVehicleOptions.regNoResText.text = it.Message
                        } else {
                            binding.layoutSelectVehicleOptions.regNoResText.text =
                                "No information available for this registration number."
                        }
                    }
            }
        }
    }

    private fun openCamera() {
        if (allPermissionsGranted()) {
            launchCamera()
        } else {
            requestPermissions()
        }
    }

    private fun launchCamera() {
        val imageTakerActivityIntent = Intent(this, ImageCaptureActivity::class.java)
        resultLauncher.launch(imageTakerActivityIntent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 10) {
                val data: Intent? = result.data
                val outputUri = data?.getStringExtra("outputUri")
                if (outputUri != null) {

                    when (imageUploadLevel) {
                        0 -> {
                            imageUploadLevel = 1
                            updateImageUi(imageUploadLevel)
                        }

                        1 -> {
                            imageUploadLevel = 2
                            updateImageUi(imageUploadLevel)
                        }

                        2 -> {
                            imageUploadLevel = 3
                            updateImageUi(imageUploadLevel)
                        }

                        3 -> {
                            imageUploadLevel = 4
                            updateImageUi(imageUploadLevel)
                        }

                        4 -> {
                            imageUploadLevel = 5
                            updateImageUi(imageUploadLevel)
                        }

                        else -> {
                            updateCardLayout(8)
                        }
                    }
                    /*    when(imageUploadLevel){

                            0->{
                              //  binding.layoutAddImages.addVinNumberImageBtn.icon = ContextCompat.getDrawable(this,R.drawable.baseline_check_24)
                            }
                            1->{
                                binding.layoutAddImages.addSpareWheelImageBtn.icon = ContextCompat.getDrawable(this,R.drawable.baseline_check_24)
                                binding.layoutAddImages.addSpareWheelImageBtn.iconTint = ContextCompat.getColorStateList(this,R.color.green)
                            }
                            2->{
                                binding.layoutAddImages.addVehicleInteriorImageBtn.icon = ContextCompat.getDrawable(this,R.drawable.baseline_check_24)
                                binding.layoutAddImages.addSpareWheelImageBtn.iconTint = ContextCompat.getColorStateList(this,R.color.green)
                            }
                            3->{
                                binding.layoutAddImages.addLoadingInteriorImageBtn.icon = ContextCompat.getDrawable(this,R.drawable.baseline_check_24)
                            }
                            4->{
                                binding.layoutAddImages.addToolsPictureImageBtn.icon = ContextCompat.getDrawable(this,R.drawable.baseline_check_24)
                            }
                            5->{
                                binding.layoutAddImages.addVinNumberImageBtn.icon = ContextCompat.getDrawable(this,R.drawable.baseline_check_24)
                            }else->{
                                showToast("Invalid Option",this@ReturnToDaActivity)
                            }
                        }*/
                } else {
                    showToast("Failed to fetch image!!", this)
                }
            } else {
                showToast("Failed!!", this)
            }
        }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", this)
            } else {
                launchCamera()
            }
        }

    private fun returnVehicle() {
        if (!binding.layoutSelectVehicleInformation.atvAddBlueMileage.text.isNullOrEmpty())
            DependencyClass.addBlueMileage =
                binding.layoutSelectVehicleInformation.atvAddBlueMileage.text.toString()
        else {
            showToast("Please add current Add Blue Mileage", this)
            return
        }
        if (!binding.layoutSelectVehicleInformation.atvVehicleCurrentMileage.text.isNullOrEmpty())
            DependencyClass.crrMileage =
                binding.layoutSelectVehicleInformation.atvVehicleCurrentMileage.text.toString()
                    .toInt()
        else {
            showToast("Please add current Mileage", this)
            return
        }
        startActivity(Intent(this, VanHireCollectionActivity::class.java))
    }

    private fun updateCardLayout(cardToShow: Int) {
        when (cardToShow) {
            -1 -> {
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = false
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )

                binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
                binding.layoutAddImages.headerAddInspectionImages.isClickable = false
                binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )

              /*  binding.layoutSupplierImages.body.isVisible = false
                binding.layoutSupplierImages.headerReturnVehicle.isClickable = false
                binding.layoutSupplierImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )*/
            }

            0 -> {
                if (binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible) {
                    binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = false
                    binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropdown)
                    )
                } else {
                    binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = true
                    binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropup)
                    )
                }
            }

            1 -> {
                if (binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible) {
                    binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                    binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropdown)
                    )
                } else {
                    binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = true
                    binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropup)
                    )
                }
            }

            3 -> {
                binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = true
                binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropup)
                )
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = false
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutAddImages.headerAddInspectionImages.isClickable = false
                binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
            }

            4 -> {
                binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = false
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = true
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropup)
                )
                binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                if (DependencyClass.selectedVehicleLocId != -1 && DependencyClass.selectedVehicleFuelId != -1 && DependencyClass.selectedVehicleOilLevelListId != -1) {
                    binding.layoutAddImages.headerAddInspectionImages.isClickable = true
                    binding.layoutAddImages.bodyAddInspectionImages.isVisible = true
                } else {
                    binding.layoutAddImages.headerAddInspectionImages.isClickable = false
                    binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
                }
            }

            5 -> {
                if (binding.layoutAddImages.bodyAddInspectionImages.isVisible) {
                    binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
                    binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropdown)
                    )
                } else {
                    binding.layoutAddImages.bodyAddInspectionImages.isVisible = true
                    binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropup)
                    )
                }
            }

            6 -> {
                binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = false
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )

                binding.layoutAddImages.bodyAddInspectionImages.isVisible = true
                binding.layoutAddImages.headerAddInspectionImages.isClickable = true
            }

/*            7 -> {
                if (binding.layoutSupplierImages.body.isVisible) {

                    binding.layoutSupplierImages.body.isVisible = false
                    binding.layoutSupplierImages.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropdown)
                    )
                } else {
                    binding.layoutSupplierImages.body.isVisible = true
                    binding.layoutSupplierImages.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropup)
                    )
                }
            }*/

            8 -> {
                binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = false
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )

                binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
                binding.layoutAddImages.headerAddInspectionImages.isClickable = true
                binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutAddImages.nextBtn.visibility = View.VISIBLE
  /*              binding.layoutSupplierImages.headerReturnVehicle.isClickable = true
                binding.layoutSupplierImages.body.isVisible = true
                binding.layoutSupplierImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropup)
                )*/
            }

            9 -> {
                updateCardLayout(8)
               /* binding.layoutSupplierImages.tilSpinnerRequestType.visibility = View.GONE
                binding.layoutSupplierImages.selectRequestTypeRV.visibility = View.GONE*/
              /*  binding.layoutSupplierImages.returnVehicleBtn.visibility = View.VISIBLE*/
            }

            10 -> {
                updateCardLayout(8)/*
                binding.layoutSupplierImages.tilSpinnerRequestType.visibility = View.VISIBLE
                binding.layoutSupplierImages.selectRequestTypeRV.visibility = View.VISIBLE*/
             /*   binding.layoutSupplierImages.returnVehicleBtn.visibility = View.VISIBLE
                binding.layoutSupplierImages.accidentImageSection.visibility = View.VISIBLE*/
            }
        }
    }

    companion object {
        const val TAG = "ReturnToDaActivity"
    }

    private fun startInspection() {
        cqOpened = true
        if (crrRegNo.isNotBlank()) {
            clientUniqueID()
            if (cqSDKInitializer.isCQSDKInitialized()) {
                try {

                    loadingDialog.show()
                    cqSDKInitializer.startInspection(activity = this, clientAttrs = ClientAttrs(
                        userName = " ",
                        dealer = " ",
                        dealerIdentifier = " ",
                        client_unique_id = App.prefs!!.vehinspectionUniqueID

                        //drivers ID +vechile iD + TOdays date dd// mm //yy::tt,mm
                    ), inputDetails = InputDetails(
                        vehicleDetails = VehicleDetails(
                            regNumber = crrRegNo.replace(
                                " ", ""
                            ), //if sent, user can't edit
                            make = "Van", //if sent, user can't edit
                            model = "Any Model", //if sent, user can't edit
                            bodyStyle = "Van"  // if sent, user can't edit - Van, Boxvan, Sedan, SUV, Hatch, Pickup [case sensitive]
                        ), customerDetails = CustomerDetails(
                            name = "", //if sent, user can't edit
                            email = "", //if sent, user can't edit
                            dialCode = "", //if sent, user can't edit
                            phoneNumber = "", //if sent, user can't edit
                        )
                    ), userFlowParams = UserFlowParams(
                        isOffline = Prefs.getInstance(App.instance).returnInspectionFirstTime!!, // true, Offline quote will be created | false, online quote will be created | null, online

                        skipInputPage = true, // true, Inspection will be started with camera page | false, Inspection will be started

                    ),

                        result = { isStarted, msg, code ->

                            Log.e(TAG, "startInspection: $msg $code")
                            if (isStarted) {
                                Prefs.getInstance(App.instance).returnInspectionFirstTime = false
                                Log.d(TAG, "isStarted $msg")
                            } else {
                                loadingDialog.dismiss()
                                if (msg == "Online quote can not be created without internet") {
                                    showToast(
                                        "Please Turn on the internet",
                                        this@CollectVehicleFromSupplier
                                    )
                                    Log.d(TAG, "CQ: Not isStarted1  $msg")
                                } else if (msg == "Sufficient data not available to create an offline quote") {
                                    Prefs.getInstance(App.instance).returnInspectionFirstTime = true
                                    showToast(
                                        "Please Turn on the internet & grant required permissions.",
                                        this@CollectVehicleFromSupplier
                                    )
                                    Log.d(TAG, "CQSDKXX : Not isStarted2  $msg")
                                } else if (msg == "Unable to download setting updates, Please check internet") {
                                    showToast(
                                        "Please Turn on the internet",
                                        this@CollectVehicleFromSupplier
                                    )
                                    Log.d(TAG, "CQSDKXX : Not isStarted3  $msg")
                                } else if (msg == "Vehicle not in fleet list") {
                                    showToast(
                                        "Vehicle not in fleet list", this@CollectVehicleFromSupplier
                                    )
                                    Log.d(TAG, "CQSDKXX : Not isStarted4  $msg")
                                }
                                Log.d(TAG, "CQSDKXX : Not isStarted5 $msg")
                            }
                            if (msg == "Success") {
                                Log.d(TAG, "CQSDKXX : Success $msg")
                            } else {

                                Log.d(TAG, "CQSDKXX : Not Success $msg")
                            }
                            if (!isStarted) {
                                Log.e(TAG, "started inspection : onCreateView: $msg $isStarted")
                            }
                        })
                } catch (_: Exception) {
                    loadingDialog.dismiss()
                }
            }
        } else {
            Toast.makeText(this, "Vehicle RegNo not found!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadingDialog.dismiss()
        if (cqOpened) {
            updateCardLayout(8)
            //cqOpened = false
        }
    }

    /*    override fun onDelete(item: GetVehicleDamageWorkingStatusResponseItem, position: Int) {
            DependencyClass.requestTypeList.remove(item)
            listAdapter.notifyItemRemoved(position)
            listAdapter.saveData(DependencyClass.requestTypeList)
        }*/
    private fun updateImageUi(imageStage: Int) {
        val drawable =
            ContextCompat.getDrawable(this@CollectVehicleFromSupplier, R.drawable.warning)
        val yesDrawable =
            ContextCompat.getDrawable(this@CollectVehicleFromSupplier, R.drawable.ic_yes3)

        binding.layoutAddImages.run {
            listOf(
                indicatorLoadingInterior,
                indicatorSpareWheel,
                indicatorToolsPicture,
                indicatorVinNumber,
                indicatorVehicleInterior
            ).onEach {
                it.setImageDrawable(
                    drawable
                )
            }
            when (imageStage) {
                1 -> {
                    indicatorSpareWheel.setImageDrawable(
                        yesDrawable
                    )
                }

                2 -> {
                    listOf(
                        indicatorSpareWheel, indicatorVehicleInterior
                    ).onEach { it.setImageDrawable(yesDrawable) }
                }

                3 -> {
                    listOf(
                        indicatorSpareWheel, indicatorVehicleInterior, indicatorLoadingInterior
                    ).onEach { it.setImageDrawable(yesDrawable) }
                }

                4 -> {
                    listOf(
                        indicatorSpareWheel,
                        indicatorVehicleInterior,
                        indicatorLoadingInterior,
                        indicatorToolsPicture
                    ).onEach { it.setImageDrawable(yesDrawable) }
                }

                5 -> {
                    listOf(
                        indicatorSpareWheel,
                        indicatorVehicleInterior,
                        indicatorLoadingInterior,
                        indicatorToolsPicture,
                        indicatorVinNumber
                    ).onEach { it.setImageDrawable(yesDrawable) }
                    updateCardLayout(8)
                }

                else -> {

                }
            }
        }

    }

    override fun onDelete(item: GetVehicleDamageWorkingStatusResponseItem, position: Int) {
        DependencyClass.requestTypeList.remove(item)
        listAdapter.notifyItemRemoved(position)
        listAdapter.saveData(DependencyClass.requestTypeList)
    }
}