package myapplication.app.pomodoro.vm

import android.media.SoundPool
import android.os.CountDownTimer
import android.widget.SeekBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TimerViewModel {
    private var _remainTime = MutableLiveData<Long>()
    private var _remainMinutes = MutableLiveData<Long>()
    private var _remainSeconds = MutableLiveData<Long>()

    private var _isMuteMode = MutableLiveData<Boolean>()
    private var _isFirst = MutableLiveData<Boolean>()
    private var _countState = MutableLiveData<Boolean>()
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
    val remainMin: LiveData<Long> get() = _remainMinutes
    val remainSec: LiveData<Long> get() = _remainSeconds

    init {
        _remainMinutes.value = 0L
        _remainSeconds.value = 0L
        _isMuteMode.value = false
        _isFirst.value = true
        _countState.value = false
        progress.value = 0
        btnText.value = "Start"
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

    fun createSeekBar() =
        object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, prog: Int, fromUser: Boolean) {
                if(fromUser){
                    progress.value = prog
                    updateRemainTimes(prog * 60 * 1000L)    //분단위 지정
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                progress.value = seekBar!!.progress
                stopCountDown()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar ?: return

                progress.value = seekBar.progress
                if(seekBar.progress == 0){
                    stopCountDown()
                }
            }
        }

    private fun updateSeekBar(remainMillis: Long){
        progress.value = (remainMillis / 1000 / 60).toInt()
    }

    fun updateRemainTimes(remainMillis: Long){
        val remainSeconds = remainMillis / 1000
        _remainTime.value = remainMillis

        _remainMinutes.value = remainSeconds / 60
        _remainSeconds.value = remainSeconds % 60
//        Log.d("min' sec", "${remainMin.value.toString()}' ${remainSec.value.toString()}")
    }

    fun completeCountDown(){
        updateRemainTimes(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let{ soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)

        }
    }
    fun stopOrStart() {
        if (_countState.value!! || progress.value == 0) stopCountDown()
        else startCountDown()
    }

    private fun startCountDown(){
        currentCountDownTimer = if(_isFirst.value!!) createCountDownTimer(progress.value!! * 60 * 1000L)
        else createCountDownTimer(_remainTime.value!!)

        currentCountDownTimer?.start()

        tickingSoundId?.let {soundId ->
            soundPool.play(soundId,1F, 1F, 0, -1, 1F)
        }
        _countState.value = true
        btnText.value = "STOP"
        _isFirst.value = false
    }

    fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
        _countState.value = false
        btnText.value = "START"
    }
}