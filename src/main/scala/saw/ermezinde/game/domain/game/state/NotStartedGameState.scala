package saw.ermezinde.game.domain.game.state

import saw.ermezinde.game.domain.game.model.NotStartedGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.game.state.NotStartedGameState.NotStartedPlayerModel
import saw.ermezinde.game.domain.player.Color
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

object NotStartedGameState {
  case class NotStartedPlayerModel(color: Color, ready: Boolean)
}
case class NotStartedGameState(
                                id: String,
                                ownerId: String,
                                waitingPlayers: Map[PlayerId, NotStartedPlayerModel],
                                game: NotStartedGameModel
                              ) extends GameState {
  override val players: Map[PlayerId, PlayerModelId] = waitingPlayers.map { case (playerId, playerModel) => playerId -> playerModel.color}
  override val gameStartTime: Option[Timestamp] = None
}
