package logic

object Helper:
  val squareside = 45
  def gothamBold(size: Int) = s"-fx-font-family: Gotham; -fx-font-weight: bold; -fx-font-size: ${size}px;"
  def gothamNormal(size: Int) = s"-fx-font-family: Gotham; -fx-font-size: ${size}px;"
  def secondToHMS(sec: Long) =
    val hours = sec / 3600
    val minutes = (sec % 3600) / 60
    val seconds = sec % 60
    f"$hours%02d:$minutes%02d:$seconds%02d"
