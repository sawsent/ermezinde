package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.board.{PlacePhaseBoardModel, PreparationPhaseBoardModel, ResolvePhaseBoardModel}
import saw.ermezinde.game.domain.card.MissionCard
import saw.ermezinde.game.domain.game.GamePhase
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId


sealed trait InPlayGameModel extends GameModel {
  override val players: Map[PlayerModelId, PlayerModel]

  val phase: GamePhase
  val round: Int
  val missionCards: List[MissionCard]
}
case class PreparationPhaseGameModel(
                                      round: Int,
                                      players: Map[PlayerModelId, PlayerModel],
                                      missionCards: List[MissionCard],
                                      diceRolls: Map[PlayerModel, Int],
                                      board: PreparationPhaseBoardModel
                                    ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PREPARATION
}

case class PlacePhaseGameModel(
                                round: Int,
                                players: Map[PlayerModelId, PlayerModel],
                                missionCards: List[MissionCard],
                                turn: Int,
                                currentPlayer: PlayerModel,
                                board: PlacePhaseBoardModel
                              ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PLACE
}

case class ResolvePhaseGameModel(
                                  round: Int,
                                  players: Map[PlayerModelId, PlayerModel],
                                  missionCards: List[MissionCard],
                                  board: ResolvePhaseBoardModel
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.RESOLVE
}

case class DiscardPhaseGameModel(
                                  round: Int,
                                  players: Map[PlayerModelId, PlayerModel],
                                  missionCards: List[MissionCard],
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.DISCARD
}

