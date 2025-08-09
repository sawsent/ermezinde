package saw.ermezinde.game.domain.state.game

import saw.ermezinde.game.domain.state.board.{PlacePhaseBoardModel, PreparationPhaseBoardModel, ResolvePhaseBoardModel}
import saw.ermezinde.game.domain.state.player.PlayerModel

sealed trait GameModel {
  val step: GameStep
  val players: Set[PlayerModel]
}

case class NotStartedGameModel(
                              players: Set[PlayerModel]
                              ) extends GameModel {
  override val step: GameStep = GameStep.NOT_STARTED
}

case class InPreparationGameModel(
                                   players: Set[PlayerModel]
                                 ) extends GameModel {
  override val step: GameStep = GameStep.PREPARATION
}

sealed trait InPlayGameModel extends GameModel {
  override val step: GameStep = GameStep.IN_PLAY
  override val players: Set[PlayerModel]

  val phase: GamePhase

  val round: Int
}
case class PreparationPhaseGameModel(
                                      round: Int,
                                      players: Set[PlayerModel],

                                      diceRolls: Map[PlayerModel, Int],
                                      board: PreparationPhaseBoardModel
                                    ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PREPARATION
}

case class PlacePhaseGameModel(
                                players: Set[PlayerModel],
                                round: Int,
                                turn: Int,
                                currentPlayer: PlayerModel,
                                board: PlacePhaseBoardModel
                              ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PLACE
}

case class ResolvePhaseGameModel(
                                  players: Set[PlayerModel],
                                  round: Int,
                                  board: ResolvePhaseBoardModel
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.RESOLVE
}

case class DiscardPhaseGameModel(
                                  players: Set[PlayerModel],
                                  round: Int
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.DISCARD
}


case class InCountingGameModel(
                                step: GameStep = GameStep.COUNTING,
                                players: Set[PlayerModel]
                              ) extends GameModel

case class FinishedGameModel(
                              step: GameStep = GameStep.FINISHED,
                              players: Set[PlayerModel]
                            ) extends GameModel