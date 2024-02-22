package logic
import scala.math.pow

trait Tower:
  def upgrade(): Unit
  def upgradePrice: Int
end Tower

case class Headquarter(x: Int, y: Int, game: Game) extends Entity(x, y) with Tower:
  var level = 1
  var HP = 2000
  var goldPer10s = 10
  val upgradePrice = 500 + 200*level

  def upgrade() =
    level += 1
    HP = (HP * 0.05).toInt
    goldPer10s = (goldPer10s * 0.05).toInt


class GunTower(x: Int, y: Int, game: Game, var damage: Int, var fireRate: Double, range: Double, val price: Int) extends Entity(x, y) with Tower:
  var level = 1
  var moneySpent = price
  var upgradePrice = (price/15.0).toInt * 10

  def upgrade() =
    level += 1
    moneySpent += upgradePrice
    fireRate = fireRate * 0.05
    damage = (damage * 0.05).toInt // TODO: check if toInt is correct
    //TODO: Exception

  def shoot() = ??? // TODO: implement


case class Cannon(x: Int, y: Int, game: Game) extends GunTower(x, y, game, 50, 1.0, 1.5, 150):

end Cannon

case class Sniper(x: Int, y: Int, game: Game) extends GunTower(x, y, game, 250, 3.0, 3.0, 200):

end Sniper

case class Sharpshooter(x: Int, y: Int, game: Game) extends GunTower(x, y, game, 30, 0.8, 6.0, 120):

end Sharpshooter

case class RocketLauncher(x: Int, y: Int, game: Game) extends GunTower(x, y, game, 500, 4.0, 5.0, 500):

end RocketLauncher

case class GrenadeLauncher(x: Int, y: Int, game: Game) extends GunTower(x, y, game, 100, 1.0, 1.0, 250):

end GrenadeLauncher



