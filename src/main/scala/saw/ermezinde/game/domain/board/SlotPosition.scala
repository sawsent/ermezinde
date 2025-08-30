package saw.ermezinde.game.domain.board

sealed trait SlotPosition
object SlotPosition {
  case object TOP_LEFT extends SlotPosition
  case object TOP_RIGHT extends SlotPosition
  case object BOTTOM_LEFT extends SlotPosition
  case object BOTTOM_RIGHT extends SlotPosition
  case object MIDDLE extends SlotPosition
}

trait BoardPower
