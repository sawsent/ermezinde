package saw.ermezinde.game.domain.state.game

import saw.ermezinde.game.domain.state.card.MissionCard
import saw.ermezinde.game.domain.state.game.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.state.game.NotStartedGameState.NotStartedPlayerModel
import saw.ermezinde.game.domain.state.player.PlayerModel
import saw.ermezinde.game.domain.state.player.PlayerModel.{Color, PlayerModelId}

object GameActorState {
  type PlayerId = String
  type Timestamp = Long
}
trait GameActorState
case object GameNoState extends GameActorState

sealed trait GameState extends GameActorState {
  val id: String
  val ownerId: String
  val gameStartTime: Option[Timestamp]
  val players: Map[PlayerId, PlayerModelId]
  val game: GameModel
  val gameStep: GameStep = game.step

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
      game = InPreparationGameModel.init(state.game)
    )
  }
}
case class InPreparationGameState(
                                   id: String,
                                   ownerId: String,
                                   gameStartTime: Option[Timestamp],
                                   players: Map[PlayerId, PlayerModelId],
                                   game: InPreparationGameModel
                                 ) extends GameState

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


case class InCountingGameState(
                                id: String,
                                ownerId: String,
                                gameStartTime: Option[Timestamp],
                                players: Map[PlayerId, PlayerModelId],
                                game: InCountingGameModel
                              ) extends GameState

case class FinishedGameState(
                              id: String,
                              ownerId: String,
                              gameStartTime: Option[Timestamp],
                              players: Map[PlayerId, PlayerModelId],
                              game: FinishedGameModel
                            ) extends GameState


