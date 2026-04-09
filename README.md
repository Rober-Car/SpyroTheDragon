## ⭐ Características principales

- Navegación mediante menú inferior con tres secciones principales: **Personajes, Mundos y Coleccionables**.
- Visualización de datos mediante **RecyclerView**, cargando la información desde archivos XML.
- Guía interactiva inicial que explica al usuario cómo usar la aplicación paso a paso.
- Uso de animaciones para mejorar la experiencia visual (efecto pulso, aparición de elementos, etc.).
- Implementación de sonidos en acciones clave para hacer la app más dinámica.
- Easter Egg oculto en la sección de personajes, activado mediante pulsación prolongada sobre Ripto, que muestra una animación personalizada creada con Canvas.

---

##  Tecnologías utilizadas

- **Kotlin** como lenguaje principal.
- **Android Studio** como entorno de desarrollo.
- **RecyclerView** para mostrar listas dinámicas.
- **Navigation Component** (NavHostFragment + BottomNavigationView) para la navegación.
- **ViewBinding** para acceder a las vistas de forma segura.
- **SoundPool** para reproducir efectos de sonido.
- **Canvas y ValueAnimator** para crear animaciones personalizadas.
- **XML** para layouts y almacenamiento de datos.

---

## Instrucciones de uso

1. Clonar el repositorio: `git clone https://github.com/tu-usuario/tu-repositorio.git`

2. Abrir el proyecto con Android Studio.

3. Esperar a que Gradle sincronice todas las dependencias automáticamente.

4. Ejecutar la aplicación en:
   - Un emulador, o
   - Un dispositivo Android físico.

**No es necesaria ninguna configuración adicional.**

---

## Conclusiones del desarrollador

En este proyecto la base de la aplicación ya estaba bastante avanzada, por lo que el trabajo principal no ha sido tanto crear la app desde cero, sino centrarse en la parte multimedia y en mejorar la experiencia del usuario.

Lo más importante ha sido aprender a integrar elementos como sonidos, animaciones y efectos visuales dentro de una aplicación ya estructurada. Por ejemplo, el uso de SoundPool para reproducir efectos o la creación de animaciones personalizadas con Canvas, que ha sido la parte más compleja.

Uno de los mayores retos ha sido entender bien cómo funcionan las vistas personalizadas y su ciclo de vida, ya que pequeños detalles como los constructores o el momento en el que se inicia una animación pueden hacer que la app falle.
