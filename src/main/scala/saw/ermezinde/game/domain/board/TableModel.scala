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
}
case class PlacePhaseTableModel()
case class ResolvePhaseTableModel()
