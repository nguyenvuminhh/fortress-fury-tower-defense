package logic


abstract class Ability(game: Game):
  val price = 750
  def use(): Unit
end Ability

class Rage(game: Game) extends Ability(game):
  def use() = ???
    
end Rage

class Freeze(game: Game) extends Ability(game):
  def use() = ???

end Freeze

class Poison(game: Game) extends Ability(game):
  def use() = ???

end Poison
