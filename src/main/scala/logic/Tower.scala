package logic

/** SCALAFX IMPORT */
import scalafx.beans.property.{DoubleProperty, ObjectProperty}
import scalafx.geometry.Insets
import scalafx.scene.Node
import scalafx.scene.image.Image
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.TextAlignment.Center
import scalafx.scene.text.{Text, TextFlow}

/** OTHER IMPORT */
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.math.BigDecimal.RoundingMode
import scala.math.*

trait Tower:
  def upgrade(): Unit
  def upgradePrice: Int
  def description: Seq[Node]
  val levelCoef = 1.05
end Tower


case class Headquarter(x: Int, y: Int) extends Tower:

  /** LEVEL */
  private var level = 1 //NEED SAVING

  /** HP */
  private var maxHP = 20000
  def getMaxHP = maxHP
  private var HP = 20000 //NEED SAVING
  def getHP = HP
  def HPpercentage = HP*1.0/maxHP
  def minusHP(amount: Int) = HP -= amount

  /** GOLD */
  private var goldPer10s = 10 //NEED SAVING
  def getGoldPer10s = goldPer10s
  private var goldBackRate = 0.6 //NEED SAVING
  def getGoldBackRate = goldBackRate

  /** UPGRADE */
  def upgradePrice = 500 + 200*level
  def upgrade() =
    level += 1
    HP = (HP * levelCoef).toInt
    maxHP = (maxHP * levelCoef).toInt
    goldPer10s = (goldPer10s * levelCoef).toInt
    goldBackRate = min(goldBackRate + 0.1, 0.9)

  /** LOAD */
  def load(stringInfo: String) =
    val info = stringInfo.split("\t").takeRight(2)
    for _ <- 1 until info(0).toInt do this.upgrade()
    HP = info(1).toInt

    
  /** DESCRIPTION */

  /** Description is a table:
   *    - heading is the first row
   *    - VBox content is the other rows
   *      + column1, 2 are children of content
   *    - heading and content are the children of a VBox, which makes a table*/
  def description =
    val stats = Seq("Level: " -> level,
      "Gold/10s: " -> getGoldPer10s,
      "Upgrade Price: " -> upgradePrice,
      "Gold back rate: " -> BigDecimal(goldBackRate).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble)

    val textFlows = stats.map ((stat, value) =>
      val statText = new Text(stat)
      statText.style = Helper.gothamBold(14)
      val valueText = new Text(s"$value")
      valueText.style = Helper.gothamNormal(14)
      new TextFlow:
        children = Seq(statText, valueText)
    )
    val info = new VBox:
      val heading = new Text("Headquarter")
      heading.style = Helper.gothamBold(20)
      heading.textAlignment = Center

      val content = new HBox:
        spacing = 20
        val column1 = new VBox:
          children = textFlows.slice(0, 2)
        val column2 = new VBox:
          children = textFlows.slice(2, 4)
        children = Seq(column1, column2)
      padding = Insets(10, 20, 0, 20)
      children = Seq(heading, content)
      spacing = 20
      
    val bg = new Rectangle:
      width = 300
      height = 130
      arcWidth = 20
      arcHeight = 20
      fill = Color.web("#BF9000")
    Seq(bg, info)

  override def toString: String = s"HQ\t$x\t$y\t$level\t$getHP\n"
  
