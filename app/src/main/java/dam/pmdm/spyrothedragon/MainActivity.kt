package dam.pmdm.spyrothedragon

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.media.SoundPool
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding
import dam.pmdm.spyrothedragon.databinding.GuideBinding
import android.view.Gravity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    // AÑADIDOS DE LA TAREA
    //Es el ViewBinding del layout guide.xml.Para acceder a sus views

    private lateinit var guideBinding: GuideBinding

    //atributo de clase para saber en que paso de la guia estamos
    private var guideStep = 0

    private var circleAnimator: AnimatorSet? = null

    // Declaración de SoundPool para reproducir efectos de sonido
    private lateinit var soundPool: SoundPool

    // ID del sonido del bocadillo cargado en SoundPool
    private var soundBocadillo = 0

    // ID del sonido de fin de guía cargado en SoundPool
    private var soundFinGuia = 0

    // Contador de clics realizados por el usuario
    private var clickCount = 0

    // Tiempo (en milisegundos) del último clic registrado
    private var lastClickTime = 0L





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // AÑADIDOS DE LA TAREA

        // inicializacion del binding de guia.xml
        guideBinding = binding.includeLayout


        if (shouldShowGuide()) {

            // Si la función indica que la guía debe mostrarse (por ejemplo,
            // porque es la primera vez que el usuario abre la app),
            // hacemos visible el layout del tutorial.
            guideBinding.guideLayout.visibility = View.VISIBLE

            // Iniciamos la animación o secuencia de pasos del tutorial.
            // Normalmente aquí empieza el paso 1 o la primera animación.
            startGuideAnimation()


        } else {

            // Si la guía NO debe mostrarse (porque ya se completó antes),
            // ocultamos el layout para que no aparezca en pantalla.
            guideBinding.guideLayout.visibility = View.GONE
        }

        // Configuración de los atributos de audio para efectos de sonido tipo juego
        val audioAttributes = android.media.AudioAttributes.Builder()
            .setUsage(android.media.AudioAttributes.USAGE_GAME)                 // Uso orientado a juegos
            .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION) // Sonidos cortos tipo efecto
            .build()

        // Inicialización del SoundPool con un máximo de 3 sonidos simultáneos
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(audioAttributes)
            .build()

        // Carga de los sonidos desde res/raw (asegúrate de que los nombres estén en minúsculas)
        soundBocadillo = soundPool.load(this, R.raw.bocadillo_sound, 1)
        soundFinGuia = soundPool.load(this, R.raw.fin_guia_sound, 1)



        guideBinding.textBocadillo.setOnClickListener {

            val pulsoFinal = getString(R.string.guide_acerca_de)

            if (pulsoFinal!= guideBinding.textBocadillo.text.toString()){

                bocadilloSound()
            }
            nextGuideStep()


        }

        guideBinding.exitGuide.setOnClickListener {

            finishGuide()
            finGuiaSound()
        }

        // FIN AÑADIDOS DE LA TAREA


        val navHostFragment: Fragment? =
            supportFragmentManager.findFragmentById(R.id.navHostFragment)

        navHostFragment?.let {
            navController = NavHostFragment.findNavController(it)

            NavigationUI.setupWithNavController(binding.navView, navController!!)
            NavigationUI.setupActionBarWithNavController(this, navController!!)

        }

        binding.navView.setOnItemSelectedListener { menuItem ->
            selectedBottomMenu(menuItem)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_characters,
                R.id.navigation_worlds,
                R.id.navigation_collectibles -> {
                    // En las pantallas de los tabs no mostramos la flecha atrás
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }

                else -> {
                    // En el resto de pantallas sí
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Solo intenta liberarlo si realmente fue inicializado
        if (::soundPool.isInitialized) {
            soundPool.release()
        }
    }

    private fun selectedBottomMenu(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_characters ->
                navController?.navigate(R.id.navigation_characters)

            R.id.nav_worlds ->
                navController?.navigate(R.id.navigation_worlds)

            else ->
                navController?.navigate(R.id.navigation_collectibles)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.about_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_info) {
            showInfoDialog()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    private fun showInfoDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.title_about)
            .setMessage(R.string.text_about)
            .setPositiveButton(R.string.accept, null)
            .show()
    }

    // AÑADIDOS DE LA TAREA INICIO
    private fun startGuideAnimation() {

        // --- 1. ANIMACIÓN DEL CÍRCULO / ICONO (Efecto Latido Infinito) ---
        // Creamos los animadores para que el icono crezca un 20% (de 1f a 1.2f)
        val scaleX = ObjectAnimator.ofFloat(guideBinding.pulseImage, "scaleX", 1f, 1.2f)
        val scaleY = ObjectAnimator.ofFloat(guideBinding.pulseImage, "scaleY", 1f, 1.2f)

        // INFINITE: La animación no termina nunca mientras la vista sea visible
        scaleX.repeatCount = ValueAnimator.INFINITE
        // REVERSE: Al llegar al final (1.2f), vuelve hacia atrás (1f) creando el efecto de pulsación suave
        scaleX.repeatMode = ValueAnimator.REVERSE

        scaleY.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatMode = ValueAnimator.REVERSE

        // Agrupamos las escalas del icono para que ocurran exactamente al mismo tiempo
        circleAnimator = AnimatorSet()
        circleAnimator?.playTogether(scaleX, scaleY)
        circleAnimator?.duration = 800
        circleAnimator?.start()

        // --- 2. ANIMACIÓN DEL BOCADILLO (Aparición con escala) ---
        // fadeIn: Pasa de transparencia total (0) a opacidad total (1)
        val fadeIn = ObjectAnimator.ofFloat(guideBinding.textBocadillo, "alpha", 0f, 1f)

        // Estas escalas hacen que el bocadillo parezca que "brota" o se expande al aparecer
        // Empieza en 0.8f (más pequeño) y termina en su tamaño natural 1f
        val scaleBubbleX = ObjectAnimator.ofFloat(guideBinding.textBocadillo, "scaleX", 0.7f, 1f)
        val scaleBubbleY = ObjectAnimator.ofFloat(guideBinding.textBocadillo, "scaleY", 0.7f, 1f)

        // Agrupamos las tres animaciones del bocadillo (transparencia + escalas X e Y)
        val bubbleAnimator = AnimatorSet()
        bubbleAnimator.playTogether(fadeIn, scaleBubbleX, scaleBubbleY)

        // Una duración de 500ms lo hace sentir ágil y dinámico
        bubbleAnimator.duration = 1000
        bubbleAnimator.start()


    }


    // Avanza al siguiente paso del tutorial
    // Avanza al siguiente paso del tutorial y actualiza la posición del pulso y el texto
    private fun nextGuideStep() {

        // Aumenta el número de paso actual del tutorial
        guideStep++

        // Obtiene los parámetros de posición del icono de pulso dentro del FrameLayout
        val params = guideBinding.pulseImage.layoutParams as FrameLayout.LayoutParams

        // Determina qué hacer según el paso actual
        when (guideStep) {

            // 🔵 Paso 1 → MUNDOS (botón central)
            // En este paso se cambia el texto del bocadillo y se recoloca el pulso en el centro
            1 -> {

                // Cambia el texto del bocadillo para explicar la sección "Mundos"
                guideBinding.textBocadillo.text =
                    getString(R.string.guide_mundos)

                navController?.navigate(R.id.navigation_worlds)


                // Coloca el pulso en la parte inferior centrada del BottomNavigation
                params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL

                // Ajusta la altura del pulso para que quede alineado visualmente
                params.bottomMargin = -50

                // Ajuste fino horizontal para centrarlo exactamente sobre el icono
                guideBinding.pulseImage.translationX = -17f

                // Aplica los nuevos parámetros al icono de pulso
                guideBinding.pulseImage.layoutParams = params


            }

            // 🟢 Paso 2 → COLECCIONABLES (botón derecho)
            // En este paso se cambia el texto y se mueve el pulso hacia la derecha
            2 -> {

                // Cambia el texto del bocadillo para explicar la sección "Coleccionables"
                guideBinding.textBocadillo.text =
                    getString(R.string.guide_coleccionables)

                navController?.navigate(R.id.navigation_collectibles)

                // Coloca el pulso en la parte inferior derecha del BottomNavigation
                params.gravity = Gravity.BOTTOM or Gravity.END

                // Ajusta la altura del pulso para que quede alineado visualmente
                params.bottomMargin = -50

                // Ajuste fino horizontal para que el pulso quede exactamente sobre el icono
                guideBinding.pulseImage.translationX = -18f

                // Aplica los nuevos parámetros al icono de pulso
                guideBinding.pulseImage.layoutParams = params
            }


            3 -> {

                // Cambiamos el texto del bocadillo por el mensaje "Acerca de"
                guideBinding.textBocadillo.text =
                    getString(R.string.guide_acerca_de)

                // Ocultamos la animación de "pulso" porque en este paso no se usa
                guideBinding.pulseImage.visibility = View.GONE

                // Mostramos la flecha que indica información adicional
                guideBinding.arrowInfo.visibility = View.VISIBLE

                // Creamos una animación vertical suave tipo rebote
                val translate = ObjectAnimator.ofFloat(
                    guideBinding.arrowInfo,   // Vista a animar
                    "translationY",           // Propiedad que cambia
                    0f,                       // Posición inicial
                    20f                       // Posición final (baja 20px)
                )

                // La animación se repetirá infinitamente
                translate.repeatCount = ValueAnimator.INFINITE

                // Cuando llegue al final, vuelve hacia atrás (efecto rebote)
                translate.repeatMode = ValueAnimator.REVERSE

                // Duración de cada ciclo de animación (0.6 segundos)
                translate.duration = 600

                // Iniciamos la animación
                translate.start()
            }

        }
    }


    //  Paso final → Ocultar la guía
    // Cuando ya no hay más pasos, se oculta el overlay del tutorial
    private fun finishGuide() {

        // Ocultamos la flecha de información
        guideBinding.arrowInfo.visibility = View.GONE

        // Mostramos de nuevo la imagen de pulso (estado inicial)
        guideBinding.pulseImage.visibility = View.VISIBLE

        // Detenemos la animación del círculo si estaba activa
        circleAnimator?.cancel()

        // Ocultamos por completo el layout del tutorial
        guideBinding.guideLayout.visibility = View.GONE

        // Reiniciamos el contador de pasos para empezar desde cero
        guideStep = 0


        navController?.navigate(R.id.navigation_characters)
        // Guardamos que la guía ya se completó


       // saveGuideCompleted()
    }

    // Guarda en las preferencias que el usuario ya ha completado la guía/tutorial
    private fun saveGuideCompleted() {

        // Accedemos al archivo de preferencias de la app.
        // "app_prefs" es el nombre del archivo donde guardamos valores persistentes.
        // MODE_PRIVATE significa que solo tu app puede leer este archivo.
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Editamos las preferencias para guardar un valor booleano.
        // "guide_completed" será la clave que usaremos para saber si el tutorial ya se mostró.
        // Guardamos 'true' para indicar que el usuario ya lo completó.
        // apply() guarda los cambios de forma asíncrona (más eficiente que commit()).
        prefs.edit().putBoolean("guide_completed", true).apply()
    }

    // Determina si se debe mostrar la guía/tutorial
    private fun shouldShowGuide(): Boolean {

        //  Accedemos al archivo de preferencias llamado "app_prefs".
        // Si no existe, Android lo crea automáticamente.
        // MODE_PRIVATE significa que solo tu app puede leer este archivo.
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

        // Leemos el valor booleano guardado bajo la clave "guide_completed".
        // - Si existe y es true → el usuario ya completó la guía.
        // - Si no existe → devuelve el valor por defecto (false).
        //
        // Como queremos saber si DEBEMOS mostrar la guía,
        // devolvemos el valor negado:
        //   - Si guide_completed = true  → !true  = false  → NO mostrar guía
        //   - Si guide_completed = false → !false = true   → SÍ mostrar guía
        return !prefs.getBoolean("guide_completed", false)
    }


    // Reproduce el sonido del bocadillo usando SoundPool
    private fun bocadilloSound() {
        // play(idSonido, volumenIzq, volumenDer, prioridad, loop, velocidad)
        soundPool.play(soundBocadillo, 1f, 1f, 1, 0, 1f)
    }

    // Reproduce el sonido de fin de guía usando SoundPool
    private fun finGuiaSound() {
        soundPool.play(soundFinGuia, 1f, 1f, 1, 0, 1f)
    }



}


