package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.behaviour.inplay.PreparationPhaseBehaviour.{PlaceEnigma, PreparationPhaseCommand, RollDicePreparation, SelectBoard}
import saw.ermezinde.game.domain.board.{BoardPosition, BoardRotation}
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state.inplay.{BoardSelectionGameState, EnigmaPlacementGameState, OrderingSelectionGameState, PreparationPhaseGameState}
import saw.ermezinde.game.util.RandomizationCapability
import saw.ermezinde.util.logging.BehaviourLogging
import saw.ermezinde.util.validation.EitherSyntax.toEither
import saw.ermezinde.util.validation.Validate

import scala.util.Try

object PreparationPhaseBehaviour {
  sealed trait PreparationPhaseCommand extends InPlayGameCommand

  case class SelectBoard(playerId: PlayerId, boardIndex: Int, boardPosition: BoardPosition, boardRotation: BoardRotation) extends PreparationPhaseCommand
  case class RollDicePreparation(playerId: PlayerId) extends PreparationPhaseCommand
  case class PlaceEnigma(playerId: PlayerId, boardPosition: BoardPosition) extends PreparationPhaseCommand

}
trait PreparationPhaseBehaviour extends BehaviourLogging {
  this: GameActor with WrongStateFallback with RandomizationCapability =>
  private implicit val BN: String = "PreparationPhaseBehaviour"
  def preparationBehaviour(state: PreparationPhaseGameState, cmd: PreparationPhaseCommand): GameActorResponse = {
    (state, cmd) match {
      case (state: BoardSelectionGameState, cmd: SelectBoard) => processSelectBoard(state, cmd)
      case (state: OrderingSelectionGameState, cmd: RollDicePreparation) => processRollDice(state, cmd)
      case (state: EnigmaPlacementGameState, cmd: PlaceEnigma) => processEnigmaPlacement(state, cmd)
      case _ => fallbackWrongStateWithReply(state, cmd)
    }
  }

  private def processSelectBoard(state: BoardSelectionGameState, cmd: SelectBoard): GameActorResponse = {
    Validate(
      (state.game.currentPlayer == state.players(cmd.playerId)) -> s"It is not ${cmd.playerId}'s turn to choose a board'",
      Try(state.game.availableBoards(cmd.boardIndex)).isSuccess -> "There is no board for that index",
      state.game.table.positionAvailable(cmd.boardPosition)     -> "There is already a board at that location"
    ).map {
      val updatedState = state.copy(
        game = state.game.placeBoard(cmd.boardIndex, cmd.boardPosition, cmd.boardRotation)
      ).checkMoveOnToDiceRolls

      context.become(behaviour(updatedState))
      s"Player ${cmd.playerId} choose board ${cmd.boardIndex} to be place at ${cmd.boardPosition} with boardRotation ${cmd.boardRotation}"
    }
  }

  private def processRollDice(state: OrderingSelectionGameState, cmd: RollDicePreparation): GameActorResponse = Validate(
    state.playersContesting.contains(cmd.playerId)                  -> s"Player '${cmd.playerId}' can't roll at this moment",
    !state.currentRoundDiceRolls.keys.toList.contains(cmd.playerId) -> s"Player '${cmd.playerId}' has already rolled for this round"
  ).map {
    val roll = randomizer.rollDice()
    val updatedState = state.playerRollDice(cmd.playerId, roll)

    context.become(behaviour(updatedState))
    s"Player ${cmd.playerId} has rolled $roll"
  }

  private def processEnigmaPlacement(state: EnigmaPlacementGameState, cmd: PlaceEnigma): GameActorResponse = Validate(
    state.enigmaOwner.contains(cmd.playerId) -> s"Player ${cmd.playerId} is not the owner of the enigma"
  ).map {
    val updatedState = state.setEnigmaPlacement(cmd.boardPosition).moveToPlacement
    context.become(behaviour(updatedState))

    s"Player ${cmd.playerId} placed the Enigma on BoardPosition ${cmd.boardPosition}"
  }
}
