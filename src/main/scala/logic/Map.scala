package logic
import grid.*

abstract class Map extends Grid[Square](20, 10):
  def initialSquare: GridPos
  def HQSquare: GridPos
end Map

class Map1 extends Map:
  val initialElements:Seq[Square] = for y <- 0 until 10; x <- 0 until 20 yield initialSquare(x, y)


  private def initialSquare(x: Int, y: Int): Square =
    val condition1 = (y == 4 && ((x >= 0 && x <= 4) || (x >= 7 && x <= 14) || (x >= 21 && x <= 27)))
    val condition2 = (y >= 5 && y <= 7) && (x == 4 || x == 14)
    val condition3 = ((x >= 4 && x <= 14) && y == 8)
    val condition4 = (x == 7 && y == 3)
    val condition5 = (x >= 7 && x <= 17) && y == 2
    val condition6 = (y >= 3 && y <= 5) && x == 17
    val condition7 = y == 6 && (x >= 17 && x <= 21)
    val condition8 = x == 21 && y == 5
    val condition9 = x == 27 && (y >= 5 && y <= 7)
    val finalCondition = condition1 || condition2 || condition3 || condition4 || condition5 || condition6 || condition7 || condition8 || condition9
    if finalCondition then Path() else Buildable()

  val initialSquare = GridPos(0, 4)
  val HQSquare = GridPos(17, 7)
  

end Map1
