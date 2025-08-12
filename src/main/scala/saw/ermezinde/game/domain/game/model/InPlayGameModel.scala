package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.board.{Board, PlacePhaseTableModel, PreparationPhaseTableModel, ResolvePhaseTableModel}
import saw.ermezinde.game.domain.card.{Deck, MissionCard}
import saw.ermezinde.game.domain.game.GamePhase
import saw.ermezinde.game.domain.game.model.PreparationPhaseGameModel.PreparationAction
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.util.Randomizer


object InPlayGameModel {
  def init(model: InPreparationGameModel): PreparationPhaseGameModel = {
    val playerOrdering = Randomizer.randomizePlayers(model.players.keys.toList)
    val deck: Deck = Randomizer.shuffleDeck(Deck(model.config.cards))
    PreparationPhaseGameModel(
      config = model.config,
      round = 1,
      players = model.players,
      playerOrdering = playerOrdering,
      currentPlayerIndex = 0,
      missionCards = model.missionCards,
      availableBoards = model.config.boards,
      deck = deck,
      diceRolls = Map.empty,
      table = PreparationPhaseTableModel.init,
      currentAction = PreparationPhaseGameModel.SELECT_BOARD
    )
  }
}
sealed trait InPlayGameModel extends GameModel {
  override val players: Map[PlayerModelId, PlayerModel]

  val phase: GamePhase
  val round: Int
  val missionCards: List[MissionCard]
}
object PreparationPhaseGameModel {
  sealed trait PreparationAction
  case object SELECT_BOARD extends PreparationAction
  case object ROLL_DICE extends PreparationAction
}
case class PreparationPhaseGameModel(
                                      config: GameConfig,
                                      round: Int,
                                      players: Map[PlayerModelId, PlayerModel],
                                      playerOrdering: List[PlayerModelId],
                                      currentPlayerIndex: Int,
                                      availableBoards: List[Board],
                                      missionCards: List[MissionCard],
                                      deck: Deck,
                                      diceRolls: Map[PlayerModelId, Int],
                                      table: PreparationPhaseTableModel,
                                      currentAction: PreparationAction
                                    ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PREPARATION

  val currentPlayer: Option[PlayerModelId] = currentAction match {
    case PreparationPhaseGameModel.SELECT_BOARD => Some(playerOrdering(currentPlayerIndex))
    case PreparationPhaseGameModel.ROLL_DICE => None
  }
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

