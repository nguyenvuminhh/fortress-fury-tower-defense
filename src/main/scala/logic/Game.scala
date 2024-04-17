package logic
import logic.grid.GridPos

/** SCALAFX IMPORT */
import scalafx.beans.property.{BooleanProperty, DoubleProperty}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

/** OTHER IMPORT */
import java.io.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import scala.collection.mutable.Buffer
import scala.io.Source
import scala.math.{pow, sqrt}

class Game (val map: Map):
  /** SCORE */
  private var score = 0 //NEED SAVING
  def getScore = score
  def addScore(amount: Int) = score += amount
  private var enemyKilled = 0 //NEED SAVING
  def getEnemyKilled = enemyKilled
  def increaseEnemyKilled() = enemyKilled += 1

  /** GOLD */
  private var gold = 1000 //NEED SAVING
  def getGold = gold
  def giveGold() = gold += headquarter.getGoldPer10s
  def addGold(amount: Int) = gold += amount

  /** WAVE */
  private var wave = 0 //NEED SAVING
  def nextWave = wave += 1
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

  /** SPEEDUP */
  val paceVector = Vector(0.5, 0.75, 1, 1.5, 2)
  private var paceIndex = 2
  def pace = paceVector(paceIndex)
  def speedup() = paceIndex = (paceIndex + 1)%5

  /** TOWERS */
  var headquarter = Headquarter(map.HQSquare.x, map.HQSquare.y) //NEED SAVING
  val widthPropertyOfHQHP = DoubleProperty(45*headquarter.HPpercentage)
  map.elementAt(map.HQSquare).addTower(headquarter)
  val gunTowerCollection = Buffer[GunTower]() //NEED SAVING
  def gunTowers = gunTowerCollection
  lazy val infoGunTowers = Vector[GunTower](Sharpshooter(-1, -1, this), Cannon(-1, -1, this), Turret(-1, -1, this), GrenadeLauncher(-1, -1, this), Sniper(-1, -1, this), RocketLauncher(-1, -1, this))

  /** ENEMIES */
  private var enemyCollection = Buffer[EnemySoldier]() //NEED SAVING
  def enemies = enemyCollection
  def filterDeadEnemy() = enemyCollection = enemyCollection.filter(_.getHP > 0)

  /** DEPLOY */
  def deploy(enemy: EnemySoldier) = enemyCollection += enemy
  var toBeDeployed = Vector[EnemySoldier]() //NEED SAVING
  var nextDeployTime: Long = 5*10 //NEED SAVING
  var cannotDeployUntil = 0L //NEED SAVING

  /** LOAD GAMES */
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
    if seqStringInfo.length > 1 then
      for stringInfo <- seqStringInfo.tail do
        val info = stringInfo.split("\t")
        val x = info(1).toDouble.toInt
        val y = info(2).toDouble.toInt
        place((info.head, 0), x, y)
        gunTowerCollection.takeRight(1).head.load(stringInfo)
  def loadEnemies(seqStringInfo: Seq[String]) =
    if seqStringInfo.nonEmpty then
      for stringInfo <- seqStringInfo do
        val info = stringInfo.split("\t")
        deploy( info.head match
          case "Infantry" => Infantry(this)
          case "Cavalry" => Cavalry(this)
          case "ArmoredCar" => ArmoredCar(this)
          case "Tank" => Tank(this) )
        enemies.takeRight(1).head.load(stringInfo)

  def loadUndeployed(stringInfo: String) =
    if stringInfo.nonEmpty then
      val info = stringInfo.split("\t")
      for i <- info do
        i match
          case "i" => toBeDeployed = toBeDeployed.appended(Infantry(this))
          case "c" => toBeDeployed = toBeDeployed.appended(Cavalry(this))
          case "a" => toBeDeployed = toBeDeployed.appended(ArmoredCar(this))
          case "t" => toBeDeployed = toBeDeployed.appended(Tank(this))
          case _ => ()

  def load() =
    pause()
    val data = Source.fromFile("src/main/resources/savedGame.txt")
    if data.nonEmpty then
      val seqStringInfo = data.getLines().toSeq
      val lineIndex = seqStringInfo.head.split("\t").map(_.toInt)
      loadTower(seqStringInfo.slice(2, lineIndex(0) + 2))
      loadEnemies(seqStringInfo.slice(lineIndex(0) + 2, lineIndex(0) + 2 + lineIndex(1)))
      loadUndeployed(seqStringInfo(lineIndex(0) + 2 + lineIndex(1)))
      loadBasic(seqStringInfo(1))
    data.close()

  /** GUN TOWER METHODS */
  def place(gunTowerType: (String, Int), x: Int, y: Int): Boolean =
    if gunTowerType._2 <= gold  && map.elementAt(GridPos(x, y)).isEmpty then
      val gunTower =
        gunTowerType._1 match
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
    else if gunTowerType._2 > gold then
      Alert(AlertType.Warning, "Not enough money").showAndWait()
      false
    else
      Alert(AlertType.Warning, "You cannot place a gun on the road or an occupied square").showAndWait()
      false

  def upgrade(gridPos: GridPos) =
    val square = map.elementAt(gridPos)
    if !square.isEmpty && square.tower.get.upgradePrice <= gold then
      square.tower.get.upgrade()
      gold -= square.tower.get.upgradePrice
    else if square.tower.get.upgradePrice > gold then Alert(AlertType.Warning, "Not enough money").showAndWait()
    else Alert(AlertType.Warning, "You did not select a tower").showAndWait()

  def remove(gridPos: GridPos) =
    map.elementAt(gridPos).tower.get match
      case tower: GunTower =>
        gold += (headquarter.getGoldBackRate * tower.price).toInt
        gunTowerCollection -= tower
        map.elementAt(gridPos).clear()
      case _ => ()


  /** ABILITY METHOD */
  val abilityPrice = 750
  //RAGE
  private var rageEndTime = 0.0
  def getRageEndTime = rageEndTime
  def rage() =
    if gold >= abilityPrice then
      gold -= abilityPrice
      gunTowers.foreach(gun => gun.rage())
      rageEndTime = survivingTimeInOneFifthSec + 15*5
    else
      Alert(AlertType.Warning, "Not enough money").showAndWait()
  def derage() =
    gunTowers.foreach(gun => gun.derage())
    rageEndTime = 0
  //FREEZE
  private var freezeEndTime = 0.0
  def getFreezeEndTime = freezeEndTime
  def freeze() =
    if gold >= abilityPrice then
      gold -= abilityPrice
      freezeEndTime = survivingTimeInOneFifthSec + 15*5
    else
      Alert(AlertType.Warning, "Not enough money").showAndWait()
  def defrost() = freezeEndTime = 0
  //POISON
  def poison() =
    if gold >= abilityPrice then
      gold -= abilityPrice
      enemies.foreach(enemy =>
        enemy.minusHP(100)
        enemy.widthProperty.value = 50*enemy.HPpercentage)
    else
      Alert(AlertType.Warning, "Not enough money").showAndWait()

  /** SAVE GAME */
  val mapType = map.toString
  def saveRecord(): Unit =
    val currentDate = LocalDate.now()
    val customFormat = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val date = currentDate.format(customFormat)
    val data = s"$date\t$mapType\t$score\t${survivingTimeInOneFifthSec / 5}\t${wave - 1}\t$enemyKilled\n"
    val filePath = "src/main/resources/record.txt"
    val writer = new BufferedWriter(new FileWriter(filePath, true))
    try {
      writer.write(data)
    }
    finally {writer.close()}

  def saveGame() =
    val filePath = "src/main/resources/savedGame.txt"
    val writer = new BufferedWriter(new FileWriter(filePath, true))
    //LINE 1
    val lineIndex = (gunTowerCollection.length+1).toString ++ "\t" ++ enemies.length.toString ++ "\n"
    //LINE 2
    val basicInfo = s"$score\t$gold\t$wave\t$survivingTimeInOneFifthSec\t$nextDeployTime\t$cannotDeployUntil\t$enemyKilled\t$mapType\n"
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
    finally {writer.close()}

end Game
