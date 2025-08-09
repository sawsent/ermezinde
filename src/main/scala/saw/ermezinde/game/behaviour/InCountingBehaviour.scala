package saw.ermezinde.game.behaviour

import org.apache.pekko.actor.Actor.Receive
import saw.ermezinde.game.behaviour.InCountingBehaviour.InCountingGameCommand
import saw.ermezinde.game.domain.state.game.GameActorState
import saw.ermezinde.util.logging.BehaviourLogging

object InCountingBehaviour {
  trait InCountingGameCommand
}
trait InCountingBehaviour extends BehaviourLogging {
  private implicit val BehaviourName: String = "InCountingBehaviour"

  def inCountingBehaviour(state: GameActorState): Receive = {
    case cmd: InCountingGameCommand => processInCountint(state, cmd)
  }

  def processInCountint(state: GameActorState, cmd: InCountingGameCommand): Unit =
    log(s"Processing Command $cmd")
}
