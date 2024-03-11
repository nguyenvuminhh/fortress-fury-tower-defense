package logic
import scalafx.beans.property.DoubleProperty
import scalafx.scene.layout.{HBox, StackPane, VBox}
import scalafx.scene.paint.Color.Blue
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.TextAlignment.Center
import scalafx.scene.text.{Text, TextFlow}

import scala.math.{atan2, pow, sqrt, toDegrees}
import scala.concurrent.*
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global

trait Tower:
  def upgrade(): Unit
  def upgradePrice: Int
end Tower

case class Headquarter(x: Double, y: Double) extends Tower:

  /** LEVEL */
  private var level = 1
  def getLevel = level

  /** HP */
  private var HP = 2000
  def getHP = HP
  private var maxHP = 2000
  def getMaxHP = maxHP
  def HPpercentage = HP*1.0/maxHP
  def minusHP(amount: Int) = HP -= amount

  /** GOLD */
  private var goldPer10s = 10
  def getGoldPer10s = goldPer10s
  private var goldBackRate = 0.6
  def getGoldBackRate = goldBackRate

  /** UPGRADE */
  def upgradePrice = 500 + 200*level
  def upgrade() =
    level += 1
    HP = (HP * 0.05).toInt
    goldPer10s = (goldPer10s * 0.05).toInt


class GunTower(name: String, x: Int, y: Int, var damage: Int, var fireRate: Double, val range: Double, val price: Int, game: Game) extends Tower:
  /** GET LOCATION */
  val getX = x
  val getY = y

  /** UPGRADE METHODS */
  private var level = 1
  private var moneySpent = price
  def upgradePrice = (price/15.0).toInt * 10
  def upgrade() =
    level += 1
    moneySpent += upgradePrice
    fireRate = fireRate * 0.05
    damage = (damage * 0.05).toInt

  /** ANGLE */
  val prevAngle = DoubleProperty(0.0)
  def updateRotationAngle =
    if target.nonEmpty then
      prevAngle.value = toDegrees(atan2((y*1.0 - target.get.getY),(x*1.0 - target.get.getX)))

  /** SHOOT METHODS */
  var onCoolDown = false
  def target: Option[EnemySoldier] = game.enemies.find(enemy => distanceTo(enemy) <= range)

  def shoot() =
    if !onCoolDown && target.nonEmpty then
      val startTime = System.currentTimeMillis()
      target.get.minusHP(damage)
      onCoolDown = true
      target.get.widthProperty.value = (50*(target.get.HPpercentage))
      updateRotationAngle
      Future {
        Thread.sleep(((fireRate*100)/game.pace).toLong)
        onCoolDown = false}

  def distanceTo(enemy: EnemySoldier) =
    sqrt(pow((x-enemy.getX), 2) + pow((y-enemy.getY), 2))

  def description =
    val stats = Seq("Level: " -> level, "Upgrade Price: " -> upgradePrice,
      "Damage: " -> damage, "Firerate: " -> fireRate, "Range: " -> range, "Price: " -> price)
    val textFlows = stats.map ((stat, value) =>
      val statText = new Text(stat)
      statText.style = s"-fx-font-size: 14px; -fx-font-weight: bold;"
      val valueText = new Text(s"$value")
      valueText.style = s"-fx-font-size: 14px; -fx-font-weight: normal;"
      new TextFlow:
        children = Seq(statText, valueText)
    )
    val info = new VBox:
      val heading = Text(name)
      heading.style = s"-fx-font-size: 20px; -fx-font-weight: bold;"
      heading.textAlignment = Center
      val content = new HBox:
        spacing = 20
        val column1 = new VBox:
          children = textFlows.slice(0, 3)
        val column2 = new VBox:
          children = textFlows.slice(3, 6)
        children = Seq(column1, column2)
      children = Seq(heading, content)

      spacing = 20 //TODO: edit spacing, alignment
    val bg = new Rectangle:
      width = 250
      height = 130
      fill = Blue
    Seq(bg, info)



end GunTower

case class Cannon(x: Int, y: Int, game: Game) extends GunTower("Cannon", x, y, 50, 1.0, 1.5, 150, game: Game)
case class Turret(x: Int, y: Int, game: Game) extends GunTower("Turret", x, y, 20, 0.4, 2.3, 200, game: Game)
case class Sniper(x: Int, y: Int, game: Game) extends GunTower("Sniper", x, y, 250, 3.0, 3.0, 200, game: Game)
case class Sharpshooter(x: Int, y: Int, game: Game) extends GunTower("Sharpshooter", x, y, 30, 0.8, 6.0, 120, game: Game)
case class RocketLauncher(x: Int, y: Int, game: Game) extends GunTower("RocketLauncher", x, y, 500, 4.0, 5.0, 500, game: Game)
case class GrenadeLauncher(x: Int, y: Int, game: Game) extends GunTower("GrenadeLauncher", x, y, 100, 1.0, 1.0, 250, game: Game)