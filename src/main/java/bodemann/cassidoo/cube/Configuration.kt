package bodemann.cassidoo.cube

import bodemann.cassidoo.cube.math.Matrix
import bodemann.cassidoo.cube.math.Vector
import bodemann.cassidoo.cube.models.Face
import bodemann.cassidoo.cube.models.cubeFaces
import bodemann.cassidoo.cube.models.tetrahedronFaces

data class Configuration(
    val width: Int,
    val height: Int,
    val transformation: Matrix,
    val target: TargetModel,
    val camera: Camera,
    val colorModel: ColorModel,
    val frameDelay: Int,
    val animate: Boolean,
)

sealed class TargetModel(
    val faces: List<Face>
) {
    object Cube : TargetModel(faces = cubeFaces())
    object Tetrahedron : TargetModel(faces = tetrahedronFaces())
}

enum class ColorModel {
    TRUE_COLOR,
    UNTRUE_COLOR
}

data class Camera(
    val position: Vector,
)