package saw.ermezinde.game.domain.game.state.inplay

import saw.ermezinde.game.domain.game.model.DiscardPhaseGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

case class DiscardPhaseGameState(
                                  id: String,
                                  ownerId: String,
                                  gameStartTime: Option[Timestamp],
                                  players: Map[PlayerId, PlayerModelId],
                                  game: DiscardPhaseGameModel
                                ) extends InPlayGameState
