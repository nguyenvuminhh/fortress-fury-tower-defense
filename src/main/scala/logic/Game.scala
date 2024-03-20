package logic


import logic.grid.GridPos

import scala.math.{pow, sqrt}
import java.util.{Timer, TimerTask}
import scala.collection.mutable.Buffer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.nio.file.{Files, Paths}
import java.io.{BufferedWriter, File, FileOutputStream, FileWriter, ObjectOutputStream, PrintWriter}
import java.nio.file.{Files, Paths}
import scala.util.{Failure, Success, Try}
import scalafx.beans.property.BooleanProperty

class Game (val map: Map) extends Serializable:
  /** SCORE */
  private var score = 0
  def getScore = score
  def addScore(amount: Int) = score += amount
  private var enemyKilled = 0
  def getEnemyKilled = enemyKilled
  def increaseEnemyKilled() = enemyKilled += 1

  /** GOLD */
  private var gold = 10000
  def getGold = gold
  def giveGold() = gold += headquarter.getGoldPer10s
  def addGold(amount: Int) = gold += amount

  /** WAVE */
  private var wave = 0
  def nextWave = wave += 1//TODO: Ask if this is necessary
  def getWave = wave

  /** PAUSE */
  private var isPaused = false
  def getIsPaused = isPaused
  def pause() = isPaused = true
  def resume() = isPaused = false

  /** GAMEOVER */
  val isOver = BooleanProperty(false)
  def getIsOver = isOver.value || headquarter.getHP <= 0
  def quit() = isOver.value = true

  /** TIME */
  private var survivingTimeInOneFifthSec: Long = 0
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
  map.elementAt(map.HQSquare).addTower(headquarter)
  private var gunTowerCollection = Buffer[GunTower]()
  def gunTowers = gunTowerCollection
  val infoGunTowers = Vector[GunTower](Sharpshooter(-1, -1, this), Cannon(-1, -1, this), Turret(-1, -1, this), GrenadeLauncher(-1, -1, this), Sniper(-1, -1, this), RocketLauncher(-1, -1, this))

  /** ENEMIES */
  private var enemyCollection = Buffer[EnemySoldier]()
  def enemies = enemyCollection
  def filterDeadEnemy() = enemyCollection = enemyCollection.filter(_.getHP > 0)

  /** DEPLOY */
  def deploy(enemy: EnemySoldier) =
    enemyCollection += enemy
  var toBeDeployed = Vector[EnemySoldier]()
  var nextDeployTime: Long = 5*10
  var cannotDeployUntil = 0L


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

  def upgrade(gridPos: GridPos) =
    val square = map.elementAt(gridPos)
    if !square.isEmpty && square.tower.get.upgradePrice <= gold then
      square.tower.get.upgrade()
      gold -= square.tower.get.upgradePrice

  def remove(pos: GridPos) =
    gold += (this.headquarter.getGoldBackRate* map.elementAt(pos).tower.get.asInstanceOf[GunTower].price).toInt
    gunTowerCollection -= map.elementAt(pos).tower.get.asInstanceOf[GunTower]
    map.elementAt(pos).clear()

  /** ABILITY METHOD */
  val abilityPrice = 750
  //RAGE
  private var rageEndTime = 0.0
  def getRageEndTime = rageEndTime
  def rage() =
    if gold >= abilityPrice then
      gold -= abilityPrice
      gunTowers.foreach(gun => gun.rage)
      rageEndTime = survivingTimeInOneFifthSec + 15*5
      println("rg")
  def derage() =
    gunTowers.foreach(gun => gun.derage)
    rageEndTime = 0
  //FREEZE
  private var freezeEndTime = 0.0
  def getFreezeEndTime = freezeEndTime
  def freeze() =
    if gold >= abilityPrice then
      gold -= abilityPrice
      freezeEndTime = survivingTimeInOneFifthSec + 15*5
  def defrost() =
    freezeEndTime = 0
  //POISON
  def poison() =
    if gold >= abilityPrice then
      gold -= abilityPrice
      enemies.foreach(enemy =>
        enemy.minusHP(100)
        enemy.widthProperty.value = 50*enemy.HPpercentage)

  /** SAVE GAME */
  def saveRecord(): Unit =
    val currentDate = LocalDate.now()
    val customFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val date = currentDate.format(customFormat)
    val data = s"$date\t$score\t${survivingTimeInOneFifthSec / 5}\t${wave - 1}\t$enemyKilled\n"
    val filePath = "src/main/resources/record.txt"
    val writer = new BufferedWriter(new FileWriter(filePath, true))
    try {
      writer.write(data)
    }
    catch
      case e: Exception => println(e.getMessage)
    finally {writer.close()}

  def saveGame() =
    val fileOutputStream = new FileOutputStream("savedGame.ser")
    val objectOutputStream = new ObjectOutputStream(fileOutputStream)
    try{
      objectOutputStream.writeObject(this)
    }
    catch {
      case e: Exception => println(e.getMessage)
    }
    finally{
      fileOutputStream.close()
      objectOutputStream.close()
    }
    
end Game
