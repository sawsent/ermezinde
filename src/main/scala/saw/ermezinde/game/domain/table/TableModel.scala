package saw.ermezinde.game.domain.table

import saw.ermezinde.game.domain.board.BoardPosition._
import saw.ermezinde.game.domain.board.{PFBoard, BoardInfo, BoardPosition, BoardRotation}
import saw.ermezinde.game.domain.slot.{PFSlot, SlotPosition}

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
  def init(table: PreparationPhaseTableModel, enigmaPlacement: BoardPosition): PlacePhaseTableModel = {
    val boards: List[(BoardPosition, PFBoard, BoardRotation)] = table.boards.map{ case (pos, Some((bi, br))) => (
      pos,
      PFBoard(
        id = bi.id,
        resolveOrderNumber = bi.resolveOrderNumber.get,
        placePhaseBoardPower = bi.placePhaseBoardPower,
        resolvePhaseBoardPower = bi.resolvePhaseBoardPower,
        slots = bi.slots.map(si => si.position -> PFSlot.fromInfo(si)).toMap
      ),
      br
    )}.toList
    PlacePhaseTableModel(
      boards = boards.map { case (bp, board, boardRotation) => bp -> board.rotate(boardRotation)}.toMap,
      enigmaPosition = enigmaPlacement
    )
  }
}
case class PlacePhaseTableModel(
                                 boards: Map[BoardPosition, PFBoard],
                                 enigmaPosition: BoardPosition
                               ) {
  override def toString: String =
    s"""|PlacePhaseTableModel:
        | ///////////////////////////////////////////////////////////////////////////
        | ///----------/----------///----------/----------///----------/----------///
        | ///          /          ///          /          ///          /          ///
        | /// (${boards(TL).slots(SlotPosition.TL)}) / (${boards(TL).slots(SlotPosition.TR)}) /// (${boards(TM).slots(SlotPosition.TL)}) / (${boards(TM).slots(SlotPosition.TR)}) /// (${boards(TR).slots(SlotPosition.TL)}) / (${boards(TR).slots(SlotPosition.TR)}) ///
        | ///       /------/      ///      /------/       ///       /------/      ///
        | ///-------/${boards(TL).slots.getOrElse(SlotPosition.MIDDLE, " None ")}/------///------/${boards(TM).slots.getOrElse(SlotPosition.MIDDLE, " None ")}/-------///-------/${boards(TR).slots.getOrElse(SlotPosition.MIDDLE, " None ")}/------///
        | ///       /------/      ///      /------/       ///       /------/      ///
        | /// (${boards(TL).slots(SlotPosition.BL)}) / (${boards(TL).slots(SlotPosition.BR)}) /// (${boards(TM).slots(SlotPosition.BL)}) / (${boards(TM).slots(SlotPosition.BR)}) /// (${boards(TR).slots(SlotPosition.BL)}) / (${boards(TR).slots(SlotPosition.BR)}) ///
        | ///          /          ///          /          ///          /          ///
        | ///----------/----------///----------/----------///----------/----------///
        | ///////////////////////////////////////////////////////////////////////////
        | ///----------/----------///----------/----------///----------/----------///
        | ///          /          ///          /          ///          /          ///
        | /// (${boards(BL).slots(SlotPosition.TL)}) / (${boards(BL).slots(SlotPosition.TR)}) /// (${boards(BM).slots(SlotPosition.TL)}) / (${boards(BM).slots(SlotPosition.TR)}) /// (${boards(BR).slots(SlotPosition.TL)}) / (${boards(BR).slots(SlotPosition.TR)}) ///
        | ///       /------/      ///      /------/       ///       /------/      ///
        | ///-------/${boards(BL).slots.getOrElse(SlotPosition.MIDDLE, " None ")}/------///------/${boards(BM).slots.getOrElse(SlotPosition.MIDDLE, " None ")}/-------///-------/${boards(BR).slots.getOrElse(SlotPosition.MIDDLE, " None ")}/------///
        | ///       /------/      ///      /------/       ///       /------/      ///
        | /// (${boards(BL).slots(SlotPosition.BL)}) / (${boards(BL).slots(SlotPosition.BR)}) /// (${boards(BM).slots(SlotPosition.BL)}) / (${boards(BM).slots(SlotPosition.BR)}) /// (${boards(BR).slots(SlotPosition.BL)}) / (${boards(BR).slots(SlotPosition.BR)}) ///
        | ///          /          ///          /          ///          /          ///
        | ///----------/----------///----------/----------///----------/----------///
        | ///////////////////////////////////////////////////////////////////////////
        |""".stripMargin
}
case class ResolvePhaseTableModel()
