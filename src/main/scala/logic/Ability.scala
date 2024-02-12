package logic

import logic.grid.Ability

class Ability(game: Game):

end Ability

class Rage(game: Game) extends Ability(game):
  def use() =
    game.gold -= 750
    
end Rage

class Freeze(game: Game) extends Ability(game):
  
end Freeze

class Poison(game: Game) extends Ability(game):
  
end Poison
