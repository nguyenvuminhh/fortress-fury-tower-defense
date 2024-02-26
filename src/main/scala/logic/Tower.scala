package logic
import scala.math.pow

trait Tower:
  def upgrade(): Unit
  def upgradePrice: Int
end Tower

case class Headquarter(x: Double, y: Double) extends Tower:
  var level = 1
  var HP = 2000
  var goldPer10s = 10
  def upgradePrice = 500 + 200*level
  
  val maxHP = 2000

  def upgrade() =
    level += 1
    HP = (HP * 0.05).toInt
    goldPer10s = (goldPer10s * 0.05).toInt

class GunTower(x: Int, y: Int, var damage: Int, var fireRate: Double, range: Double, val price: Int) extends Tower:
  private var level = 1
  private var moneySpent = price
  def upgradePrice = (price/15.0).toInt * 10

  def upgrade() =
    level += 1
    moneySpent += upgradePrice
    fireRate = fireRate * 0.05
    damage = (damage * 0.05).toInt


  def shoot(target: EnemySoldier) =
    target.minusHP(damage)

case class Cannon(x: Int, y: Int) extends GunTower(x, y, 50, 1.0, 1.5, 150):

end Cannon

case class Turret(x: Int, y: Int) extends GunTower(x, y, 20, 0.3, 2.3, 200):

end Turret

case class Sniper(x: Int, y: Int) extends GunTower(x, y, 250, 3.0, 3.0, 200):

end Sniper

case class Sharpshooter(x: Int, y: Int) extends GunTower(x, y, 30, 0.8, 6.0, 120):

end Sharpshooter

case class RocketLauncher(x: Int, y: Int) extends GunTower(x, y, 500, 4.0, 5.0, 500):

end RocketLauncher

case class GrenadeLauncher(x: Int, y: Int) extends GunTower(x, y, 100, 1.0, 1.0, 250):

end GrenadeLauncher



