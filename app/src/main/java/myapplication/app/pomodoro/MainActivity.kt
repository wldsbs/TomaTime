package myapplication.app.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import myapplication.app.pomodoro.databinding.ActivityMainBinding
import myapplication.app.pomodoro.vm.TimerViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var viewModel: TimerViewModel = TimerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.apply {
            lifecycleOwner = this@MainActivity
        }

        binding.seekBar.setOnSeekBarChangeListener(
            viewModel.createSeekBar()
        )
        initSounds()
    }

    private fun initSounds(){
        viewModel.tickingSoundId = viewModel.soundPool.load(this, R.raw.timer_ticking, 1)
        viewModel.bellSoundId = viewModel.soundPool.load(this, R.raw.timer_bell, 1)
    }

    override fun onResume() {
        super.onResume()
        viewModel.soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.soundPool.release()
    }
}