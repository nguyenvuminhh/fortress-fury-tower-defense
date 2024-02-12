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

  /** Returns the compass direction that is the opposite of this one. For instance,
    * calling this method on `North` returns `South`. */
  def opposite = this.clockwise.clockwise

  /** Determines whether the given compass direction is the opposite of this one.
    * For example, `North` and `South` are opposites of each other.*/
  def isOpposite(that: CompassDir) = this.xStep == -that.xStep && this.yStep == -that.yStep

  /** Determines whether this compass direction is on the East–West axis. That is,
    * returns `true` for `East`` and `West`, `false` for `North` and `South`. */
  def isHorizontal = this.xStep != 0

  /** Determines whether this compass direction is on the North–South axis. That is,
    * returns `true` for `North` and `South`, `false` for `East` and West`. */
  def isVertical = this.yStep != 0

end CompassDir



/** This companion object of [[CompassDir type `CompassDir`]] provides a selection of related
  * constants and utility methods.
  *
  * This object has an alias in the top-level package [[o1]], so it’s accessible to students
  * simply via `import o1.*`. */
object CompassDir:
  /** a collection of all the four directions, in clockwise order starting with `North` */
  val Clockwise = Vector[CompassDir](North, East, South, West)

  /** The number of the compass directions represented by class `CompassDir`. Four, that is. */
  val Count = Clockwise.size

  private val next = Clockwise.zip(Clockwise.tail ++ Clockwise.init).toMap
  private val previous = this.next.map( _.swap )

  private type Key = scala.swing.event.Key.Value
  private val  Key = scala.swing.event.Key
  private val ArrowToDir = Map(Key.Up -> North, Key.Left -> West, Key.Down-> South, Key.Right-> East)
  private val WASDToDir  = Map(Key.W  -> North, Key.A    -> West, Key.S   -> South, Key.D    -> East)
  private val KeyToDir   = ArrowToDir ++ WASDToDir

  /** Returns the [[CompassDir]] that corresponds to the given arrow key.
    * For example, the right arrow corresponds to `East`.
    * @param key  any key on the keyboard
    * @return one of the four `CompassDir`s; `None` if the given key is not one of the four arrow keys
    * @see [[fromWASD]], [[fromKey]] */
  def fromArrowKey(key: Key) = ArrowToDir.get(key)

  /** Returns the [[CompassDir]] that corresponds to the given WASD key.
    * For example, the D key corresponds to `East`.
    * @param key  any key on the keyboard
    * @return one of the four `CompassDir`s; `None` if the given key is not one of the four WASD keys
    * @see [[fromArrowKey]], [[fromKey]] */
  def fromWASD(key: Key)     = WASDToDir.get(key)

  /** Returns the [[CompassDir]] that corresponds to the given arrow key.
    * For example, the right arrow or the D key corresponds to `East`.
    * @param key  any key on the keyboard
    * @return one of the four `CompassDir`s; `None` if the given key is not one of the four arrow keys
    *         or one of the four WASD keys
    * @see [[fromArrowKey]], [[fromWASD]] */
  def fromKey(key: Key)      = KeyToDir.get(key)

end CompassDir

