package logic
import grid.*

class Map extends Grid[Square](20, 10):

end Map

class Map1 extends Map:
  val initialElements:Seq[Square] = for y <- 0 until 10; x <- 0 until 20 yield initialSquare(x, y)


  private def initialSquare(x: Int, y: Int): Square =
    val condition1 = (y == 4 && ((x >= 0 && x <= 4) || (x >= 7 && x <= 14)))
    val condition2 = (y >= 5 && y <= 7) && (x == 4 || x == 14)
    val condition3 = ((x >= 4 && x <= 14) && y == 8)
    val condition4 = (x == 7 && y == 3)
    val condition5 = (x >= 7 && x <= 17) && y == 2
    val condition6 = (y >= 3 && y <= 6) && x == 17
    val finalCondition = condition1 || condition2 || condition3 || condition4 || condition5 || condition6
    if finalCondition then Path() else Buildable()

  

end Map1
