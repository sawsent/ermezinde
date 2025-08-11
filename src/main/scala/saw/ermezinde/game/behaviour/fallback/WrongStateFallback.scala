package saw.ermezinde.game.behaviour.fallback

import saw.ermezinde.game.GameActor.GameActorCommand
import saw.ermezinde.game.domain.game.state.GameActorState
import saw.ermezinde.util.logging.BehaviourLogging

trait WrongStateFallback extends BehaviourLogging {
  def fallbackWrongState(state: GameActorState, cmd: GameActorCommand)(implicit behaviourName: String): Unit =
    logger.debug(behaviourName || s"Received command ${cmd.getClass.getSimpleName} while with state ${state.getClass.getSimpleName}. Ignoring.")
}
