package logic.grid

import logic.grid.CompassDir.*

import scala.annotation.targetName

/** An object of type `GridPos` represents a pair of integer coordinates. Such a pair can
  * be used to reference a point on a [[Grid]].
  *
  * The coordinate axes are named `x` and `y`. In this coordinate system, `x` increases
  * “eastwards” and y` increases “southwards”.
  *
  * `GridPos` objects are immutable.
  *
  * This class has an alias in the top-level package [[o1]], so it’s accessible to students
  * simply via `import o1.*`.
  *
  * @param x  an x coordinate
  * @param y  a y coordinate */
final case class GridPos(val x: Int, val y: Int):

  /** Determines whether this grid position equals the given one. This is the case if
    * the two have identical x and y coordinates. */
  @targetName("equals")
  def ==(another: GridPos): Boolean = this.x == another.x && this.y == another.y

  /** Returns a textual description of this position. The description is of the form `"(x,y)"`. */
  override def toString = s"($x,$y)"

end GridPos

