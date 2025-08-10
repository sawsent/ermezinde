package saw.ermezinde.game.behaviour

import org.apache.pekko.actor.Actor.Receive
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.inplay.{DiscardPhaseBehaviour, PlacePhaseBehaviour, PreparationPhaseBehaviour, ResolvePhaseBehaviour}
import saw.ermezinde.game.domain.state.game.{DiscardPhaseGameState, FinishedGameState, GameActorState, GameNoState, GameState, InCountingGameState, InPlayGameState, InPreparationGameState, NotStartedGameState, PlacePhaseGameState, PreparationPhaseGameState, ResolvePhaseGameState}
import saw.ermezinde.util.logging.BehaviourLogging

object InPlayBehaviour {
  trait InPlayGameCommand
}
trait InPlayBehaviour extends PreparationPhaseBehaviour with PlacePhaseBehaviour with ResolvePhaseBehaviour with DiscardPhaseBehaviour
  with BehaviourLogging {

  private val BN: String = "InPlayBehaviour"

  def inPlayBehaviour(state: GameActorState): Receive = {
    case cmd: InPlayGameCommand => state match {
      case s: InPlayGameState => processInPlay(s, cmd)
      case _ => logger.debug(BN || s"Received $cmd while with wrong state. Ignoring.")
    }
  }

  def processInPlay(state: InPlayGameState, cmd: InPlayGameCommand): Unit = {
    logger.debug(BN || s"Processing cmd: $cmd")
    state match {
      case s: PreparationPhaseGameState => preparationBehaviour(s)(cmd)
      case s: PlacePhaseGameState => placeBehaviour(s)(cmd)
      case s: ResolvePhaseGameState => resolveBehaviour(s)(cmd)
      case s: DiscardPhaseGameState => discardBehaviour(s)(cmd)
    }
  }

}
