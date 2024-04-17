package logic.grid

import scala.reflect.ClassTag
import scala.util.{Failure, Success, Try}

/** The trait `Grid` represents rectangular grids that contain elements of a particular
  * kind. Each element in a grid is located at a unique pair of coordinates, represented
  * as a [[GridPos]].
  *
  * X coordinates run from 0 to `width-1`, y coordinates from 0 to `height-1`.
  * (0,0) corresponds to the upper left corner of the grid.
  *
  * There are different kinds of grids: the type of element that a grid contains is
  * defined by the grid’s type parameter. For instance, `Grid[Square]` is a grid where
  * each pair of x and y coordinates contains a `Square` object, and `Grid[House]` is
  * a grid containing `House` objects.
  *
  * A `Grid` is mutable: it is possible to replace an element at a particular `GridPos`
  * with another. (Depending on the element type, the elements may be individually
  * mutable, too.) The width and height of a grid never change, however.
  *
  * Upon creation, a `Grid` initializes itself by calling [[initialElements]], which
  * produces an initial state for the grid.
  *
  * This trait has an alias in the top-level package [[o1]], so it’s accessible to students
  * simply via `import o1.*`.
  *
  * @param width   the number of elements in each row of the grid
  * @param height  the number of elements in each column of the grid */
trait Grid[Element: ClassTag](val width: Int, val height: Int):

  /** the number of elements in this grid, in total. (Equals `width` times `height`.) */
  val size = width * height


  private val contents: Array[Array[Element]] =
    val elems = try this.initialElements catch case npe: NullPointerException => throw RuntimeException("Grid initialization failed with a NullPointerException.\nPossible cause: trying to access an element’s (still nonexistent) neighbors or other parts of the unready grid while initializing.", npe)
    require(elems.sizeIs == this.size, s"The number of elements returned by initialElements (${elems.size}) did not equal width times height (${this.size}).")
    elems.toArray.grouped(this.width).toArray.transpose


  /** Returns the element at the given pair of coordinates. (This does the same as `apply`.)
    * @param location  a location on the grid (which must be within range or this method will fail with an error) */
  def elementAt(location: GridPos) =
    require(this.contains(location), s"Attempted to access a non-existent location $location of a ${width}-by-${height} grid.")
    this.contents(location.x)(location.y)


  /** Modifies the grid by replacing the existing element at the given location with the new element.
    * @param location    a location on the grid (which must be within range or this method will fail with an error)
    * @param newElement  the new element that replaces the old one at `location` */
  def update(location: GridPos, newElement: Element) =
    require(this.contains(location), s"Attempted to update element at a non-existent location $location of a ${width}-by-${height} grid.")
    this.contents(location.x)(location.y) = newElement


  /** Checks whether the grid contains the given x and y coordinates. */
  private def contains(x: Int, y: Int) =
    0 <= x && x < this.width && 0 <= y && y < this.height


  /** Determines whether the grid contains the given pair of coordinates.
    * For instance, a grid with a width and height of 5 will contain (0, 0)
    * and (4, 4) but not (-1, -1), (4, 5) or (5, 4). */
  def contains(location: GridPos): Boolean = this.contains(location.x, location.y)


  /** Generates the elements that initially occupy the grid. This method is automatically
    * invoked by every `Grid` object upon creation. Subtypes should implement this method
    * as appropriate for the particular sort of grid desired.
    *
    * Note that since this method produces the `Grid`’s initial contents, it gets called
    * during initialization before the `Grid` actually has any elements as content. Therefore,
    * subtypes’ implementations of this method must not depend on the state of the new `Grid`
    * (by calling `neighbors` or `elementAt`, for instance) or attempt to modify the `Grid`
    * (with `update`, for instance).
    *
    * This method is meant for initialization purposes only. To access all the elements of
    * an already-initialized `Grid`, call [[allElements]] instead.
    *
    * @return a collection of size `width` times `height` that contains the initial grid elements.
    *         The first element in the collection will appear at `GridPos` (0,0), the second at (1,0),
    *         and so on, filling in the first row before continuing on the second row at (0,1). */
  def initialElements: Seq[Element]


  /** Returns the element at the given pair of coordinates. (This does the same as `elementAt`.)
    * @param location  a location on the grid (which must be within range or this method
    *                  will fail with an error) */
  def apply(location: GridPos) = this.elementAt(location)

  /** Returns a collection of all the locations on the grid. */
  def allPositions: Vector[GridPos] =
    (0 until this.size).map( n => GridPos(n % this.width, n / this.width) ).toVector


  /** Returns a collection of all the elements currently in the grid. */
  def allElements: Vector[Element] =
    for pos <- this.allPositions; elem <- Option(this(pos)) yield elem


  /** Swaps the elements at two given locations on the grid. The given locations must be
    * within range or this method will fail with an error. */
  def swap(location1: GridPos, location2: GridPos) =
    val temporary = this(location1)
    this(location1) = this(location2)
    this(location2) = temporary

end Grid

