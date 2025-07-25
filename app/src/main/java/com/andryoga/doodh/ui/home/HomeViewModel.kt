package com.andryoga.doodh.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.andryoga.doodh.data.db.DoodhDao
import com.andryoga.doodh.data.db.DoodhEntity
import com.andryoga.doodh.models.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HomeViewModel(
    private val doodhDao: DoodhDao
) : ViewModel() {
    private val _selectedDateQuantity = MutableLiveData(0.0)
    val selectedDateQuantity: LiveData<Double> = _selectedDateQuantity

    private val _currentSelectedDate = MutableLiveData<Date>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _currentSelectedDate.asFlow().collectLatest { date ->
                // Ensure date is not null before observing
                date?.let {
                    doodhDao.getDoodhRecordForDay(
                        date.day, date.month, date.year
                    ).collectLatest { doodhEntity ->
                        val qty = doodhEntity?.qty ?: 0.0
                        _selectedDateQuantity.postValue(qty)
                    }
                }
            }
        }
    }
    fun onSaveClick(qty: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val date = _currentSelectedDate.value!!
            doodhDao.insertDoodhRecord(DoodhEntity( date.day, date.month, date.year,qty))
        }
    }

    fun setSelectedDate(date: Date) {
        _currentSelectedDate.value = date
    }
}

class HomeViewModelFactory(private val doodhDao: DoodhDao): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(doodhDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}