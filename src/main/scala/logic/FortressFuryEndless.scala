package logic

import scala.math.max
import java.util.{Timer, TimerTask}

object FortressFuryEndless extends App:
  val game = EndlessGame(Map1)
  var wave = 0
  
  def unit(amount: Int, unitType: Int): Vector[EnemySoldier] =
    Vector.tabulate(amount){i =>
      unitType match
        case 1 => Infantry(game)
        case 2 => Cavalry(game)
        case 3 => ArmoredCar(game)
        case 4 => Tank(game)
    }
  def deployWave(wave: Int) =
    def a = wave / 5
    def b = wave / 4
    def c = wave / 3 + 5
    def d = wave / 2 + 10
    val troops = unit(a, 4) ++ unit(b, 3) ++ unit(c, 2) ++ unit(d, 1)
    val timer = new Timer()
    var index = 0
    val task = new TimerTask:
      def run() =
        if index < troops.length then
          game.deploy(troops(index))
          index += 1
        else
          timer.cancel()
    timer.schedule(task, 0, 1000)
  
  

