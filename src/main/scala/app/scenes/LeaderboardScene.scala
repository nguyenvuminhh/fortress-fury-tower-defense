package app.scenes

import app.Scenes
import scalafx.Includes.*
import scalafx.application.JFXApp3
import scalafx.beans.property.{ObjectProperty, StringProperty}
import scalafx.collections.ObservableBuffer
import scalafx.delegate.SFXDelegate
import scalafx.geometry.Pos.TopCenter
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.scene.effect.BlendMode.Green
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{BorderPane, ColumnConstraints, GridPane, HBox, RowConstraints, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.geometry.Pos.*

import scala.io.Source

class LeaderboardScene(
    mainStage: JFXApp3.PrimaryStage,
    selectedScene: ObjectProperty[Scenes],
) extends Scene:

  val dataPath = "src/main/resources/record.txt"
  val data = Source.fromFile(dataPath)
  var dataRow = Seq[Seq[String]]()
  try {
      for (line <- data.getLines) {
        val date = line.split("\t").head
        val map = line.split("\t")(1)
        val score = line.split("\t")(2)
        val survivingTime = secondToHMS(line.split("\t")(3))
        val wavesSurvived = line.split("\t")(4)
        val enemiesKilled = line.split("\t")(5)
        dataRow = dataRow.appended(Seq(date, map, score, survivingTime, wavesSurvived, enemiesKilled))
      }
  } catch {
    case e: Exception => println(s"An error occurred: ${e.getMessage}")
  } finally {
    data.close()
  }
  def tableLength = math.min(dataRow.length+1, 10)
  val dataRowSorted = Seq(Seq("Date", "Map",  "Score", "Surviving Time", "Waves Survived", "Enemies Killed")) ++ dataRow.sortBy(row => row(1).toLong).reverse

  val table = new VBox:
    children = Seq.tabulate(tableLength)(i =>
      val row = new HBox:
        alignment = Center
        children = Seq.tabulate(6)(j =>
          new StackPane:
            val bg = new Rectangle:
              width = 150
              height = 50
              fill = if i%2 == 0 then Color.web("#BF9000") else Color.web("#F4C55E")
            val text = new Text(dataRowSorted(i)(j))
            text.style = if i == 0 then s"-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: 15;" else s"-fx-font-family: Gotham; -fx-font-size: 15;"
            children = Seq(bg, text);)
      row)
    alignment = Center
    translateY = 0

  val banner = new ImageView:
    image = Image("image/leaderboardBanner.png")
    fitWidth = 500
    preserveRatio = true
    alignmentInParent = TopCenter

  val backButton = new ImageView:
    fitWidth = 100
    fitHeight = 100
    image = Image("image/backButton.png")
    margin = Insets(20)
    alignmentInParent = TopLeft
    onMouseClicked = () => selectedScene.value = Scenes.LobbyScene

  val maincontainer = GridPane()
  root = maincontainer

  val column0 = new ColumnConstraints:
    percentWidth = 25
  val column1 = new ColumnConstraints:
    percentWidth = 50
  val column2 = new ColumnConstraints:
    percentWidth = 25
  val row0 = new RowConstraints:
    percentHeight = 15
  val row1 = new RowConstraints:
    percentHeight = 85

  maincontainer.columnConstraints = Array(column0, column1, column2)
  maincontainer.rowConstraints = Array(row0, row1)

  maincontainer.add(backButton, 0, 0, 1, 1)
  maincontainer.add(banner, 1, 0, 1, 1)
  maincontainer.add(table, 0, 1, 3, 1)

  def secondToHMS(second: String) =
    val sec = second.toLong
    val hours = sec / 3600
    val minutes = (sec % 3600) / 60
    val seconds = sec % 60
    f"$hours%02d:$minutes%02d:$seconds%02d"
