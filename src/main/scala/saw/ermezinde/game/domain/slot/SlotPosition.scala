package saw.ermezinde.game.domain.slot

sealed trait SlotPosition {
  def rotatedSlotPosition: SlotPosition
}
object SlotPosition {
  case object TL extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = TR
  }
  case object TR extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = BR
  }
  case object BL extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = TL
  }
  case object BR extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = BL
  }
  case object MIDDLE extends SlotPosition {
    override def rotatedSlotPosition: SlotPosition = MIDDLE
  }

  val all: List[SlotPosition] = List(TL, TR, BL, BR, MIDDLE)
}
