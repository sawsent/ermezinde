package saw.ermezinde.game.domain.table

import saw.ermezinde.game.domain.board.{Board, BoardPosition, BoardRotation}

object PreparationPhaseTableModel {
  def init: PreparationPhaseTableModel = {
    PreparationPhaseTableModel(
      BoardPosition.all.map(_ -> None).toMap
    )
  }
}
case class PreparationPhaseTableModel(
                                     boards: Map[BoardPosition, Option[(Board, BoardRotation)]]
                                     ) {
  def positionAvailable(bp: BoardPosition): Boolean = boards(bp).isEmpty
  def boardAt(bp: BoardPosition): Option[Board] = boards(bp).map(_._1)

  def placeBoard(board: Board, boardPosition: BoardPosition, boardRotation: BoardRotation): PreparationPhaseTableModel = copy(
    boards = boards ++ Map(boardPosition -> Some(board -> boardRotation))
  )
  def isFull: Boolean = BoardPosition.all.forall(boards(_).nonEmpty)

  override def toString: String =
    s"PreparationPhaseTableModel[ " +
      s"${boards.map { case (boardPosition, board) => s"$boardPosition -> ${board.map(b => s"Board(${b._1.id}, ${b._2})").getOrElse("None")}"}} ]"
}

object PlacePhaseTableModel {
  def init(table: PreparationPhaseTableModel, enigmaPlacement: BoardPosition): PlacePhaseTableModel = PlacePhaseTableModel(

  )
}
case class PlacePhaseTableModel(

                               )
case class ResolvePhaseTableModel()
