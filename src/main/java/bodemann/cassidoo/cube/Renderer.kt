package bodemann.cassidoo.cube

import kotlin.math.abs
import kotlin.math.floor

typealias Color = Vector

data class Image(
    val width: Int,
    val height: Int,
    val pixels: List<Color>,
)

data class Rectangle(
    val point: Vector,
    val a: Vector,
    val b: Vector,
    val color: Color,
) {
    val normal: Vector by lazy { a cross b }
}

data class Ray(
    val point: Vector,
    val direction: Vector,
)

data class Hit(
    val point: Vector,
    val distance: Float,
)

class Renderer(
    val configuration: Configuration,
    val faces: List<Rectangle> = listOf(
        /*top*/
        Rectangle(
            point = Vector(-0.5f, 0.5f, -0.5f),
            a = Vector(0, 0, 1),
            b = Vector(1, 0, 0),
            color = Color(1.0f, 0.0f, 0.0f),
        ),
        /* bottom */
        Rectangle(
            point = Vector(0.5f, -0.5f, 0.5f),
            a = Vector(-1, 0, 0),
            b = Vector(0, 0, -1),
            color = Color(0.0f, 1.0f, 0.0f),
        ),
        /* right */
        Rectangle(
            point = Vector(0.5f, 0.5f, 0.5f),
            a = Vector(0, -1, 0),
            b = Vector(0, 0, -1),
            color = Color(0.0f, 0.0f, 1.0f),
        ),
        /* back */
        Rectangle(
            point = Vector(0.5f, 0.5f, 0.5f),
            a = Vector(-1, 0, 0),
            b = Vector(0, -1, 0),
            color = Color(1.0f, 0.0f, 1.0f),
        ),
        /* left */
        Rectangle(
            point = Vector(-0.5f, 0.5f, 0.5f),
            a = Vector(0, -1, 0),
            b = Vector(0, 0, -1),
            color = Color(0.0f, 1.0f, 1.0f),
        ),
        /* front */
        Rectangle(
            point = Vector(-0.5f, 0.5f, -0.5f),
            a = Vector(1, 0, 0),
            b = Vector(0, -1, 0),
            color = Color(1.0f, 1.0f, 1.0f),
        ),
    )
) {
    fun render() {
        var transformation = configuration.transformation

        do {
            val image = renderImage(transformation)
            if (configuration.animate) {
                println(ansiClearScreen())
            }
            consoleOutput(image)

            if (configuration.animate) {
                Thread.sleep(configuration.frameDelay.toLong()) // because yolo
            }
            transformation = transformation * configuration.transformation
        } while (configuration.animate)
    }

    private fun renderImage(transformation: Matrix): Image {
        val pixels = mutableListOf<Color>()

        for (y in 0 until configuration.height) {
            for (x in 0 until configuration.width) {
                pixels.add(
                    renderPixelWithPerspectiveCamera(x, y, transformation)
                )
            }
        }

        return Image(
            width = configuration.width,
            height = configuration.height,
            pixels = pixels
        )
    }

    private fun renderPixelWithPerspectiveCamera(
        x: Int,
        y: Int,
        transformation: Matrix,
    ): Color {
        val width = configuration.width
        val height = configuration.height

        val cameraPos = configuration.camera.position
        val targetDir = Vector(
            x = x / width.toFloat() - 0.5f,
            y = y / height.toFloat() - 0.5f,
            z = 1.0f
        )

        val hits = faces.associate { origin ->
            val transformed = origin.transform(transformation)
            val hit = transformed.intersects(Ray(cameraPos, targetDir))
            if (hit != null) {
                transformed to hit
            } else {
                null to null
            }
        }.filterValues { it != null }

        val closestHit = hits.minByOrNull { entry ->
            val (_, hit) = entry
            hit?.distance ?: Float.POSITIVE_INFINITY
        }

        return if (
            closestHit != null &&
            closestHit.key != null &&
            closestHit.value != null
        ) {
            // take the color of the face hit
            // use some ~fog~, darkening it based on distance. buggy if too close ...
            (closestHit.key!!.color * (1f / closestHit.value!!.distance)).clamp(0f, 1f)
        } else {
            Color.zero
        }
    }

    private fun consoleOutput(image: Image) {
        print(ansiClearScreen())
        for (y in 0 until configuration.height step 2) {
            for (x in 0 until configuration.width) {
                // draw a top rectangle in foreground, background is second row.
                val foreground = image.pixel(x, y)
                val background = image.pixel(x, y + 1)

                print("${ansiColor(foreground, background)}▀")
            }
            // remove line end color spill
            println(deansi())
        }
    }

    fun ansiColor(
        foreground: Color,
        background: Color
    ): String {
        return when (configuration.colorModel) {
            ColorModel.UNTRUE_COLOR -> {
                val ansiFore = foreground.toAnsi8Bit()
                val ansiBack = background.toAnsi8Bit()
                "\u001b[38;5;${ansiFore};48;5;${ansiBack}m"
            }

            ColorModel.TRUE_COLOR -> {
                val ansiFore = foreground.toAnsi24Bit()
                val ansiBack = background.toAnsi24Bit()
                "\u001b[38;2;${ansiFore};48;2;${ansiBack}m"
            }
        }
    }

}

private fun Rectangle.transform(transformation: Matrix): Rectangle = Rectangle(
    point = transformation * point,
    a = transformation * a,
    b = transformation * b,
    color = color
)

fun Image.pixel(x: Int, y: Int): Color =
    pixels[(y % height) * width + (x % width)]


fun ansiClearScreen() = "\u001b[3J\u001b[H"

fun deansi(): String = "\u001b[m"

private fun Color.toAnsi24Bit(): String =
    "${(this.r * 255).toInt()};${(this.g * 255).toInt()};${(this.b * 255).toInt()}"


private fun Color.toAnsi8Bit(): Int =
    16 +
            floor(r.clamp(0f, 1f) * 6).toInt() * 6 * 6 +
            floor(g.clamp(0f, 1f) * 6).toInt() * 6 +
            floor(b.clamp(0f, 1f) * 6).toInt()

private fun Rectangle.intersects(ray: Ray): Hit? {
    val projection = ray.direction dot normal
    if (abs(projection) < Vector.minDelta) {
        return null
    }

    val d = ((point - ray.point) dot normal) / projection

    return if (d > Vector.minDelta) {
        val x = ray.point + d * ray.direction
        val px = x - point

        val a = this.a dot px
        val b = this.b dot px

        if (
            a in -Vector.minDelta..1.0f &&
            b in -Vector.minDelta..1.0f
        )
            Hit(distance = d, point = x)
        else {
            null
        }
    } else {
        null
    }
}