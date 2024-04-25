package com.clebs.celerity.fragments

import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentUserprofileBinding
import com.clebs.celerity.models.requests.UpdateProfileRequestBody
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.SaveChangesCallback
import com.clebs.celerity.utils.showErrorDialog
import com.clebs.celerity.utils.showToast
import com.tapadoo.alerter.Alerter


class Userprofile : Fragment() {
    lateinit var mbinding: FragmentUserprofileBinding
    private var isedit: Boolean = false
    var saveChangesCallback: SaveChangesCallback? = null
    lateinit var mainViewModel: MainViewModel
    var ninetydaysBoolean: Boolean? = null
    var edtold: String? = null
    var thirdpartyaccessrequested: Boolean? = null

    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
    var edtnew: String? = null
    var firstname: String? = null
    var lastname: String? = null
    var isthirdpartyAccess: Boolean? = null
    var isthirdpartyAccessRequested: Boolean? = null
    var isthirdpartyAccessRemoved: Boolean? = null
    private lateinit var fragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentUserprofileBinding.inflate(inflater, container, false)
        }

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        fragmentManager = (activity as HomeActivity).fragmentManager

        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)

        GetDriversBasicInformation()
        HomeActivity.ActivityHomeBinding.title.setText("")

        mbinding.logout.setOnClickListener {
            (activity as HomeActivity).showAlertLogout()
        }


