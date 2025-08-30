package saw.ermezinde.game.domain.board

case class Board(
                id: String,
                resolveOrderNumber: Int,
                boardPower: BoardPower,
                slots: Map[SlotPosition, Slot]
                ) {
  def hasMiddleSlot: Boolean = slots.keys.toList.contains(SlotPosition.MIDDLE)
}

