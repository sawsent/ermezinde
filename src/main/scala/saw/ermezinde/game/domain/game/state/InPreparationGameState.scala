package saw.ermezinde.game.domain.game.state

import saw.ermezinde.game.domain.game.model.InPreparationGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.game.state.inplay.InPlayGameState
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

object InPreparationGameState {
  def init(state: NotStartedGameState, startTime: Timestamp): InPreparationGameState = {
    InPreparationGameState(
      id = state.id,
      ownerId = state.ownerId,
      gameStartTime = Some(startTime),
      players = state.players,
      game = InPreparationGameModel.init(state.game, state.players.values.toList),
      playersReady = Set.empty
    )
  }
}
case class InPreparationGameState(
                                   id: String,
                                   ownerId: String,
                                   gameStartTime: Option[Timestamp],
                                   players: Map[PlayerId, PlayerModelId],
                                   playersReady: Set[PlayerId],
                                   game: InPreparationGameModel
                                 ) extends GameState {
  val currentPlayer: PlayerId = players.find { case (_, playerModelId) => game.currentPlayerId == playerModelId}.map(_._1).get
  def moveToInPlayIfReady: GameState = if (playersReady == players.keys.toSet) InPlayGameState.init(this) else this
}
