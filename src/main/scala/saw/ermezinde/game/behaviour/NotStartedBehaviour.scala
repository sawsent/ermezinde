package saw.ermezinde.game.behaviour

import org.apache.pekko.actor.Actor.Receive
import saw.ermezinde.game.behaviour.NotStartedBehaviour.NotStartedGameCommand
import saw.ermezinde.game.domain.state.game.{GameActorState, NotStartedGameModel, NotStartedGameState}
import saw.ermezinde.util.logging.BehaviourLogging

object NotStartedBehaviour {
  trait NotStartedGameCommand
}
trait NotStartedBehaviour extends BehaviourLogging {
  private implicit val BN: String = "NotStartedBehaviour"

  def notStartedBehaviour(state: GameActorState): Receive = {
    case cmd: NotStartedGameCommand => state match {
      case s: NotStartedGameState => processNotStarted(s, cmd)
      case _ => logger.debug(BN || s"Received cmd: $cmd with wrong state. Ignoring.")
    }
  }

  def processNotStarted(state: NotStartedGameState, cmd: NotStartedGameCommand): Unit = {
    logger.debug(BN || s"Processing cmd: $cmd")
  }

}
