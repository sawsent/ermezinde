package saw.ermezinde.game.domain.board

import saw.ermezinde.game.domain.slot.SlotPosition

object BoardPosition {
  case object TL extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(TM, BL)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.TL, SlotPosition.TR, SlotPosition.BL)
  }
  case object TM extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(TL, TR, BM)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.TL, SlotPosition.TR)
  }
  case object TR extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(TM, BR)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.TL, SlotPosition.TL, SlotPosition.BR)
  }
  case object BL extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(TL, BM)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.BR, SlotPosition.BL, SlotPosition.TR)
  }
  case object BM extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(BL, BR, TM)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.BL, SlotPosition.BR)
  }
  case object BR extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(BM, TR)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.BL, SlotPosition.BR, SlotPosition.TR)
  }

  def all: Set[BoardPosition] = Set(TL, TM, TR, BL, BM, BR)
}
sealed trait BoardPosition {
  val adjacents: Set[BoardPosition]
  val outsideSlotPositions: Set[SlotPosition]
}

