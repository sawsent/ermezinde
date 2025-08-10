package saw.ermezinde.game.domain.state.game

import saw.ermezinde.game.domain.state.board.{PlacePhaseBoardModel, PreparationPhaseBoardModel, ResolvePhaseBoardModel}
import saw.ermezinde.game.domain.state.card.MissionCard
import saw.ermezinde.game.domain.state.player.PlayerModel
import saw.ermezinde.game.domain.state.player.PlayerModel.PlayerModelId

import scala.util.Random

sealed trait GameModel {
  val step: GameStep
  val players: Map[PlayerModelId, PlayerModel]
}

case class NotStartedGameModel() extends GameModel {
  override val step: GameStep = GameStep.NOT_STARTED
  override val players: Map[PlayerModelId, PlayerModel] = Map.empty
}

object InPreparationGameModel {
  def init(model: NotStartedGameModel): InPreparationGameModel = {
    val playerOrdering = Random.shuffle(model.players.keys.toList)

    InPreparationGameModel(
      model.players,
      playerOrdering,
      List.empty
    )
  }
}
case class InPreparationGameModel(
                                   players: Map[PlayerModelId, PlayerModel],
                                   playerOrdering: List[PlayerModelId],
                                   missionCards: List[MissionCard]
                                 ) extends GameModel {
  override val step: GameStep = GameStep.PREPARATION
}

sealed trait InPlayGameModel extends GameModel {
  override val step: GameStep = GameStep.IN_PLAY
  override val players: Map[PlayerModelId, PlayerModel]

  val phase: GamePhase

  val round: Int
}
case class PreparationPhaseGameModel(
                                      round: Int,
                                      players: Map[PlayerModelId, PlayerModel],

                                      diceRolls: Map[PlayerModel, Int],
                                      board: PreparationPhaseBoardModel
                                    ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PREPARATION
}

case class PlacePhaseGameModel(
                                players: Map[PlayerModelId, PlayerModel],
                                round: Int,
                                turn: Int,
                                currentPlayer: PlayerModel,
                                board: PlacePhaseBoardModel
                              ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PLACE
}

case class ResolvePhaseGameModel(
                                  players: Map[PlayerModelId, PlayerModel],
                                  round: Int,
                                  board: ResolvePhaseBoardModel
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.RESOLVE
}

case class DiscardPhaseGameModel(
                                  players: Map[PlayerModelId, PlayerModel],
                                  round: Int
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.DISCARD
}


case class InCountingGameModel(
                                step: GameStep = GameStep.COUNTING,
                                players: Map[PlayerModelId, PlayerModel]
                              ) extends GameModel

case class FinishedGameModel(
                              step: GameStep = GameStep.FINISHED,
                              players: Map[PlayerModelId, PlayerModel]
                            ) extends GameModel
