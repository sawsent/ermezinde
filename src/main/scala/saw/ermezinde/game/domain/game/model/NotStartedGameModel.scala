package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

case class NotStartedGameModel() extends GameModel {
  override val players: Map[PlayerModelId, PlayerModel] = Map.empty
}
