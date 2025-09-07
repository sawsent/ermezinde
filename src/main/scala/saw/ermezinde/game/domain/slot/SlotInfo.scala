package saw.ermezinde.game.domain.slot

sealed trait SlotInfo {
  val position: SlotPosition
  val isPrize: Boolean
  val topSecret: Boolean
}
case class NormalSlotInfo(
                           position: SlotPosition,
                           topSecret: Boolean,
                           visionLevel: VisionLevel,
                           resolveOrderNumber: Int,
                           alwaysOutside: Boolean
                         ) extends SlotInfo {
  override val isPrize: Boolean = false
}

case class PrizeSlotInfo(
                          position: SlotPosition,
                          topSecret: Boolean,
                          prizeAmount: Int
                        ) extends SlotInfo {
  override val isPrize: Boolean = true
}

sealed trait VisionLevel
object VisionLevel {
  case object NOTHING extends VisionLevel
  case object SAME_BOARD extends VisionLevel
  case object ADJACENT_BOARDS extends VisionLevel
  case object ALL_BOARDS extends VisionLevel
  case object SAME_BOARD_TWICE extends VisionLevel

  val all: List[VisionLevel] = List(NOTHING, SAME_BOARD, SAME_BOARD_TWICE, ADJACENT_BOARDS, ALL_BOARDS)
}