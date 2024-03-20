package logic
import grid.*

import java.io

abstract class Map extends Grid[Square](32, 11) with io.Serializable:
  def startingSquare: GridPos
  def HQSquare: GridPos
  def crashSquare: GridPos
  def turningSquare: Vector[(Double, Double)]
  def turningDirection: Vector[Int]
end Map

object Map1 extends Map:
  
  /** CONDITION OF THE PATH */
  private def initialSquare(x: Int, y: Int): Square =
    var result: Square = Path()
    val h1 = ((x >= 0 && x <= 3) || (x >= 8 && x <= 13) || (x >= 22 && x <= 26))  && (y == 4)
    val h2 = (x >= 5 && x <= 13)                                                  && (y == 8)
    val h3 = (x >= 8 && x <= 16)                                                  && (y == 2)
    val h4 = (x >= 18 && x <= 20)                                                 && (y == 6)
    val v1 = (x == 4 || x == 14 || x == 27)                                       && (y >= 5 && y <= 7)
    val v2 = (x == 7)                                                             && (y == 3)
    val v3 = (x == 17)                                                            && (y >= 3 && y <= 5)
    val v4 = (x == 21)                                                            && (y == 5)
    val c1 = Vector((4, 4), (14, 4), (17, 2), (27, 4)).contains((x,y))
    val c2 = Vector((4, 8), (7, 4), (17, 6)).contains((x,y))
    val c3 = Vector((14, 8), (21, 6)).contains((x,y))
    val c4 = Vector((7, 2), (21, 4)).contains((x,y))
    val finalCondition = h1 || h2 || h3 || h4 || v1 || v2 || v3 || v4 || c1 || c2 || c3 || c4
    if finalCondition then
      result = Path()
    else result = Buildable()
    result

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
  val turningVector = turningSquare.zip(turningDirection)

end Map1
