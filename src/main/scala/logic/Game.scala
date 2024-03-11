package logic


import logic.grid.GridPos

import scala.math.{pow, sqrt}
import java.util.{Timer, TimerTask}
import scala.collection.mutable.Buffer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.nio.file.{Files, Paths}
import java.io.{FileWriter, BufferedWriter, File}
import java.nio.file.{Paths, Files}
import scala.util.{Try, Success, Failure}
import scalafx.beans.property.BooleanProperty

import java.io.PrintWriter

class Game (val map: Map):
  /** SCORE */
  private var score = 0
  def getScore = score
  def addScore(amount: Int) = score += amount

  /** GOLD */
  private var gold = 50000
  def getGold = gold
  def giveGold() = gold += headquarter.getGoldPer10s

  /** WAVE */
  private var wave = 0
  def getWave = wave

  /** PAUSE */
  private var isPaused = false
  def getIsPaused = isPaused
  def pause() = isPaused = true
  def resume() = isPaused = false

  /** GAMEOVER */
  val isOver = BooleanProperty(false)
  def getIsOver = isOver.value || headquarter.getHP == 0
  def quit() = isOver.value = true
  private var survivingTimeInOneFifthSec: Long = 0

  /** TIME */
  def getSurvivingTimeInOneFifthSec = survivingTimeInOneFifthSec
  def increaseTime() = survivingTimeInOneFifthSec += 1
  def survivingTimeToString =
    val elapsedTime = survivingTimeInOneFifthSec/5
    val hours = elapsedTime / 3600
    val minutes = (elapsedTime % 3600) / 60
    val seconds = elapsedTime % 60
    f"$hours%02d:$minutes%02d:$seconds%02d"

  /** SPEEDUP */
  val paceVector = Vector(0.5, 0.75, 1, 1.5, 20)
  private var paceIndex = 2
  def pace = paceVector(paceIndex)
  def speedup() = paceIndex = (paceIndex + 1)%5

  /** TOWERS */
  val headquarter = Headquarter(map.HQSquare.x, map.HQSquare.y)
  private var gunTowerCollection = Buffer[GunTower]()
  def gunTowers = gunTowerCollection

  /** ENEMIES */
  private var enemyCollection = Buffer[EnemySoldier]()
  def enemies = enemyCollection
  def filterDeadEnemy() = enemyCollection = enemyCollection.filter(_.getHP > 0)

  /** BASIC DEPLOY METHOD */
  def deploy(enemy: EnemySoldier) =
    enemyCollection += enemy

  /** GUN TOWER METHODS */
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

  /** ABILITY METHOD */
  def use(ability: Ability) =
    if ability.price <= gold then
      ability.use()
      gold -= ability.price

  /** SAVE GAME */
  def saveRecord(): Unit =
    val currentDate = LocalDate.now()
    val customFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val date = currentDate.format(customFormat)
    val data = s"$date\t$score\t$survivingTimeInOneFifthSec\n"
    val filePath = "src/main/resources/record.txt"
    val writer = new BufferedWriter(new FileWriter(filePath, true))
    writer.write(data)
    writer.close()

end Game
