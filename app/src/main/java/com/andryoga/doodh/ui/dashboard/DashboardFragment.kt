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

    private val dashboardViewModel: DashboardViewModel by viewModels {
        val application = requireActivity().application as MyApplication
        DashboardViewModelFactory(application.doodhDao)
    }

    private var yearItems = arrayOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.records.adapter = recordAdapter

        binding.monthTv.apply {
            setText(Constants.MONTH_TEXT[Calendar.getInstance().get(Calendar.MONTH)], false)
            setOnItemClickListener { parent, _, position, _ ->
                val selectedMonth: String = parent.getItemAtPosition(position).toString()
                dashboardViewModel.updateMonth(selectedMonth)
            }
        }

        binding.yearTv.apply {
            setText(Calendar.getInstance().get(Calendar.YEAR).toString(), false)
            setOnItemClickListener { parent, _, position, _ ->
                val selectedYear = parent.getItemAtPosition(position).toString().toInt()
                dashboardViewModel.updateYear(selectedYear)
            }
        }

        setObservers()

        dashboardViewModel.initVM()
    }

    private fun setObservers() {
        dashboardViewModel.recordsOfMonth.observe(viewLifecycleOwner) { records ->
            binding.totalQty.text = "Total Qty: ${records.sumOf { it.qty }} Litres"
            recordAdapter.submitList(records)
        }

        dashboardViewModel.yearItems.observe(viewLifecycleOwner) { items ->
            yearItems = items.map { it.toString() }.toTypedArray()
            binding.yearTv.setSimpleItems(yearItems)
        }

        dashboardViewModel.monthItems.observe(viewLifecycleOwner) { items ->
            binding.monthTv.setSimpleItems(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}