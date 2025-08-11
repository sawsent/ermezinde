package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.board.{PlacePhaseTableModel, PreparationPhaseTableModel, ResolvePhaseTableModel}
import saw.ermezinde.game.domain.card.MissionCard
import saw.ermezinde.game.domain.game.GamePhase
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId


object InPlayGameModel {
  def init(model: InPreparationGameModel): PreparationPhaseGameModel = PreparationPhaseGameModel(
    config = model.config,
    round = 1,
    players = model.players,
    missionCards = model.missionCards,
    diceRolls = Map.empty,
    table = PreparationPhaseTableModel.init(model.config)
  )
}
sealed trait InPlayGameModel extends GameModel {
  override val players: Map[PlayerModelId, PlayerModel]

  val phase: GamePhase
  val round: Int
  val missionCards: List[MissionCard]
}
case class PreparationPhaseGameModel(
                                      config: GameConfig,
                                      round: Int,
                                      players: Map[PlayerModelId, PlayerModel],
                                      missionCards: List[MissionCard],
                                      diceRolls: Map[PlayerModelId, Int],
                                      table: PreparationPhaseTableModel
                                    ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PREPARATION
}

case class PlacePhaseGameModel(
                                config: GameConfig,
                                round: Int,
                                players: Map[PlayerModelId, PlayerModel],
                                missionCards: List[MissionCard],
                                turn: Int,
                                currentPlayer: PlayerModel,
                                table: PlacePhaseTableModel
                              ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PLACE
}

case class ResolvePhaseGameModel(
                                  config: GameConfig,
                                  round: Int,
                                  players: Map[PlayerModelId, PlayerModel],
                                  missionCards: List[MissionCard],
                                  table: ResolvePhaseTableModel
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.RESOLVE
}

case class DiscardPhaseGameModel(
                                  config: GameConfig,
                                  round: Int,
                                  players: Map[PlayerModelId, PlayerModel],
                                  missionCards: List[MissionCard],
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.DISCARD
}

