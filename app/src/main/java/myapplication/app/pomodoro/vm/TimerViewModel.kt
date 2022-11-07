package myapplication.app.pomodoro.vm

import android.media.SoundPool
import android.os.*
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TimerViewModel {
    var vibrator: Vibrator? = null
    private var _remainTime = MutableLiveData<Long>()
    private var remainMinutes = MutableLiveData<Long>()
    private var _remainSeconds = MutableLiveData<Long>()

//    var imgResource = MutableLiveData<Int>()
    var isMuteMode = MutableLiveData<Boolean>()
    private var _isFirst = MutableLiveData<Boolean>()
    var countState = MutableLiveData<Boolean>()
    var progress = MutableLiveData<Int>()
    var btnText = MutableLiveData<String>()

    var currentCountDownTimer: CountDownTimer? = null
    val soundPool = SoundPool.Builder().build()!!
    var tickingSoundId: Int? = null
    var bellSoundId: Int? = null
    //정보전달
    // 데이터 가공 -> 가져다가 쓰는쪽

    //엔티티는 서버에서 받아온 정보
    //model 사용자에게 보여지는 정보(가공완료)
    val remainSec: LiveData<Long> get() = _remainSeconds

    init {
        remainMinutes.value = 0L
        _remainSeconds.value = 0L
        isMuteMode.value = false
        _isFirst.value = true
        countState.value = false
        progress.value = 0
        btnText.value = "Start"
//        imgResource.value = R.drawable.ic_baseline_volume_up_24
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTimes(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }

    fun onProgressChanged(seekBar: SeekBar?, prog: Int, fromUser: Boolean) {
        if(fromUser){
            progress.value = prog
            updateRemainTimes(prog * 60 * 1000L)    //분단위 지정
        }
    }

    fun onStartTrackingTouch(seekBar: SeekBar?) {
        progress.value = seekBar!!.progress
        stopCountDown()
    }

    fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar ?: return

        progress.value = seekBar.progress
        if(seekBar.progress == 0){
            stopCountDown()
        }
    }

    private fun updateSeekBar(remainMillis: Long){
        progress.value = (remainMillis / 1000 / 60).toInt()
    }

    fun updateRemainTimes(remainMillis: Long){
        val remainSeconds = remainMillis / 1000
        _remainTime.value = remainMillis

        remainMinutes.value = remainSeconds / 60
        _remainSeconds.value = remainSeconds % 60
//        Log.d("min' sec", "${remainMin.value.toString()}' ${remainSec.value.toString()}")
    }

    fun completeCountDown(){
        updateRemainTimes(0)
        updateSeekBar(0)
        btnText.value = "Start"

        soundPool.autoPause()
        if(isMuteMode.value == false){
            bellSoundId?.let{ soundId ->
                soundPool.play(soundId, 1F, 1F, 0, -1, 1F)
            }
        }
        else{
            //무음모드일때 타이머 완료 알림
            if(Build.VERSION.SDK_INT >= 26){
                //진동시간, 세기 설정
                vibrator!!.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            }else{ vibrator!!.vibrate(500) }
        }
    }
    fun stopOrStart() {
        if (countState.value!! || progress.value == 0) stopCountDown()
        else startCountDown()
    }
//    fun controlSoundMode(){
//        isMuteMode.value = !isMuteMode.value!!
//
//        if(isMuteMode.value!!) {
//            imgResource.value = R.drawable.ic_baseline_volume_off_24
//            soundPool.autoPause()
//        }
//        else if(_countState.value!!) {
//            imgResource.value = R.drawable.ic_baseline_volume_up_24
//            soundPool.autoResume()
//        }
//    }

    private fun startCountDown(){
        currentCountDownTimer = if(_isFirst.value!!) createCountDownTimer(progress.value!! * 60 * 1000L)
        else createCountDownTimer(_remainTime.value!!)

        currentCountDownTimer?.start()

        tickingSoundId?.let {soundId ->
            if(!isMuteMode.value!!) soundPool.play(soundId,1F, 1F, 0, -1, 1F)
        }
        countState.value = true
        btnText.value = "STOP"
        _isFirst.value = false
    }

    private fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
        countState.value = false
        btnText.value = "START"
    }
}