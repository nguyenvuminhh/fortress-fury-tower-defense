package logic


import logic.grid.GridPos

import java.util.{Timer, TimerTask}
import scala.collection.mutable.Buffer

trait Game:
  def map: Map

  private var score = 0
  def yieldScore = score

  private var gold = 50000
  def yieldGold = gold

  private var wave = 0
  def yieldWave = wave

  val headquarter = Headquarter(map.HQSquare.x+0, map.HQSquare.y+0)
  var gunTowerCollection = Buffer[GunTower]()
  var enemyCollection = Buffer[EnemySoldier]()
  /** Deploy methods */
  def deploy(enemy: EnemySoldier) =
    enemyCollection += enemy
  def unit(amount: Int, unitType: Int): Vector[EnemySoldier] =
    Vector.tabulate(amount){i =>
      unitType match
        case 1 => Infantry(this)
        case 2 => Cavalry(this)
        case 3 => ArmoredCar(this)
        case 4 => Tank(this)
    }
  def deployWave(wave: Int) =
    def a = wave / 5
    def b = wave / 4
    def c = wave / 3 + 5
    def d = wave / 2 + 10
    val troops = unit(a, 4) ++ unit(b, 3) ++ unit(c, 2) ++ unit(d, 1)
    val timer = new Timer()
    var index = 0
    val task = new TimerTask:
      def run() =
        if index < troops.length then
          deploy(troops(index))
          index += 1
        else
          timer.cancel()
    timer.schedule(task, 0, 1000)

  def place(gunTowerType: String, x: Int, y: Int) =
    if getPrice(gunTowerType) <= gold && map.elementAt(GridPos(x, y)).isEmpty then
      val gunTower =
        gunTowerType match
          case "Sharpshooter"     => Sharpshooter(x, y)
          case "Cannon"           => Cannon(x, y)
          case "Turret"           => Turret(x, y)
          case "GrenadeLauncher"  => GrenadeLauncher(x, y)
          case "Sniper"           => Sniper(x, y)
          case "RocketLauncher"   => RocketLauncher(x, y)
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
  def remove(x: Int, y: Int) =
    map.elementAt(GridPos(x, y)).clear()

  def use(ability: Ability) =
    if ability.price <= gold then
      ability.use()
      gold -= ability.price
  def giveGold() = gold += headquarter.goldPer10s
  def pause() = ???
  def resume() = ???
  def quit() = ???
  def restart() = ???
  def finish() = ???

end Game

class EndlessGame(theMap: Map) extends Game:
  def map = theMap
  def survivingTime: Int = 0
