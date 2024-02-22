package logic

import logic.grid.Ability

class Ability(game: Game):
  val price = 750
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
