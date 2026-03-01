package dam.pmdm.spyrothedragon.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dam.pmdm.spyrothedragon.R
import dam.pmdm.spyrothedragon.models.Character

/**
 * Adaptador para gestionar la visualización de una lista de personajes en un RecyclerView.
 */
class CharactersAdapter(
    private val list: List<Character>,
    // Callback para manejar el evento de pulsación larga sobre un personaje
    private val onLongClick: (Character) -> Unit
) : RecyclerView.Adapter<CharactersAdapter.CharactersViewHolder>() {

    /**
     * Mapa que asocia el identificador de imagen del modelo con su recurso drawable correspondiente.
     */
    private val characterImages = mapOf(
        "spyro" to R.drawable.spyro,
        "hunter" to R.drawable.hunter,
        "elora" to R.drawable.elora,
        "ripto" to R.drawable.ripto
    )

    /**
     * Infla el diseño de la celda (cardview) y crea el ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharactersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cardview, parent, false)
        return CharactersViewHolder(view)
    }

    /**
     * Vincula los datos del personaje con las vistas de la celda.
     */
    override fun onBindViewHolder(holder: CharactersViewHolder, position: Int) {
        val character = list[position]
        holder.nameTextView.text = character.name

        // Obtiene el recurso de imagen del mapa. Si no existe, usa un placeholder por defecto.
        val drawableRes = characterImages[character.image] ?: R.drawable.placeholder
        holder.imageImageView.setImageResource(drawableRes)

        // Configura el listener para la pulsación larga en el elemento de la lista
        holder.itemView.setOnLongClickListener {
            onLongClick(character)
            true
        }
    }

    /**
     * Devuelve la cantidad total de elementos en la lista.
     */
    override fun getItemCount(): Int = list.size

    /**
     * Clase interna que mantiene las referencias a las vistas de cada elemento de la lista.
     */
    class CharactersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val imageImageView: ImageView = itemView.findViewById(R.id.image)
    }
}