//        if (isthirdpartyAccessRequested != null && isthirdpartyAccessRequested!!.equals(true)) {
//
//            mbinding.Tvthirdparty.setText("Third party Access is Requested")
//        } else if (isthirdpartyAccessRequested != null && isthirdpartyAccessRequested!!.equals(false)) {
//            mbinding.Tvthirdparty.setText("Request Third Party Access")
//        }

        if (Prefs.getInstance(App.instance).days.equals("1")) {
            mbinding.editImg.performClick()

            mbinding.save.visibility = View.VISIBLE
            mbinding.emailtext.isEnabled = true
            mbinding.emailtext.isClickable = true

            mbinding.emailtext.isFocusable = true
            mbinding.emailtext.isFocusableInTouchMode = true
            mbinding.emailtext.requestFocus()

            val colorRes = R.color.white
            val color = ContextCompat.getColor(requireContext(), colorRes)


            // Set background tint using a specific color
            mbinding.emailtext.backgroundTintList = ColorStateList.valueOf(color)
            mbinding.phonetext.backgroundTintList = ColorStateList.valueOf(color)

            mbinding.phonetext.isEnabled = true
            mbinding.phonetext.isFocusable = true
            mbinding.phonetext.isFocusableInTouchMode = true

            mbinding.editImg.isClickable = false
            mbinding.editImg.isEnabled = false


            mbinding.editImg.alpha = 0.5f

//                mbinding.icInfos.visibility = View.VISIBLE
            mbinding.useEmailas.visibility = View.VISIBLE


            (activity as HomeActivity).disableBottomNavigationView()

            mbinding.emailtext.isFocusable = true


        } else {
            (activity as HomeActivity).enableBottomNavigationView()
        }


        mbinding.checkbox.setOnClickListener {
            if (mbinding.checkbox.isChecked) {
                mainViewModel.GetThirdPartyAccess(Prefs.getInstance(requireContext()).userID.toInt())
            } else {
                mainViewModel.RemoveThirdPartyAccess(Prefs.getInstance(requireContext()).userID.toInt())
            }
        }
        mainViewModel.livedatathirdpartyaccess.observe(viewLifecycleOwner) {

            if (it != null) {
                if (it.Status.equals("200")) {
                    Alerter.create(requireActivity())
                        .setTitle("")
                        .setIcon(R.drawable.logo_new)
                        .setText("Third Party access has been requested")
                        .setBackgroundColorInt(resources.getColor(R.color.medium_orange))
                        .show()
                    GetDriversBasicInformation()
//                    mainViewModel.GetDriversBasicInformation(Prefs.getInstance(requireContext()).userID.toDouble())

//                    Prefs.getInstance(App.instance).save("requestedthirdparty", "1")
////                    showToast("Third Party Access Is Requested.", requireContext())

                } else {
//                    showToast(it.Message, requireContext())

                }


            } else {
                showToast("Failed to provide third party Access.", requireContext())
                mbinding.checkbox.isChecked = false
            }


        }

        mainViewModel.livedataremovethirdpartyaccess.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.Status.equals("200")) {
//                    Prefs.getInstance(App.instance).save("removethirdparty", "1")
////                    mbinding.Tvthirdparty.text = "Third Party Access Is Requested to remove."
                    Alerter.create(requireActivity())
                        .setTitle("")
                        .setIcon(R.drawable.logo_new)
                        .setText("Third Party access has been removed successfully.")
                        .setBackgroundColorInt(resources.getColor(R.color.medium_orange))
                        .show()
                    GetDriversBasicInformation()
//                    mainViewModel.GetDriversBasicInformation(Prefs.getInstance(requireContext()).userID.toDouble())

                } else {
                    showToast(it.Message, requireContext())
                }

            } else {
                showToast("Failed to Remove third party Access", requireContext())
            }


        }


        mbinding.editImg.setOnClickListener {
            if (isedit) {
                mbinding.save.visibility = View.VISIBLE
                mbinding.emailtext.isEnabled = true
                mbinding.emailtext.isClickable = true
                mbinding.emailtext.isFocusable = true
                mbinding.emailtext.isFocusableInTouchMode = true
                mbinding.emailtext.requestFocus()

                /*   val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
                   imm!!.showSoftInput(mbinding.emailtext, InputMethodManager.SHOW_FORCED)*/

                mbinding.txtChangePassword.visibility = View.VISIBLE
                val colorRes = R.color.white
                val color = ContextCompat.getColor(requireContext(), colorRes)


                // Set background tint using a specific color
                mbinding.emailtext.backgroundTintList = ColorStateList.valueOf(color)
                mbinding.passtext.backgroundTintList = ColorStateList.valueOf(color)
                mbinding.phonetext.backgroundTintList = ColorStateList.valueOf(color)
                mbinding.addresstext.backgroundTintList = ColorStateList.valueOf(color)


                mbinding.phonetext.isEnabled = true
                mbinding.phonetext.isFocusable = true
                mbinding.phonetext.isFocusableInTouchMode = true

                mbinding.addresstext.isEnabled = true
                mbinding.addresstext.isFocusable = true
                mbinding.addresstext.isFocusableInTouchMode = true

                mbinding.editImg.alpha = 0.5f

//                mbinding.icInfos.visibility = View.VISIBLE
                mbinding.useEmailas.visibility = View.VISIBLE
            } else {
                val newColor = resources.getColor(R.color.very_light_grey_two)
                mbinding.emailtext.backgroundTintList = ColorStateList.valueOf(newColor)
                mbinding.passtext.backgroundTintList = ColorStateList.valueOf(newColor)
                mbinding.phonetext.backgroundTintList = ColorStateList.valueOf(newColor)
                mbinding.addresstext.backgroundTintList = ColorStateList.valueOf(newColor)
                mbinding.save.visibility = View.GONE
                mbinding.emailtext.isEnabled = false
                mbinding.emailtext.isFocusable = false
                mbinding.emailtext.isFocusableInTouchMode = false
                val colorRes = R.color.very_light_grey_two
                val color = ContextCompat.getColor(requireContext(), colorRes)

                mbinding.emailtext.backgroundTintList = ColorStateList.valueOf(color)
                mbinding.passtext.backgroundTintList = ColorStateList.valueOf(color)
                mbinding.phonetext.backgroundTintList = ColorStateList.valueOf(color)
                mbinding.addresstext.backgroundTintList = ColorStateList.valueOf(color)
                mbinding.txtChangePassword.visibility = View.GONE

                mbinding.usertext.isEnabled = false
                mbinding.usertext.isFocusable = false
                mbinding.usertext.isFocusableInTouchMode = false

                mbinding.passtext.isEnabled = false
                mbinding.passtext.isFocusable = false
                mbinding.passtext.isFocusableInTouchMode = false

                mbinding.phonetext.isEnabled = false
                mbinding.phonetext.isFocusable = false
                mbinding.phonetext.isFocusableInTouchMode = false

                mbinding.addresstext.isEnabled = false
                mbinding.addresstext.isFocusable = false
                mbinding.addresstext.isFocusableInTouchMode = false

                mbinding.editImg.alpha = 1f
//                mbinding.icInfos.visibility = View.GONE
                mbinding.useEmailas.visibility = View.GONE
            }
            isedit = !isedit

        }
        mbinding.txtChangePassword.setOnClickListener {

            showAlertChangePasword()
        }
        mbinding.useEmailas.setOnClickListener {
            showAlert()
        }
        mbinding.save.setOnClickListener {
//            if (ninetydaysBoolean == true) {
//                updateProfile90dys()
//
//            } else {
            if (Prefs.getInstance(App.instance).days.equals("1")) {
                updateProfile90dys()
            } else {
                updateprofileregular()
            }

//            }
//            updateProfile90dys()
        }
        return mbinding.root
    }

    override fun onPause() {
        super.onPause()
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mbinding.emailtext.getWindowToken(), 0)
    }

    fun showAlert() {
        val factory = LayoutInflater.from(requireActivity())
        val view: View = factory.inflate(R.layout.profile_popup, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        val textview_yes: TextView = view.findViewById<TextView>(R.id.tv_next_yes)
        val textview_no: TextView = view.findViewById<TextView>(R.id.tv_next)
        deleteDialog.setView(view)
        textview_yes.setOnClickListener {

            UseEmailAsUSername()
            deleteDialog.dismiss()

        }
        textview_no.setOnClickListener {

            deleteDialog.dismiss()
        }

        deleteDialog.setCancelable(false)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }

    fun showAlertChangePasword() {
        val factory = LayoutInflater.from(requireActivity())
        val view: View = factory.inflate(R.layout.change_password_dialog, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()

        val edt_old: EditText = view.findViewById(R.id.edt_old)
        val edt_new: EditText = view.findViewById(R.id.edt_new)
        val edt_new_two = view.findViewById<EditText>(R.id.edt_new_sec)
        val button: TextView = view.findViewById(R.id.save)
        deleteDialog.setView(view)
        button.setOnClickListener {
            if (edt_old.text.isEmpty()) {
                edt_old.setError("please enter old password")
            } else if (edt_new.text.isEmpty()) {
                edt_new.setError("please enter new password")
            } else if (edt_new_two.text.isEmpty()) {
                edt_new_two.setError("please re-enter new password")
            } else if (!edt_new.text.toString().equals(edt_new_two.text.toString())) {

                showToast("New password fields doesnot match", requireActivity())
            } else {
                edtold = edt_old.text.toString()
                edtnew = edt_new.text.toString()
                updateProfilePassword()
                deleteDialog.dismiss()


            }


        }
        deleteDialog.setCancelable(true)
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }


    fun showAlertChangePasword90dys() {
        val factory = LayoutInflater.from(requireActivity())
        val view: View = factory.inflate(R.layout.change_passwordninetydays, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        deleteDialog.setView(view)

        val button: TextView = view.findViewById(R.id.save)
        button.setOnClickListener {

            deleteDialog.dismiss()


        }

        deleteDialog.setCancelable(true)
        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }

    fun GetDriversBasicInformation() {
//        mbinding.pb.visibility = View.VISIBLE
        showDialog()
        mbinding.FormLayout.alpha = 0.5f
        mainViewModel.GetDriversBasicInformation(
            Prefs.getInstance(App.instance).userID.toDouble()
        ).observe(requireActivity(), Observer {
            if (it != null) {
                Log.e("responseprofile", "GetDriversBasicInformation: ")
//                mbinding.name.text = it.firstName + " " + it.lastName
//
//
//                mbinding.usertext.setText(it.firstName + " " + it.lastName)
                mbinding.name.text = it.firstName + " " + it.lastName
                mbinding.usertext.setText(it.UserName)
                isthirdpartyAccess = it.IsThirdPartyChargeAccessAllowed
                firstname = it.firstName
                lastname = it.lastName
                mbinding.emailtext.setText(it.emailID)
                mbinding.passtext.setText("**********")
                mbinding.phonetext.setText(it.PhoneNumber)
                mbinding.addresstext.setText(it.Address)
                thirdpartyaccessrequested = it.IsThirdPartyChargeAccessApplied
//                mbinding.pb.visibility = View.GONE
                mbinding.FormLayout.alpha = 1f

                ninetydaysBoolean = it.IsUsrProfileUpdateReqin90days
                mbinding.checkbox.isChecked = it.IsThirdPartyChargeAccessAllowed.equals(true)
                isthirdpartyAccess = it.IsThirdPartyChargeAccessAllowed


                /*if (it.IsThirdPartyChargeAccessAllowed != null && it.IsThirdPartyChargeAccessAllowed.equals(
                        true
                    )
                ) {
                    mbinding.Tvthirdparty.text = "Third party Access Has been Provided"
                    mbinding.checkbox.visibility = View.VISIBLE
                    mbinding.checkbox.isChecked = true

                } else if (it.IsThirdPartyChargeAccessAllowed != null && it.IsThirdPartyChargeAccessAllowed.equals(
                        false
                    ) && it.IsThirdPartyChargeAccessApplied.equals(false) || it.IsThirdPartyChargeAccessApplied.equals(
                        null
                    )
                ) {
                    mbinding.Tvthirdparty.text = "Request for third party Access."
                    mbinding.checkbox.visibility = View.VISIBLE
                    mbinding.checkbox.isChecked = false
                } else if (it.IsThirdPartyChargeAccessAllowed.equals(false) && it.IsThirdPartyChargeAccessApplied.equals(
                        true
                    )
                ) {
                    mbinding.Tvthirdparty.text = "Third party Access is Requested."
                    mbinding.checkbox.visibility = View.GONE
                }else if (it.IsThirdPartyChargeAccessAllowed.equals(false) && it.IsThirdPartyChargeAccessApplied.equals(
                        null
                    )
                ) {
                    mbinding.Tvthirdparty.text = "Request for third party Access."
                    mbinding.checkbox.visibility = View.VISIBLE
                }*/

                if (it.IsThirdPartyChargeAccessAllowed == false && it.IsThirdPartyChargeAccessApplied == null) {
                    mbinding.Tvthirdparty.text = "Request for third party Access."
                    mbinding.checkbox.visibility = View.VISIBLE
                    mbinding.checkbox.isChecked = false
                } else if (it.IsThirdPartyChargeAccessAllowed.equals(false) && it.IsThirdPartyChargeAccessApplied.equals(
                        true
                    )
                ) {
                    mbinding.Tvthirdparty.text = "Third party Access is Requested."
                    mbinding.checkbox.visibility = View.GONE
                    //mbinding.checkbox.isChecked =
                } else if (it.IsThirdPartyChargeAccessAllowed == true) {
                    mbinding.Tvthirdparty.text = "Third party Access is Granted."
                    mbinding.checkbox.visibility = View.GONE
                } else if (it.IsThirdPartyChargeAccessAllowed == false && it.IsThirdPartyChargeAccessApplied == false) {
                    mbinding.Tvthirdparty.text = "Request for third party Access."
                    mbinding.checkbox.visibility = View.VISIBLE
                    mbinding.checkbox.isChecked = false
                } else {
                    mbinding.Tvthirdparty.text = "Request for third party Access."
                    mbinding.checkbox.visibility = View.VISIBLE
                    mbinding.checkbox.isChecked = false
                }


//                if (it.IsThirdPartyChargeAccessAllowed.equals(true)) {
//                    mbinding.Tvthirdparty.setText("Third party Access has been granted")
//                } else {
//                    mbinding.Tvthirdparty.setText("Third party Access is not granted")
//                }


//                if (it.IsUsrProfileUpdateReqin90days.equals(true)) {
//                    showAlertChangePasword90dys()
//                }
                hideDialog()
            }
        })
    }

    fun UseEmailAsUSername() {


        mainViewModel.UseEmailasUsername(
            Prefs.getInstance(App.instance).userID.toDouble(), mbinding.emailtext.text.toString()
        ).observe(requireActivity(), Observer {
            Log.e("dkfjdkfjdfkj", "UseEmailAsUSername: ")
            if (it?.Status!!.equals(200)) {
//                mbinding.usertext.setText(mbinding.emailtext.text.toString())

                showToast("Email has been used as username", requireContext())

                Log.e("dlkfdlkfl", "UseEmailAsUSernamesuccess: " + it.Status + it.message)
            }


        })
    }

    fun updateProfile90dys() {
        mainViewModel.UpdateDAprofileninetydays(
            Prefs.getInstance(App.instance).userID.toDouble(),
            mbinding.emailtext.text.toString(),
            mbinding.phonetext.text.toString()
        ).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                isedit = false
                mbinding.save.visibility = View.GONE
                if (it?.Status!!.equals(200)) {
                    Log.e("90daysdisdjskjds", "updateProfile90dys: ")
                    showToast("Profile Updated", requireContext())
                    Prefs.getInstance(App.instance).days = "0"
                    saveChangesCallback?.onChangesSaved()
//                    Prefs.getInstance(App.instance).save("90days", "0")
                    (activity as HomeActivity).enableBottomNavigationView()
                    findNavController().navigate(R.id.homedemoFragment)

                }
            }


        })
    }

    fun updateProfilePassword() {

        mainViewModel.updateProfilepassword(
            Prefs.getInstance(App.instance).userID.toDouble(),
            edtold!!, edtnew!!
        )
        mainViewModel.updateprofilelivedata.observe(viewLifecycleOwner) {

            if (it != null) {

                if (it.Status == "200") {
                    Toast.makeText(
                        requireActivity(),
                        "Password has been changed",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("succcessssss", "updateProfilePassword1: ")
//                    showToast("Password has been changed", requireContext())
                }

            } else {
                showErrorDialog(fragmentManager, "0", "Error in changing password.")
                Log.e("succcessssss", "updateProfilePassword1: ")
//                    showToast("Error in changing password", requireContext())
            }
        }
    }

    fun updateprofileregular() {

        mainViewModel.updateprofileRegular(
            UpdateProfileRequestBody(
                Prefs.getInstance(App.instance).userID.toInt(),
                mbinding.emailtext.text.toString(),
                mbinding.phonetext.text.toString(),
                mbinding.addresstext.text.toString()
            )
        )

        mainViewModel.updateprofileregular.observe(viewLifecycleOwner) {
            if (it != null) {
                isedit = false
                mbinding.save.visibility = View.GONE
                if (it.Status.equals("200")) {

                    showToast("profile successfully updated", requireContext())

                } else {
                    showErrorDialog(fragmentManager, "0", "Error in updating profile")

                }
            }

        }

    }

}