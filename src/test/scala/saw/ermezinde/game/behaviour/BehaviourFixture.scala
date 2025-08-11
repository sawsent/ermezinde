package saw.ermezinde.game.behaviour

import org.apache.pekko.actor.ActorRef
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout
import org.mockito.MockitoSugar.mock
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.GameActorCommand
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.Duration
import org.mockito.MockitoSugar.reset
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import saw.ermezinde.game.domain.game.GameActorState

import scala.concurrent.Await


trait BehaviourFixture {
  implicit val askTimeout: Timeout = Timeout(1.seconds)
  val awaitDuration: Duration = Duration(1, TimeUnit.SECONDS)

  // mocks
  val mockFallback: (GameActorState, GameActorCommand) => Unit = mock[(GameActorState, GameActorCommand) => Unit]

  def resetMocks(): Unit = reset(mockFallback)

  def stateShouldBe(victim: ActorRef, state: GameActorState): Unit = {
    Await.result(victim ? TestDebugBehaviour.GetState, awaitDuration).asInstanceOf[GameActorState] shouldBe state
  }

  object TestDebugBehaviour {
    trait DebugCommands extends GameActorCommand
    case class SetState(state: GameActorState) extends DebugCommands
    case object GetState extends DebugCommands

  }
  trait TestDebugBehaviour {
    this: GameActor =>

    import TestDebugBehaviour._

    override def behaviour(state: GameActorState): Receive = debugBehaviour(state).orElse(gameBehaviour(state)).orElse(common(state))

    def debugBehaviour(state: GameActorState): Receive = {
      case SetState(state) => context.become(behaviour(state))
      case GetState => sender() ! state
    }
  }

  trait TestWrongStateFallback extends WrongStateFallback {
    override def fallbackWrongState(state: GameActorState, cmd: GameActorCommand)(implicit behaviourName: String): Unit = mockFallback.apply(state, cmd)
  }


  trait DoNothingNoStateBehaviour extends NoStateBehaviour {
    this: GameActor with WrongStateFallback =>
    override def noStateBehaviour(state: GameActorState): Receive = PartialFunction.empty
  }
  trait DoNothingNotStartedBehaviour extends NotStartedBehaviour {
    this: GameActor with WrongStateFallback =>
    override def notStartedBehaviour(state: GameActorState): Receive = PartialFunction.empty
  }
  trait DoNothingInPreparationBehaviour extends InPreparationBehaviour  {
    this: GameActor with WrongStateFallback =>
    override def inPreparationBehaviour(state: GameActorState): Receive = PartialFunction.empty
  }
  trait DoNothingInPlayBehaviour extends InPlayBehaviour {
    this: GameActor with WrongStateFallback =>
    override def inPlayBehaviour(state: GameActorState): Receive = PartialFunction.empty
  }
  trait DoNothingInCountingBehaviour extends InCountingBehaviour {
    this: GameActor with WrongStateFallback =>
    override def inCountingBehaviour(state: GameActorState): Receive = PartialFunction.empty
  }
  trait DoNothingFinishedBehaviour extends FinishedBehaviour {
    this: GameActor with WrongStateFallback =>
    override def finishedBehaviour(state: GameActorState): Receive = PartialFunction.empty
  }
}
