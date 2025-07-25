package com.andryoga.doodh.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.andryoga.doodh.data.db.DoodhDao
import com.andryoga.doodh.data.db.DoodhEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(
    private val doodhDao: DoodhDao
) : ViewModel() {
    private val _yearItems = MutableLiveData<Array<Int>>()
    val yearItems: LiveData<Array<Int>> = _yearItems

    private val _recordsOfMonth = MutableLiveData<List<DoodhEntity>>()
    val recordsOfMonth: LiveData<List<DoodhEntity>> = _recordsOfMonth

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _yearItems.postValue(
                doodhDao.getAllDistinctYears()
            )

            val calendar = Calendar.getInstance()
            getRecords(
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR)
            )
        }
    }

    fun getRecords(month: Int, year: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _recordsOfMonth.postValue(
                doodhDao.getDoodhRecordsForMonth(
                    month, year
                )
            )
        }

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