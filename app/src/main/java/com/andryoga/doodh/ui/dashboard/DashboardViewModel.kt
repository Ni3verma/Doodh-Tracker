package com.andryoga.doodh.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andryoga.doodh.data.db.DoodhDao
import com.andryoga.doodh.data.db.DoodhEntity
import com.andryoga.doodh.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(
    private val doodhDao: DoodhDao
) : ViewModel() {
    private val _yearItems = MutableLiveData<Array<Int>>()
    val yearItems: LiveData<Array<Int>> = _yearItems

    val monthItems: LiveData<Array<String>> = MutableLiveData(Constants.MONTH_TEXT)

    private val _recordsOfMonth = MutableLiveData<List<DoodhEntity>>()
    val recordsOfMonth: LiveData<List<DoodhEntity>> = _recordsOfMonth

    private var selectedMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)

    fun initVM() {
        viewModelScope.launch(Dispatchers.IO) {
            _yearItems.postValue(
                doodhDao.getAllDistinctYears()
            )
        }

        getRecords()
    }

    private fun getRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            _recordsOfMonth.postValue(
                doodhDao.getDoodhRecordsForMonth(
                    selectedMonth, selectedYear
                )
            )
        }
    }

    fun updateMonth(newMonth: String) {
        selectedMonth = Constants.MONTH_TEXT.indexOf(newMonth)
        getRecords()
    }

    fun updateYear(newYear: Int) {
        selectedYear = newYear
        getRecords()
    }
}

class DashboardViewModelFactory(private val doodhDao: DoodhDao) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(doodhDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}