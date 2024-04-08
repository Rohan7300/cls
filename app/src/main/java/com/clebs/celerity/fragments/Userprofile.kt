package com.clebs.celerity.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
import com.clebs.celerity.utils.showErrorDialog
import com.clebs.celerity.utils.showToast

class Userprofile : Fragment() {
    lateinit var mbinding: FragmentUserprofileBinding
    private var isedit: Boolean = false
    lateinit var mainViewModel: MainViewModel
    var ninetydaysBoolean: Boolean? = null
    var edtold: String? = null
    var edtnew: String? = null
    var firstname: String? = null
    var lastname: String? = null
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

        mbinding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                mainViewModel.GetThirdPartyAccess(Prefs.getInstance(requireContext()).userID.toInt())
                mbinding.checkbox.isChecked=true
            }

        }


        mainViewModel.livedatathirdpartyaccess.observe(viewLifecycleOwner) {

            if (it != null) {
                if (it.Status.equals("200")) {
                    showToast("Third Party Access Is Provided To User", requireContext())
                    mbinding.checkbox.isChecked = true
                } else {
                    showToast(it.Message, requireContext())
                    mbinding.checkbox.isChecked = false
                }


            } else {
                showToast("Failed to provide third party Access", requireContext())
                mbinding.checkbox.isChecked = false
            }


        }


        mbinding.editImg.setOnClickListener {
            if (isedit) {
                mbinding.save.visibility = View.VISIBLE
                mbinding.emailtext.isEnabled = true
                mbinding.emailtext.isFocusable = true
                mbinding.emailtext.isFocusableInTouchMode = true


//                mbinding.usertext.isEnabled = true
//                mbinding.usertext.isFocusable = true
//                mbinding.usertext.isFocusableInTouchMode = true

//                mbinding.passtext.isEnabled = true
//                mbinding.passtext.isFocusable = true
//                mbinding.passtext.isFocusableInTouchMode = true

                mbinding.phonetext.isEnabled = true
                mbinding.phonetext.isFocusable = true
                mbinding.phonetext.isFocusableInTouchMode = true

                mbinding.addresstext.isEnabled = true
                mbinding.addresstext.isFocusable = true
                mbinding.addresstext.isFocusableInTouchMode = true

                mbinding.editImg.alpha = 0.5f

                mbinding.icInfos.visibility = View.VISIBLE
                mbinding.useEmailas.visibility = View.VISIBLE
            } else {
                mbinding.save.visibility = View.GONE
                mbinding.emailtext.isEnabled = false
                mbinding.emailtext.isFocusable = false
                mbinding.emailtext.isFocusableInTouchMode = false


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
                mbinding.icInfos.visibility = View.GONE
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
            updateprofileregular()
//            }
//            updateProfile90dys()
        }
        return mbinding.root
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
        mbinding.pb.visibility = View.VISIBLE
        mbinding.FormLayout.alpha = 0.5f
        mainViewModel.GetDriversBasicInformation(
            Prefs.getInstance(App.instance).userID.toDouble()
        ).observe(requireActivity(), Observer {
            if (it != null) {
                Log.e("responseprofile", "GetDriversBasicInformation: ")
                mbinding.name.text = it.firstName + " " + it.lastName
                mbinding.usertext.setText(it.firstName + " " + it.lastName)
                firstname = it.firstName
                lastname = it.lastName
                mbinding.emailtext.setText(it.emailID)
                mbinding.passtext.setText("**********")
                mbinding.phonetext.setText(it.PhoneNumber)
                mbinding.addresstext.setText(it.Address)
                mbinding.pb.visibility = View.GONE
                mbinding.FormLayout.alpha = 1f
                ninetydaysBoolean = it.IsUsrProfileUpdateReqin90days
//                if (it.IsUsrProfileUpdateReqin90days.equals(true)) {
//                    showAlertChangePasword90dys()
//                }
            }
        })
    }

    fun UseEmailAsUSername() {

        mainViewModel.UseEmailasUsername(
            Prefs.getInstance(App.instance).userID.toDouble(), "chakshit@gmail.com"
        ).observe(requireActivity(), Observer {
            Log.e("dkfjdkfjdfkj", "UseEmailAsUSername: ")
            if (it?.Status!!.equals(200)) {
                mbinding.usertext.setText(mbinding.emailtext.text.toString())

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

                    showToast("ProfileUpdated", requireContext())

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