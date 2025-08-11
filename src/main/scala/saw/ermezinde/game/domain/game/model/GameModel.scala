package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

trait GameModel {
  val players: Map[PlayerModelId, PlayerModel]
  val config: GameConfig = GameConfig.default
}
