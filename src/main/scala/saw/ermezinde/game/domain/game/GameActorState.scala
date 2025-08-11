package saw.ermezinde.game.domain.game

import saw.ermezinde.game.domain.result.ResultTable
import GameActorState.{PlayerId, Timestamp}
import NotStartedGameState.NotStartedPlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.{Color, PlayerModelId}

object GameActorState {
  type PlayerId = String
  type Timestamp = Long
}
trait GameActorState {
  val id: String
}
case object GameNoState extends GameActorState {
  override val id: PlayerId = ""
}

sealed trait GameState extends GameActorState {
  val id: String
  val ownerId: String
  val gameStartTime: Option[Timestamp]
  val players: Map[PlayerId, PlayerModelId]
  val game: GameModel
}

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

object InPreparationGameState {
  def init(state: NotStartedGameState, startTime: Timestamp): InPreparationGameState = {
    InPreparationGameState(
      id = state.id,
      ownerId = state.ownerId,
      gameStartTime = Some(startTime),
      players = state.players,
      game = InPreparationGameModel.init(state.players.values.toList),
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
}

sealed trait InPlayGameState extends GameState {
  override val game: InPlayGameModel
}
case class PreparationPhaseGameState(
                                      id: String,
                                      ownerId: String,
                                      gameStartTime: Option[Timestamp],
                                      players: Map[PlayerId, PlayerModelId],
                                      game: PreparationPhaseGameModel
                                    ) extends InPlayGameState

case class PlacePhaseGameState(
                                id: String,
                                ownerId: String,
                                gameStartTime: Option[Timestamp],
                                players: Map[PlayerId, PlayerModelId],
                                game: PlacePhaseGameModel
                              ) extends InPlayGameState

case class ResolvePhaseGameState(
                                  id: String,
                                  ownerId: String,
                                  gameStartTime: Option[Timestamp],
                                  players: Map[PlayerId, PlayerModelId],
                                  game: ResolvePhaseGameModel
                                ) extends InPlayGameState

case class DiscardPhaseGameState(
                                  id: String,
                                  ownerId: String,
                                  gameStartTime: Option[Timestamp],
                                  players: Map[PlayerId, PlayerModelId],
                                  game: DiscardPhaseGameModel
                                ) extends InPlayGameState


object InCountingGameState {
  def init(state: InPlayGameState): InCountingGameState = {
    val updateGame: InCountingGameModel = InCountingGameModel.init(state.game)
    val resultTable = ResultTable.fromGameResults(state.players, updateGame.result)
    InCountingGameState(
      id = state.id,
      ownerId = state.ownerId,
      gameStartTime = state.gameStartTime,
      players = state.players,
      game = updateGame,
      resultTable
    )
  }
}
case class InCountingGameState(
                                id: String,
                                ownerId: String,
                                gameStartTime: Option[Timestamp],
                                players: Map[PlayerId, PlayerModelId],
                                game: InCountingGameModel,
                                resultTable: ResultTable
                              ) extends GameState

case class FinishedGameState(
                              id: String,
                              ownerId: String,
                              gameStartTime: Option[Timestamp],
                              players: Map[PlayerId, PlayerModelId],
                              game: FinishedGameModel
                            ) extends GameState


