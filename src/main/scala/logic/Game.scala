package logic

import logic.grid.GridPos

trait Game:
  val map: Map

  private var score = 0
  def yieldScore = score

  private var gold = 0
  def yieldGold = gold

  private var wave = 0
  def yieldWave = wave

  private var gunTowerVector = Vector[GunTower]()
  val headquarter = Headquarter(map.HQSquare.x, map.HQSquare.y)
  private var enemyVector = Vector[EnemySoldier]()

  def deploy() = ???
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

class EndlessGame extends Game:
  def survivingTime: Int = ???
  val map = Map1()


abstract class CampaignGame(level: Int, waveLimit: Int) extends Game:
  def completeStar =
    val rate = headquarter.HP / headquarter.maxHP*1.0
    if rate >= 0.9 then 3
    else if rate >= 0.7 then 2
    else 1
  def win() = ???

