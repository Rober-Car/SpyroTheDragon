package dam.pmdm.spyrothedragon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding
import dam.pmdm.spyrothedragon.ui.SpotlightView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // AÑADIDOS DE LA TAREA INICIO
        /**
         * Se usa .post { ... } porque necesitamos que la interfaz esté
         * totalmente dibujada antes de calcular posiciones. Si no,
         * los valores de ancho y alto serían 0.
         * post {}  Coloca tu código en la cola del hilo principal (UI Thread)
         * Ejecutarlo cuando la vista ya haya terminado su proceso de layout
         *
         * Es como decir:
         *
         * “Haz esto cuando todo esté listo visualmente.”
         */
        binding.navView.post {

            // 1. Instanciamos nuestra vista de foco personalizada
            val spotlight = SpotlightView(this)

            // 2. Accedemos a las "tripas" del BottomNavigationView.
            // El NavView contiene un hijo interno (un ViewGroup) que guarda los iconos.
            val bottomNav = binding.navView
            val menuView = bottomNav.getChildAt(0) as ViewGroup

            // 3. Obtenemos el primer elemento del menú (el índice 0 suele ser "Personajes")
            val itemPersonajes = menuView.getChildAt(0)

            // 4. Calcular la posición exacta en la pantalla.
            // location[0] guardará la X (horizontal) y location[1] la Y (vertical).
            val location = IntArray(2)
            itemPersonajes.getLocationOnScreen(location)

            // 5. Encontrar el centro del icono.
            // Sumamos a la posición inicial la mitad de su ancho/alto.
            val centerX = location[0] + itemPersonajes.width / 2f
            val centerY = location[1] + itemPersonajes.height / 2f

            // 6. Configuramos los parámetros del SpotlightView
            spotlight.centerX = centerX
            spotlight.centerY = centerY
            spotlight.radius = itemPersonajes.width.toFloat() // El radio depende del tamaño del icono

            // 7. Añadimos el foco al contenedor del layout y lo hacemos visible
            binding.overlayGuia.addView(spotlight)
            binding.overlayGuia.visibility = View.VISIBLE
        }


        // AÑADIDOS DE LA TAREA FIN



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
}
