package com.clebs.celerity.fragments.exterior

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentSpareWheelBinding
import com.clebs.celerity.fragments.BaseInteriorFragment
import com.clebs.celerity.models.requests.SaveVechileDefectSheetRequest
import com.clebs.celerity.ui.App
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.setImageView
import com.clebs.celerity.utils.showToast

class SpareWheelFragment : BaseInteriorFragment() {

    private lateinit var mBinding: FragmentSpareWheelBinding
    var VdhDaId = 0
    var VdhVmId = 0
    var VdhLmId = 0
    var VdhOdoMeterReading = 0
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
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.spareWheelFragment)
        clickListeners()


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

            edtMilTwo.setOnClickListener {
             /*                editMil2Visibilty(
                    mBinding.tvNext,
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )*/
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
                setImageView(defaultIv, it.exSpareWheel.toString())
                if (it.dfNameSpareWheel!!.isNotEmpty() && it.dfNameSpareWheel != "f") {
                    edtDefect.setText(it.dfNameSpareWheel.toString())
                }
            }
        }
    }

    override fun saveNnext() {
        var userId = Prefs.getInstance(requireContext()).userID.toInt()
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

        mBinding.spareWheelPB.visibility = View.VISIBLE
        var isApiCallInProgress = false
        VdhDaId = userId
        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner, Observer { it ->
            it?.let {
                if (!isApiCallInProgress) {
                    isApiCallInProgress = true
                    VdhVmId = it.vmId
                    VdhLmId = it.vmLocId
                    VdhOdoMeterReading = it.vehicleLastMillage

                    prefs.saveLocationID(it.vmLocId)

                    val request = SaveVechileDefectSheetRequest(
                        weekNo = 0,
                        yearNo = 0,
                        vdhVmId = VdhVmId,
                        vdhLmId = VdhLmId,
                        vdhDaId =userId,
                        vdhOdoMeterReading = VdhOdoMeterReading,
                        vdhComments = "TEST",
                        vdhPocIsaction = true,
                        vdhIsDefected = true,
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
                        vdhFuelOilLeaksComment = imageEntity.dfNameOilCoolantLevel!!
                    )

                    viewModel.SaveVehDefectSheet(request)
                }
            }
        })

        viewModel.GetDriversBasicInformation(
            Prefs.getInstance(App.instance).userID.toDouble()).observe(viewLifecycleOwner) {
            if (it!=null){
                if(it.vmRegNo!=null){
                    viewModel.GetVehicleInformation(prefs.userID.toInt(),it.vmRegNo)
                }

            }
        }


        viewModel.SaveVehDefectSheetResponseLiveData.observe(viewLifecycleOwner, Observer {
            mBinding.spareWheelPB.visibility = View.GONE
            if(it!=null){
                navigateTo(R.id.completeTaskFragment)
            }else{
                showToast("Failed to save!!",requireContext())
            }
        })
    }

    private fun dOrf(value: String?): String {
        return if (value == "f") "F" else "D"
    }
}