package logic

import logic.grid.CompassDir.*
import logic.grid.GridPos
import scalafx.beans.property.DoubleProperty
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, StackPane}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.FontWeight.Black


import java.io
import scala.math.BigDecimal.RoundingMode
import scala.math.{Pi, cos, pow, round, sin}



abstract class EnemySoldier(game: Game, baseDamage: Int):

  val damage: Int = (baseDamage*pow(1.3, game.getWave)).toInt
  def id: String
  /** HP */
  private var HP = damage
  def getHP = HP
  private var maxHP = damage
  def getMaxHP = maxHP
  def HPpercentage = 1.0*getHP/getMaxHP
  def minusHP(amount: Int) = HP -= amount
  def isDead = HP <= 0

  /** GOLD AND POINT */
  val gold = damage/100
  val point = damage*10
  
  /** POSITION */
  private var x = game.map.startingSquare.x*1.0
  private var y = game.map.startingSquare.y*1.0
  def getX = x
  def getY = y
  private var pace = 0.2
  private var heading = East
  def getHeading = heading

  /** ADVANCE METHODS */
  val adjustConst = 45-0.25
  var step = 0
  def advance() =
    if x == this.game.map.crashSquare.x && y == this.game.map.crashSquare.y then crash()
    else
      turnDirection match
        case 1 => turnRight()
        case 0 => turnLeft()
        case -1 => ()
      x = BigDecimal(x +heading.xStep*pace).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble
      y = BigDecimal(y +heading.yStep*pace).setScale(1, BigDecimal.RoundingMode.HALF_UP).toDouble
      imageView.layoutY = adjustConst*y
      imageView.layoutX = adjustConst*x
      step += 1
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
    game.widthPropertyOfHQHP.value = 45*game.headquarter.HPpercentage
  
  /** UI */
  def picturePath: String
  val HPbarWidth = 45
  val widthProperty = DoubleProperty(HPbarWidth*HPpercentage)
  val enemyImage = new ImageView:
    fitHeight = 45
    fitWidth = 45
    rotate = 90
    layoutX = (getX*adjustConst)
    layoutY = (getY*adjustConst)
  val HPimage = new StackPane:
    val maxHPbar = new Rectangle:
      width = HPbarWidth
      height = 5
      fill = Color.Black
    val HPbar = new Rectangle:
      width <== widthProperty
      height = 5
      fill = Color.Green
      alignmentInParent = scalafx.geometry.Pos.TopLeft
    children = Seq(maxHPbar, HPbar)
  val imageView = new BorderPane(enemyImage, null, null, HPimage, null)

  /** LOAD */
  def load(stringInfo: String) =
    val info = stringInfo.split("\t").drop(1)
    for _ <- 0 until info(0).toInt do this.advance()
    HP = (maxHP * info(1).toDouble).toInt
    widthProperty.value = HPbarWidth*HPpercentage

case class Infantry(game: Game) extends EnemySoldier(game, 200):
  override val picturePath = "image/infantry.png"
  val id = "i\t"
  override def toString = s"Infantry\t$step\t$HPpercentage\n"
end Infantry

case class Cavalry(game: Game) extends EnemySoldier(game, 400):
  override val picturePath = "image/cavalry.png"
  val id = "c\t"
  override def toString = s"Cavalry\t$step\t$HPpercentage\n"
end Cavalry

case class ArmoredCar(game: Game) extends EnemySoldier(game, 600):
  override val picturePath = "image/armoredCar.png"
  val id = "a\t"
  override def toString = s"ArmoredCar\t$step\t$HPpercentage\n"
end ArmoredCar

case class Tank(game: Game) extends EnemySoldier(game, 800):
  override val picturePath = "image/tank.png"
  val id = "t\t"
  override def toString = s"Tank\t$step\t$HPpercentage\n"
end Tank

