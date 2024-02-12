package logic

trait Game:
  val map: Map

  private var score = 0
  def yieldScore = score

  private var gold = 0
  def yieldGold = gold

  private var wave = 0
  def yieldWave = wave

  def deploy(company: Company) = ???
  def place(gunTower: GunTower) = ???
  def upgrade(gunTower: GunTower) = ???
  def remove(gunTower: GunTower) = ???
  def use(ability: Ability) = ???
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

