package saw.ermezinde.game

import org.apache.pekko.actor.{Actor, Props}
import saw.ermezinde.game.GameActor.DebugSetState
import saw.ermezinde.game.behaviour._
import saw.ermezinde.game.domain.state.game.{GameActorState, GameNoState, GameState}

object GameActor {
  def props: Props = Props(new GameActor)
  case class DebugSetState(state: GameActorState)
}
class GameActor extends Actor
  with NotStartedBehaviour with FinishedBehaviour
  with InPreparationBehaviour with InPlayBehaviour
  with InCountingBehaviour {

  override def receive: Receive = behaviour(GameNoState)

  def gameBehaviour(state: GameActorState): Receive = {
    notStartedBehaviour(state)
      .orElse(inPreparationBehaviour(state))
      .orElse(inPlayBehaviour(state))
      .orElse(inCountingBehaviour(state))
      .orElse(finishedBehaviour(state))
  }

  def behaviour(state: GameActorState): Receive = debugBehaviour.orElse(gameBehaviour(state)).orElse(common(state))

  def common(state: GameActorState): Receive = {
    case msg => println(s"Received unknown message: $msg while with state: $state")
  }

  val debugBehaviour: Receive = {
    case DebugSetState(s: GameState) =>
      context.become(behaviour(s))
  }
}

