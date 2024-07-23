package com.clebs.celerity.fragments.exterior

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentSpareWheelBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.fragments.BaseInteriorFragment
import com.clebs.celerity.models.requests.SaveVechileDefectSheetRequest
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.dialogs.NoInternetDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.getVRegNo
import com.clebs.celerity.utils.setImageView
import com.clebs.celerity.utils.showErrorDialog
import com.clebs.celerity.utils.startUploadWithWorkManager

class SpareWheelFragment : BaseInteriorFragment() {

    private lateinit var mBinding: FragmentSpareWheelBinding
    private var VdhDaId = 0
    private var VdhVmId = 0
    private var VdhLmId = 0
    var isNetworkActive: Boolean = true
    private var secondTry = false
    private var VdhOdoMeterReading = 0
    lateinit var loadingDialog: LoadingDialog
    lateinit var internetDialog: NoInternetDialog
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
    private lateinit var fragmentManager: FragmentManager
    var isDefected = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::mBinding.isInitialized) mBinding =
            FragmentSpareWheelBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentManager = (activity as HomeActivity).fragmentManager
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.spareWheelFragment)
        loadingDialog = (activity as HomeActivity).loadingDialog
        loadingDialog.dismiss()
        mBinding.tvNext.visibility = View.GONE
        clickListeners()
        internetDialog = (activity as HomeActivity).internetDialog
        val networkManager = (activity as HomeActivity).networkManager
        networkManager.observe(viewLifecycleOwner) {
            if (it) {
                isNetworkActive = true
                internetDialog.hideDialog()
            } else {
                isNetworkActive = false
                internetDialog.showDialog(fragmentManager)
            }
        }
        setDefault(mBinding.imageUploadIV, mBinding.edtDefect)
    }

    override fun clickListeners() {
        mBinding.run {
            edtMil.setOnClickListener {
                editMil1Visibilty(
                    mBinding.tvNext,
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )
            }
            imageRadio.setOnClickListener {
                editMil1Visibilty(
                    mBinding.tvNext,
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )
            }

            edtMilTwo.setOnClickListener {
                editMil2VisibilityNew(
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )
                functionalView = true
                defectView = false
                saveNnext()
            }

            imageRadioTwo.setOnClickListener {
                editMil2VisibilityNew(
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )
                functionalView = true
                defectView = false
                saveNnext()
            }

            imageUploadIV.setOnClickListener {
                pictureDialogBase64(imageUploadIV)
            }
            edtDefect.doAfterTextChanged {
                doAfterTextChanged(mBinding.tvNext, mBinding.edtDefect)
            }
            tvNext.setOnClickListener {
                saveNnext()
            }
        }
    }

    fun setDefault(defaultIv: ImageView, edtDefect: EditText) {
        imageViewModel.images.value.let {
            if (it != null) {
                imageEntity = imageViewModel.images.value!!
                setImageView(defaultIv, it.exSpareWheel.toString(), requireContext())
                if (it.dfNameSpareWheel!!.isNotEmpty() && it.dfNameSpareWheel != "f") {
                    edtDefect.setText(it.dfNameSpareWheel.toString())
                }
            }
        }
    }

    override fun saveNnext() {
        var userId = Prefs.getInstance(requireContext()).clebUserId.toInt()
        if (isNetworkActive) {
            showDialog()
            if (defectView) {
                if (base64 != null) {
                    imageEntity.exSpareWheel = base64
                    imageViewModel.insertImage(imageEntity)
                }
                if (defectName!!.toString().isNotEmpty()) {
                    imageEntity.dfNameSpareWheel = defectName!!.toString()
                    imageViewModel.insertDefectName(imageEntity)
                }
            } else if (functionalView) {
                imageEntity.exSpareWheel = "empty"
                imageViewModel.insertImage(imageEntity)
                imageEntity.dfNameSpareWheel = "f"
                imageViewModel.insertDefectName(imageEntity)
            }
            var isApiCallInProgress = false
            VdhDaId = userId
            viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
                hideDialog()
                if (it != null) {
                    if (!isApiCallInProgress) {
                        isApiCallInProgress = true
                        VdhVmId = it.vmId
                        if (it.vmId != 0)
                            Prefs.getInstance(requireContext()).vmId = it.vmId
                        if (it.vmId != null && Prefs.getInstance(App.instance).vmId == 0)
                            Prefs.getInstance(App.instance).vmId = it.vmId.toString().toInt()
                        Prefs.getInstance(App.instance).save("lm", it.vmLocId.toString())
                        VdhLmId = it.vmLocId
                        VdhOdoMeterReading =
                            Prefs.getInstance(requireContext()).vehicleLastMileage
                                ?: it.vehicleLastMillage

                        prefs.saveLocationID(it.vmLocId)

                        val request = SaveVechileDefectSheetRequest(
                            weekNo = 0,
                            yearNo = 0,
                            vdhVmId = VdhVmId,
                            vdhLmId = VdhLmId,
                            VdhDate = getCurrentDateTime(),
                            vdhDaId = userId,
                            vdhOdoMeterReading = VdhOdoMeterReading,
                            vdhComments = "TEST",
                            vdhPocIsaction = true,
                            vdhBrakes = dOrf(imageEntity.dfNameBrakedEbsAbs),
                            vdhBrakesComment = imageEntity.dfNameBrakedEbsAbs!!,
                            vdhCabSecurityInterior = dOrf(imageEntity.dfNameCabSecurityInterior),
                            vdhCabSecurityInteriorComment = imageEntity.dfNameCabSecurityInterior!!,
                            vdhFuelAdBlueLevel = dOrf(imageEntity.dfNameFuelAdBlueLevel),
                            vdhFuelAdBlueLevelComment = imageEntity.dfNameFuelAdBlueLevel!!,
                            vdhExcessiveEngineExhaustSmoke = dOrf(imageEntity.dfNameExcessiveEngExhaustSmoke),
                            vdhExcessiveEngineExhaustSmokeComment = imageEntity.dfNameExcessiveEngExhaustSmoke!!,
                            vdhIndicatorsSideRepeaters = dOrf(imageEntity.dfNameIndicatorsSideRepeaters),
                            vdhIndicatorsSideRepeatersComment = imageEntity.dfNameIndicatorsSideRepeaters!!,
                            vdhHornReverseBeeper = dOrf(imageEntity.dfNameHornReverseBeeper),
                            vdhHornReverseBeeperComment = imageEntity.dfNameHornReverseBeeper!!,
                            vdhLights = dOrf(imageEntity.dfNameFogLights),
                            vdhLightsComment = imageEntity.dfNameFogLights!!,
                            vdhMirrors = dOrf(imageEntity.dfNameMirrors),
                            vdhMirrorsComment = imageEntity.dfNameMirrors!!,
                            vdhOilFuelCoolantLeaks = dOrf(imageEntity.dfNameOilFuelCoolantLeaks),
                            vdhOilFuelCoolantLeaksComment = imageEntity.dfNameOilFuelCoolantLeaks!!,
                            vdhReflectorsMarkers = dOrf(imageEntity.dfNameReflectorsMarkers),
                            vdhReflectorsMarkersComment = imageEntity.dfNameReflectorsMarkers!!,
                            vdhRegistrationPlates = dOrf(imageEntity.dfNameRegistrationNumberPlates),
                            vdhRegistrationPlatesComment = imageEntity.dfNameRegistrationNumberPlates!!,
                            vdhSeatBelt = dOrf(imageEntity.dfNameSeatBelt),
                            vdhSeatBeltComment = imageEntity.dfNameSeatBelt!!,
                            vdhSpareWheel = dOrf(imageEntity.dfNameSpareWheel),
                            vdhSpareWheelComment = imageEntity.dfNameSpareWheel!!,
                            vdhSteering = dOrf(imageEntity.dfNameSteeringControl),
                            vdhSteeringComment = imageEntity.dfNameSteeringControl!!,
                            vdhTyresWheels = dOrf(imageEntity.dfNameTyreConditionThreadDepth),
                            vdhTyresWheelsComment = imageEntity.dfNameTyreConditionThreadDepth!!,
                            vdhVehFront = dOrf(imageEntity.dfNameBodyDamageFront),
                            vdhVehFrontComment = imageEntity.dfNameBodyDamageFront!!,
                            vdhVehNearSide = dOrf(imageEntity.dfNameBodyDamageNearSide),
                            vdhVehNearSideComment = imageEntity.dfNameBodyDamageNearSide!!,
                            vdhVehOffside = dOrf(imageEntity.dfNameBodyDamageOffside),
                            vdhVehOffsideComment = imageEntity.dfNameBodyDamageOffside!!,
                            vdhVehRear = dOrf(imageEntity.dfNameBodyDamageRear),
                            vdhVehRearComment = imageEntity.dfNameBodyDamageRear!!,
                            vdhVehicleLockingSystem = dOrf(imageEntity.dfNameVehicleLockingSystem),
                            vdhVehicleLockingSystemComment = imageEntity.dfNameVehicleLockingSystem!!,
                            vdhWarningServiceLights = dOrf(imageEntity.dfNameWarningServiceLights),
                            vdhWarningServiceLightsComment = imageEntity.dfNameWarningServiceLights!!,
                            vdhWheelsWheelFixings = dOrf(imageEntity.dfNameWheelFixings),
                            vdhWheelsWheelFixingsComment = imageEntity.dfNameWheelFixings!!,
                            vdhWindowsGlassVisibility = dOrf(imageEntity.dfNameWindowGlass),
                            vdhWindowsGlassVisibilityComment = imageEntity.dfNameWindowGlass!!,
                            vdhWindscreen = dOrf(imageEntity.dfNameWindScreen),
                            vdhWindscreenComment = imageEntity.dfNameWindScreen!!,
                            vdhWipersWashers = dOrf(imageEntity.dfNameWipersWashers),
                            vdhWipersWashersComment = imageEntity.dfNameWipersWashers!!,
                            vdhFuelOilLeaks = dOrf(imageEntity.dfNameOilCoolantLevel),
                            vdhFuelOilLeaksComment = imageEntity.dfNameOilCoolantLevel!!,
                            vdhIsDefected = isDefected,
                        )
                        if (isDefected) {
                            startUploadWithWorkManager(2, prefs, requireContext(), VdhLmId, VdhVmId)
                        }
                        (activity as HomeActivity).viewModel.SaveVehDefectSheet(request)
                    }
                } else {
                    if (prefs.scannedVmRegNo.isNotEmpty() && !secondTry) {
                        showDialog()
                        secondTry = true
                        viewModel.GetVehicleInformation(prefs.clebUserId.toInt(), getVRegNo(prefs))
                    }
                }
            }

            viewModel.GetVehicleInformation(prefs.clebUserId.toInt(), getVRegNo(prefs))
            viewModel.SaveVehDefectSheetResponseLiveData.observe(viewLifecycleOwner) {
                hideDialog()
                if (it != null) {
                    var osData = (activity as HomeActivity).osData
                    osData.isDefectSheetFilled = true
                    (activity as HomeActivity).oSyncViewModel.insertData(osData)
                    navigateTo(R.id.newCompleteTaskFragment)
                } else {
                    showErrorDialog(fragmentManager, "SPW-01", "Failed to Save")
                }
            }
        } else {
            val dialog = NoInternetDialog()
            dialog.showDialog(fragmentManager)
        }
    }

    private fun dOrf(value: String?): String {
        if (value != "f")
            isDefected = true
        return if (value == "f") "F" else "D"
    }
}