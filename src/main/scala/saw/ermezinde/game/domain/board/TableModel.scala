package saw.ermezinde.game.domain.board

import saw.ermezinde.game.domain.GameConfig

trait TableModel

object PreparationPhaseTableModel {
  def init(config: GameConfig): PreparationPhaseTableModel = {
    PreparationPhaseTableModel()
  }
}
case class PreparationPhaseTableModel()
case class PlacePhaseTableModel()
case class ResolvePhaseTableModel()
