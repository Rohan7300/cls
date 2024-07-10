package com.clebs.celerity_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.databinding.ActivitySubmitWeeklyDefectBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass.currentWeeklyDefectItem
import com.clebs.celerity_admin.viewModels.MainViewModel

class SubmitWeeklyDefectActivity : AppCompatActivity() {
    lateinit var binding: ActivitySubmitWeeklyDefectBinding
    private lateinit var vm: MainViewModel
    private var selectedOilLevelID: Int = -1
    private var selectedEngineCoolantLevelID: Int = -1
    private var selectedBreakFluidLevelID: Int = -1
    private var selectedWindscreenWashingID: Int = -1
    private var selectedWindScreenConditionID: Int = -1
    private lateinit var oilListNames:List<String>
    private lateinit var oilLevelIds:List<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        vm = ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_submit_weekly_defect)

        if (currentWeeklyDefectItem != null)
            vm.GetWeeklyDefectCheckImages(currentWeeklyDefectItem!!.vdhCheckId)

        observers()
    }

    private fun observers() {
        vm.lDGetWeeklyDefectCheckImages.observe(this) {
            if (it != null) {

                selectedOilLevelID = it.VdhDefChkImgOilLevelId
                selectedEngineCoolantLevelID = it.EngineCoolantLevelId
                selectedBreakFluidLevelID = it.BrakeFluidLevelId
                selectedWindscreenWashingID = it.WindowScreenWashingLiquidId
                selectedWindScreenConditionID = it.WindScreenConditionId
                vm.GetVehOilLevelList()
                vm.GetVehWindScreenConditionStatus()
                Log.d(
                    "Selections",
                    "$selectedOilLevelID \n$selectedEngineCoolantLevelID \n$selectedBreakFluidLevelID \n$selectedWindscreenWashingID \n$selectedWindScreenConditionID"
                )
                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthFrontNs,
                    binding.tyreDepthFrontImageUploadBtn,
                    binding.tyreDepthFrontImageFileName
                )

                setRadioCard(
                    it.TyrePressureFrontNS,
                    binding.tyrePressureFrontFullRB,
                    binding.tyrePressureFrontBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthRearNs,
                    binding.tyreDepthRearImageUploadBtn,
                    binding.tyreDepthRearImageUploadFileName
                )

                setRadioCard(
                    it.TyrePressureFrontNS,
                    binding.tyrePressureRearNSFullRB,
                    binding.tyrePressureRearNSBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthRearOs,
                    binding.tyreDepthRearOSImageUploadBtn,
                    binding.tyreDepthRearOSFileNameTV
                )

                setRadioCard(
                    it.TyrePressureRearOS,
                    binding.tyrePressureRearOSFULLRB,
                    binding.tyrePressureRearOSBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthFrontOs,
                    binding.tyreDepthFrontOSImageUploadBtn,
                    binding.tyreDepthFrontOSImageFilenameTV
                )

                setRadioCard(
                    it.TyrePressureFrontOS,
                    binding.tyrePressureFrontOSFullRB,
                    binding.tyrePressureFrontOSBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgEngineOilLevel,
                    binding.engineOilImageUploadBtn,
                    binding.engineOilImageUploadFileName
                )

                setUploadCardBtn(
                    it.VdhDefChkImgAddBlueLevel,
                    binding.addBlueLevelUploadBtn,
                    binding.addBlueLevelUploadFileName
                )

                setUploadCardBtn(
                    it.VdhDefChkImgNswingMirror,
                    binding.nsWingMirrorUploadBtn,
                    binding.nsWingMirrorUploadFileName
                )

                setUploadCardBtn(
                    it.VdhDefChkImgOswingMirror,
                    binding.osWingMirrorUploadBtn,
                    binding.osWingMirrorUploadFileName
                )
                setUploadCardBtn(
                    it.VdhDefChkImgVan360Video,
                    binding.Three60VideoUploadBtn,
                    binding.Three60VideoFileNameTV
                )

                if (it.VdhVerAdminComment.isNotBlank()) {
                    binding.actionCommentET.setText(it.VdhVerAdminComment)
                }
            }
        }


        vm.lDGetVehWindScreenConditionStatus.observe(this) {windScreenConditionList->
            if (windScreenConditionList != null) {
                val windScreenConditionStatusNameList = windScreenConditionList.map { it.Name }
                val windScreenConditionStatusNameId = windScreenConditionList.map { it.Id }

                if (selectedWindScreenConditionID != -1) {
                    binding.spinnerWindScreenCondition.setText(
                        windScreenConditionStatusNameList[windScreenConditionStatusNameId.indexOf(
                            selectedWindScreenConditionID
                        )]
                    )
                    binding.spinnerWindScreenCondition.setSelection(
                        windScreenConditionStatusNameId.indexOf(
                            selectedWindScreenConditionID
                        )
                    )
                }

                setSpinner(
                    binding.spinnerWindScreenCondition,
                    windScreenConditionStatusNameList,
                    windScreenConditionStatusNameId
                )

            }
        }

        vm.lDGetVehOilLevelList.observe(this) {oilLevelList->
            if (oilLevelList != null) {
                oilListNames = oilLevelList.map { it.VehOilLevelName }
                oilLevelIds = oilLevelList.map { it.VehOilLevelId }

                Log.d(
                    "Selections", "indexes ${oilLevelIds.indexOf(selectedOilLevelID)}" +
                            "\n${oilLevelIds.indexOf(selectedEngineCoolantLevelID)}" +
                            "\n${oilLevelIds.indexOf(selectedBreakFluidLevelID)}" +
                            "\n${oilLevelIds.indexOf(selectedWindscreenWashingID)}"
                )

                if (selectedOilLevelID != -1) {
                    binding.spinnerOilLevel.setText(
                        oilListNames[oilLevelIds.indexOf(
                            selectedOilLevelID
                        )]
                    )
                    binding.spinnerOilLevel.setSelection(oilLevelIds.indexOf(selectedOilLevelID))
                }


                if (selectedEngineCoolantLevelID != -1) {
                    binding.spinnerEngineCoolant.setText(
                        oilListNames[oilLevelIds.indexOf(
                            selectedEngineCoolantLevelID
                        )]
                    )
                    binding.spinnerEngineCoolant.setSelection(
                        oilLevelIds.indexOf(
                            selectedEngineCoolantLevelID
                        )
                    )
                }

                if (selectedBreakFluidLevelID != -1) {
                    binding.spinnerBrakeFluid.setText(
                        oilListNames[oilLevelIds.indexOf(
                            selectedBreakFluidLevelID
                        )]
                    )
                    binding.spinnerBrakeFluid.setSelection(
                        oilLevelIds.indexOf(
                            selectedBreakFluidLevelID
                        )
                    )
                }

                if (selectedWindscreenWashingID != -1) {
                    binding.spinnerWindscreenWashingLiquid.setText(
                        oilListNames[oilLevelIds.indexOf(
                            selectedWindscreenWashingID
                        )]
                    )
                    binding.spinnerWindscreenWashingLiquid.setSelection(
                        oilLevelIds.indexOf(
                            selectedWindscreenWashingID
                        )
                    )
                }

                setSpinner(binding.spinnerOilLevel, oilListNames, oilLevelIds)
                setSpinner(binding.spinnerEngineCoolant, oilListNames, oilLevelIds)
                setSpinner(binding.spinnerBrakeFluid, oilListNames, oilLevelIds)
                setSpinner(binding.spinnerWindscreenWashingLiquid, oilListNames, oilLevelIds)
            }
        }

    }

    private fun setUploadCardBtn(
        vdhDefChkImgTyreThreadDepthFrontNs: String,
        tyreDepthFrontImageUploadBtn: AppCompatButton,
        tyreDepthFrontImageFileName: TextView
    ) {
        if (vdhDefChkImgTyreThreadDepthFrontNs.isNotBlank()) {
            "Upload Again".also { tyreDepthFrontImageUploadBtn.text = it }
            tyreDepthFrontImageFileName.text = vdhDefChkImgTyreThreadDepthFrontNs
            tyreDepthFrontImageFileName.setTextColor(ContextCompat.getColor(this, R.color.blue_hex))
            tyreDepthFrontImageUploadBtn.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.greenBtn)
        }
    }

    private fun setRadioCard(
        tyrePressureFrontNS: Boolean,
        tyrePressureFrontFullRB: RadioButton,
        tyrePressureFrontBelowRB: RadioButton
    ) {
        if (tyrePressureFrontNS) {
            tyrePressureFrontFullRB.isChecked = true
        } else {
            tyrePressureFrontBelowRB.isChecked = true
        }
    }

    private fun setSpinner(
        spinner: AutoCompleteTextView,
        items: List<String>,
        ids: List<Int>
    ) {
        val itemsList = mutableListOf<String>()
        Log.d("ID", "$ids")
        itemsList.addAll(items)
        val adapter =
            ArrayAdapter(this, R.layout.dropdown_menu_popup_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.setAdapter(adapter)
        spinner.setOnItemClickListener { parent, _, position, _ ->
            run {
                parent?.let { nonNullParent ->

                    val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                    selectedItem.let {
      /*                  when (spinner) {
                            *//*                     binding.spinnerRouteType -> {
                                                     selectedRouteType = selectedItem
                                                     selectedRouteId = ids[position]
                                                 }*//*
                        }*/
                    }

                }
            }
        }
    }
}