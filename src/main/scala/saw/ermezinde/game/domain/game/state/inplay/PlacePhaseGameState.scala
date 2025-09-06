package saw.ermezinde.game.domain.game.state.inplay

import saw.ermezinde.game.domain.game.model.PlacePhaseGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

object PlacePhaseGameState {
  def init(state: BoardSelectionGameState, playerOrdering: List[PlayerId]): PlacePhaseGameState =
    PlacePhaseGameState(
      state.id,
      state.ownerId,
      state.gameStartTime,
      state.players,
      PlacePhaseGameModel.init(state.game, playerOrdering.map(p => state.players(p)))
    )
}
case class PlacePhaseGameState(
                                id: String,
                                ownerId: String,
                                gameStartTime: Option[Timestamp],
                                players: Map[PlayerId, PlayerModelId],
                                game: PlacePhaseGameModel
                              ) extends InPlayGameState
