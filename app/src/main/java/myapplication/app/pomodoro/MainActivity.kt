package myapplication.app.pomodoro

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import myapplication.app.pomodoro.databinding.ActivityMainBinding
import myapplication.app.pomodoro.vm.CountViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var viewModel: CountViewModel = CountViewModel()

    private var countState = false
    private var isFirst = true
    private val button: Button by lazy{
        findViewById(R.id.btn)
    }
    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }
    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.apply {
            lifecycleOwner = this@MainActivity
        }

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    //view에 있는 리스너와 실제 로직 연결
    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if(fromUser){
                        viewModel.updateRemainTimes(progress * 60 * 1000L)    //분단위 지정
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return

                    if(seekBar.progress == 0){
                        stopCountDown()
                    } else{
                        startCountDown()
                    }
                }
            }
        )

        button.setOnClickListener {
            if (countState || seekBar.progress == 0) stopCountDown()
            else startCountDown()
        }
    }

    private fun initSounds(){
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                viewModel.updateRemainTimes(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }
    private fun startCountDown(){
        currentCountDownTimer = if(isFirst) createCountDownTimer(seekBar.progress * 60 * 1000L)
        else createCountDownTimer(viewModel.remainTime.value!!)

        currentCountDownTimer?.start()

        tickingSoundId?.let {soundId ->
            soundPool.play(soundId,1F, 1F, 0, -1, 1F)
        }
        countState = true
        button.text = "STOP"
        isFirst = false
    }

    private fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
        countState = false
        button.text = "START"
    }

    private fun completeCountDown(){
        viewModel.updateRemainTimes(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let{ soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)

        }
    }

    private fun updateSeekBar(remainMillis: Long){
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
}