package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.board.PlacePhaseBoardPower.{ChangeResolveOrderNumber, RotateBoard, Roulette}
import saw.ermezinde.game.domain.board.PFUseBoardPower.{UseChangeResolveOrderBoardPower, UseRotateBoardPower, UseRouletteBoardPower}
import saw.ermezinde.game.domain.board.{BoardInfo, BoardPosition, BoardRotation, PFUseBoardPower, PlacePhaseBoardPower}
import saw.ermezinde.game.domain.card.{Card, Deck, MissionCard}
import saw.ermezinde.game.domain.game.GamePhase
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.slot.{NormalSlotInfo, PFNormalSlot, PFPrizeSlot, PrizeSlotInfo}
import saw.ermezinde.game.domain.table.{PlacePhaseTableModel, PreparationPhaseTableModel, ResolvePhaseTableModel, SlotPositionDTO}


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
  def init(model: PreparationPhaseGameModel, playerOrdering: List[PlayerModelId], enigmaPlacement: BoardPosition): PlacePhaseGameModel = {
    val nrOfPrizeCardsToGive = model.table.boards.map(_._2.get).keys.flatMap(_.slots).filter(_.isPrize).map(_.asInstanceOf[PrizeSlotInfo]).map(_.prizeAmount).sum
    val (deck, cardsToGive) = model.deck.take(nrOfPrizeCardsToGive)
    PlacePhaseGameModel(
      config = model.config,
      round = model.round,
      players = model.players,
      deck = deck,
      currentPlayerIdx = 0,
      missionCards = model.missionCards,
      table = PlacePhaseTableModel.init(model.table, enigmaPlacement, cardsToGive),
      playerOrdering = playerOrdering,
    )
  }
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

  val currentPlayer: PlayerModelId = playerOrdering(currentPlayerIdx % playerOrdering.length)
  def nextPlayer: PlacePhaseGameModel = copy(currentPlayerIdx = currentPlayerIdx + 1)
  val allCardsPlaced: Boolean = currentPlayerIdx == 12
  private val placeRound: Int = (currentPlayerIdx / playerOrdering.length).ceil.toInt

  def playerPlaceCard(id: PlayerModelId, cardId: String, slotPosition: SlotPositionDTO): PlacePhaseGameModel = {
    val player = players(id)
    val card = player.hand.find(_.id == cardId).get
    val updatedModel = player.copy(
      hand = player.hand.filterNot(_ == card)
    )

    copy(
      players = players + (id -> updatedModel),
      table = table.placeCard(id, card, slotPosition)
    ).nextPlayer
  }

  def slotIsPlaceable(sp: SlotPositionDTO, playerModelId: PlayerModelId): Boolean = table.slotIsPlaceable(sp, playerModelId, placeRound)

  def isValidPowerUsageDTO(playerModelId: PlayerModelId, slotPositionDTO: SlotPositionDTO, powerUsageDTO: Option[PFUseBoardPower]): Boolean = {
    table.boardIdToBoard.get(slotPositionDTO.boardId).map(board => (board.power, powerUsageDTO, board))
      .exists({
        case (None, None, _) => true
        case (Some(RotateBoard), Some(_: UseRotateBoardPower), board) => board.nonPrizeSlots.forall(_.card.isEmpty)
        case (Some(_: Roulette), Some(UseRouletteBoardPower(card)), _) =>
          players(playerModelId).hand.contains(card) || players(playerModelId).discarded.contains(card)

        case (Some(ChangeResolveOrderNumber), Some(UseChangeResolveOrderBoardPower(change)), board) =>
          board.slots.get(slotPositionDTO.slotPosition)
            .filter(!_.isPrize)
            .map(_.asInstanceOf[PFNormalSlot])
            .exists(_.slotInfo.resolveOrderNumber >= math.abs(change))
        case _ => false
      })
  }

  def useBoardPower(playerModelId: PlayerModelId, boardId: String, powerUsageDTO: PFUseBoardPower): PlacePhaseGameModel = {
    val board = table.boardIdToBoard(boardId)
    val boardPosition = table.boardIdToBoardPosition(boardId)

    val playerModel = players(playerModelId)
    val updatedPlayerModel = powerUsageDTO match {
      case UseRouletteBoardPower(card) => playerModel.copy(
        hand = playerModel.hand.filterNot(_ == card),
        discarded = playerModel.discarded.filterNot(_ == card)
      )
      case _ => playerModel
    }

    val updatedBoard = board.power.map(_.use(powerUsageDTO, board)).getOrElse(board)

    val updatedTable = table.copy(
      boards = table.boards + (boardPosition -> updatedBoard)
    )
    copy(
      table = updatedTable,
      players = players + (updatedPlayerModel.id -> updatedPlayerModel)
    )
  }
}

object ResolvePhaseGameModel {
  def init(game: PlacePhaseGameModel): ResolvePhaseGameModel = ResolvePhaseGameModel(
    config = game.config,
    round = game.round,
    players = game.players,
    missionCards = game.missionCards,
    table = ResolvePhaseTableModel.init(game.table),
    deck = game.deck
  )
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

