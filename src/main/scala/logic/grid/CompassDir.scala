package logic.grid

/** The type `CompassDir` represents the cardinal compass directions in a grid-like coordinate
  * system. There are exactly four instances of this type: `North`, `East`, `South` and `West`,
  * which are also defined in this package.
  *
  * All the `CompassDir` objects are immutable.
  *
  * This type and its instances have aliases in the top-level package [[o1]], so they are
  * accessible to students simply via `import o1.*`.
  *
  * @see [[GridPos]]
  * @param xStep  the change in x coordinate if one moves one step in this direction.
  *               For instance, `West` has an `xStep` of -1 and `North` has an `xStep` of 0.
  * @param yStep  the change in y coordinate if one moves one step in this direction.
  *               For instance, `North` has an `yStep` of -1 and `West` has an `yStep` of 0. */
enum CompassDir(val xStep: Int, val yStep: Int) derives CanEqual:
  // Note to students: The word enum starts the definition of a special sort of class. You
  // can’t inherit the enum class except for defining a number of “cases” of it right here.
  // Consequently, there are precisely four objects of type CompassDir, as defined below.

  /** This immutable singleton object represents the northwardly compass direction.
    * It’s one of the four predefined instances of `CompassDir`. It has an alias in the
    * top-level package [[o1]], so it’s accessible to students simply via `import o1.*`. */
  case North extends CompassDir( 0,-1)

  /** This immutable singleton object represents the eastwardly compass direction.
    * It’s one of the four predefined instances of `CompassDir`. It has an alias in the
    * top-level package [[o1]], so it’s accessible to students simply via `import o1.*`. */
  case East  extends CompassDir( 1, 0)

  /** This immutable singleton object represents the southwardly compass direction.
    * It’s one of the four predefined instances of `CompassDir`. It has an alias in the
    * top-level package [[o1]], so it’s accessible to students simply via `import o1.*`. */
  case South extends CompassDir( 0, 1)

  /** This immutable singleton object represents the westwardly compass direction.
    * It’s one of the four predefined instances of `CompassDir`. It has an alias in the
    * top-level package [[o1]], so it’s accessible to students simply via `import o1.*`. */
  case West  extends CompassDir(-1, 0)


  /** Returns the next of the four compass directions, clockwise from this one.
    * For instance, calling this method on `North` returns `East`. */
  def clockwise = CompassDir.next(this)

  /** Returns the next of the four compass directions, counterclockwise from this
    * one. For instance, calling this method on `North` returns `West`. */
  def counterClockwise = CompassDir.previous(this)

end CompassDir



/** This companion object of [[CompassDir type `CompassDir`]] provides a selection of related
  * constants and utility methods.
  *
  * This object has an alias in the top-level package [[o1]], so it’s accessible to students
  * simply via `import o1.*`. */
object CompassDir:
  /** a collection of all the four directions, in clockwise order starting with `North` */
  val Clockwise = Vector[CompassDir](North, East, South, West)

  private val next = Clockwise.zip(Clockwise.tail ++ Clockwise.init).toMap
  private val previous = this.next.map( _.swap )


end CompassDir

