package saw.ermezinde.game

import org.apache.pekko.actor.{Actor, Props}
import saw.ermezinde.game.behaviour._
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.domain.state.game.{GameActorState, GameNoState, GameState}

object GameActor {
  def props: Props = Props(
    new GameActor with NoStateBehaviour
      with NotStartedBehaviour with InPreparationBehaviour with InPlayBehaviour
      with InCountingBehaviour with FinishedBehaviour with WrongStateFallback
  )
  // Commands
  trait GameActorCommand


  // Response
  type GameFailureResponse = String
  type GameSuccessResponse = String
  type GameActorResponse = Either[GameFailureResponse, GameSuccessResponse]
}
class GameActor extends Actor {
  this: NoStateBehaviour with NotStartedBehaviour with FinishedBehaviour with InPreparationBehaviour with InPlayBehaviour with InCountingBehaviour =>

  override def receive: Receive = behaviour(GameNoState)

  protected def gameBehaviour(state: GameActorState): Receive = {
    noStateBehaviour(state)
      .orElse(notStartedBehaviour(state))
      .orElse(inPreparationBehaviour(state))
      .orElse(inPlayBehaviour(state))
      .orElse(inCountingBehaviour(state))
      .orElse(finishedBehaviour(state))
  }

  def behaviour(state: GameActorState): Receive = gameBehaviour(state).orElse(common(state))

  def common(state: GameActorState): Receive = {
    case msg => println(s"Received unknown message: $msg while with state: $state")
  }
}

