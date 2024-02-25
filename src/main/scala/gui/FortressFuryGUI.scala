import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.geometry.Pos.BaselineCenter
import scalafx.geometry.VPos.Baseline
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, GridPane, HBox, Pane, StackPane, VBox}
import scalafx.scene.shape.{Rectangle, Circle}
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import scala.util.Random
import scala.math.min

object FortressFuryGUI extends JFXApp3:

  def start() =
    /** UI Variables */
    val squareside = 50
    val windowWidth = 1495
    val windowHeight = 750
    val colNum = 31
    val rowNum = 11

    /** Game Variables */
    val level = 1
    val wave = 2
    var gold = 5000
    var score = 102921387019L
    var HP = 1200
    val maxHP = 1600
    val gunPrice = Vector(120, 150, 200, 250, 300, 500)


    /** Center */
    val grid = new GridPane()
    grid.alignmentInParent = scalafx.geometry.Pos.Center
    grid.prefHeight = rowNum * squareside
    grid.prefWidth = colNum * squareside
    grid.hgap = -1
    grid.vgap = -1

    for
      row <- 0 until rowNum
      col <- 0 until colNum
    do
      val square = new StackPane:
        val squareBg = new Rectangle:
          width = squareside
          height = squareside
          fill = Transparent
        val squareImage = new ImageView:
          image =
            val kind = squaretype(col + 1, row)
            if kind == "buildable" then
              new Image("doc/image/" + kind + min(Random.nextInt(30)+1, 7).toString +  ".png")
            else new Image("doc/image/" + kind +  ".png")
          fitHeight = squareside
          fitWidth = squareside
        children = Seq(squareBg, squareImage)
      grid.add(square, col, row)

    val center = grid

    /** Top */
    val topleft = new VBox:
      spacing = 10
      padding = Insets(0, 20, 0, 20)
      val levelWaveLabel = Label(s"Level $level - Wave $wave")
      levelWaveLabel.font = Font.font("Arial", FontWeight.Bold, null, 20)
      /** Gold */
      val goldLabel = new Text {
        text = "Gold: "
        font = Font.font("Arial", FontWeight.Bold, null, 15)
      }
      val goldValue = new Text {
        text = gold.toString
        font = Font.font("Arial", null, null, 15)
      }
      val goldText = new TextFlow {
        children = Seq(goldLabel, goldValue)
      }
      /** HP */
      val HPLabel = new Text {
        text = "HP: "
        font = Font.font("Arial", FontWeight.Bold, null, 15)
      }
      val HPValue = new Text {
        text = HP.toString + "/" + maxHP.toString
        font = Font.font("Arial", null, null, 15)
      }
      val HPText = new TextFlow {
        children = Seq(HPLabel, HPValue)
      }
      children = Array(levelWaveLabel, goldText, HPText)
    val topcenter = new Label(score.toString)
    topcenter.font = Font.font("Arial", FontWeight.Bold, null, 36)
    val topright = new HBox:
      val bgSize = 70
      val iconSize = 50
      val setting = new StackPane:
        val settingBg = new Rectangle:
          height = bgSize
          width = bgSize
          fill = Blue
        val settingIcon = new Rectangle:
          height = iconSize
          width = iconSize
          fill = Red
        children = Seq(settingBg, settingIcon)
        alignment = scalafx.geometry.Pos.Center
      val speedup = new StackPane:
        val speedupBg = new Rectangle:
          height = bgSize
          width = bgSize
          fill = Blue
        val speedupIcon = new Rectangle:
          height = iconSize
          width = iconSize
          fill = Red
        children = Seq(speedupBg, speedupIcon)
        alignment = scalafx.geometry.Pos.Center
      children = Seq(speedup, setting)
      padding = Insets(0, 45, 0, 0)
      spacing = 10

    val top = new BorderPane(topcenter, null, topright, null, topleft)
    top.padding = Insets(0, 0, 15, 0)

    /** Bottom */
    val bottom = new HBox:
      val btWidth = 100
      val btHeight = 130
      val gunButtons = Array.tabulate(6) { i =>
        val bg = new Rectangle:
          width = btWidth
          height = btHeight
          arcWidth = 20
          arcHeight = 20
          fill = Red
          children = new VBox:
            children = Array(Label(s"Level $level - Wave $wave"), Label(s"Gold: $gold"), Label(s"HP: $HP/$maxHP"))
        val gunPicture = new StackPane:
          val sqCannon = Image("doc/image/SqCannon.png")
          val sqSharpshooter = Image("doc/image/SqSharpshooter.png")
          val sqSniper = Image("doc/image/SqSniper.png")
          val sqTurret = Image("doc/image/SqTurret.png")
          val sqGrenadeLauncher = Image("doc/image/SqGrenadeLauncher.png")
          val sqRocketLauncher = Image("doc/image/SqRocketLauncher.png")
          val sqGunCollection = Vector(sqSharpshooter, sqCannon, sqTurret, sqGrenadeLauncher, sqSniper, sqRocketLauncher)
          val pictureBg = new Rectangle:
            width = btWidth*4/5
            height = btWidth*4/5
            arcWidth = 20
            arcHeight = 20
            fill = Transparent
            padding = Insets(btWidth/15, 0, 0, 0)
          val pictureContent = new ImageView:
            fitWidth = btWidth*4/5
            fitHeight = btWidth*4/5
            image = sqGunCollection(i)
          children = Seq(pictureBg, pictureContent)


        val priceLabel = Label(s"${gunPrice(i)}")
        priceLabel.font = Font.font("Arial", FontWeight.Bold, null, 20)

        val contentOfButton = new VBox:
          alignment = BaselineCenter
          children = Array(gunPicture, priceLabel)
          padding = Insets(btWidth/15, 0, 0, 0)
        new StackPane:
          maxHeight = btHeight
          children += bg
          children += contentOfButton
          alignment = scalafx.geometry.Pos.Center

  }
      children = gunButtons
      spacing = 10
      padding = Insets(0, 0, 10, 0)
      alignment = scalafx.geometry.Pos.Center

    /** Root */
    val root = BorderPane(center, top, null, bottom, null)
    root.prefWidth = windowWidth
    root.prefHeight = windowHeight
    root.center = grid


    stage = new JFXApp3.PrimaryStage:
      title = "UniqueProjectName"
      width = windowWidth
      height = windowHeight
      scene =  Scene(parent = root)

  end start


  def squaretype(x: Int, y: Int) =
    val h1 = ((x >= 0 && x <= 3) || (x >= 8 && x <= 13) || (x >= 22 && x <= 26))  && (y == 4)
    val h2 = (x >= 5 && x <= 13)                                                  && (y == 8)
    val h3 = (x >= 8 && x <= 16)                                                  && (y == 2)
    val h4 = (x >= 18 && x <= 20)                                                 && (y == 6)
    val v1   = (x == 4 || x == 14 || x == 27)                                       && (y >= 5 && y <= 7)
    val v2   = (x == 7)                                                             && (y == 3)
    val v3   = (x == 17)                                                            && (y >= 3 && y <= 5)
    val v4   = (x == 21)                                                            && (y == 5)
    val c1     = Vector((4, 4), (14, 4), (17, 2), (27, 4)).contains((x,y))
    val c2     = Vector((4, 8), (7, 4), (17, 6)).contains((x,y))
    val c3     = Vector((14, 8), (21, 6)).contains((x,y))
    val c4     = Vector((7, 2), (21, 4)).contains((x,y))
    if h1 || h2 || h3 || h4 then "horizontal"
    else if v1 || v2 || v3 || v4 then "vertical"
    else if c1 then "corner1"
    else if c2 then "corner2"
    else if c3 then "corner3"
    else if c4 then "corner4"
    else "buildable"
end FortressFuryGUI


