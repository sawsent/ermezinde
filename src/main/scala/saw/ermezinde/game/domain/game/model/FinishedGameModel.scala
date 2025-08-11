package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.card.MissionCard
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

object FinishedGameModel {
  def init(model: InCountingGameModel): FinishedGameModel = FinishedGameModel(
    model.players,
    model.missionCards
  )
}
case class FinishedGameModel(
                              players: Map[PlayerModelId, PlayerModel],
                              missionCards: List[MissionCard]
                            ) extends GameModel