import scalafx.application.JFXApp3
import scalafx.geometry.Insets
import scalafx.geometry.Pos.BaselineCenter
import scalafx.geometry.VPos.Baseline
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.layout.{BorderPane, GridPane, HBox, Pane, StackPane, VBox}
import scalafx.scene.shape.{Rectangle, Circle}
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight}
import scalafx.scene.text.{Text, TextFlow}


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
    val gunPrice = Vector(100, 200, 300, 400, 500)

    /** Center */
    val grid = new GridPane()
    grid.alignmentInParent = scalafx.geometry.Pos.Center
    grid.prefHeight = rowNum * squareside
    grid.prefWidth = colNum * squareside
    grid.hgap = 0
    grid.vgap = 0

    for
      row <- 0 until rowNum
      col <- 0 until colNum
    do
      val rectangle = new Rectangle:
        width = squareside
        height = squareside
        fill = if finalCondition(col+1, row) then Green else Blue
      grid.add(rectangle, col, row)

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
      val btWidth = 70
      val btHeight = 100
      val buttons = Array.tabulate(5) { i =>
        val bg = new Rectangle:
          width = btWidth
          height = btHeight
          arcWidth = 20
          arcHeight = 20
          fill = Red
          children = new VBox:
            children = Array(Label(s"Level $level - Wave $wave"), Label(s"Gold: $gold"), Label(s"HP: $HP/$maxHP"))
        val picture = new Rectangle:
          width = btWidth*4/5
          height = btWidth*4/5
          arcWidth = 20
          arcHeight = 20
          fill = Blue
          padding = Insets(btWidth/10, 0, 0, 0)
        val priceLabel = Label(s"${gunPrice(i)}")

        val contentOfButton = new VBox:
          alignment = BaselineCenter
          children = Array(picture, priceLabel)
          padding = Insets(btWidth/10, 0, 0, 0)
        new StackPane:
          maxHeight = btHeight
          children += bg
          children += contentOfButton
          alignment = scalafx.geometry.Pos.Center

  }
      children = buttons
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


  def finalCondition(x: Int, y: Int) =
    val condition1 = (y == 4 && ((x >= 0 && x <= 4) || (x >= 7 && x <= 14) || (x >= 21 && x <= 27)))
    val condition2 = (y >= 5 && y <= 7) && (x == 4 || x == 14)
    val condition3 = ((x >= 4 && x <= 14) && y == 8)
    val condition4 = (x == 7 && y == 3)
    val condition5 = (x >= 7 && x <= 17) && y == 2
    val condition6 = (y >= 3 && y <= 5) && x == 17
    val condition7 = y == 6 && (x >= 17 && x <= 21)
    val condition8 = x == 21 && y == 5
    val condition9 = x == 27 && (y >= 5 && y <= 7)
    condition1 || condition2 || condition3 || condition4 || condition5 || condition6 || condition7 || condition8 || condition9
end FortressFuryGUI


