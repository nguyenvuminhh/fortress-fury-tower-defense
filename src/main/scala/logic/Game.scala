package logic

import logic.grid.GridPos
import scala.collection.mutable.Buffer

trait Game:
  def map: Map

  private var score = 0
  def yieldScore = score

  private var gold = 0
  def yieldGold = gold

  private var wave = 0
  def yieldWave = wave

  val headquarter = Headquarter(map.HQSquare.x+0, map.HQSquare.y+0)
  private var gunTowerVector = Vector[GunTower]()
  var enemyVector = Buffer[EnemySoldier]()

  def deploy(enemy: EnemySoldier) =
    enemyVector += enemy
  def place(gunTower: GunTower, x: Int, y: Int) =
    if gunTower.price <= gold then
      map.elementAt(GridPos(x, y)).addTower(gunTower)
      gold -= gunTower.price
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
