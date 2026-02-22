package dam.pmdm.spyrothedragon.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.adapters.WorldsAdapter
import dam.pmdm.spyrothedragon.databinding.FragmentWorldsBinding
import dam.pmdm.spyrothedragon.models.World
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import androidx.navigation.fragment.findNavController

class WorldsFragment : Fragment() {

    private var _binding: FragmentWorldsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WorldsAdapter
    private val worldsList = mutableListOf<World>()

    //añadidos de la tarea

    // Guarda la hora del último clic (en milisegundos)
    private var lastClickTime: Long = 0

    // Cuenta cuántas veces se ha pulsado el mismo mundo seguido
    private var clickCount = 0

    // Almacena qué posición se pulsó para que no valga pulsar mundos distintos
    private var lastClickedPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWorldsBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerViewWorlds
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WorldsAdapter(worldsList) { position ->
            // Cada vez que el adaptador detecta un clic, nos manda la posición aquí
            verificarEasterEgg(position)
        }
        recyclerView.adapter = adapter

        loadWorlds()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadWorlds() {
        try {
            val inputStream: InputStream =
                resources.openRawResource(R.raw.worlds)

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var currentWorld: World? = null

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        when (parser.name) {
                            "world" -> currentWorld = World()
                            "name" -> currentWorld?.name = parser.nextText()
                            "description" -> currentWorld?.description = parser.nextText()
                            "image" -> currentWorld?.image = parser.nextText()
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name == "world" && currentWorld != null) {
                            worldsList.add(currentWorld)
                        }
                    }
                }
                eventType = parser.next()
            }

            adapter.notifyDataSetChanged()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * Gestiona la lógica del Easter Egg midiendo el tiempo y la posición de los clics.
     * @param position Índice del mundo pulsado en el RecyclerView.
     */
    private fun verificarEasterEgg(position: Int) {
        // 1. Capturamos el tiempo actual
        val currentTime = System.currentTimeMillis()

        // 2. LÓGICA DE CONTEO: ¿Es rápido (<1s) y en el mismo sitio?
        if (position == lastClickedPosition && (currentTime - lastClickTime) < 1000) {
            clickCount++ // Incrementamos AQUÍ
        } else {
            clickCount = 1 // Si no, empezamos de nuevo
        }

        // 3. ACTUALIZAMOS las referencias para el próximo clic
        lastClickTime = currentTime
        lastClickedPosition = position

        // 4. COMPROBACIÓN FINAL: Si ya hemos llegado a 3, lanzamos el secreto
        if (clickCount == 3) {
            // Reseteamos para que no se quede en "3" para siempre
            clickCount = 0
            lastClickedPosition = -1

            // LANZAMOS EL DIALOG (que es lo que hemos decidido finalmente)
            mostrarVideoDialog()
        }
    }
    private fun mostrarVideoDialog() {
        // 1. Creamos el Diálogo con fondo negro y pantalla completa
        // Esto asegura que, aunque el vídeo no ocupe todo, el fondo sea negro y no se vea la app detrás.
        val dialog = android.app.Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        // 2. Cargamos el layout que contiene el VideoView
        dialog.setContentView(R.layout.easter_egg_world)

        // 3. Buscamos el VideoView por su ID
        val videoView = dialog.findViewById<VideoView>(R.id.videoViewEaster)

        // 4. Preparamos la ruta del vídeo (res/raw/videoplayback.mp4)
        val uri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.videoplayback}")

        // 5. Asignamos la URI al reproductor
        videoView.setVideoURI(uri)

        // 6. Configuramos qué pasa cuando el vídeo termina
        videoView.setOnCompletionListener {
            // Cerramos el diálogo para volver automáticamente a la lista de mundos
            dialog.dismiss()
        }

        // 7. Mostramos el diálogo en pantalla
        dialog.show()

        // 8. Iniciamos la reproducción
        videoView.start()
    }
}
