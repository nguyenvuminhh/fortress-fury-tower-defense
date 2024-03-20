package logic

import java.io

trait Square extends io.Serializable:
  def isEmpty: Boolean
  def isPlacable: Boolean
  def tower: Option[Tower]
  def addTower(tower: Tower): Unit
  def clear(): Unit
end Square

class Path extends Square:
  val isEmpty = false
  val isPlacable = false
  val tower = None
  def addTower(tower: Tower) = ()
  def removeTower(tower: Tower) = ()
  def clear() = ()


end Path

class Buildable extends Square:
  def isEmpty = occupant.isEmpty
  val isPlacable = true
  private var occupant: Option[Tower] = None
  def tower = occupant

  def addTower(tower: Tower) =
    if this.isEmpty then
      occupant = Some(tower)

  def clear() =
    occupant = None

end Buildable

