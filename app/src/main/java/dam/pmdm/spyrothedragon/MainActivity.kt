package dam.pmdm.spyrothedragon

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        // AÑADIDOS DE LA TAREA




        // inicializacion del binding de guia.xml
        guideBinding = binding.includeLayout


        //Esta instrucción cambia el estado de visibilidad de una vista
        //View.VISIBLE: Es una constante que le dice al sistema:
        // "Dibuja este elemento en pantalla y que ocupe su espacio correspondiente"
        guideBinding.guideLayout.visibility = View.VISIBLE
        startGuideAnimation()

        guideBinding.textBocadillo.setOnClickListener {
            nextGuideStep()
        }

        guideBinding.exitGuide.setOnClickListener {

            finishGuide()
        }

        // AÑADIDOS DE LA TAREA


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
    // Avanza al siguiente paso del tutorial
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

                // Cambia el texto del bocadillo para explicar la sección "información"
                guideBinding.textBocadillo.text =
                    getString(R.string.guide_acerca_de)

                // Coloca el pulso en la parte inferior derecha del BottomNavigation
                params.gravity = Gravity.TOP or Gravity.END

                // Ajusta la altura del pulso para que quede alineado visualmente
                params.topMargin = -120

                guideBinding.pulseImage.translationX = -25f

                // Aplica los nuevos parámetros al icono de pulso
                guideBinding.pulseImage.layoutParams = params
            }
        }
    }


    // 🔚 Paso final → Ocultar la guía
// Cuando ya no hay más pasos, se oculta el overlay del tutorial
    private fun finishGuide() {

        //Detener animaciones del círculo
        circleAnimator?.cancel()
        // Oculta completamente el layout del tutorial
        guideBinding.guideLayout.visibility = View.GONE
        //resetear el contador
        guideStep = 0
    }
}


