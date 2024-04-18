package logic
import logic.grid.*

abstract class Map extends Grid[Square](32, 11):
  def startingSquare: GridPos
  def HQSquare: GridPos
  def crashSquare: GridPos
  def squareType(x: Int, y: Int): String
  def turningSquare: Vector[(Double, Double)]
  def turningDirection: Vector[Int]
end Map

class Map1 extends Map:
  
  /** CONDITION OF THE PATH */
  private def hCondition(x: Int, y: Int) =
    val h1 = ((x >= 0 && x <= 3) || (x >= 8 && x <= 13) || (x >= 22 && x <= 26))  && (y == 4)
    val h2 = (x >= 5 && x <= 13)                                                  && (y == 8)
    val h3 = (x >= 8 && x <= 16)                                                  && (y == 2)
    val h4 = (x >= 18 && x <= 20)                                                 && (y == 6)
    h1 || h2 || h3 || h4
  private def vCondition(x: Int, y: Int)  =
    val v1 = (x == 4 || x == 14 || x == 27)                                       && (y >= 5 && y <= 7)
    val v2 = (x == 7)                                                             && (y == 3)
    val v3 = (x == 17)                                                            && (y >= 3 && y <= 5)
    val v4 = (x == 21)                                                            && (y == 5)
    v1 || v2 || v3 || v4
  private def c1Condition(x: Int, y: Int) = Vector((4, 4), (14, 4), (17, 2), (27, 4)).contains((x,y))
  private def c2Condition(x: Int, y: Int) = Vector((4, 8), (7, 4), (17, 6)).contains((x,y))
  private def c3Condition(x: Int, y: Int) = Vector((14, 8), (21, 6)).contains((x,y))
  private def c4Condition(x: Int, y: Int) = Vector((7, 2), (21, 4)).contains((x,y))
  private def initialSquare(x: Int, y: Int): Square =
    var result: Square = Path()
    val finalCondition = hCondition(x, y) || vCondition(x, y) || c1Condition(x, y) || c2Condition(x, y) || c3Condition(x, y) || c4Condition(x, y)
    if finalCondition then
      result = Path()
    else result = Buildable()
    result

  def squareType(x: Int, y: Int) =
    if hCondition(x, y) then "horizontal"
    else if vCondition(x, y) then "vertical"
    else if c1Condition(x, y) then "corner1"
    else if c2Condition(x, y) then "corner2"
    else if c3Condition(x, y) then "corner3"
    else if c4Condition(x, y) then "corner4"
    else "buildable"

  /** PLACING THE SQUARES */
  def initialElements: Seq[Square] = {
    val elements = for {
      y <- 0 until height
      x <- 0 until width
    } yield initialSquare(x, y)
    elements.toSeq
  }
  
  /** SPECIAL SQUARES */
  val startingSquare = GridPos(0, 4)
  val crashSquare = GridPos(27, 7)
  val HQSquare = GridPos(27, 8)
  val turningSquare = Vector((4, 4), (4, 8), (14, 8), (14, 4), (7, 4), (7, 2), (17, 2), (17, 6), (21, 6), (21, 4), (27, 4))
  val turningDirection = Vector(1, 0, 0, 0, 1, 1, 1, 0, 0, 1, 1)
  override def toString = "1"

end Map1

