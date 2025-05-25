package bodemann.cassidoo.cube.models

import bodemann.cassidoo.cube.math.Vector
import bodemann.cassidoo.cube.models.Face.Rectangle
import bodemann.cassidoo.cube.models.Face.Triangle

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
