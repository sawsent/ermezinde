package saw.ermezinde.game.behaviour

import org.apache.pekko.actor.Actor.Receive
import saw.ermezinde.game.behaviour.FinishedBehaviour.FinishedGameCommand
import saw.ermezinde.game.domain.state.game.GameActorState
import saw.ermezinde.util.logging.BehaviourLogging

object FinishedBehaviour {
  trait FinishedGameCommand
}
trait FinishedBehaviour extends BehaviourLogging {
  private implicit val BehaviourName: String = "FinishedBehaviour"

  def finishedBehaviour(state: GameActorState): Receive = {
    case cmd: FinishedGameCommand => processFinishedGameCommand(state, cmd)
  }

  def processFinishedGameCommand(state: GameActorState, cmd: FinishedGameCommand): Unit = {
    log(s"Processing command $cmd")
  }
}
