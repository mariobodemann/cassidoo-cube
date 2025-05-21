package bodemann.cassidoo.cube

data class Configuration(
    val width: Int,
    val height: Int,
    val camera: Camera,
    val transformation: Matrix,
    val colorModel: ColorModel,
    val frameDelay: Int,
    val animate: Boolean,
)

enum class ColorModel {
    TRUE_COLOR,
    UNTRUE_COLOR
}

data class Camera(
    val position: Vector,
)