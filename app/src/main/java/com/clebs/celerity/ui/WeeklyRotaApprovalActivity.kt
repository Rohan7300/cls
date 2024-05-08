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
import com.clebs.celerity.utils.showToast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeeklyRotaApprovalActivity : AppCompatActivity() {
    lateinit var viewmodel: MainViewModel
    lateinit var repo: MainRepo
    lateinit var pref: Prefs
    lateinit var binding: ActivityWeeklyrotaBinding
    var lrnID: Int = 0
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weeklyrota)
        setContentView(binding.root)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        repo = MainRepo(apiService)
        pref = Prefs(this)
        loadingDialog = LoadingDialog(this)
        viewmodel = ViewModelProvider(this, MyViewModelFactory(repo))[MainViewModel::class.java]

        binding.mainLayout.visibility = View.GONE
        binding.nodataLayout.visibility = View.VISIBLE

        binding.weekrotaheader.text =
            "Please review your rota for week ${getCurrentWeek()} & year: ${getCurrentYear()}"
        viewmodel.GetViewFullScheduleInfo(
            pref.clebUserId.toInt(),
            0,
            getCurrentYear(),
            getCurrentWeek()
        )
        loadingDialog.show()
        binding.approve.setOnClickListener {
            loadingDialog.show()
            viewmodel.ApproveWeeklyRotabyDA(pref.clebUserId.toInt(), lrnID)
        }


        viewmodel.WeeklyRotaExistForDAApproval(pref.clebUserId.toInt())
        viewmodel.liveDataWeeklyRotaExistForDAApproval.observe(this){
            if(it==null){
                finish()
                showToast("Weekly Rota not exist for approval.",this)
            }

        }

        viewmodel.liveDataApproveWeeklyRota.observe(this) {
            loadingDialog.dismiss()
            if (it != null)
                finish()
            else
                showToast("Failed to update weekly rota or already approved!!", this)
        }
        viewmodel.livedatagetvechilescheduleinfo.observe(this) {
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
                binding.rlicons.visibility = View.VISIBLE

                it.map {

                    val formattedDate = convertDateFormat(it.sundayDate)
                    binding.tvDateShow1.text = "SUN $formattedDate"
                    val formattedDate2 = convertDateFormat(it.mondayDate)
                    binding.tvDateShow2.text = "MON $formattedDate2"

                    val formattedDate3 = convertDateFormat(it.tuesdayDate)
                    binding.tvDateShow3.text = "TUE " + formattedDate3
                    lrnID = it.lrnID

                    val formattedDate4 = convertDateFormat(it.wednesdayDate)
                    binding.tvDateShow4.text = "WED " + formattedDate4

                    val formattedDate5 = convertDateFormat(it.thursdayDate)

                    binding.tvDateShow5.text = "THU " + formattedDate5

                    val formattedDate6 = convertDateFormat(it.fridayDate)

                    binding.tvDateShow6.text = "FRI " + formattedDate6

                    val formattedDate7 = convertDateFormat(it.saturdayDate)
                    binding.tvDateShow7.text = "SAT " + formattedDate7


                    binding.tvIsWorkingShowSunday.text = it.sundayLocation
                    binding.tvIsWorkingShowTuesday.text = it.tuesdayLocation
                    binding.tvIsWorkingShowWed.text = it.wednesdayLocation
                    binding.tvIsWorkingShowThu.text = it.thursdayLocation
                    binding.tvIsWorkingShowFri.text = it.fridayLocation
                    binding.tvIsWorkingShowMonday.text = it.mondayLocation
                    binding.tvIsWorkingShowSat.text = it.saturdayLocation

                }
            } else {
                binding.nodataLayout.visibility = View.VISIBLE
                binding.mainLayout.visibility = View.GONE
                binding.viewfullschedule.isClickable = false
                binding.viewfullschedule.isEnabled = false
                binding.viewfulldatalayout.visibility = View.GONE
                binding.llnodata.visibility = View.VISIBLE
                binding.rlicons.visibility = View.GONE
            }

        }
    }

}