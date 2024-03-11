package logic

import logic.grid.CompassDir.*
import scalafx.beans.property.DoubleProperty
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{StackPane, BorderPane}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.FontWeight.Black
import scalafx.scene.paint.Color.*

import scala.math.{Pi, cos, round, sin}
import scala.math.BigDecimal.RoundingMode



abstract class EnemySoldier(game: Game, damage: Int):
  
  /** HP */
  private var HP = damage
  def getHP = HP
  private var maxHP = damage
  def getMaxHP = maxHP
  def HPpercentage = 1.0*HP/maxHP
  def minusHP(amount: Int) = HP -= amount
  def isDead = HP <= 0

  /** GOLD AND POINT */
  val gold = damage/10
  val point = damage*10

  
  /** POSITION */
  private var x = game.map.startingSquare.x*1.0
  private var y = game.map.startingSquare.y*1.0
  def getX = x
  def getY = y
  private var pace = 0.2
  private var heading = East

  /** ADVANCE METHODS */
  val adjustConst = 50-0.3882
  def advance() =
    turnDirection match
      case 1 => turnRight()
      case 0 => turnLeft()
      case -1 => ()
    x = BigDecimal(x +heading.xStep*pace).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble
    y = BigDecimal(y +heading.yStep*pace).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble
    imageView.layoutY = adjustConst*y
    imageView.layoutX = adjustConst*x
  def turnRight() =
    heading = heading.clockwise
    enemyImage.rotate.value += 90
  def turnLeft() =
    heading = heading.counterClockwise
    enemyImage.rotate.value -= 90
  def turnDirection =
    val index = game.map.turningSquare.indexOf((x,y))
    if index != -1 then game.map.turningDirection(index) else -1
  def crash() =
    game.headquarter.minusHP(damage)
    HP = 0
  
  /** UI */
  def picturePath: String
  val widthProperty = DoubleProperty(50*HPpercentage)
  val enemyImage = new ImageView:
    fitHeight = 50
    fitWidth = 50
    rotate = 90
    layoutX = (getX*adjustConst)
    layoutY = (getY*adjustConst)
  val HPimage = new StackPane:
    val maxHPbar = new Rectangle:
      width = 50
      height = 5
      fill = Color.Black
    val HPbar = new Rectangle:
      width <== widthProperty
      height = 5
      fill = Color.Green
      alignmentInParent = scalafx.geometry.Pos.TopLeft
    children = Seq(maxHPbar, HPbar)
  val imageView = new BorderPane(enemyImage, null, null, HPimage, null)

case class Infantry(game: Game) extends EnemySoldier(game, 200):
  override val picturePath = "image/infantry.png"
  override def toString = s"Infantry($getX, $getY)"
end Infantry

case class Cavalry(game: Game) extends EnemySoldier(game, 400):
  override val picturePath = "image/cavalry.png"
  override def toString = s"Cavalry($getX, $getY)"
end Cavalry

case class ArmoredCar(game: Game) extends EnemySoldier(game, 600):
  override val picturePath = "image/armoredCar.png"
  override def toString = s"ArmoredCar($getX, $getY)"
end ArmoredCar

case class Tank(game: Game) extends EnemySoldier(game, 800):
  override val picturePath = "image/tank.png"
  override def toString = s"Tank($getX, $getY)"
end Tank

