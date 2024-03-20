package app


import app.scenes.{GameOverScene, GameScene, LeaderBoardScene, LobbyScene}
import logic.Game
import scalafx.application.JFXApp3
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.geometry.Pos.BaselineCenter
import scalafx.geometry.VPos.Baseline
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, GridPane, HBox, Pane, StackPane, VBox}
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import scalafx.stage.Screen

import java.io.{FileInputStream, InputStream, ObjectInputStream}
import scala.util.Random
import scala.math.min


object FortressFuryGUI extends JFXApp3:

  def start() =
    val selectedScene = ObjectProperty(Scenes.LobbyScene)

    val mainStage = new JFXApp3.PrimaryStage:
      title = "Fortress Fury"
      width = 1650
      height = 750
      minWidth = 400
      minHeight = 300
      icons += Image("image/icon.jpg")


    selectedScene.onChange((_, _, newValue) =>
      newValue match
        case Scenes.NewGameScene =>
          stage.setScene(GameScene(mainStage, selectedScene, new Game(logic.Map1)))

        case Scenes.ContinueGameScene =>
          stage.setScene(GameScene(mainStage, selectedScene, loadGame()))

        case Scenes.LobbyScene =>
          stage.setScene(LobbyScene(mainStage, selectedScene))

        case Scenes.LeaderBoardScene =>
          stage.setScene(LeaderBoardScene(mainStage, selectedScene))

        case Scenes.GameOverScene =>
          stage.setScene(GameOverScene(mainStage, selectedScene))
    )

    mainStage.scene = LobbyScene(mainStage, selectedScene)//GameScene(mainStage, selectedScene, new Game(logic.Map1))
    stage = mainStage

    def loadGame(): Game =
      val fileInputStream = new FileInputStream("savedGame.ser")
      val objectInputStream = new ObjectInputStream(fileInputStream)
      try {
        objectInputStream.readObject().asInstanceOf[Game]
      } finally {
        fileInputStream.close()
        objectInputStream.close()
      }

  end start

end FortressFuryGUI


