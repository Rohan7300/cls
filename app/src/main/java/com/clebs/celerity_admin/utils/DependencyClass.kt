package com.clebs.celerity_admin.utils

import androidx.lifecycle.MutableLiveData
import com.clebs.celerity_admin.models.GetVehicleCollectionHistoryResponseItem
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponseItem
import com.clebs.celerity_admin.models.WeeklyDefectChecksModelItem

object DependencyClass {
    var currentWeeklyDefectItem: WeeklyDefectChecksModelItem? = null
    var VehInspectionDate:String? = null
    var crrSelectedVehicleType:String? = null
    var crDASelectedVehicleType:String? = null
    var VehicleAllocatedTODAID: Int=0
    var selectedCompanyId: Int = -1
    var selectedVehicleId: Int = -1
    var selectedVehicleIdDA: Int = -1
    var selectedVehicleLocId: Int = -1
    var selectedVehicleLocIdDA: Int = -1

    var selectedVehicleFuelId: Int = -1
    var selectedVehicleFuelIdDA: Int = -1
    var selectedVehicleOilLevelListId: Int = -1
    var selectedVehicleOilLevelListIdDA: Int = -1
    var selectedRequestTypeId: Int = -1
    var addBlueMileage: String? = "0"
    var selectedVehicleLocationName:String = ""
    var crrMileage: Int = 0
    var requestTypeList =
        arrayListOf<GetVehicleDamageWorkingStatusResponseItem>()
    var accidentImagePos:MutableLiveData<Int> = MutableLiveData()
    lateinit var currentVehicleCollectionHistoryResponseItem:GetVehicleCollectionHistoryResponseItem

    var collectionSelectedVehicleLocId: Int = -1
    var collectionSelectedVehicleFuelId: Int = -1
    var collectionSelectedVehicleOilLevelListId: Int = -1
}