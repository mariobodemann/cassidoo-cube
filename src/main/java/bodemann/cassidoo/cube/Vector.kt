package bodemann.cassidoo.cube

import kotlin.math.sqrt

data class Vector(
    val x: Float = 0.0f,
    val y: Float = 0.0f,
    val z: Float = 0.0f,
) {
    constructor(x: Int = 0, y: Int = 0, z: Int = 0) : this(
        x = x.toFloat(),
        y = y.toFloat(),
        z = z.toFloat()
    )

    constructor(components: Array<Float>) : this(
        x = components.getOrNull(0) ?: 0.0f,
        y = components.getOrNull(1) ?: 0.0f,
        z = components.getOrNull(2) ?: 0.0f,
    )

    companion object {
        val minDelta: Float = 0.001f

        val zero = Vector(0, 0, 0)
    }
}

val Vector.r: Float get() = x
val Vector.g: Float get() = y
val Vector.b: Float get() = z

val Vector.length: Float get() = sqrt(x * x + y * y + z * z)

operator fun Vector.minus(other: Vector): Vector = Vector(
    x = x - other.x,
    y = y - other.y,
    z = z - other.z
)

infix fun Vector.dot(other: Vector): Float = x * other.x + y * other.y + z * other.z

infix fun Vector.cross(other: Vector): Vector = Vector(
    x = y * other.z - z * other.y,
    y = z * other.x - x * other.z,
    z = x * other.y - y * other.x
)

operator fun Vector.times(scalar: Float): Vector = Vector(x * scalar, y * scalar, z * scalar)

operator fun Float.times(vector: Vector): Vector = vector * this

operator fun Vector.plus(other: Vector): Vector = Vector(x + other.x, y + other.y, z + other.z)