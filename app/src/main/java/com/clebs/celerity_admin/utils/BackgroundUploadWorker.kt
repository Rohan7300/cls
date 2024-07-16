package com.clebs.celerity_admin.utils

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.clebs.celerity_admin.models.SaveDefectSheetWeeklyOSMCheckRequest
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class BackgroundUploadWorker(
    private var appContext: Context, workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {
    override fun doWork(): ListenableWorker.Result {
        val defectSheetUserId = inputData.getInt("defectSheetUserId", 0)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        GlobalScope.launch {
            val dbDefectSheet = App.offlineSyncDB?.getDefectSheet(
                DependencyClass.currentWeeklyDefectItem!!.vdhCheckId
            )
            if (dbDefectSheet != null) {
                val response = mainRepo.SaveDefectSheetWeeklyOSMCheck(
                    SaveDefectSheetWeeklyOSMCheckRequest(
                        Comment = dbDefectSheet.comment?:"",
                        PowerSteering =dbDefectSheet.powerSteeringCheck,
                        PowerSteeringLiquid =dbDefectSheet.powerSteeringCheck,
                        TyrePressureFrontNS =getRadioButtonState(dbDefectSheet.tyrePressureFrontNSRB),
                        TyrePressureFrontOS = getRadioButtonState(dbDefectSheet.tyrePressureFrontNSRB),
                        TyrePressureRearNS =getRadioButtonState(dbDefectSheet.tyrePressureFrontNSRB),
                        TyrePressureRearOS =getRadioButtonState(dbDefectSheet.tyrePressureFrontNSRB),
                        TyreThreadDepthFrontNSVal =0,
                        TyreThreadDepthFrontOSVal =0,
                        TyreThreadDepthRearNSVal =0,
                        TyreThreadDepthRearOSVal =0,
                        UserId = defectSheetUserId ,
                        VdhAdminComment ="",
                        VdhBrakeFluidLevelId =dbDefectSheet.brakeFluidLevelID,
                        VdhCheckId =dbDefectSheet.id,
                        VdhDefChkImgOilLevelId =dbDefectSheet.oilLevelID,
                        VdhEngineCoolantLevelId =dbDefectSheet.engineCoolantLevelID,
                        VdhWindScreenConditionId =dbDefectSheet.windScreenConditionId,
                        VdhWindowScreenWashingLiquidId =dbDefectSheet.windScreenWashingLevelId,
                        WeeklyActionCheck = dbDefectSheet.WeeklyActionCheck,
                        WeeklyApproveCheck =dbDefectSheet.WeeklyApproveCheck,
                        WindowScreenState =false,
                        WindscreenWashingLiquid =false
                    )
                )
                if(response.isSuccessful||response.failed){
                    if(dbDefectSheet.tyreDepthFrontNSImage!=null){

                    }
                    if(dbDefectSheet.tyreDepthRearNSImage!=null){

                    }
                    if(dbDefectSheet.tyreDepthFrontOSImage!=null){

                    }
                    if(dbDefectSheet.addBlueLevelImage!=null){

                    }
                    if(dbDefectSheet.nsWingMirrorImage!=null){

                    }
                    if(dbDefectSheet.osWingMirrorImage!=null){

                    }
                    if(dbDefectSheet.threeSixtyVideo!=null){

                    }
                }
            }
        }

        return Result.success()
    }
}