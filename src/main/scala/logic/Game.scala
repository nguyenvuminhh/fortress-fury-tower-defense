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

  def deploy(company: Company) = ???
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
  def giveGold() = ???
  def pause() = ???
  def resume() = ???
  def quit() = ???
  def restart() = ???
  def finish() = ???

end Game

class EndlessGame
  def survivingTime: Int = ???


class CampaignGame(level: Int, waveLimit: Int) extends Game:
  def completeStar = ???
  def win() = ???

