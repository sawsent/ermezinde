package saw.ermezinde.game.domain.board

import saw.ermezinde.game.domain.slot.SlotPosition

object BoardPosition {
  case object TL extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(TM, BL)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.TL, SlotPosition.TR, SlotPosition.BL)

    override def adjacentSlotPositions(slotPosition: SlotPosition): Set[(BoardPosition, SlotPosition)] = slotPosition match {
      case SlotPosition.TL => Set(TL -> TR, TL -> BL)
      case SlotPosition.TR => Set(TL -> TL, TL -> BR, TM -> TL)
      case SlotPosition.BL => Set(TL -> TL, TL -> BR, BL -> TL)
      case SlotPosition.BR => Set(TL -> BL, TL -> TR, TM -> BL, BL -> TR)
      case SlotPosition.MIDDLE => Set.empty
    }
  }
  case object TM extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(TL, TR, BM)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.TL, SlotPosition.TR)

    override def adjacentSlotPositions(slotPosition: SlotPosition): Set[(BoardPosition, SlotPosition)] = slotPosition match {
      case SlotPosition.TL => Set(TM -> SlotPosition.BL, TM -> SlotPosition.TR, TL -> SlotPosition.TR)
      case SlotPosition.TR => Set(TM -> SlotPosition.TL, TM -> SlotPosition.BR, TR -> SlotPosition.TL)
      case SlotPosition.BL => Set(TM -> SlotPosition.TL, TM -> SlotPosition.BR, TL -> SlotPosition.BR, BM -> SlotPosition.TL)
      case SlotPosition.BR => Set(TM -> SlotPosition.BL, TM -> SlotPosition.TR, TR -> SlotPosition.BL, BM -> SlotPosition.TR)
      case SlotPosition.MIDDLE => Set.empty
    }
  }
  case object TR extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(TM, BR)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.TL, SlotPosition.TL, SlotPosition.BR)

    override def adjacentSlotPositions(slotPosition: SlotPosition): Set[(BoardPosition, SlotPosition)] = slotPosition match {
      case SlotPosition.TL => Set(TR -> SlotPosition.TR, TR -> SlotPosition.BL, TM -> SlotPosition.TR)
      case SlotPosition.TR => Set(TR -> SlotPosition.TL, TR -> SlotPosition.BR)
      case SlotPosition.BL => Set(TR -> SlotPosition.TL, TR -> SlotPosition.BR, BR -> SlotPosition.TL, TM -> SlotPosition.BR)
      case SlotPosition.BR => Set(TR -> SlotPosition.BL, TR -> SlotPosition.TR, BR -> SlotPosition.TR)
      case SlotPosition.MIDDLE => Set.empty
    }
  }
  case object BL extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(TL, BM)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.BR, SlotPosition.BL, SlotPosition.TR)

    override def adjacentSlotPositions(slotPosition: SlotPosition): Set[(BoardPosition, SlotPosition)] = slotPosition match {
      case SlotPosition.TL => Set(BL -> SlotPosition.TR, BL -> SlotPosition.BL, TL -> SlotPosition.BL)
      case SlotPosition.TR => Set(BL -> SlotPosition.TL, BL -> SlotPosition.BR, TL -> SlotPosition.BR, BM -> SlotPosition.TL)
      case SlotPosition.BL => Set(BL -> SlotPosition.TL, BL -> SlotPosition.BR)
      case SlotPosition.BR => Set(BL -> SlotPosition.BL, BL -> SlotPosition.TR, BM -> SlotPosition.BL)
      case SlotPosition.MIDDLE => Set.empty
    }
  }
  case object BM extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(BL, BR, TM)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.BL, SlotPosition.BR)

    override def adjacentSlotPositions(slotPosition: SlotPosition): Set[(BoardPosition, SlotPosition)] = slotPosition match {
      case SlotPosition.TL => Set(BM -> SlotPosition.TR, BM -> SlotPosition.BL, BL -> SlotPosition.TR, TM -> SlotPosition.BL)
      case SlotPosition.TR => Set(BM -> SlotPosition.TL, BM -> SlotPosition.BR, TM -> SlotPosition.BR, BR -> SlotPosition.TL)
      case SlotPosition.BL => Set(BM -> SlotPosition.TL, BM -> SlotPosition.BR, BL -> SlotPosition.BR)
      case SlotPosition.BR => Set(BM -> SlotPosition.BL, BM -> SlotPosition.TR, BR -> SlotPosition.BL)
      case SlotPosition.MIDDLE => Set.empty
    }
  }
  case object BR extends BoardPosition {
    override val adjacents: Set[BoardPosition] = Set(BM, TR)
    override val outsideSlotPositions: Set[SlotPosition] = Set(SlotPosition.BL, SlotPosition.BR, SlotPosition.TR)
    override def adjacentSlotPositions(slotPosition: SlotPosition): Set[(BoardPosition, SlotPosition)] = slotPosition match {
      case SlotPosition.TL => Set(BR -> SlotPosition.BL, BR -> SlotPosition.TR, BM -> SlotPosition.TR, TR -> SlotPosition.BL)
      case SlotPosition.TR => Set(BR -> SlotPosition.TL, BR -> SlotPosition.BR, TR -> SlotPosition.BR)
      case SlotPosition.BL => Set(BR -> SlotPosition.TL, BR -> SlotPosition.BR, BM -> SlotPosition.BR)
      case SlotPosition.BR => Set(BR -> SlotPosition.BL, BR -> SlotPosition.TR)
      case SlotPosition.MIDDLE => Set.empty
    }
  }

  def all: Set[BoardPosition] = Set(TL, TM, TR, BL, BM, BR)
}
sealed trait BoardPosition {
  val adjacents: Set[BoardPosition]
  val outsideSlotPositions: Set[SlotPosition]
  def adjacentSlotPositions(slotPosition: SlotPosition): Set[(BoardPosition, SlotPosition)]
}

