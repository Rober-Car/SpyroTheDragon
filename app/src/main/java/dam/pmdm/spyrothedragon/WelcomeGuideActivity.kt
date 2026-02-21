package dam.pmdm.spyrothedragon.ui

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.media.SoundPool
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dam.pmdm.spyrothedragon.MainActivity
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.databinding.ActivityWelcomeGuideBinding

class WelcomeGuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeGuideBinding

    // Declaración de SoundPool para reproducir efectos de sonido
    private lateinit var soundPool: SoundPool

    // ID del sonido del huevo cargado en SoundPool
    private var huevoSoundId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityWelcomeGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonHuevo.setOnClickListener {

            // 1️ Cambiamos el fondo del ImageView por la animación
            binding.botonHuevo.setImageResource(R.drawable.animation_huevo)

            // 2 Obtenemos el AnimationDrawable
            val animation = binding.botonHuevo.drawable as AnimationDrawable

            // 3 Iniciamos animación
            animation.start()


            // Inicialización de SoundPool para reproducir efectos de sonido cortos
            soundPool = SoundPool.Builder()
                .setMaxStreams(1)   // Número máximo de sonidos simultáneos
                .build()

            // Carga del sonido desde la carpeta res/raw (asegúrate de que el nombre esté en minúsculas)
            huevoSoundId = soundPool.load(this, R.raw.inicial_sound, 1)


            /// Espera 400 ms para que avance la animación del huevo
            binding.botonHuevo.postDelayed({

                huevoSound()   // 🔊 Reproduce el sonido del huevo

                // Espera 1200 ms para dejar que el sonido termine
                binding.botonHuevo.postDelayed({

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    // Transición suave entre actividades
                    overridePendingTransition(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )

                    finish()

                }, 1200)

            }, 100)

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }


    // Función que reproduce el sonido del huevo usando SoundPool
    private fun huevoSound() {
        // Reproduce el sonido cargado: volumen izq, der, prioridad, loop, velocidad
        soundPool.play(huevoSoundId, 1f, 1f, 1, 0, 1f)
    }

}
