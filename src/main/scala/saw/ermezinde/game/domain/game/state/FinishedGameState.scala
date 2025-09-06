package saw.ermezinde.game.domain.game.state

import saw.ermezinde.game.domain.game.model.FinishedGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.result.ResultTable


object FinishedGameState {
  def init(state: InCountingGameState): FinishedGameState = {
    val endTime = System.currentTimeMillis()
    FinishedGameState(
      id = state.id,
      ownerId = state.ownerId,
      gameStartTime = state.gameStartTime,
      gameEndTime = Some(endTime),
      players = state.players,
      results = state.resultTable,
      game = FinishedGameModel.init(state.game)
    )
  }
}
case class FinishedGameState(
                              id: String,
                              ownerId: String,
                              gameStartTime: Option[Timestamp],
                              gameEndTime: Option[Timestamp],
                              players: Map[PlayerId, PlayerModelId],
                              results: ResultTable,
                              game: FinishedGameModel
                            ) extends GameState {
  val gameDurationMillis: Option[Long] = gameStartTime.flatMap(start => gameEndTime.map(end => end - start))
}
