package com.clebs.celerity_admin.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.FragmentCLSOsmHomeBinding
import com.clebs.celerity_admin.databinding.FragmentGalleryBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CLSOsmHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CLSOsmHomeFragment : Fragment() {
    lateinit var binding: FragmentCLSOsmHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCLSOsmHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.head.isSelected = true
        return root
    }

}