package saw.ermezinde.game.domain.game.state.inplay

import saw.ermezinde.game.domain.board.dto.{PFUseBoardPowerDTO, PFUseRoulettePowerDTO}
import saw.ermezinde.game.domain.board.{BoardPosition, PFUseBoardPower}
import saw.ermezinde.game.domain.game.model.PlacePhaseGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.table.SlotPositionDTO

object PlacePhaseGameState {
  def init(state: BoardSelectionGameState, playerOrdering: List[PlayerId], enigmaPlacement: BoardPosition): PlacePhaseGameState =
    PlacePhaseGameState(
      state.id,
      state.ownerId,
      state.gameStartTime,
      state.players,
      PlacePhaseGameModel.init(state.game, playerOrdering.map(p => state.players(p)), enigmaPlacement)
    )
}
case class PlacePhaseGameState(
                                id: String,
                                ownerId: String,
                                gameStartTime: Option[Timestamp],
                                players: Map[PlayerId, PlayerModelId],
                                game: PlacePhaseGameModel
                              ) extends InPlayGameState {
  def currentPlayer: PlayerId = rPlayers(game.currentPlayer)

  def playerPlaceCard(playerId: PlayerId, cardId: String, slotPosition: SlotPositionDTO): PlacePhaseGameState = {
    val updatedGame = game.playerPlaceCard(players(playerId), cardId, slotPosition)
    copy(
      game = updatedGame
    )
  }

  def useBoardPower(playerId: PlayerId, boardId: String, boardPowerUsageDTO: PFUseBoardPowerDTO): PlacePhaseGameState = {
    val powerUsage = toBoardPower(playerId, boardPowerUsageDTO)
    copy(
      game = game.useBoardPower(players(playerId), boardId, powerUsage)
    )
  }

  def endTurn: InPlayGameState = {
    copy(
      game = game.nextPlayer
    ).verifications
  }

  def verifications: InPlayGameState = if (game.allCardsPlaced) {
    ResolvePhaseGameState.init(copy())
  } else copy()

  def toBoardPower(playerId: PlayerId, dto: PFUseBoardPowerDTO): PFUseBoardPower = dto match {
    case d: PFUseBoardPower.UseChangeResolveOrderBoardPower => d
    case d: PFUseBoardPower.UseRotateBoardPower => d
    case d: PFUseRoulettePowerDTO => (d.fromHand, d.cardId) match {
      case (true, Some(cardId)) => PFUseBoardPower.UseRouletteBoardPower(game.players(players(playerId)).hand.find(_.id == cardId).get)
      case (false, None) => PFUseBoardPower.UseRouletteBoardPower(game.players(players(playerId)).discarded.head)
    }
  }
}
