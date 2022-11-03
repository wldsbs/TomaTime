package myapplication.app.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import myapplication.app.pomodoro.databinding.ActivityMainBinding
import myapplication.app.pomodoro.vm.TimerViewModel

class MainActivity : AppCompatActivity() {
    //todo 무음모드일때 카운트다운 끝나면 어떻게 알려주지,,,?
    private lateinit var binding: ActivityMainBinding
    private var viewModel: TimerViewModel = TimerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.apply {
            lifecycleOwner = this@MainActivity
        }

        binding.volumeBtn.setOnClickListener{
            controlSoundMode()
        }
        initSounds()
    }

    private fun controlSoundMode(){
        viewModel.isMuteMode.value = !viewModel.isMuteMode.value!!

        if(viewModel.isMuteMode.value!!) binding.volumeBtn.setImageResource(R.drawable.ic_baseline_volume_off_24)
        else binding.volumeBtn.setImageResource(R.drawable.ic_baseline_volume_up_24)

        if(viewModel.isMuteMode.value!!) {
            viewModel.soundPool.autoPause()
        }
        else if(viewModel.countState.value!! && viewModel.isMuteMode.value == false) {
            viewModel.soundPool.autoResume()
        }
    }

    private fun initSounds(){
        viewModel.tickingSoundId = viewModel.soundPool.load(this, R.raw.timer_ticking, 1)
        viewModel.bellSoundId = viewModel.soundPool.load(this, R.raw.timer_bell, 1)
    }

    override fun onResume() {
        super.onResume()
        if(!viewModel.isMuteMode.value!!) viewModel.soundPool.autoResume()
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