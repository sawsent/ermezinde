package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.board.{Board, BoardPosition, BoardRotation, PlacePhaseTableModel, PreparationPhaseTableModel, ResolvePhaseTableModel}
import saw.ermezinde.game.domain.card.{Deck, MissionCard}
import saw.ermezinde.game.domain.game.GamePhase
import saw.ermezinde.game.domain.game.state.GameActorState.DiceRoll
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.util.{Deterministic, Randomization}


object InPlayGameModel extends Deterministic {
  this: Randomization =>

  def init(model: InPreparationGameModel): PreparationPhaseGameModel = {
    val playerOrdering = randomizePlayers(model.players.keys.toList)
    val deck: Deck = shuffleDeck(Deck(model.config.cards))
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
      table = PreparationPhaseTableModel.init
    )
  }
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
                                      playerOrdering: List[PlayerModelId],
                                      currentPlayerIndex: Int,
                                      availableBoards: List[Board],
                                      missionCards: List[MissionCard],
                                      deck: Deck,
                                      diceRolls: Map[PlayerModelId, DiceRoll],
                                      table: PreparationPhaseTableModel,
                                    ) extends InPlayGameModel with Deterministic {
  override val phase: GamePhase = GamePhase.PREPARATION

  val currentPlayer: PlayerModelId = playerOrdering(currentPlayerIndex)

  def chooseBoard(boardIndex: Int, boardPosition: BoardPosition, boardRotation: BoardRotation): PreparationPhaseGameModel = {
    val board = availableBoards(boardIndex)
    copy(
      currentPlayerIndex = (currentPlayerIndex + 1) % players.toList.length,
      table = table.placeBoard(board, boardPosition, boardRotation),
      availableBoards = availableBoards.filterNot(_ == board)
    )
  }
}

object PlacePhaseGameModel {
  def init(model: PreparationPhaseGameModel, playerOrdering: List[PlayerModelId]): PlacePhaseGameModel = PlacePhaseGameModel(
    config = model.config,
    round = model.round,
    players = model.players,
    missionCards = model.missionCards,
    table = PlacePhaseTableModel.init(model.table),
    playerOrdering = playerOrdering
  )
}
case class PlacePhaseGameModel(
                                config: GameConfig,
                                round: Int,
                                players: Map[PlayerModelId, PlayerModel],
                                missionCards: List[MissionCard],
                                playerOrdering: List[PlayerModelId],
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

