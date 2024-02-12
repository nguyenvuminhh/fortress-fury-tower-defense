package logic
import scala.math.{pow, sqrt}

class Entity(val x: Double, val y: Double):
  def distanceTo(another: Entity) = sqrt( pow((this.x - another.x), 2) + pow((this.y - another.y), 2))
end Entity
