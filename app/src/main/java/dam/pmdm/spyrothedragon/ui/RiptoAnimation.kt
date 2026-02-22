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
 * Vista de Ripto con efecto de "Súper Nova Solar".
 * La luz es masiva, difusa y con colores cálidos que imitan al sol.
 */
class RiptoAnimation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var riptoBitmap: Bitmap
    private var animValue = 0f // Factor de animación de 0.0 a 1.0
    private var animator: ValueAnimator? = null

    // --- CONFIGURACIÓN DE PINCELES (Paints) ---

    // Pincel para el gran resplandor expansivo
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // SCREEN: Mezcla los colores sumando brillo. Clave para efectos de fuego/luz.
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SCREEN)

        // BlurMaskFilter: Difuminado extremo (80f).
        // Hace que el borde del círculo sea invisible y parezca una nube de gas/luz.
        maskFilter = BlurMaskFilter(80f, BlurMaskFilter.Blur.NORMAL)
    }

    // Pincel para la onda de choque dorada
    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f // Línea fina para no romper la estética del brillo suave
    }

    // Pincel para el centro físico del diamante
    private val nucleoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Color crema cálido (Sol), huyendo del blanco frío anterior
        color = Color.parseColor("#FFFDE7")
        style = Paint.Style.FILL
        // Un ligero desenfoque para que el núcleo no parezca un "pegote"
        maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
    }

    // --- PALETA DE COLORES SOLARES ---
    // Definimos la progresión: Crema -> Naranja -> Rojo -> Transparente
    private val coloresSolares = intArrayOf(
        Color.parseColor("#FFFDE7"), // 0%: El centro más caliente
        Color.parseColor("#FFB300"), // 10%: Transición a naranja
        Color.RED,                   // 40%: El cuerpo principal de la magia es rojo
        Color.TRANSPARENT            // 100%: Desvanecimiento total en el espacio
    )

    // Indica en qué parte del radio (0.0 a 1.0) ocurre cada cambio de color
    private val paradasColores = floatArrayOf(0f, 0.1f, 0.4f, 1f)

    init {
        // OBLIGATORIO: Desactivar aceleración GPU para que los BlurMaskFilter se dibujen.
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        // Cargamos y escalamos a la mitad (ahorro de memoria RAM)
        val original = BitmapFactory.decodeResource(resources, R.drawable.ripto)
        riptoBitmap = original.scale(original.width / 2, original.height / 2)

        if (original != riptoBitmap) original.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnimation()
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        if (!riptoBitmap.isRecycled) riptoBitmap.recycle()
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 1. Fondo negro (Contraste máximo para el brillo)
        canvas.drawColor(Color.BLACK)

        // 2. Centrado de Ripto en la pantalla
        val left = (width - riptoBitmap.width) / 2f
        val top = (height - riptoBitmap.height) / 2f

        // 3. Posición de la gema con tu ajuste manual (-320f a la izquierda)
        val diamondX = left + (riptoBitmap.width * 0.50f) - 320f
        val diamondY = top + (riptoBitmap.height * 0.42f)

        // --- DIBUJO POR CAPAS ---

        // CAPA 1: Personaje (Se queda debajo para ser bañado por la luz)
        canvas.drawBitmap(riptoBitmap, left, top, null)

        // CAPA 2: Mega Resplandor (Glow)
        // Radio masivo: llega hasta 500 píxeles de radio
        val glowRadius = 50f + (animValue * 450f)

        // El degradado se recalcula en cada frame para adaptarse al radio que crece
        val gradient = RadialGradient(
            diamondX, diamondY, glowRadius,
            coloresSolares, paradasColores, Shader.TileMode.CLAMP
        )

        glowPaint.shader = gradient
        // La opacidad sube hasta 245 (casi opaco en el máximo brillo)
        glowPaint.alpha = (animValue * 245).toInt()

        // Dibujamos la gran esfera de luz solar
        canvas.drawCircle(diamondX, diamondY, glowRadius, glowPaint)

        // CAPA 3: Núcleo (El centro de la gema)
        // Aumentamos el tamaño (20f) para que destaque en medio de tanta luz
        nucleoPaint.alpha = (200 + (55 * animValue)).toInt()
        canvas.drawCircle(diamondX, diamondY, 20f, nucleoPaint)

        // CAPA 4: Onda Expansiva de Oro
        // Viaja todavía más lejos (hasta 650f) para dar sensación de explosión
        val waveRadius = 60f + (animValue * 590f)
        wavePaint.color = Color.parseColor("#FFD600") // Color oro brillante
        wavePaint.alpha = (130 * (1 - animValue)).toInt() // Se desvanece al expandirse

        canvas.drawCircle(diamondX, diamondY, waveRadius, wavePaint)
    }

    /**
     * Animación cíclica de 1.8 segundos para un efecto majestuoso y lento.
     */
    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1800 // Tiempo total del pulso
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()

            addUpdateListener {
                animValue = it.animatedValue as Float
                invalidate() // Forzar llamada a onDraw()
            }
            start()
        }
    }
}