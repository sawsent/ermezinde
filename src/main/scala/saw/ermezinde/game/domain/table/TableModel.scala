package saw.ermezinde.game.domain.table

import saw.ermezinde.game.domain.board.BoardPosition._
import saw.ermezinde.game.domain.board.{BoardInfo, BoardPosition, BoardRotation, PFBoard}
import saw.ermezinde.game.domain.card.Card
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.slot.{PFNormalSlot, PFSlot, SlotPosition, VisionLevel}

object PreparationPhaseTableModel {
  def init: PreparationPhaseTableModel = {
    PreparationPhaseTableModel(
      BoardPosition.all.map(_ -> None).toMap
    )
  }
}
case class PreparationPhaseTableModel(
                                     boards: Map[BoardPosition, Option[(BoardInfo, BoardRotation)]]
                                     ) {
  def positionAvailable(bp: BoardPosition): Boolean = boards(bp).isEmpty
  def boardAt(bp: BoardPosition): Option[BoardInfo] = boards(bp).map(_._1)

  def placeBoard(board: BoardInfo, boardPosition: BoardPosition, boardRotation: BoardRotation): PreparationPhaseTableModel = copy(
    boards = boards ++ Map(boardPosition -> Some(board -> boardRotation))
  )
  def isFull: Boolean = BoardPosition.all.forall(boards(_).nonEmpty)

  override def toString: String =
    s"PreparationPhaseTableModel[ " +
      s"${boards.map { case (boardPosition, board) => s"$boardPosition -> ${board.map(b => s"Board(${b._1.id}, ${b._2})").getOrElse("None")}"}} ]"
}

object PlacePhaseTableModel {
  def init(table: PreparationPhaseTableModel, enigmaPlacement: BoardPosition, prizeCards: List[Card]): PlacePhaseTableModel = {
    val boards: List[(BoardPosition, PFBoard)] = table.boards.map{ case (pos, Some((bi, br))) => (
      pos,
      PFBoard(
        id = bi.id,
        resolveOrderNumber = bi.resolveOrderNumber.get,
        power = bi.placePhaseBoardPower,
        slots = bi.slots.map(si => si.position -> PFSlot.fromInfo(si)).toMap
      ).rotate(br),
    )}.toList

    val withPrize = prizeCards.foldLeft(boards)((boards, prizeCard) => {
      val updatedBoard = boards
        .find { case (_, board) => board.prizeSlot._2.cards.length < board.prizeSlot._2.slotInfo.prizeAmount }
        .map { case (pos, board) =>
          val prizeSlot = board.prizeSlot
          val withCard = prizeSlot._2.place(prizeCard)
          pos -> board.copy(
              slots = board.slots + (prizeSlot._1 -> withCard)
            )
        }
        .get

      (boards.toMap + updatedBoard).toList
    })



    PlacePhaseTableModel(
      boards = withPrize.toMap,
      enigmaPosition = enigmaPlacement
    )
  }
}
case class PlacePhaseTableModel(
                                 boards: Map[BoardPosition, PFBoard],
                                 enigmaPosition: BoardPosition
                               ) {

  val boardIdToBoard: Map[String, PFBoard] = boards.map { case (_, b) => b.id -> b }
  val boardIdToBoardPosition: Map[String, BoardPosition] = boards.map { case (bp, b) => b.id -> bp }
  private val flatSlotViewByPosition: Map[(BoardPosition, SlotPosition), PFSlot] = boards.toList.flatMap { case (bp, b) => b.slots.map { case (sp, s) => (bp, sp) -> s}}.toMap
  private val flatSlotViewByBoardId: Map[SlotPositionDTO, PFSlot] = boards.toList.flatMap { case (_, b) => b.slots.map { case (sp, s) => SlotPositionDTO(b.id, sp) -> s}}.toMap

  private val outsideSlots: Set[(BoardPosition, SlotPosition)] =
    flatSlotViewByPosition
      .filter(!_._2.isPrize)
      .map{ case (k, s) => k -> s.asInstanceOf[PFNormalSlot]}
      .filter{ case ((bp, sp), s) => bp.outsideSlotPositions.contains(sp) || s.slotInfo.alwaysOutside }
      .keySet

  def placeCard(playerModelId: PlayerModelId, card: Card, slotPos: SlotPositionDTO): PlacePhaseTableModel = {
    val board = boardIdToBoard(slotPos.boardId)
    val boardPosition = boardIdToBoardPosition(slotPos.boardId)

    val slot = board.slots(slotPos.slotPosition)

    val updatedSlot = slot.place(card).setPlacedBy(playerModelId)

    val updatedBoard = board.copy(
      slots = board.slots + (slotPos.slotPosition -> updatedSlot)
    )

    copy(
      boards = boards + (boardPosition -> updatedBoard)
    )
  }

  def slotExists(s: SlotPositionDTO): Boolean = flatSlotViewByBoardId.contains(s)
  def slotIsPlaceable(s: SlotPositionDTO): Boolean = !flatSlotViewByBoardId.get(s).filterNot(_.isPrize).forall(_.asInstanceOf[PFNormalSlot].card.isEmpty)
  def slotIsPlaceable(s: SlotPositionDTO, playerModelId: PlayerModelId, currentRound: Int): Boolean = currentRound match {
    case 1 => slotIsPlaceable(s)
    case _ =>
      flatSlotViewByPosition
        .filterNot{case (_, slot) => slot.isPrize}
        .map { case (pos, slot) => pos -> slot.asInstanceOf[PFNormalSlot] }
        .filter { case (_, slot) => slot.placedBy.contains(playerModelId) }
        .map { case ((bp, sp), _) => bp.adjacentSlotPositions(sp) }
        .flatten
        .toSet
        .concat(outsideSlots)
        .filter(!flatSlotViewByPosition(_).isPrize)
        .filter(flatSlotViewByPosition(_).asInstanceOf[PFNormalSlot].card.isEmpty)
        .contains(boardIdToBoardPosition(s.boardId) -> s.slotPosition)

  }
  def slotCanSeeCards(from: SlotPositionDTO, to: List[SlotPositionDTO]): Boolean = {
    val fromSlot = flatSlotViewByBoardId(from).asInstanceOf[PFNormalSlot]
    fromSlot.slotInfo.visionLevel match {
      case VisionLevel.NOTHING => to.isEmpty
      case VisionLevel.SAME_BOARD => to.length <= 1 && to.head.boardId == from.boardId
      case VisionLevel.ADJACENT_BOARDS => to.length <= 1 && boardIdToBoardPosition(from.boardId).adjacents.contains(boardIdToBoardPosition(to.head.boardId))
      case VisionLevel.ALL_BOARDS => to.length <= 1
      case VisionLevel.SAME_BOARD_TWICE => 0 < to.length && to.length <= 2 && to.forall(_.boardId == from.boardId)
    }
  }

}

object ResolvePhaseTableModel {
  def init(table: PlacePhaseTableModel): ResolvePhaseTableModel = ResolvePhaseTableModel()
}
case class ResolvePhaseTableModel()
