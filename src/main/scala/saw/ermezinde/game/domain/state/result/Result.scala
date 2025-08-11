package saw.ermezinde.game.domain.state.result

trait Result {
  val value: Option[Int]
  def hide: Result
  def show: Result
}

case class Hidden(i: Int) extends Result {
  override val value: Option[Int] = None
  override def hide: Result = this
  override def show: Result = Shown(i)
}
case class Shown(i: Int) extends Result {
  override val value: Option[Int] = Some(i)
  override def hide: Result = Hidden(i)
  override def show: Result = this
}
