package com.andryoga.doodh.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andryoga.doodh.MyApplication
import com.andryoga.doodh.databinding.FragmentHomeBinding
import com.andryoga.doodh.models.Date
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel: HomeViewModel by viewModels {
            // Get the DAO from your Application class
            val application = requireActivity().application as MyApplication
            HomeViewModelFactory(application.doodhDao)
        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Calendar.getInstance().apply {
            homeViewModel.setSelectedDate(
                Date(
                    get(Calendar.DAY_OF_MONTH),
                    get(Calendar.MONTH),
                    get(Calendar.YEAR)
                )
            )
        }


        binding.calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            homeViewModel.setSelectedDate(Date(dayOfMonth, month, year))
        }

        binding.calendar.date
        binding.save.setOnClickListener {
            homeViewModel.onSaveClick(
                binding.qty.text.toString().toDouble()
            )
        }

        homeViewModel.selectedDateQuantity.observe(viewLifecycleOwner) { qty ->
            binding.qty.setText(qty.toString())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}