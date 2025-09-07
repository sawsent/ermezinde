package saw.ermezinde.game.domain.board

import saw.ermezinde.game.domain.slot.{PFNormalSlot, PFSlot, SlotPosition}

case class PFBoard(
                id: String,
                resolveOrderNumber: Int,
                power: Option[PlacePhaseBoardPower],
                slots: Map[SlotPosition, PFSlot]
                ) {
  def hasMiddleSlot: Boolean = slots.keys.toList.contains(SlotPosition.MIDDLE)

  def rotate: PFBoard = copy(
    slots = slots.map { case (slotPosition, slot) =>
      slotPosition.rotatedSlotPosition -> slot
    }
  )

  def nonPrizeSlots: List[PFNormalSlot] = slots.values.filter(_.isInstanceOf[PFNormalSlot]).map(_.asInstanceOf[PFNormalSlot]).toList

  def rotate(boardRotation: BoardRotation): PFBoard = (0 until boardRotation.degrees/90).toList.foldLeft(this)((board, _) => board.rotate)
}

