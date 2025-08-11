package saw.ermezinde.game.behaviour

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse}
import saw.ermezinde.game.behaviour.FinishedBehaviour.{FinishedGameCommand, GetResults}
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.domain.game.state.{FinishedGameState, GameActorState}
import saw.ermezinde.util.logging.BehaviourLogging

object FinishedBehaviour {
  trait FinishedGameCommand extends GameActorCommand
  case object GetResults extends FinishedGameCommand
}
trait FinishedBehaviour extends BehaviourLogging {
  this: GameActor with WrongStateFallback =>
  private implicit val BN: String = "FinishedBehaviour"

  def finishedBehaviour(state: GameActorState): Receive = {
    case cmd: FinishedGameCommand => state match {
      case s: FinishedGameState =>
        val response = processFinishedGameCommand(s, cmd)
        sender() ! response
      case s: GameActorState => fallbackWrongState(s, cmd)

    }
  }

  def processFinishedGameCommand(state: FinishedGameState, cmd: FinishedGameCommand): GameActorResponse = {
    cmd match {
      case GetResults =>
        Right(state.toString)
    }
  }

}
