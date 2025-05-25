package bodemann.cassidoo.cube.models

import bodemann.cassidoo.cube.math.Vector
import bodemann.cassidoo.cube.math.clamp
import bodemann.cassidoo.cube.math.minus
import bodemann.cassidoo.cube.math.normalize
import bodemann.cassidoo.cube.math.plus
import bodemann.cassidoo.cube.math.div
import bodemann.cassidoo.cube.math.times
import bodemann.cassidoo.cube.models.Face.Rectangle
import bodemann.cassidoo.cube.models.Face.Triangle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

typealias Color = Vector

fun tetrahedronFaces(): List<Triangle> = listOf(
    /*front*/
    Triangle(
        points = listOf(
            Vector(0.0f, 0.5f, 0.0f),
            Vector(0.5f, -0.5f, -0.5f),
            Vector(-0.5f, -0.5f, -0.5f),
        ),
        color = Color(1f, 1f, 1f),
    ),
    /*right*/
    Triangle(
        points = listOf(
            Vector(0.0f, 0.5f, 0.0f),
            Vector(0.5f, -0.5f, -0.5f),
            Vector(0.0f, -0.5f, 0.5f),
        ),
        color = Color(0f, 1f, 0f),
    ),
    /*left*/
    Triangle(
        points = listOf(
            Vector(0.0f, 0.5f, 0.0f),
            Vector(-0.5f, -0.5f, -0.5f),
            Vector(0.0f, -0.5f, 0.5f),
        ),
        color = Color(0f, 0f, 1f),
    ),
    /*bottom*/
    Triangle(
        points = listOf(
            Vector(0.5f, -0.5f, -0.5f),
            Vector(-0.5f, -0.5f, -0.5f),
            Vector(0.0f, -0.5f, 0.5f),
        ),
        color = Color(1f, 0f, 0f),
    ),
)

fun cubeFaces(): List<Rectangle> = listOf(
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


fun createPolarSphere(radius: Float, verticalSubdivisions: Int, horizontalSubdivisions: Int): List<Face> {
    val faces = mutableListOf<Face>()

    // North Pole triangles
    for (i in 0 until horizontalSubdivisions) {
        val angle1 = 2 * PI.toFloat() * i / horizontalSubdivisions.toFloat()
        val angle2 = 2 * PI.toFloat() * (i + 1f) / horizontalSubdivisions.toFloat()

        val pTop = Vector(0f, radius, 0f)
        val p1 = Vector(
            radius * sin(PI.toFloat() / verticalSubdivisions) * cos(angle1),
            radius * cos(PI.toFloat() / verticalSubdivisions),
            radius * sin(PI.toFloat() / verticalSubdivisions) * sin(angle1)
        )
        val p2 = Vector(
            radius * sin(PI.toFloat() / verticalSubdivisions) * cos(angle2),
            radius * cos(PI.toFloat() / verticalSubdivisions),
            radius * sin(PI.toFloat() / verticalSubdivisions) * sin(angle2)
        )

        faces.add(Triangle(listOf(pTop, p1, p2), p1.normalize()))
    }

    // South Pole triangles
    for (i in 0 until horizontalSubdivisions) {
        val angle1 = 2 * PI.toFloat() * i / horizontalSubdivisions.toFloat()
        val angle2 = 2 * PI.toFloat() * (i + 1) / horizontalSubdivisions.toFloat()

        val pBottom = Vector(0f, -radius, 0f)
        val p1 = Vector(
            radius * sin(PI.toFloat() / verticalSubdivisions) * cos(angle1),
            -radius * cos(PI.toFloat() / verticalSubdivisions),
            radius * sin(PI.toFloat() / verticalSubdivisions) * sin(angle1)
        )
        val p2 = Vector(
            radius * sin(PI.toFloat() / verticalSubdivisions) * cos(angle2),
            -radius * cos(PI.toFloat() / verticalSubdivisions),
            radius * sin(PI.toFloat() / verticalSubdivisions) * sin(angle2)
        )

        faces.add(Triangle(listOf(pBottom, p2, p1), p1.normalize()))
    }

    // rectangle strips
    for (j in 1 until verticalSubdivisions) {
        val phi1 = PI.toFloat() * j / verticalSubdivisions.toFloat()
        val phi2 = PI.toFloat() * (j + 1f) / verticalSubdivisions.toFloat()

        for (i in 0 until horizontalSubdivisions) {
            val theta1 = 2f * PI.toFloat() * i / horizontalSubdivisions.toFloat()
            val theta2 = 2f * PI.toFloat() * (i + 1f) / horizontalSubdivisions.toFloat()

            val p1 = Vector(sin(phi1) * cos(theta1), cos(phi1), sin(phi1) * sin(theta1)) * radius
            val p2 = Vector(sin(phi1) * cos(theta2), cos(phi1), sin(phi1) * sin(theta2)) * radius
            val p3 = Vector(sin(phi2) * cos(theta1), cos(phi2), sin(phi2) * sin(theta1)) * radius
            val p4 = Vector(sin(phi2) * cos(theta2), cos(phi2), sin(phi2) * sin(theta2)) * radius

            val color = ((p1 + p2 + p4) / 3f).normalize().clamp(0f, 1f)
            faces.add(Triangle(listOf(p1, p2, p3), color))
            faces.add(Triangle(listOf(p2, p3, p4), color))
        }
    }

    return faces
}
