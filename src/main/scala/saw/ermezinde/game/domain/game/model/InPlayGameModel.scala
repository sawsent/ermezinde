package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.board.{Board, BoardPosition, BoardRotation}
import saw.ermezinde.game.domain.card.{Deck, MissionCard}
import saw.ermezinde.game.domain.game.GamePhase
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.table.{PlacePhaseTableModel, PreparationPhaseTableModel, ResolvePhaseTableModel}
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
                                      table: PreparationPhaseTableModel,
                                    ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PREPARATION

  val currentPlayer: PlayerModelId = playerOrdering(currentPlayerIndex)

  def placeBoard(boardIndex: Int, boardPosition: BoardPosition, boardRotation: BoardRotation): PreparationPhaseGameModel = {
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

