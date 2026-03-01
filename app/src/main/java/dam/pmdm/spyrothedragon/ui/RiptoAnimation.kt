package dam.pmdm.spyrothedragon.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.graphics.scale
import dam.pmdm.spyrothedragon.R

/**
 * Vista personalizada que dibuja una animación especial para Ripto.
 * Se dibuja directamente sobre el Canvas (lienzo).
 */
class RiptoAnimation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Imagen de Ripto
    private var riptoBitmap: Bitmap

    // Progreso de la animación (va de 0 a 1)
    private var animValue = 0f

    // Motor que genera los números para la animación
    private var animator: ValueAnimator? = null

    // --- Pinceles (Paints) para dibujar ---

    // Pincel para el brillo de la magia (efecto de luz)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Modo SCREEN para que los colores se sumen y brillen
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)
        // Difumina los bordes para que parezca una nube de luz
        maskFilter = BlurMaskFilter(80f, BlurMaskFilter.Blur.NORMAL)
    }

    // Pincel para la onda circular (solo el borde)
    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    // Pincel para el centro brillante de la gema
    private val nucleoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFFDE7")
        style = Paint.Style.FILL
        maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
    }

    // Colores para el degradado del sol/fuego (de centro a fuera)
    private val coloresSolares = intArrayOf(
        Color.parseColor("#FFFDE7"), // Crema
        Color.parseColor("#FFB300"), // Naranja
        Color.RED,                   // Rojo
        Color.TRANSPARENT            // Transparente
    )
    
    // Dónde cambia cada color (0 es el centro, 1 es el borde)
    private val paradasColores = floatArrayOf(0f, 0.1f, 0.4f, 1f)

    init {
        // Desactivamos la aceleración por hardware para que funcionen los filtros de desenfoque
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // Cargamos y escalamos la imagen para ahorrar memoria
        val original = BitmapFactory.decodeResource(resources, R.drawable.ripto)
        riptoBitmap = original.scale(original.width / 2, original.height / 2)
        if (original != riptoBitmap) original.recycle()
    }

    // Se inicia la animación cuando la vista aparece
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    // Limpiamos recursos cuando la vista desaparece
    override fun onDetachedFromWindow() {
        animator?.cancel()
        if (!riptoBitmap.isRecycled) riptoBitmap.recycle()
        super.onDetachedFromWindow()
    }

    // Aquí es donde se dibuja todo capa por capa
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Fondo negro
        canvas.drawColor(Color.BLACK)

        // 2. Calculamos dónde centrar a Ripto
        val left = (width - riptoBitmap.width) / 2f
        val top = (height - riptoBitmap.height) / 2f

        // 3. Posición de la gema del bastón
        val diamondX = left + (riptoBitmap.width * 0.50f) - 320f
        val diamondY = top + (riptoBitmap.height * 0.42f)

        // --- Dibujo de elementos ---

        // Dibujamos a Ripto
        canvas.drawBitmap(riptoBitmap, left, top, null)

        // Dibujamos el resplandor circular que crece y se encoge
        val glowRadius = 50f + (animValue * 450f)
        val gradient = RadialGradient(
            diamondX, diamondY, glowRadius,
            coloresSolares, paradasColores, Shader.TileMode.CLAMP
        )
        glowPaint.shader = gradient
        glowPaint.alpha = (animValue * 245).toInt()
        canvas.drawCircle(diamondX, diamondY, glowRadius, glowPaint)

        // Dibujamos el centro de la gema
        nucleoPaint.alpha = (200 + (55 * animValue)).toInt()
        canvas.drawCircle(diamondX, diamondY, 20f, nucleoPaint)

        // Dibujamos la onda dorada exterior
        val waveRadius = 60f + (animValue * 590f)
        wavePaint.color = Color.parseColor("#FFD600")
        wavePaint.alpha = (130 * (1 - animValue)).toInt()
        canvas.drawCircle(diamondX, diamondY, waveRadius, wavePaint)
    }

    // Configura la animación infinita
    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1800
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()

            addUpdateListener {
                animValue = it.animatedValue as Float
                // Refresca la pantalla para dibujar el siguiente frame
                invalidate() 
            }
            start()
        }
    }
}
