package saw.ermezinde.game.domain.board

sealed trait BoardRotation {
  val degrees: Int
}

object BoardRotation {
  def fromDegrees(degrees: Int): BoardRotation = degrees match {
    case _0.degrees   => _0
    case _90.degrees  => _90
    case _180.degrees => _180
    case _270.degrees => _270
  }
}

case object _0 extends BoardRotation {
  override val degrees: Int = 0
}

case object _90 extends BoardRotation {
  override val degrees: Int = 90
}

case object _180 extends BoardRotation {
  override val degrees: Int = 180
}

case object _270 extends BoardRotation {
  override val degrees: Int = 270
}

