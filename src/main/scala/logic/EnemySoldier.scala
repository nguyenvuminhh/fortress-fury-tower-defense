package logic

class EnemySoldier(var x: Double, var y: Double, game: Game, damage: Int) extends Entity(x, y):
  var HP = damage
  val gold = damage/10
  val point = ??? //TODO: Implement
  var isDead = false

  def crash() =
    if this.distanceTo(game.headquarter) <= 1 then
      isDead = true
      game.headquarter.HP -= damage

case class Infantry(var x: Double, var y: Double, game: Game) extends EnemySoldier(x, y, game, 100):

end Infantry

case class Calvary(var x: Double, var y: Double, game: Game) extends EnemySoldier(x, y, game, 100):

end Calvary

case class ArmoredCar(var x: Double, var y: Double, game: Game) extends EnemySoldier(x, y, game, 100):

end ArmoredCar

case class Tank(var x: Double, var y: Double, game: Game) extends EnemySoldier(x, y, game, 100):

end Tank