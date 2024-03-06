package logic
import scalafx.beans.property.DoubleProperty

import scala.math.{atan, pow, sqrt, toDegrees}
import scala.concurrent.*
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

trait Tower:
  def upgrade(): Unit
  def upgradePrice: Int
end Tower

case class Headquarter(x: Double, y: Double) extends Tower:
  private var level = 1
  def getLevel = level
  private var HP = 2000
  def getHP = HP
  private var goldPer10s = 10
  def getGoldPer10s = goldPer10s
  private var goldBackRate = 0.6
  def getGoldBackRate = goldBackRate
  def upgradePrice = 500 + 200*level
  
  val maxHP = 2000

  def upgrade() =
    level += 1
    HP = (HP * 0.05).toInt
    goldPer10s = (goldPer10s * 0.05).toInt

class GunTower(x: Int, y: Int, var damage: Int, var fireRate: Double, val range: Double, val price: Int, game: Game) extends Tower:
  val getX = x
  val getY = y
  private var level = 1
  private var moneySpent = price
  def upgradePrice = (price/15.0).toInt * 10
  var onCoolDown = false
  def target: Option[EnemySoldier] =
    game.enemies.find(enemy => distanceTo(enemy) <= range)
  private var prevAngle = 0.0
  def rotationAngle = 
    if target.nonEmpty then 
      prevAngle = toDegrees(atan((x*1.0-target.get.getX)/(y*1.0-target.get.getY)))
      prevAngle
    else prevAngle
  
  def upgrade() =
    level += 1
    moneySpent += upgradePrice
    fireRate = fireRate * 0.05
    damage = (damage * 0.05).toInt

  def shoot() =
    if !onCoolDown && target.nonEmpty then
      val startTime = System.currentTimeMillis()
      target.get.minusHP(damage)
      onCoolDown = true
      target.get.widthProperty.value = (50*(target.get.HPpercentage))
      Future {
        Thread.sleep((fireRate*100).toLong)
        onCoolDown = false}
  def distanceTo(enemy: EnemySoldier) =
    sqrt(pow((x-enemy.getX), 2) + pow((y-enemy.getY), 2))
    

case class Cannon(x: Int, y: Int, game: Game) extends GunTower(x, y, 50, 1.0, 1.5, 150, game: Game):

end Cannon

case class Turret(x: Int, y: Int, game: Game) extends GunTower(x, y, 20, 0.4, 2.3, 200, game: Game):

end Turret

case class Sniper(x: Int, y: Int, game: Game) extends GunTower(x, y, 250, 3.0, 3.0, 200, game: Game):

end Sniper

case class Sharpshooter(x: Int, y: Int, game: Game) extends GunTower(x, y, 30, 0.8, 6.0, 120, game: Game):

end Sharpshooter

case class RocketLauncher(x: Int, y: Int, game: Game) extends GunTower(x, y, 500, 4.0, 5.0, 500, game: Game):

end RocketLauncher

case class GrenadeLauncher(x: Int, y: Int, game: Game) extends GunTower(x, y, 100, 1.0, 1.0, 250, game: Game):

end GrenadeLauncher