class GunTower(val name: String, x: Int, y: Int, var damage: Int, var fireRate: Double, var range: Double, val price: Int, game: Game) extends Tower:

  /** STATISTIC */
  private var rageConst = 1.0
  def getDamage = (damage*rageConst).toInt
  def getFireRate = fireRate*1.0/rageConst
  def roundedFireRate = BigDecimal(getFireRate/levelCoef).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble
  def getRange = range*rageConst

  /** GET LOCATION */
  val getX = x
  val getY = y

  /** UPGRADE METHODS */
  private var level = 1
  private var moneySpent = price
  def upgradePrice = (price/15.0).toInt * 10 + 30*level
  def upgrade() =
    level += 1
    moneySpent += upgradePrice
    fireRate = fireRate/levelCoef
    damage = (damage * levelCoef).toInt

  /** ANGLE */
  val prevAngle = DoubleProperty(0.0)
  def updateRotationAngle() =
    if target.nonEmpty then prevAngle.value = toDegrees(atan2((y*1.0 - target.get.getY),(x*1.0 - target.get.getX)))

  lazy val image = ObjectProperty(Image("image/ci" + name + ".png"))
  /** LOAD */
  def load(stringInfo: String) =
    val info = stringInfo.split("\t").takeRight(1).head.toInt
    for _ <- 0 until info do this.upgrade()

  /** SHOOT METHODS */
  var onCoolDown = false
  def target: Option[EnemySoldier] = game.enemies.find(enemy => distanceTo(enemy) <= getRange)
  def shoot() =
    if !onCoolDown && target.nonEmpty then
      target.get.minusHP(getDamage)
      onCoolDown = true
      target.get.widthProperty.value = (50*(target.get.HPpercentage))
      updateRotationAngle()
      image.value = Image("image/ci" + name + "Shoot.png")
      Future {
        Thread.sleep(((getFireRate*50)/game.pace).toLong)
        image.value = Image("image/ci" + name + ".png")
        Thread.sleep(((getFireRate*950)/game.pace).toLong)
        onCoolDown = false}
  def distanceTo(enemy: EnemySoldier) =
    sqrt(pow((x-enemy.getX), 2) + pow((y-enemy.getY), 2))

  /** ABILITY */
  def rage() = rageConst = 1.15
  def derage() = rageConst = 1

  /** DESCRIPTION */
  /** Description is a table:
   *    - heading is the first row
   *    - VBox content is the other rows
   *      + column1, 2 are children of content
   *    - heading and content are the children of a VBox, which makes a table*/
  def description =
    val stats = Seq("Level: " -> level, "Upgrade Price: " -> upgradePrice,
      "Damage: " -> damage, "Firerate: " -> roundedFireRate, "Range: " -> range, "Price: " -> price)
    val textFlows = stats.map ((stat, value) =>
      val statText = new Text(stat)
      statText.style = Helper.gothamBold(14)
      val valueText = new Text(s"$value")
      valueText.style = Helper.gothamNormal(14)
      new TextFlow:
        children = Seq(statText, valueText)
    )
    val info = new VBox:
      val heading = new Text(if getY == -1 then name else s"$name ($getX, $getY)")
      heading.style = Helper.gothamBold(20)
      heading.textAlignment = Center
      val content = new HBox:
        spacing = 20
        val column1 = new VBox:
          children = textFlows.slice(0, 3)
        val column2 = new VBox:
          children = textFlows.slice(3, 6)
        children = Seq(column1, column2)
      children = Seq(heading, content)
      spacing = 20
      padding = Insets(10, 20, 0, 20)
      
    val bg = new Rectangle:
      width = 300
      height = 130
      arcWidth = 20
      arcHeight = 20
      fill = Color.web("#BF9000")

    Seq(bg, info)

  override def toString: String = s"$name\t$x\t$y\t$level\n"

end GunTower

case class Sharpshooter(x: Int, y: Int, game: Game) extends GunTower("Sharpshooter", x, y, 45, 0.8, 7.0, 120, game: Game)
case class Cannon(x: Int, y: Int, game: Game) extends GunTower("Cannon", x, y, 90, 0.6, 3, 150, game: Game)
case class Turret(x: Int, y: Int, game: Game) extends GunTower("Turret", x, y, 90, 0.6, 5.0, 200, game: Game)
case class Sniper(x: Int, y: Int, game: Game) extends GunTower("Sniper", x, y, 375, 3.0, 7.0, 300, game: Game)
case class GrenadeLauncher(x: Int, y: Int, game: Game) extends GunTower("GrenadeLauncher", x, y, 270, 1.0, 3.0, 250, game: Game)
case class RocketLauncher(x: Int, y: Int, game: Game) extends GunTower("RocketLauncher", x, y, 525, 1.4, 5.0, 500, game: Game)
