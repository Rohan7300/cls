package com.clebs.celerity.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityWeeklyrotaBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertDateFormat
import com.clebs.celerity.utils.getCurrentWeek
import com.clebs.celerity.utils.getCurrentYear
import com.clebs.celerity.utils.noInternetCheck
import com.clebs.celerity.utils.showToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeeklyRotaApprovalActivity : AppCompatActivity() {
    lateinit var viewmodel: MainViewModel
    lateinit var repo: MainRepo
    lateinit var pref: Prefs
    private var notificationID = 0
    lateinit var binding: ActivityWeeklyrotaBinding
    var lrnID: Int = 0
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weeklyrota)
        setContentView(binding.root)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val actionID = intent.getIntExtra("actionID", 0)
        notificationID = intent.getIntExtra("notificationID", 0)

        repo = MainRepo(apiService)
        pref = Prefs(this)
        loadingDialog = LoadingDialog(this)
        viewmodel = ViewModelProvider(this, MyViewModelFactory(repo))[MainViewModel::class.java]

        binding.mainLayout.visibility = View.GONE
        binding.nodataLayout.visibility = View.VISIBLE
        noInternetCheck(this,binding.nointernetLL,this)

        /*        viewmodel.GetViewFullScheduleInfo(
                    pref.clebUserId.toInt(),
                    0,
                    getCurrentYear(),
                    getCurrentWeek()
                )*/
        viewmodel.GetWeeklyLocationRotabyId(
            actionID
        )
        loadingDialog.show()
        binding.approve.setOnClickListener {
            loadingDialog.show()
            viewmodel.ApproveWeeklyRotabyDA(pref.clebUserId.toInt(), lrnID)
        }
        binding.reviewLater.setOnClickListener {
            finish()
        }


        viewmodel.WeeklyRotaExistForDAApproval(pref.clebUserId.toInt())
        viewmodel.liveDataWeeklyRotaExistForDAApproval.observe(this) {
            if (it == null) {
                finish()
                showToast("Weekly Rota not exist for approval.", this)
                viewmodel.MarkNotificationAsRead(notificationID)
            }

        }

        viewmodel.liveDataApproveWeeklyRota.observe(this) {
            viewmodel.MarkNotificationAsRead(notificationID)
            loadingDialog.dismiss()
            if (it != null)
                finish()
            else
                showToast("Failed to update weekly rota or already approved!!", this)
        }
        viewmodel.liveDataWeeklyLocationRotabyId.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {

                binding.mainLayout.visibility = View.VISIBLE
                binding.nodataLayout.visibility = View.GONE
                val currentDate = Calendar.getInstance()
                currentDate.set(Calendar.HOUR_OF_DAY, 0)
                currentDate.set(Calendar.MINUTE, 0)
                currentDate.set(Calendar.SECOND, 0)
                currentDate.set(Calendar.MILLISECOND, 0)
                currentDate.add(Calendar.DAY_OF_YEAR, 1)

                val tomorrowDate = currentDate.time
                val dateFormat = SimpleDateFormat("EEE MMM dd yyyy", Locale.US)

                val formattedDatetwo: String = dateFormat.format(tomorrowDate)

//                Log.e("tomotmoit", "onCreateView: " + formattedDate)
                binding.viewfullschedule.isClickable = true
                binding.viewfullschedule.isEnabled = true
                binding.llnodata.visibility = View.GONE

                it.map {
                    binding.weekrotaheader.text =
                        "Please review your rota for week ${it.WeekNo} & year: ${it.YearNo}"
                    val formattedDate = convertDateFormat(it.SundayDate)
                    binding.tvDateShow1.text = "SUN $formattedDate"
                    val formattedDate2 = convertDateFormat(it.MondayDate)
                    binding.tvDateShow2.text = "MON $formattedDate2"

                    val formattedDate3 = convertDateFormat(it.TuesdayDate)
                    binding.tvDateShow3.text = "TUE " + formattedDate3
                    lrnID = it.LrnID

                    val formattedDate4 = convertDateFormat(it.WednesdayDate)
                    binding.tvDateShow4.text = "WED " + formattedDate4

                    val formattedDate5 = convertDateFormat(it.ThursdayDate)

                    binding.tvDateShow5.text = "THU " + formattedDate5

                    val formattedDate6 = convertDateFormat(it.FridayDate)

                    binding.tvDateShow6.text = "FRI " + formattedDate6

                    val formattedDate7 = convertDateFormat(it.SaturdayDate)
                    binding.tvDateShow7.text = "SAT " + formattedDate7


                    binding.tvIsWorkingShowSunday.text = it.SundayLocation
                    binding.tvIsWorkingShowTuesday.text = it.TuesdayLocation
                    binding.tvIsWorkingShowWed.text = it.WednesdayLocation
                    binding.tvIsWorkingShowThu.text = it.ThursdayLocation
                    binding.tvIsWorkingShowFri.text = it.FridayLocation
                    binding.tvIsWorkingShowMonday.text = it.MondayLocation
                    binding.tvIsWorkingShowSat.text = it.SaturdayLocation

                }
            } else {
                binding.nodataLayout.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
                binding.viewfullschedule.isClickable = false
                binding.viewfullschedule.isEnabled = false
                binding.viewfulldatalayout.visibility = View.GONE
                binding.llnodata.visibility = View.VISIBLE
            }

        }
    }

}