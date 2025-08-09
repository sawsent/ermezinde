package saw.ermezinde.game.behaviour

import org.apache.pekko.actor.Actor.Receive
import saw.ermezinde.game.behaviour.InPreparationBehaviour.InPreparationGameCommand
import saw.ermezinde.game.domain.state.game.GameActorState
import saw.ermezinde.util.logging.BehaviourLogging


object InPreparationBehaviour {
  trait InPreparationGameCommand

  case object TestInPreparationCommand extends InPreparationGameCommand
}
trait InPreparationBehaviour extends BehaviourLogging {
  private implicit val BehaviourName: String = "InPreparationBehaviour"

  def inPreparationBehaviour(state: GameActorState): Receive = {
    case cmd: InPreparationGameCommand => processInPreparation(state, cmd)
  }

  def processInPreparation(state: GameActorState, cmd: InPreparationGameCommand): Unit = {
    log(s"Processing cmd: $cmd")
  }

}