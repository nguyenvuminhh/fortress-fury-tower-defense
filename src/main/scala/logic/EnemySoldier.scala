package logic

import scala.math.{sin, cos, Pi}


abstract class EnemySoldier(game: Game, damage: Int):
  var HP = damage
  def minusHP(amount: Int) = HP -= amount
  val gold = damage/10
  val point = damage*10 //TODO: Implement
  var isDead = false
  
  /** Position */
  var x = game.map.startingSquare.x*1.0
  var y = game.map.startingSquare.y*1.0
  var pace = 0.2
  var heading = 0.0
  def advance() =
    turnDirection match
      case 1 => turnRight()
      case 0 => turnLeft()
      case -1 => ()
    x += cos(heading).toInt*pace
    y += sin(heading).toInt*pace
  def turnRight() = heading -= Pi/2
  def turnLeft() = heading += Pi/2
  def picturePath: String
  def turnDirection =
    val index = game.map.turningSquare.indexOf((x,y))
    if index != -1 then game.map.turningDirection(index) else -1

  def crash() = ???

case class Infantry(game: Game) extends EnemySoldier(game, 100):
  val picturePath = "image/infantry.png"
end Infantry

case class Cavalry(game: Game) extends EnemySoldier(game, 100):
  val picturePath = "image/calvary.png"
end Cavalry

case class ArmoredCar(game: Game) extends EnemySoldier(game, 100):
  val picturePath = "image/armoredCar.png"
end ArmoredCar

case class Tank(game: Game) extends EnemySoldier(game, 100):
  val picturePath = "image/tank.png"
end Tank

