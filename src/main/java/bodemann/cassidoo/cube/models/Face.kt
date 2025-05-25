package bodemann.cassidoo.cube.models

import bodemann.cassidoo.cube.Color
import bodemann.cassidoo.cube.Hit
import bodemann.cassidoo.cube.Ray
import bodemann.cassidoo.cube.math.Vector
import bodemann.cassidoo.cube.math.Vector.Companion.DELTA
import bodemann.cassidoo.cube.math.cross
import bodemann.cassidoo.cube.math.dot
import bodemann.cassidoo.cube.math.minus
import bodemann.cassidoo.cube.math.normalize
import bodemann.cassidoo.cube.math.plus
import bodemann.cassidoo.cube.math.times
import kotlin.math.abs

sealed class Face(
    open val color: Color
) {
    data class Rectangle(
        val point: Vector,
        val a: Vector,
        val b: Vector,
        override val color: Color,
    ) : Face(color) {
        val normal: Vector = a cross b

        override fun intersects(ray: Ray): Hit? {
            val projection = ray.direction dot normal
            if (abs(projection) < DELTA) {
                return null
            }

            val d = ((point - ray.point) dot normal) / projection

            return if (d > DELTA) {
                val intersectionPoint = ray.point + d * ray.direction
                val p = intersectionPoint - point

                val dot_aa = a dot a
                val dot_ab = a dot b
                val dot_bb = b dot b
                val dot_ap = a dot p
                val dot_bp = b dot p
                val denominator = dot_aa * dot_bb - dot_ab * dot_ab

                if (abs(denominator) < DELTA) {
                    return null // Parallel or degenerate triangle
                }

                val u = (dot_bb * dot_ap - dot_ab * dot_bp) / denominator
                val v = (dot_aa * dot_bp - dot_ab * dot_ap) / denominator

                if (
                    u in -DELTA..1.0f + DELTA &&
                    v in -DELTA..1.0f + DELTA
                ) {
                    Hit(distance = d, point = p)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    data class Triangle(
        val points: List<Vector>,
        override val color: Color,
    ) : Face(color) {
        val a: Vector = points[1] - points[0]
        val b: Vector = points[2] - points[0]
        val normal: Vector = (a cross b).normalize()

        override fun intersects(ray: Ray): Hit? {
            val projection = ray.direction dot normal
            if (abs(projection) < DELTA) {
                return null
            }

            val d = ((points[0] - ray.point) dot normal) / projection

            return if (d > DELTA) {
                val intersectionPoint = ray.point + d * ray.direction
                val p = intersectionPoint - points[0]

                val dot_aa = a dot a
                val dot_ab = a dot b
                val dot_bb = b dot b
                val dot_ap = a dot p
                val dot_bp = b dot p
                val denominator = dot_aa * dot_bb - dot_ab * dot_ab

                if (abs(denominator) < DELTA) {
                    return null // Parallel or degenerate triangle
                }

                val u = (dot_bb * dot_ap - dot_ab * dot_bp) / denominator
                val v = (dot_aa * dot_bp - dot_ab * dot_ap) / denominator
                if (
                    u in -DELTA..1.0f + DELTA &&
                    v in -DELTA..1.0f + DELTA &&
                    (u + v) in -DELTA..1.0f + DELTA
                ) {
                    Hit(distance = d, point = intersectionPoint)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }

    abstract fun intersects(ray: Ray): Hit?
}