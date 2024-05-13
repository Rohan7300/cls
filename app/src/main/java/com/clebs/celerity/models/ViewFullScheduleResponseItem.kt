package com.clebs.celerity.models


import com.google.gson.annotations.SerializedName

data class ViewFullScheduleResponseItem(
    @SerializedName("AmApprovedByID")
    val amApprovedByID: Any,
    @SerializedName("CheckLrnID")
    val checkLrnID: Boolean,
    @SerializedName("CreatedIOn")
    val createdIOn: String,
    @SerializedName("FriCount")
    val friCount: Int,
    @SerializedName("FridayComment")
    val fridayComment: Any,
    @SerializedName("FridayDate")
    val fridayDate: String,
    @SerializedName("FridayLocation")
    val fridayLocation: String,
    @SerializedName("FridayLocationId")
    val fridayLocationId: Int,
    @SerializedName("FridayWaveTime")
    val fridayWaveTime: Any,
    @SerializedName("FridayWaveTimeString")
    val fridayWaveTimeString: String,
    @SerializedName("HasThisLocationAccess")
    val hasThisLocationAccess: Boolean,
    @SerializedName("IsLocked")
    val isLocked: Boolean,
    @SerializedName("LmID")
    val lmID: Int,
    @SerializedName("LocationName")
    val locationName: String,
    @SerializedName("LrnID")
    val lrnID: Int,
    @SerializedName("MonCount")
    val monCount: Int,
    @SerializedName("MondayComment")
    val mondayComment: Any,
    @SerializedName("MondayDate")
    val mondayDate: String,
    @SerializedName("MondayLocation")
    val mondayLocation: String,
    @SerializedName("MondayLocationId")
    val mondayLocationId: Int,
    @SerializedName("MondayWaveTime")
    val mondayWaveTime: Any,
    @SerializedName("MondayWaveTimeString")
    val mondayWaveTimeString: String,
    @SerializedName("NotCountFriday")
    val notCountFriday: Boolean,
    @SerializedName("NotCountMonday")
    val notCountMonday: Boolean,
    @SerializedName("NotCountSaturday")
    val notCountSaturday: Boolean,
    @SerializedName("NotCountSunday")
    val notCountSunday: Boolean,
    @SerializedName("NotCountThursday")
    val notCountThursday: Boolean,
    @SerializedName("NotCountTuesday")
    val notCountTuesday: Boolean,
    @SerializedName("NotCountWednesday")
    val notCountWednesday: Boolean,
    @SerializedName("OSMApprovedByID")
    val oSMApprovedByID: Any,
    @SerializedName("SatCount")
    val satCount: Int,
    @SerializedName("SaturdayComment")
    val saturdayComment: Any,
    @SerializedName("SaturdayDate")
    val saturdayDate: String,
    @SerializedName("SaturdayLocation")
    val saturdayLocation: String,
    @SerializedName("SaturdayLocationId")
    val saturdayLocationId: Any,
    @SerializedName("SaturdayWaveTime")
    val saturdayWaveTime: Any,
    @SerializedName("SaturdayWaveTimeString")
    val saturdayWaveTimeString: String,
    @SerializedName("SunCount")
    val sunCount: Int,
    @SerializedName("SundayComment")
    val sundayComment: Any,
    @SerializedName("SundayDate")
    val sundayDate: String,
    @SerializedName("SundayLocation")
    val sundayLocation: String,
    @SerializedName("SundayLocationId")
    val sundayLocationId: Int,
    @SerializedName("SundayWaveTime")
    val sundayWaveTime: Any,
    @SerializedName("SundayWaveTimeString")
    val sundayWaveTimeString: String,
    @SerializedName("ThurCount")
    val thurCount: Int,
    @SerializedName("ThursdayComment")
    val thursdayComment: Any,
    @SerializedName("ThursdayDate")
    val thursdayDate: String,
    @SerializedName("ThursdayLocation")
    val thursdayLocation: String,
    @SerializedName("ThursdayLocationId")
    val thursdayLocationId: Int,
    @SerializedName("ThursdayWaveTime")
    val thursdayWaveTime: Any,
    @SerializedName("ThursdayWaveTimeString")
    val thursdayWaveTimeString: String,
    @SerializedName("TuesCount")
    val tuesCount: Int,
    @SerializedName("TuesdayComment")
    val tuesdayComment: Any,
    @SerializedName("TuesdayDate")
    val tuesdayDate: String,
    @SerializedName("TuesdayLocation")
    val tuesdayLocation: String,
    @SerializedName("TuesdayLocationId")
    val tuesdayLocationId: Int,
    @SerializedName("TuesdayWaveTime")
    val tuesdayWaveTime: Any,
    @SerializedName("TuesdayWaveTimeString")
    val tuesdayWaveTimeString: String,
    @SerializedName("UserID")
    val userID: Int,
    @SerializedName("UserName")
    val userName: String,
    @SerializedName("WedCount")
    val wedCount: Int,
    @SerializedName("WednesdayComment")
    val wednesdayComment: Any,
    @SerializedName("WednesdayDate")
    val wednesdayDate: String,
    @SerializedName("WednesdayLocation")
    val wednesdayLocation: String,
    @SerializedName("WednesdayLocationId")
    val wednesdayLocationId: Int,
    @SerializedName("WednesdayWaveTime")
    val wednesdayWaveTime: Any,
    @SerializedName("WednesdayWaveTimeString")
    val wednesdayWaveTimeString: String,
    @SerializedName("WeekNo")
    val weekNo: Int,
    @SerializedName("YearNo")
    val yearNo: Int,
    @SerializedName("NextWorkingDate")
    val NextWorkingDate:String,
    @SerializedName("NextWorkingDay")
    val NextWorkingDay:String,
    @SerializedName("NextWorkingLoc")
    val NextWorkingLoc:String,
    @SerializedName("NextWorkingDayWaveTime")
    val NextWorkingDayWaveTime:String?=null
)