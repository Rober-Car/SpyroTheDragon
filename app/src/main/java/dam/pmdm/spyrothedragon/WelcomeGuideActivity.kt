package dam.pmdm.spyrothedragon

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dam.pmdm.spyrothedragon.databinding.ActivityWelcomeGuideBinding

class WelcomeGuideActivity : AppCompatActivity() {


    private lateinit var binding: ActivityWelcomeGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeGuideBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}
