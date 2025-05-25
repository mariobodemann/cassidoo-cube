package bodemann.cassidoo.cube.math

import kotlin.math.cos
import kotlin.math.sin

data class Matrix(
    val coefficients: Array<Float>
) {
    init {
        if (coefficients.size != 9) throw IllegalArgumentException("not a 3x3 matrix.")
    }

    companion object {
        fun eye(): Matrix = Matrix(
            arrayOf(
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f,
            )
        )

        fun scale(s: Vector): Matrix = Matrix(
            arrayOf(
                s.x, 0f, 0f,
                0f, s.y, 0f,
                0f, 0f, s.z,
            )
        )

        fun rotate(yaw: Float, pitch: Float, roll: Float): Matrix =
            rotateZ(yaw) *
                    rotateY(pitch) *
                    rotateX(roll)

        fun rotateX(a: Float): Matrix = Matrix(
            arrayOf(
                1f, 0f, 0f,
                0f, cos(a), -sin(a),
                0f, sin(a), cos(a),
            )
        )

        fun rotateY(a: Float): Matrix = Matrix(
            arrayOf(
                cos(a), 0.0f, sin(a),
                0.0f, 1.0f, 0.0f,
                -sin(a), 0.0f, cos(a)
            )
        )

        fun rotateZ(a: Float): Matrix = Matrix(
            arrayOf(
                cos(a), -sin(a), 0.0f,
                sin(a), cos(a), 0.0f,
                0.0f, 0.0f, 1.0f,
            )
        )
    }
}

operator fun Matrix.times(other: Matrix): Matrix {
    val result = mutableListOf<Float>()

    for (i in 0 until 3) {
        val row = other.row(i)
        for (j in 0 until 3) {
            val column = column(j)
            result.add(row dot column)
        }
    }

    return Matrix(result.toTypedArray())
}

fun Matrix.row(n: Int) = Vector(
    coefficients[n * 3 + 0],
    coefficients[n * 3 + 1],
    coefficients[n * 3 + 2],
)

fun Matrix.column(n: Int) = Vector(
    coefficients[n],
    coefficients[n + 3],
    coefficients[n + 6],
)

operator fun Matrix.times(vector: Vector): Vector =
    Vector(
        column(0) dot vector,
        column(1) dot vector,
        column(2) dot vector,
    )