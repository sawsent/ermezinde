package saw.ermezinde.game.domain.result

trait Result[T] {
  val value: Option[T]
  def hide: Result[T]
  def reveal: Result[T]

  val isRevealed: Boolean
  val isHidden: Boolean

  def getOrElse(orElse: T): T
}

case class Hidden[T](i: T) extends Result[T] {
  override val value: Option[T] = None
  override def hide: Result[T] = this
  override def reveal: Result[T] = Shown[T](i)

  override val isRevealed: Boolean = false
  override val isHidden: Boolean = true

  override def getOrElse(orElse: T): T = orElse
}
case class Shown[T](i: T) extends Result[T] {
  override val value: Option[T] = Some(i)
  override def hide: Result[T] = Hidden(i)
  override def reveal: Result[T] = this

  override val isRevealed: Boolean = true
  override val isHidden: Boolean = false

  override def getOrElse(orElse: T): T = i
}
