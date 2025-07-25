package com.andryoga.doodh.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.andryoga.doodh.MyApplication
import com.andryoga.doodh.databinding.FragmentDashboardBinding
import com.andryoga.doodh.utils.Constants
import java.util.Calendar

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val recordAdapter = DoodhRecordAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel: DashboardViewModel by viewModels {
            // Get the DAO from your Application class
            val application = requireActivity().application as MyApplication
            DashboardViewModelFactory(application.doodhDao)
        }

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        binding.monthTv.setText(
//            Constants.MONTH_TEXT[Calendar.getInstance().get(Calendar.MONTH)],
//            false
//        )
        binding.monthTv.setSimpleItems(Constants.MONTH_TEXT)

        binding.yearTv.setText(Calendar.getInstance().get(Calendar.YEAR).toString(), false)

        binding.records.adapter = recordAdapter

        binding.monthTv.setOnItemClickListener { parent, view, position, id ->
            val selectedMonth = position
            val selectedYear = binding.yearTv.text.toString().toInt()

            dashboardViewModel.getRecords(selectedMonth, selectedYear)
        }

        binding.yearTv.setOnItemClickListener { parent, view, position, id ->
            val selectedMonth = binding.monthTv.text.toString().toInt()
            val selectedYear = parent.getItemAtPosition(position).toString().toInt()

            dashboardViewModel.getRecords(selectedMonth, selectedYear)
        }

        dashboardViewModel.yearItems.observe(viewLifecycleOwner) { items ->
            binding.yearTv.setSimpleItems(items.map { it.toString() }.toTypedArray())
        }

        dashboardViewModel.recordsOfMonth.observe(viewLifecycleOwner) { records ->
            binding.totalQty.text = "Total Qty: ${records.sumOf { it.qty }} Litres"
            recordAdapter.submitList(records)
        }

        val calendar = Calendar.getInstance()
        dashboardViewModel.getRecords(
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR)
        )

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}