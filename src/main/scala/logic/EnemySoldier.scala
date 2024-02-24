package logic

class EnemySoldier(game: Game, damage: Int):
  var HP = damage
  def minusHP(amount: Int) = HP -= amount
  val gold = damage/10
  val point = ??? //TODO: Implement
  var isDead = false

  def crash() = ???

case class Infantry(game: Game) extends EnemySoldier(game, 100):

end Infantry

case class Calvary(game: Game) extends EnemySoldier(game, 100):

end Calvary

case class ArmoredCar(game: Game) extends EnemySoldier(game, 100):

end ArmoredCar

case class Tank(game: Game) extends EnemySoldier(game, 100):

end Tank

