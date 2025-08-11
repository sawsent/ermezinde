package saw.ermezinde.game.behaviour

import org.apache.pekko.actor.Actor.Receive
import saw.ermezinde.game.behaviour.InCountingBehaviour.InCountingGameCommand
import saw.ermezinde.game.domain.game.GameActorState
import saw.ermezinde.util.logging.BehaviourLogging

object InCountingBehaviour {
  trait InCountingGameCommand
}
trait InCountingBehaviour extends BehaviourLogging {
  private val BN: String = "InCountingBehaviour"

  def inCountingBehaviour(state: GameActorState): Receive = {
    case cmd: InCountingGameCommand => processInCountint(state, cmd)
  }

  def processInCountint(state: GameActorState, cmd: InCountingGameCommand): Unit =
    logger.debug(BN || s"Processing Command $cmd")
}
