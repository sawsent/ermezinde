package saw.ermezinde.game.domain.board

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
}

object PlacePhaseTableModel {
  def init(table: PreparationPhaseTableModel): PlacePhaseTableModel = PlacePhaseTableModel(

  )
}
case class PlacePhaseTableModel()
case class ResolvePhaseTableModel()
