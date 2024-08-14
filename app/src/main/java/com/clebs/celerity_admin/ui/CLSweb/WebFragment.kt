package com.clebs.celerity_admin.ui.CLSweb

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.SplashActivityTwo
import com.clebs.celerity_admin.databinding.FragmentWebBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.viewModels.MainViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WebFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WebFragment : Fragment() {
    lateinit var binding: FragmentWebBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWebBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.web.settings.javaScriptEnabled = true
        binding.web.settings.loadWithOverviewMode = true
        binding.web.webViewClient = WebViewClient()
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)

        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        binding.web.visibility=View.GONE
        binding.pb.visibility=View.VISIBLE
        mainViewModel.GetemergencyContact(
            Prefs.getInstance(SplashActivityTwo.instance).clebUserIds.toString().toInt()
        ).observe(viewLifecycleOwner, Observer {
            if (it!=null){

                binding.web.loadData(it, "text/html", "UTF-8")
                binding.web.visibility=View.VISIBLE
                binding.pb.visibility=View.GONE
            }


        })
        // Set the user agent to Firefox

        binding.web.loadUrl("https://www.celerity-ls.com/")
        return root

    }
}