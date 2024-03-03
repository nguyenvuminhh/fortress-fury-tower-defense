package logic

import logic.grid.CompassDir.*

import scala.math.{Pi, cos, round, sin}
import scala.math.BigDecimal.RoundingMode



abstract class EnemySoldier(game: Game, damage: Int):
  var HP = damage
  def minusHP(amount: Int) = HP -= amount
  val gold = damage/10
  val point = damage*10 //TODO: Implement
  var isDead = false
  
  /** Position */
  var x = game.map.startingSquare.x*1.0
  var y = game.map.startingSquare.y*1.0
  var pace = 1
  var heading = East
  def advance() =
    turnDirection match
      case 1 => turnRight()
      case 0 => turnLeft()
      case -1 => ()
    x = BigDecimal(x +heading.xStep*pace).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble
    y = BigDecimal(y +heading.yStep*pace).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble
  def turnRight() = heading = heading.clockwise
  def turnLeft() = heading = heading.counterClockwise
  def picturePath: String
  def turnDirection =
    val index = game.map.turningSquare.indexOf((x,y))
    if index != -1 then game.map.turningDirection(index) else -1

  def crash() = ???

case class Infantry(game: Game) extends EnemySoldier(game, 100):
  val picturePath = "image/infantry.png"
  override def toString = s"Infantry($x, $y)"
end Infantry

case class Cavalry(game: Game) extends EnemySoldier(game, 100):
  val picturePath = "image/calvary.png"
  override def toString = s"Cavalry($x, $y)"
end Cavalry

case class ArmoredCar(game: Game) extends EnemySoldier(game, 100):
  val picturePath = "image/armoredCar.png"
  override def toString = s"ArmoredCar($x, $y)"
end ArmoredCar

case class Tank(game: Game) extends EnemySoldier(game, 100):
  val picturePath = "image/tank.png"
  override def toString = s"Tank($x, $y)"
end Tank

