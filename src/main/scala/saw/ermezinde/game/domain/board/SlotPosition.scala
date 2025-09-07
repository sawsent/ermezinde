package saw.ermezinde.game.domain.board

sealed trait SlotPosition {
  def rotatedSlotPosition: SlotPosition
}
object SlotPosition {
  case object TOP_LEFT extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = TOP_RIGHT
  }
  case object TOP_RIGHT extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = BOTTOM_RIGHT
  }
  case object BOTTOM_LEFT extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = TOP_LEFT
  }
  case object BOTTOM_RIGHT extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = BOTTOM_LEFT
  }
  case object MIDDLE extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = MIDDLE
  }
}
