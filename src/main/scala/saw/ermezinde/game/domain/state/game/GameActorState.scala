package saw.ermezinde.game.domain.state.game

import saw.ermezinde.game.domain.state.game.GameActorState.PlayerId
import saw.ermezinde.game.domain.state.player.PlayerModel.PlayerModelId

object GameActorState {
  type PlayerId = String
}
trait GameActorState
case object GameNoState extends GameActorState

sealed trait GameState extends GameActorState {
  val id: String
  val ownerId: String
  val players: Map[PlayerId, PlayerModelId]
  val game: GameModel
  val gameStep: GameStep = game.step

}

case class NotStartedGameState(
                                id: String,
                                ownerId: String,
                                players: Map[PlayerId, PlayerModelId],
                                game: NotStartedGameModel
                              ) extends GameState

case class InPreparationGameState(
                                   id: String,
                                   ownerId: String,
                                   players: Map[PlayerId, PlayerModelId],
                                   game: InPreparationGameModel
                                 ) extends GameState

sealed trait InPlayGameState extends GameState {
  override val game: InPlayGameModel
}
case class PreparationPhaseGameState(
                                      id: String,
                                      ownerId: String,
                                      players: Map[PlayerId, PlayerModelId],
                                      game: PreparationPhaseGameModel
                                    ) extends InPlayGameState

case class PlacePhaseGameState(
                                id: String,
                                ownerId: String,
                                players: Map[PlayerId, PlayerModelId],
                                game: PlacePhaseGameModel
                              ) extends InPlayGameState

case class ResolvePhaseGameState(
                                  id: String,
                                  ownerId: String,
                                  players: Map[PlayerId, PlayerModelId],
                                  game: ResolvePhaseGameModel
                                ) extends InPlayGameState

case class DiscardPhaseGameState(
                                  id: String,
                                  ownerId: String,
                                  players: Map[PlayerId, PlayerModelId],
                                  game: DiscardPhaseGameModel
                                ) extends InPlayGameState


case class InCountingGameState(
                                id: String,
                                ownerId: String,
                                players: Map[PlayerId, PlayerModelId],
                                game: InCountingGameModel
                              ) extends GameState

case class FinishedGameState(
                              id: String,
                              ownerId: String,
                              players: Map[PlayerId, PlayerModelId],
                              game: FinishedGameModel
                            ) extends GameState


