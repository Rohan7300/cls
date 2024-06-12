package com.clebs.celerity_admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.databinding.ActivityLoginBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.toast
import com.clebs.celerity_admin.viewModels.MainViewModel
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    lateinit var ActivityLoginBinding: ActivityLoginBinding
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        val window = window
        val winParams = window.attributes
        winParams.flags =
            winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        window.attributes = winParams
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                )
        window.statusBarColor = resources.getColor(R.color.transparent, null)
        super.onCreate(savedInstanceState)
        ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)


        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]


        var isPasswordVisible = false
        ActivityLoginBinding.passIcon.setOnClickListener {
            val cursorPosition = ActivityLoginBinding.edtPass.selectionEnd
            if (!isPasswordVisible) {
                ActivityLoginBinding.edtPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                ActivityLoginBinding.passIcon.setImageDrawable(getDrawable(R.drawable.visible2))
            } else {
                ActivityLoginBinding.edtPass.transformationMethod = PasswordTransformationMethod.getInstance()
                ActivityLoginBinding.passIcon.setImageDrawable(getDrawable(R.drawable.hidden2))
            }
            ActivityLoginBinding.edtPass.setSelection(cursorPosition)
            isPasswordVisible = !isPasswordVisible
        }
        ActivityLoginBinding.btLogin.setOnClickListener {
            next()
        }

    }

    fun next() {
        if (ActivityLoginBinding.edtUser.text?.isEmpty() == true) {
            ActivityLoginBinding.textError.visibility=View.VISIBLE

            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show()
        } else if (ActivityLoginBinding.edtPass.text?.isEmpty() == true) {
            ActivityLoginBinding.textError.visibility=View.GONE
            ActivityLoginBinding.textErrorTwo.visibility=View.VISIBLE
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
        } else {

            ActivityLoginBinding.textError.visibility=View.GONE
            ActivityLoginBinding.textErrorTwo.visibility=View.GONE
            mainViewModel.loginUser(
                LoginRequest(
                    ActivityLoginBinding.edtUser.text.toString(),
                    ActivityLoginBinding.edtPass.text.toString()
                )
            ).observe(this, Observer {

                if (it != null) {
                    if (it.UserRole == "S") {
                        setLoggedIn(true)
                        Prefs.getInstance(applicationContext).accessToken = it.token
                        Prefs.getInstance(applicationContext).clebUserId = it.userID.toString()
                        val i = Intent(
                            this@LoginActivity,
                            MainActivityTwo::class.java
                        )

                        startActivity(i)
                    } else {
                        setLoggedIn(false)
                        toast("Invalid Login")
                    }
                }
                else{
                    setLoggedIn(false)
                }

            })


        }
    }
    private fun setLoggedIn(isLoggedIn: Boolean) {
        Prefs.getInstance(applicationContext).saveBoolean("isLoggedIn", isLoggedIn)
    }
}