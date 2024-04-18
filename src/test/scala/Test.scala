import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.mutable.*
import app.*
import logic.grid.GridPos
import logic.{Cannon, Cavalry, Game, Infantry, Map1, Turret, Headquarter}


class Test extends AnyFlatSpec with Matchers:
  val game = Game(new logic.Map1())

  //TODO: please comment line 153 and 156 of Tower.scala, 148 of Game.scala before testing

  /** giveGold() */
  "Gold" should "increased by 10 after giveGold() was called once" in {
    val temp = game.getGold
    game.giveGold()
    game.getGold shouldEqual temp + 10
  }

  it should "increased by 100 after giveGold() was called 10 times" in {
    val temp = game.getGold
    for _ <- 0 until 10 do
      game.giveGold()
    game.getGold shouldEqual temp + 100
  }
  /** deploy(enemy) */
  "The game" should "have an infantry soldier in starting square after deploying one" in {
    game.deploy(Infantry(game))
    game.enemies.contains(Infantry(game)) shouldEqual true
  }

  it should "have a cavalry soldier in starting square after deploying one" in {
    game.deploy(Cavalry(game))
    game.enemies.contains(Cavalry(game)) shouldEqual true
  }

  /** advance() */

  "The enemy" should "move 1 block to the east after advance 5 times" in {
    val enemy = game.enemies.head
    val tempX = enemy.getX
    val tempY = enemy.getY
    for _ <- 0 until 5 do enemy.advance()
    (enemy.getX - tempX) == 1 && (enemy.getY - tempY) == 0 shouldEqual true
  }

  it should "move 3 block to the east after advance 15 times" in {
    val enemy = game.enemies.head
    val tempX = enemy.getX
    val tempY = enemy.getY
    for _ <- 0 until 15 do enemy.advance()
    (enemy.getX - tempX) == 3 && (enemy.getY - tempY) == 0 shouldEqual true
  }

  it should "turn right and move 1 block (to the south) after advance at a turning square" in {
    val enemy = game.enemies.head
    val tempX = enemy.getX
    val tempY = enemy.getY
    for _ <- 0 until 5 do enemy.advance()
    (enemy.getX - tempX) == 0 && (enemy.getY - tempY) == 1 && enemy.getHeading == logic.grid.CompassDir.South shouldEqual true
  }

  it should "crash when advance at crash square" in {
    val enemy = game.enemies.head
    for _ <- 0 until 55*5 do enemy.advance()
    enemy.advance()
    enemy.isDead shouldEqual true
    game.headquarter.HPpercentage < 1 shouldEqual true
  }
  /** place(gun) */

  "The game" should "have a Cannon at (6, 4) after placing one" in {
    game.place(("Cannon", 150), 6, 4)
    game.map.elementAt(GridPos(6, 4)).tower.get shouldEqual Cannon(6, 4, game)
  }

  it should "not have a Turret at (6, 4) after placing one" in {
    game.place(("Turret", 200), 6, 4)
    game.map.elementAt(GridPos(6, 4)).tower.get == Turret(6, 4, game) shouldEqual false
  }

  it should "not have a Turret at (1, 4) after placing one" in {
    game.place(("Turret", 200), 1, 4)
    game.map.elementAt(GridPos(1, 4)).tower.isEmpty shouldEqual true
  }

  it should "not have a Cannon at (6, 4) after removing one" in {
    game.remove(GridPos(6, 4))
    game.map.elementAt(GridPos(6, 4)).tower.isEmpty shouldEqual true
  }

  it should "not remove the headquarter" in {
    game.remove(GridPos(27, 8))
    game.map.elementAt(GridPos(27, 8)).tower.get shouldEqual Headquarter(27, 8)
  }

  /** shoot() */
  "A gun" should "not shoot an out of range enemy" in {
    game.place(("Cannon", 150), 1, 7)
    game.enemies.clear()
    game.deploy(Infantry(game))
    game.gunTowers.head.shoot()
    game.enemies.head.HPpercentage shouldEqual 1
  }

  it should "shoot an in range enemy" in {
    for _ <- 0 until 5 do game.enemies.head.advance()
    game.gunTowers.head.shoot()
    game.enemies.head.HPpercentage < 1 shouldEqual true
  }