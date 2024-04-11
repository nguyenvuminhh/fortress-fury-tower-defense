package app


import app.scenes.{GameScene, LeaderboardScene, LobbyScene, MapSelectionScene}
import logic.Game
import scalafx.application.JFXApp3
import scalafx.beans.property.ObjectProperty
import scalafx.geometry.Insets
import scalafx.geometry.Pos.BaselineCenter
import scalafx.geometry.VPos.Baseline
import scalafx.scene.Scene
import scalafx.scene.control.Label
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.*
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.{Circle, Rectangle}
import scalafx.scene.text.{Font, FontWeight, Text, TextFlow}
import scalafx.stage.Screen

import java.io.{FileInputStream, InputStream, ObjectInputStream}
import scala.math.min
import scala.util.Random


object FortressFuryApp extends JFXApp3:

  def start() =
    val selectedScene = ObjectProperty(Scenes.LobbyScene)

    val mainStage = new JFXApp3.PrimaryStage:
      title = "Fortress Fury"
      width = 1400
      height = 750
      resizable = true
      icons += Image("image/icon.jpg")


    selectedScene.onChange((_, _, newValue) =>
      newValue match
        case Scenes.NewGameScene1 =>
          stage.setScene(GameScene(mainStage, selectedScene, new Game(logic.Map1), false))

        case Scenes.NewGameScene2 =>
          stage.setScene(GameScene(mainStage, selectedScene, new Game(logic.Map2), false))

        case Scenes.ContinueGameScene1 =>
          stage.setScene(GameScene(mainStage, selectedScene, new Game(logic.Map1), true))

        case Scenes.ContinueGameScene2 =>
          stage.setScene(GameScene(mainStage, selectedScene, new Game(logic.Map2), true))

        case Scenes.MapSelectionScene =>
          stage.setScene(MapSelectionScene(mainStage, selectedScene))

        case Scenes.LobbyScene =>
          stage.setScene(LobbyScene(mainStage, selectedScene))

        case Scenes.LeaderboardScene =>
          stage.setScene(LeaderboardScene(mainStage, selectedScene))
    )

    mainStage.scene = LobbyScene(mainStage, selectedScene) //GameScene(mainStage, selectedScene, new Game(logic.Map1), true) //
    stage = mainStage

  end start

end FortressFuryApp


