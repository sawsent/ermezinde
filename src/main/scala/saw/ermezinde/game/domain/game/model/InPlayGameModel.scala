package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.board.{Board, BoardInfo, BoardPosition, BoardRotation}
import saw.ermezinde.game.domain.card.{Card, Deck, MissionCard}
import saw.ermezinde.game.domain.game.GamePhase
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.table.{PlacePhaseTableModel, PreparationPhaseTableModel, ResolvePhaseTableModel}


object InPlayGameModel {

  def init(model: InPreparationGameModel): PreparationPhaseGameModel = {
    val playerOrdering = model.config.randomizer.randomizePlayers(model.players.keys.toList)
    val deck: Deck = model.config.randomizer.shuffleDeck(Deck(model.config.cards))
    PreparationPhaseGameModel(
      config = model.config,
      round = 1,
      players = model.players,
      playerOrdering = playerOrdering,
      currentPlayerIndex = 0,
      missionCards = model.missionCards,
      availableBoards = model.config.boards,
      deck = deck,
      table = PreparationPhaseTableModel.init,
      enigmaOwner = None
    )
  }
}
sealed trait InPlayGameModel extends GameModel {
  val phase: GamePhase
  val round: Int
  val missionCards: List[MissionCard]
  val deck: Deck
}

object PreparationPhaseGameModel {
  def newRound(model: DiscardPhaseGameModel): PreparationPhaseGameModel = PreparationPhaseGameModel(
    config = model.config,
    round = model.round + 1,
    players = model.players,
    enigmaOwner = model.enigmaOwner,
    playerOrdering = model.config.randomizer.randomizePlayers(model.players.keys.toList),
    currentPlayerIndex = 0,
    availableBoards = model.config.boards,
    missionCards = model.missionCards,
    deck = model.deck,
    table = PreparationPhaseTableModel.init,
  )
}
case class PreparationPhaseGameModel(
                                      config: GameConfig,
                                      round: Int,
                                      players: Map[PlayerModelId, PlayerModel],
                                      enigmaOwner: Option[PlayerModelId],
                                      playerOrdering: List[PlayerModelId],
                                      currentPlayerIndex: Int,
                                      availableBoards: List[BoardInfo],
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
  def init(model: PreparationPhaseGameModel, playerOrdering: List[PlayerModelId], enigmaPlacement: BoardPosition): PlacePhaseGameModel = PlacePhaseGameModel(
    config = model.config,
    round = model.round,
    players = model.players,
    deck = model.deck,
    currentPlayerIdx = 0,
    missionCards = model.missionCards,
    table = PlacePhaseTableModel.init(model.table, enigmaPlacement),
    playerOrdering = playerOrdering,
  )
}
case class PlacePhaseGameModel(
                                config: GameConfig,
                                round: Int,
                                players: Map[PlayerModelId, PlayerModel],
                                missionCards: List[MissionCard],
                                deck: Deck,
                                playerOrdering: List[PlayerModelId],
                                currentPlayerIdx: Int,
                                table: PlacePhaseTableModel
                              ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PLACE

  val currentPlayer: PlayerModelId = playerOrdering(currentPlayerIdx)
  def nextPlayer: PlacePhaseGameModel = copy(currentPlayerIdx = (currentPlayerIdx + 1) % playerOrdering.length)
}

case class ResolvePhaseGameModel(
                                  config: GameConfig,
                                  round: Int,
                                  players: Map[PlayerModelId, PlayerModel],
                                  missionCards: List[MissionCard],
                                  table: ResolvePhaseTableModel,
                                  deck: Deck
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.RESOLVE
}

object DiscardPhaseGameModel {
  def init(model: ResolvePhaseGameModel, enigmaOwner: Option[PlayerModelId]): DiscardPhaseGameModel = DiscardPhaseGameModel(
    config = model.config,
    round = model.round,
    players = model.players,
    enigmaOwner = enigmaOwner,
    missionCards = model.missionCards,
    deck = model.deck
  )
}
case class DiscardPhaseGameModel(
                                  config: GameConfig,
                                  round: Int,
                                  players: Map[PlayerModelId, PlayerModel],
                                  enigmaOwner: Option[PlayerModelId],
                                  missionCards: List[MissionCard],
                                  deck: Deck
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.DISCARD

  def playerDiscardCards(id: PlayerModelId, cardsToDiscard: List[Card]): DiscardPhaseGameModel = {
    val updatedPlayer = players(id).copy(
      discarded = players(id).discarded ++ cardsToDiscard,
      hand = players(id).hand.filterNot(cardsToDiscard.contains(_))
    )
    val updatedPlayers = players + (id -> updatedPlayer)
    copy(
      players = updatedPlayers
    )
  }
}

