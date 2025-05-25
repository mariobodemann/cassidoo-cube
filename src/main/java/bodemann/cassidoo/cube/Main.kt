@file:JvmName("Main")

package bodemann.cassidoo.cube

import bodemann.cassidoo.cube.math.Matrix
import bodemann.cassidoo.cube.math.Vector
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    var key = ""
    val options = args.associate {
        if (it.startsWith("--")) {
            if (it.contains("=")) {
                val (k, v) = it.split("=")
                k.drop(2) to v
            } else {
                key = it
                null to null
            }
        } else {
            if (key.isNotBlank()) {
                val backupKey = key
                key = ""
                backupKey to it
            } else {
                key = it
                null to null
            }
        }
    }.filter { (k, v) -> k != null && k.isNotBlank() && v != null }
        .toMutableMap()

    val configuration = Configuration(
        width = options.remove("width")?.toInt() ?: 32,
        height = options.remove("height")?.toInt() ?: 32,
        transformation = options.remove("transform")?.toTargetTransformation(options) ?: Matrix.rotate(0f, 45f, 0f),
        target = options.remove("target")?.toTargetModel() ?: TargetModel.Cube,
        camera = options.remove("camera")?.toCamera() ?: Camera(Vector(0, 0, -2)),
        colorModel = options.remove("colormodel")?.toColorModel() ?: ColorModel.TRUE_COLOR,
        frameDelay = options.remove("framedelay")?.toInt() ?: 32,
        animate = options.remove("animate")?.toBoolean() ?: false,
    )

    if (key.isNotEmpty()) {
        when (key) {
            "--help", "-h" -> {
                printHelp()
                exitProcess(-32)
            }

            else -> {
                println("Error: Dangling option found: $key. Violently crashing.")
                exitProcess(-4)
            }
        }
    }

    if (!options.isEmpty()) {
        println(
            "Error: Found these optional parameter: ${
                options.keys.joinToString(", ") { "$it=${options[it]}" }
            }. Exiting.")
        exitProcess(-123)
    }

    println("Cube configuration: $configuration.")

    val renderer = Renderer(configuration)
    renderer.render()
}

private fun String.toTargetTransformation(options: MutableMap<String?, String?>): Matrix {
    return if (contains("r")) {
        val (_, x, y, z) = split(",")
        Matrix.rotate(
            Math.toRadians(z.toDoubleOrNull() ?: 0.0).toFloat(),
            Math.toRadians(y.toDoubleOrNull() ?: 0.0).toFloat(),
            Math.toRadians(x.toDoubleOrNull() ?: 0.0).toFloat(),
        )
    } else {
        Matrix.eye()
    }
}

private fun String.toCamera(): Camera = Camera(
    position = toVector()
)

private fun String.toColorModel(): ColorModel = when (this) {
    "24" -> ColorModel.TRUE_COLOR
    "8" -> ColorModel.UNTRUE_COLOR
    else -> ColorModel.UNTRUE_COLOR.also { println("ERROR $this not a valid color model. Defaulting to $it.") }
}

private fun String.toVector(): Vector {
    val split = split(",")

    val components = if (split.size < 3) {
        val missing = 3 - split.size
        split.map {
            it.toFloatOrNull() ?: 0.0f
        } + (0 until missing).map { 0.0f }
    } else {
        split.map { it.toFloatOrNull() ?: 0.0f }
    }.toFloatArray().toTypedArray()

    return Vector(components)
}

private fun String.toTargetModel(): TargetModel = when (this) {
    "cube" -> TargetModel.Cube
    "tetrahedron" -> TargetModel.Tetrahedron
    else -> TargetModel.Cube.also { println("ERROR $this not a valid target model. Defaulting to $it.") }
}

private fun printHelp() = println(
    """
    ~~~ Cassidoo Cube ~~~
    
    This is the result of getting inspired by something amazing. Drawing a cube. In the command line.
    Thanks @cassidoo. 
    
    
    https://buttondown.com/cassidoo/archive/imagination-is-more-important-than-knowledge/ 
    (look for the interview question of the week)
    
    By default this app will render a rotating cube in your terminal. Overengineered an nice.
    
    There are some options, you might use these:
    
    --width=[INT] -------------+------ size of the window to render cube in
    --height=[INT] -----------/
    --transform=r,YAW,PITCH,ROLL ----- to rotate, will be applied continuously when rotating 
    --camera=[X,Y,Z] ----------------- position of the camera
    --colormodel={8|24} -------------- bit depth of color mode (not all terminal support 24!)
    --framedelay=[INT] --------------- delay after each cube is drawn. THREAD.SLEEP MATE!
    --animate=[BOOLEAN] -------------- animate the cube?
    --help --------------------------- You got this.
    
    Examples:
    
    > ./gradlew assemble # build it!
    > kotlin -cp build/libs/cassidoo-cube-1.0-SNAPSHOT.jar bodemann.cassidoo.cube.Main \
      --transform=r,12,3,01 --animate=true --framedelay=32 --camera=0,0,-2
      
    This will build the app first (!) and then uses kotlin(!), sets the classpath to the jar this 
    build created, and displays a cube that rotates arround all three axis.
    
    > alias cassidoo-cube="kotlin -cp ${'$'}{PWD}/build/libs/cassidoo-cube-1.0-SNAPSHOT.jar \
       bodemann.cassidoo.cube.Main --transform=r,1,2,3 --animate=true"
       
    The above snippet will create an alias 'cassidoo-cube' that renders the same cube, no matter
    if you are in the project folder or not. Magic.
    
    Use [kitty](https://sw.kovidgoyal.net/kitty/) "The fast, feature-rich, GPU based 
    terminal emulator" for best experience.
    
    Thanks for this rabbit hole and nerd snipe. 💚
    
""".trimIndent()
)