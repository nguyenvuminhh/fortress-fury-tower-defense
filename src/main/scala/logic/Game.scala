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

import scala.io.Source

class Game (val map: Map) extends Serializable:
  /** SCORE */
  private var score = 0 //NEED SAVING
  def getScore = score
  def addScore(amount: Int) = score += amount
  private var enemyKilled = 0 //NEED SAVING TODO
  def getEnemyKilled = enemyKilled
  def increaseEnemyKilled() = enemyKilled += 1

  /** GOLD */
  private var gold = 10000 //NEED SAVING
  def getGold = gold
  def giveGold() = gold += headquarter.getGoldPer10s
  def addGold(amount: Int) = gold += amount

  /** WAVE */
  private var wave = 0 //NEED SAVING
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
  private var survivingTimeInOneFifthSec: Long = 0 //NEED SAVING
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
  var headquarter = Headquarter(map.HQSquare.x, map.HQSquare.y) //NEED SAVING
  map.elementAt(map.HQSquare).addTower(headquarter)
  private var gunTowerCollection = Buffer[GunTower]() //NEED SAVING
  def gunTowers = gunTowerCollection
  val infoGunTowers = Vector[GunTower](Sharpshooter(-1, -1, this), Cannon(-1, -1, this), Turret(-1, -1, this), GrenadeLauncher(-1, -1, this), Sniper(-1, -1, this), RocketLauncher(-1, -1, this))

  /** ENEMIES */
  private var enemyCollection = Buffer[EnemySoldier]() //NEED SAVING
  def enemies = enemyCollection
  def filterDeadEnemy() = enemyCollection = enemyCollection.filter(_.getHP > 0)

  /** DEPLOY */
  def deploy(enemy: EnemySoldier) =
    enemyCollection += enemy
  var toBeDeployed = Vector[EnemySoldier]() //NEED SAVING
  var nextDeployTime: Long = 5*10 //NEED SAVING TODO
  var cannotDeployUntil = 0L //NEED SAVING TODO

  /** LOAD METHODS */
  def loadBasic(stringInfo: String) =
    val info = stringInfo.split("\t")
    score = info(0).toInt
    gold = info(1).toInt
    wave = info(2).toInt
    survivingTimeInOneFifthSec = info(3).toLong
    nextDeployTime = info(4).toLong
    cannotDeployUntil = info(5).toLong
    enemyKilled = info(6).toInt
  def loadTower(seqStringInfo: Seq[String]) =
    headquarter.load(seqStringInfo.head)
    for stringInfo <- seqStringInfo.tail do
      gold += 500
      val info = stringInfo.split("\t")
      val x = info(1).toDouble.toInt
      val y = info(2).toDouble.toInt
      place(info.head, x, y)
      gunTowerCollection.takeRight(1).head.load(stringInfo)
  def loadEnemies(seqStringInfo: Seq[String]) =
    for stringInfo <- seqStringInfo do
      val info = stringInfo.split("\t")
      deploy( info.head match
        case "Infantry" => Infantry(this)
        case "Cavalry" => Cavalry(this)
        case "ArmoredCar" => ArmoredCar(this)
        case "Tank" => Tank(this))
      enemies.takeRight(1).head.load(stringInfo)
      println(stringInfo)
  def loadUndeployed(stringInfo: String) =
    val info = stringInfo.split("\t") //TODO: check if need reverse
    for i <- info do
      i match
        case "i" => toBeDeployed = toBeDeployed.appended(Infantry(this))
        case "c" => toBeDeployed = toBeDeployed.appended(Cavalry(this))
        case "a" => toBeDeployed = toBeDeployed.appended(ArmoredCar(this))
        case "t" => toBeDeployed = toBeDeployed.appended(Tank(this))
        case "n" => ()

  def load() =
    pause()
    val data = Source.fromFile("savedGame.txt")
    val seqStringInfo = data.getLines().toSeq
    val lineIndex = seqStringInfo.head.split("\t")
    loadTower(seqStringInfo.slice(2, lineIndex(0).toInt + 2))
    loadEnemies(seqStringInfo.slice(lineIndex(0).toInt + 2, lineIndex(0).toInt + 2 + lineIndex(1).toInt))
    loadUndeployed(seqStringInfo(lineIndex(0).toInt + 2 + lineIndex(1).toInt))
    loadBasic(seqStringInfo(1))

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
    val filePath = "savedGame.txt"
    val writer = new BufferedWriter(new FileWriter(filePath, true))
    //LINE 1
    val lineIndex = (gunTowerCollection.length+1).toString ++ "\t" ++ enemies.length.toString ++ "\n"
    //LINE 2
    val basicInfo = s"$score\t$gold\t$wave\t$survivingTimeInOneFifthSec\t$nextDeployTime\t$cannotDeployUntil\t$enemyKilled\n"
    //LINE 3 -> N
    val gunInfo =
      if gunTowerCollection.nonEmpty then
        headquarter.toString ++ gunTowerCollection.map(_.toString).reduce((a,b) => a ++ b)
      else headquarter.toString
    //LINE N+1 -> M
    val enemyInfo = if enemies.nonEmpty then enemies.map(_.toString).reduce((a,b) => a ++ b) else ""
    //LINE M+1
    val undeployedInfo = if toBeDeployed.nonEmpty then toBeDeployed.map(_.id).reduce((a,b) => a ++ b) else "n"

    val data = lineIndex ++ basicInfo ++ gunInfo ++ enemyInfo ++ undeployedInfo
    try {
      writer.write(data)
    }
    catch
      case e: Exception => println(e.getMessage)
    finally {writer.close()}
end Game
