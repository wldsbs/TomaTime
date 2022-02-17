package fastcampus.app.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    //나중에 remainMinutesTextView에 접근을 했을때 findViewById 실행해서 가져옴
    private val remainMinutesTextView: TextView by lazy{
        findViewById(R.id.remainMinutesTextView)
    }

    private val seekBar: SeekBar by lazy{
        findViewById(R.id.seekBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
    }

    //view에 있는 리스너와 실제 로직 연결
    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    remainMinutesTextView.text = "%02d".format(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            }
        )

    }
}