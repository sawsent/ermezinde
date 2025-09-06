package saw.ermezinde.game.behaviour

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse}
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.behaviour.inplay.DiscardPhaseBehaviour.DiscardPhaseCommand
import saw.ermezinde.game.behaviour.inplay.PlacePhaseBehaviour.PlacePhaseCommand
import saw.ermezinde.game.behaviour.inplay.PreparationPhaseBehaviour.PreparationPhaseCommand
import saw.ermezinde.game.behaviour.inplay.ResolvePhaseBehaviour.ResolvePhaseCommand
import saw.ermezinde.game.behaviour.inplay.{DiscardPhaseBehaviour, PlacePhaseBehaviour, PreparationPhaseBehaviour, ResolvePhaseBehaviour}
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state._
import saw.ermezinde.game.domain.game.state.inplay.{DiscardPhaseGameState, InPlayGameState, PlacePhaseGameState, PreparationPhaseGameState, ResolvePhaseGameState}
import saw.ermezinde.game.validation.PlayerIdValidation.PlayerIdValidation
import saw.ermezinde.util.logging.BehaviourLogging
import saw.ermezinde.util.validation.Validate

object InPlayBehaviour {
  trait InPlayGameCommand extends GameActorCommand {
    val playerId: PlayerId
  }
}
trait InPlayBehaviour extends PreparationPhaseBehaviour with PlacePhaseBehaviour with ResolvePhaseBehaviour with DiscardPhaseBehaviour
  with BehaviourLogging {
  this: GameActor with WrongStateFallback =>

  private implicit val BN: String = "InPlayBehaviour"

  def inPlayBehaviour(state: GameActorState): Receive = {
    case cmd: InPlayGameCommand => state match {
      case s: InPlayGameState =>
        val response = Validate(
          cmd.playerId.inGame(s),
        ).flatMap(processInPlay(s, cmd))

        sender() ! response

      case s: GameActorState => fallbackWrongState(s, cmd)
    }
  }

  def processInPlay(state: InPlayGameState, cmd: InPlayGameCommand): GameActorResponse = {
    logger.debug(BN || s"Processing cmd: $cmd")
    (state, cmd) match {
      case (s: PreparationPhaseGameState, cmd: PreparationPhaseCommand) => preparationBehaviour(s, cmd)
      case (s: PlacePhaseGameState,       cmd: PlacePhaseCommand)       => placeBehaviour(s, cmd)
      case (s: ResolvePhaseGameState,     cmd: ResolvePhaseCommand)     => resolveBehaviour(s, cmd)
      case (s: DiscardPhaseGameState,     cmd: DiscardPhaseCommand)     => discardBehaviour(s, cmd)
      case _ => Left(s"Wrong phase: state phase: ${state.game.phase}, cmd: $cmd")
    }
  }


  implicit class InPlayStateValidations(state: InPlayGameState) {

  }
}
