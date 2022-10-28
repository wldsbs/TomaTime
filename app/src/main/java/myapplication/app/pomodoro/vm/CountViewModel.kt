package myapplication.app.pomodoro.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CountViewModel {
    //Count 모델을 만들어야하나 말아야하나,,,,,?
    private var _remainTime = MutableLiveData<Long>()
    private var _remainMinutes = MutableLiveData<Long>()
    private var _remainSeconds = MutableLiveData<Long>()

    val remainTime: LiveData<Long> get() = _remainTime
    val remainMin: LiveData<Long> get() = _remainMinutes
    val remainSec: LiveData<Long> get() = _remainSeconds

    init {
        _remainMinutes.value = 0L
        _remainSeconds.value = 0L
    }

    fun updateRemainTimes(remainMillis: Long){
        val remainSeconds = remainMillis / 1000
        _remainTime.value = remainMillis

        _remainMinutes.value = remainSeconds / 60
        _remainSeconds.value = remainSeconds % 60
        Log.d("min' sec", "${remainMin.value.toString()}' ${remainSec.value.toString()}")

    }

}