class Map2 extends Map:

  /** CONDITION OF THE PATH */
  private def hCondition(x: Int, y: Int) =
    val h1 = (x >= 0 && x <= 3)   && (y == 7)
    val h2 = (x >= 5 && x <= 18)  && (y == 2)
    val h3 = (x >= 11 && x <= 18) && (y == 5)
    val h4 = (x >= 11 && x <= 24) && (y == 7)
    h1 || h2 || h3 || h4
  private def vCondition(x: Int, y: Int)  =
    val v1 = (x == 4)             && (y >= 3 && y <= 6)
    val v2 = (x == 19)            && (y >= 3 && y <= 4)
    val v3 = (x == 10)            && (y == 6)
    val v4 = (x == 25)            && (y >= 4 && y <= 6)
    v1 || v2 || v3 || v4
  private def c1Condition(x: Int, y: Int) = (x, y) == (19, 2)
  private def c2Condition(x: Int, y: Int) = (x, y) == (10, 7)
  private def c3Condition(x: Int, y: Int) = Vector( (19, 5), (25, 7), (4, 7) ).contains( (x, y) )
  private def c4Condition(x: Int, y: Int) = Vector( (4, 2), (10, 5) ).contains( (x, y) )
  private def initialSquare(x: Int, y: Int): Square =
    var result: Square = Path()
    val finalCondition = hCondition(x, y) || vCondition(x, y) || c1Condition(x, y) || c2Condition(x, y) || c3Condition(x, y) || c4Condition(x, y)
    if finalCondition then
      result = Path()
    else result = Buildable()
    result

  def squareType(x: Int, y: Int) =
    if hCondition(x, y) then "horizontal"
    else if vCondition(x, y) then "vertical"
    else if c1Condition(x, y) then "corner1"
    else if c2Condition(x, y) then "corner2"
    else if c3Condition(x, y) then "corner3"
    else if c4Condition(x, y) then "corner4"
    else "buildable"

  /** PLACING THE SQUARES */
  def initialElements: Seq[Square] = {
    val elements = for {
      y <- 0 until height
      x <- 0 until width
    } yield initialSquare(x, y)
    elements.toSeq
  }

  /** SPECIAL SQUARES */
  val startingSquare = GridPos(0, 7)
  val crashSquare = GridPos(25, 4)
  val HQSquare = GridPos(25, 3)
  val turningSquare = Vector((4, 7), (4, 2), (19, 2), (19, 5), (10, 5), (10, 7), (25, 7))
  val turningDirection = Vector(0, 1, 1, 1, 0, 0, 0)

  override def toString = "2"

end Map2

class Map3 extends Map:

  /** CONDITION OF THE PATH */
  private def hCondition(x: Int, y: Int) =
    val h1 = (x >= 0 && x <= 8)   && (y == 6)
    val h2 = (x >= 4 && x <= 8)   && (y == 4)
    val h3 = (x >= 4 && x <= 12)  && (y == 2)
    val h4 = (x >= 14 && x <= 20) && (y == 4)
    val h5 = (x >= 22 && x <= 24) && (y == 6)
    h1 || h2 || h3 || h4 || h5
  private def vCondition(x: Int, y: Int)  = Vector( (9, 5), (3, 3), (13, 3), (21, 5), (25, 5), (25, 4), (25, 3) ).contains( (x, y) )
  private def c1Condition(x: Int, y: Int) = Vector( (9, 4), (13, 2), (21, 4) ).contains( (x, y) )
  private def c2Condition(x: Int, y: Int) = Vector( (3, 4), (13, 4), (21, 6) ).contains( (x, y) )
  private def c3Condition(x: Int, y: Int) = Vector( (9, 6), (25, 6) ).contains( (x, y) )
  private def c4Condition(x: Int, y: Int) = (x, y) == (3, 2)
  private def initialSquare(x: Int, y: Int): Square =
    var result: Square = Path()
    val finalCondition = hCondition(x, y) || vCondition(x, y) || c1Condition(x, y) || c2Condition(x, y) || c3Condition(x, y) || c4Condition(x, y)
    if finalCondition then
      result = Path()
    else result = Buildable()
    result

  def squareType(x: Int, y: Int) =
    if hCondition(x, y) then "horizontal"
    else if vCondition(x, y) then "vertical"
    else if c1Condition(x, y) then "corner1"
    else if c2Condition(x, y) then "corner2"
    else if c3Condition(x, y) then "corner3"
    else if c4Condition(x, y) then "corner4"
    else "buildable"

  /** PLACING THE SQUARES */
  def initialElements: Seq[Square] = {
    val elements = for {
      y <- 0 until height
      x <- 0 until width
    } yield initialSquare(x, y)
    elements.toSeq
  }

  /** SPECIAL SQUARES */
  val startingSquare = GridPos(0, 6)
  val crashSquare = GridPos(25, 3)
  val HQSquare = GridPos(25, 2)
  val turningSquare = Vector( (9, 6), (9, 4), (3, 4), (3, 2), (13, 2), (13, 4), (21, 4), (21, 6), (25, 6) )
  val turningDirection = Vector(0, 0, 1, 1, 1, 0, 1, 0, 0)

  override def toString = "2"

end Map3