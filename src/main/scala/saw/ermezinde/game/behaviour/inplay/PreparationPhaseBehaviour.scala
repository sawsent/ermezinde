package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.behaviour.inplay.PreparationPhaseBehaviour.{PlaceEnigma, PreparationPhaseCommand, PreparationPhaseDiceRoll, SelectBoard}
import saw.ermezinde.game.domain.board.{BoardPosition, BoardRotation}
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state.{BoardSelectionGameState, EnigmaPlacementGameState, PreparationPhaseGameState, OrderingSelectionGameState}
import saw.ermezinde.game.syntax.Validate
import saw.ermezinde.util.logging.BehaviourLogging

object PreparationPhaseBehaviour {
  sealed trait PreparationPhaseCommand extends InPlayGameCommand

  case class SelectBoard(playerId: PlayerId, boardIndex: Int, boardPosition: BoardPosition, boardRotation: BoardRotation) extends PreparationPhaseCommand
  case class PreparationPhaseDiceRoll(playerId: PlayerId) extends PreparationPhaseCommand
  case class PlaceEnigma(playerId: PlayerId, boardPosition: BoardPosition) extends PreparationPhaseCommand

}
trait PreparationPhaseBehaviour extends BehaviourLogging {
  this: WrongStateFallback =>
  private implicit val BN: String = "PreparationPhaseBehaviour"
  def preparationBehaviour(state: PreparationPhaseGameState, cmd: PreparationPhaseCommand): GameActorResponse = {
    (state, cmd) match {
      case (state: BoardSelectionGameState, cmd: SelectBoard) => processSelectBoard(state, cmd)
      case (state: OrderingSelectionGameState, cmd: PreparationPhaseDiceRoll) => processRollDice(state, cmd)
      case (state: EnigmaPlacementGameState, cmd: PlaceEnigma) => processEnigmaPlacement(state, cmd)
      case _ => fallbackWrongStateWithReply(state, cmd)
    }
  }

  private def processSelectBoard(state: BoardSelectionGameState, board: SelectBoard): GameActorResponse = {
    Validate(
      Either.cond(false, "", "Not Implemented")
    ).map {
      "Not Implemented but passed validation"
    }
  }

  private def processRollDice(state: OrderingSelectionGameState, cmd: PreparationPhaseDiceRoll): GameActorResponse = Validate(
    Either.cond(false, "", "Not Implemented")
  ).map {
    "Not Implemented but passed validation"
  }

  private def processEnigmaPlacement(state: EnigmaPlacementGameState, cmd: PlaceEnigma): GameActorResponse = Validate(
    Either.cond(false, "", "Not Implemented")
  ).map {
    "Not Implemented but passed validation"
  }
}
