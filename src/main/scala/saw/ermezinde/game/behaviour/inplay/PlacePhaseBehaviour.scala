package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.behaviour.inplay.PlacePhaseBehaviour.{PlacePhaseCommand, PlayerPlaceCard}
import saw.ermezinde.game.domain.board.PFUseBoardPower
import saw.ermezinde.game.domain.board.dto.{PFUseBoardPowerDTO, PFUseRoulettePowerDTO}
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state.GameState
import saw.ermezinde.game.domain.game.state.inplay.PlacePhaseGameState
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.table.SlotPositionDTO
import saw.ermezinde.util.validation.Validate
import saw.ermezinde.util.validation.EitherSyntax.toEither

import scala.util.Try

object PlacePhaseBehaviour {
  sealed trait PlacePhaseCommand extends InPlayGameCommand

  case class PlayerPlaceCard(playerId: PlayerId, cardId: String, slot: SlotPositionDTO, visionUsage: List[SlotPositionDTO], boardPowerUsageDTO: Option[PFUseBoardPowerDTO]) extends PlacePhaseCommand
}
trait PlacePhaseBehaviour {
  this: GameActor with WrongStateFallback =>
  private implicit val BN: String = "PlacePhaseBehaviour"
  def placeBehaviour(state: GameState, cmd: PlacePhaseCommand): GameActorResponse = state match {
    case state: PlacePhaseGameState => processPlace(state, cmd)
    case _ => fallbackWrongStateWithReply(state, cmd)
  }

  def processPlace(state: PlacePhaseGameState, cmd: PlacePhaseCommand): GameActorResponse = cmd match {
    case cmd @ PlayerPlaceCard(playerId, cardId, slot, visionUsage, bpUsageDTO) => val stx = Syntax(state, cmd); import stx._
      Validate(
        isPlayerCorrect -> s"It's not $playerId's turn",
        playerHasCard   -> s"Player $playerId does not have the card $cardId",
        boardExists -> s"Board ${slot.boardId} does not exist in the table",
        slotExists(slot) -> s"Slot $slot does not exist",
        slotIsPlaceable(slot, playerId) -> s"Slot $slot is not placeable",
        visionUsage.forall(slotExists) -> s"Slots $visionUsage does not exist",
        canSeeCards -> s"Cant see cards $visionUsage from slot $slot",
        canUsePower -> s"Cant use power $bpUsageDTO"
      ).map {
        val withCardPlaced = state.playerPlaceCard(playerId, cardId, slot)
        val withBoardPowerUsed = bpUsageDTO.map(withCardPlaced.useBoardPower(playerId, slot.boardId, _)).getOrElse(withCardPlaced)
        val updatedState = withBoardPowerUsed.endTurn

        context.become(behaviour(updatedState))
        s"Player $playerId placed card $cardId in slot ${slot.slotPosition} of board ${slot.boardId}"
      }

  }

  private case class Syntax(s: PlacePhaseGameState, c: PlayerPlaceCard) {
    private val playerModel: PlayerModel = s.game.players(s.players(c.playerId))
    val isPlayerCorrect: Boolean = s.currentPlayer == c.playerId
    val playerHasCard: Boolean = playerModel.hand.exists(_.id == c.cardId)

    val boardExists: Boolean = s.game.table.boards.exists(_._2.id == c.slot.boardId)

    def slotExists(sp: SlotPositionDTO): Boolean = s.game.table.slotExists(sp)
    def slotIsPlaceable(sp: SlotPositionDTO, playerId: PlayerId): Boolean = s.game.slotIsPlaceable(sp, s.players(playerId))
    val canSeeCards: Boolean = s.game.table.slotCanSeeCards(c.slot, c.visionUsage)

    def toBoardPowerOption: Option[PFUseBoardPower] = c.boardPowerUsageDTO.map(s.toBoardPower(c.playerId, _))
    val canUsePower: Boolean = s.game.isValidPowerUsageDTO(playerModel.id, c.slot, toBoardPowerOption) && Try(c.boardPowerUsageDTO.map(s.toBoardPower(c.playerId, _))).isSuccess


  }

}
