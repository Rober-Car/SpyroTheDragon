package dam.pmdm.spyrothedragon.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dam.pmdm.spyrothedragon.MainActivity
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.databinding.ActivityWelcomeGuideBinding

class WelcomeGuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonHuevo.setOnClickListener {

            // 1️⃣ Cambiamos el fondo del ImageView por la animación
            binding.botonHuevo.setImageResource(R.drawable.animation_huevo)

            // 2️⃣ Obtenemos el AnimationDrawable
            val animation = binding.botonHuevo.drawable as AnimationDrawable

            // 3️⃣ Iniciamos animación
            animation.start()

            // 4️⃣ Esperamos a que termine (400ms aprox)
            binding.botonHuevo.postDelayed({

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                overridePendingTransition(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )

                finish()

            }, 400) // Ajusta si cambias duración
        }
    }
}
