package bodemann.cassidoo.cube.math

import kotlin.math.max
import kotlin.math.min
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
        const val DELTA: Float = 0.000001f

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

operator fun Vector.div(scalar: Float): Vector = Vector(x / scalar, y / scalar, z / scalar)

operator fun Float.times(vector: Vector): Vector = vector * this

operator fun Vector.plus(other: Vector): Vector = Vector(x + other.x, y + other.y, z + other.z)

fun Vector.normalize(): Vector = this / length

fun Vector.clamp(minimum: Float, maximum: Float): Vector = Vector(
    x.clamp(minimum, maximum),
    y.clamp(minimum, maximum),
    z.clamp(minimum, maximum),
)

fun Float.clamp(minimum: Float, maximum: Float): Float =
    min(max(this, minimum), maximum)