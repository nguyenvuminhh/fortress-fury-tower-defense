package app.scenes

import app.Scenes
import scalafx.application.JFXApp3
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.delegate.SFXDelegate
import scalafx.Includes.*
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.effect.BlendMode.Green
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, HBox, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.text.Text
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color.*

import scala.io.Source

class LeaderBoardScene (
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
) extends Scene:

  val dataPath = "src/main/resources/record.txt"
  val data = Source.fromFile(dataPath)
  var dataRow = Seq[Seq[String]]()
  try {
      for (line <- data.getLines) {
        val date = line.split("\t").head
        val score = line.split("\t")(1)
        val survivingTime = secondToHMS(line.split("\t")(2))
        val wavesSurvived = line.split("\t")(3)
        val enemiesKilled = line.split("\t")(4)
        dataRow = dataRow.appended(Seq(date, score, survivingTime, wavesSurvived, enemiesKilled))
      }
  } catch {
    case e: Exception => println(s"An error occurred: ${e.getMessage}")
  } finally {
    data.close()
  }
  def tableLength = math.min(dataRow.length+1, 10)
  val dataRowSorted = Seq(Seq("Date", "Score", "Surviving Time", "Waves Survived", "Enemies Killed")) ++ dataRow.sortBy(row => row(1).toLong).reverse

  val table = new VBox:
    children = Seq.tabulate(tableLength)(i =>
      val row = new HBox:
        children = Seq.tabulate(5)(j =>
          new StackPane:
            val bg = new Rectangle:
              width = 150
              height = 50
              fill = if i%2 == 0 then Color.web("#BF9000") else Color.web("#F4C55E")
            val text = new Text(dataRowSorted(i)(j))
            text.style = if i == 0 then s"-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 15;" else s"-fx-font-family: Gotham; -fx-font-size: 15;"
            children = Seq(bg, text);)
      row)
    alignmentInParent = scalafx.geometry.Pos.TopCenter

  val banner = new ImageView:
    image = Image("image/banner.png")
    fitWidth = 500
    preserveRatio = true

  val backButton = new ImageView:
    fitWidth = 100
    fitHeight = 100
    image = Image("image/backButton.png")
    onMouseClicked = (event) =>
      selectedScene.value = Scenes.LobbyScene
      print("ok")
  val resetButton = new ImageView:
    fitWidth = 100
    fitHeight = 100
    image = Image("image/backButton.png")
    onMouseClicked = (event) => selectedScene.value = Scenes.LobbyScene
  val center = new StackPane:
    children += table
    alignment = scalafx.geometry.Pos.TopCenter
    padding = Insets(0, 0, 0, 550) //TODO: ask about alignment
  val top = new BorderPane(banner, null, resetButton, null, backButton)
  top.padding = Insets(20, 20, 20, 20)

  val maincontainer = new BorderPane(center, top, null, null, null)

  root = maincontainer


  def secondToHMS(second: String) =
    val sec = second.toLong
    val hours = sec / 3600
    val minutes = (sec % 3600) / 60
    val seconds = sec % 60
    f"$hours%02d:$minutes%02d:$seconds%02d"
