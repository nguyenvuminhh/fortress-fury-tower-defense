package logic


import logic.grid.GridPos
import scala.math.{sqrt, pow}

import java.util.{Timer, TimerTask}
import scala.collection.mutable.Buffer

trait Game:
  def map: Map

  private var score = 0
  def getScore = score

  private var gold = 50000
  def getGold = gold

  private var wave = 0
  def getWave = wave

  val headquarter = Headquarter(map.HQSquare.x+0, map.HQSquare.y+0)
  private var gunTowerCollection = Buffer[GunTower]()
  def gunTowers = gunTowerCollection

  private var enemyCollection = Buffer[EnemySoldier]()
  def enemies = enemyCollection
  def filterDeadEnemy() = enemyCollection = enemyCollection.filter(_.getHP > 0)

  def deploy(enemy: EnemySoldier) =
    enemyCollection += enemy

  def place(gunTowerType: String, x: Int, y: Int) =
    if getPrice(gunTowerType) <= gold && map.elementAt(GridPos(x, y)).isEmpty then
      val gunTower =
        gunTowerType match
          case "Sharpshooter"     => Sharpshooter(x, y, this)
          case "Cannon"           => Cannon(x, y, this)
          case "Turret"           => Turret(x, y, this)
          case "GrenadeLauncher"  => GrenadeLauncher(x, y, this)
          case "Sniper"           => Sniper(x, y, this)
          case "RocketLauncher"   => RocketLauncher(x, y, this)
      map.elementAt(GridPos(x, y)).addTower(gunTower)
      gunTowerCollection += gunTower
      gold -= gunTower.price
      true
    else false

  def getPrice(name: String) =
    name match
      case "Sharpshooter"     => 120
      case "Cannon"           => 150
      case "Turret"           => 200
      case "GrenadeLauncher"  => 250
      case "Sniper"           => 300
      case "RocketLauncher"   => 500
  def upgrade(x: Int, y: Int) =
    val square = map.elementAt(GridPos(x, y))
    if !square.isEmpty && square.tower.get.upgradePrice <= gold then
      square.tower.get.upgrade()
      gold -= square.tower.get.upgradePrice
  def remove(pos: GridPos) =
    gold -= (this.headquarter.getGoldBackRate* map.elementAt(pos).tower.get.asInstanceOf[GunTower].price).toInt
    gunTowerCollection -= map.elementAt(pos).tower.get.asInstanceOf[GunTower]
    map.elementAt(pos).clear()

  def use(ability: Ability) =
    if ability.price <= gold then
      ability.use()
      gold -= ability.price
  def giveGold() = gold += headquarter.getGoldPer10s
  def pause() = ???
  def resume() = ???
  def quit() = ???
  def restart() = ???
  def finish() = ???



end Game

class EndlessGame(theMap: Map) extends Game:
  def map = theMap
  def survivingTime: Int = 0
