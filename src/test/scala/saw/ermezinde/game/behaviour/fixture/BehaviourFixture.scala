package saw.ermezinde.game.behaviour.fixture

import org.apache.pekko.actor.ActorRef
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout
import org.mockito.MockitoSugar.{mock, reset}
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse}
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.behaviour._
import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.card.Deck
import saw.ermezinde.game.domain.game.state.GameActorState
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.util.RandomizationCapability
import saw.ermezinde.util.Randomization

import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}


trait BehaviourFixture {
  implicit val askTimeout: Timeout = Timeout(1.seconds)
  val awaitDuration: Duration = Duration(1, TimeUnit.SECONDS)

  def printResponse(cmd: Any)(implicit victim: ActorRef): Unit = {
    Try(Await.result(victim ? cmd, awaitDuration)) match {
      case Failure(_) => println(s"Failure on cmd: $cmd")
      case Success(value) => println(value)
    }
  }

  def send(any: Any)(implicit victim: ActorRef): Unit = {
    victim ! any
  }

  def askActor(any: Any)(implicit victim: ActorRef): Any = {
    Await.result(victim ? any, awaitDuration)
  }

  def askActorWithPrint(any: Any)(implicit victim: ActorRef): Any = {
    val res = Await.result(victim ? any, awaitDuration)
    println(res)
    res
  }

  // mocks
  val mockFallback: (GameActorState, GameActorCommand) => Unit = mock[(GameActorState, GameActorCommand) => Unit]

  def resetMocks(): Unit = reset(mockFallback)

  def stateShouldBe(victim: ActorRef, state: GameActorState): Unit = {
    Await.result(victim ? TestDebugBehaviour.GetState, awaitDuration).asInstanceOf[GameActorState] shouldBe state
  }

  abstract class AbstractVictim(config: GameConfig) extends GameActor(config) with DoNothingNoStateBehaviour with  DoNothingNotStartedBehaviour
    with DoNothingInPreparationBehaviour with DoNothingInPlayBehaviour with DoNothingInCountingBehaviour with DoNothingFinishedBehaviour
    with TestWrongStateFallback with TestRandomizationCapability with TestDebugBehaviour

  object TestDebugBehaviour {
    trait DebugCommands extends GameActorCommand
    case class SetState(state: GameActorState) extends DebugCommands
    case object GetState extends DebugCommands
  }
  trait TestDebugBehaviour {
    this: GameActor =>

    import TestDebugBehaviour._

    override def behaviour(state: GameActorState): Receive = beforeProcessing(state).andThen(debugBehaviour(state).orElse(gameBehaviour(state)).orElse(common(state)))

    def debugBehaviour(state: GameActorState): Receive = {
      case SetState(state) => context.become(behaviour(state))
      case GetState => sender() ! state
    }
  }

  trait TestWrongStateFallback extends WrongStateFallback {
    override def fallbackWrongState(state: GameActorState, cmd: GameActorCommand)(implicit behaviourName: String): Unit = mockFallback.apply(state, cmd)
  }

  val rollDiceMock: () => (Int, Int) = mock[() => (Int, Int)]
  trait TestRandomizationCapability extends RandomizationCapability {
    override protected val randomizer: Randomization = new Randomization {
      override def randomizePlayers(players: List[PlayerModelId]): List[PlayerModelId] = players
      override def randomizePlayerIds(playerIds: List[PlayerId]): List[PlayerId] = playerIds
      override def shuffleDeck(deck: Deck): Deck = deck
      override def rollDice(): (Int, Int) = rollDiceMock.apply()
    }
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
    this: GameActor with WrongStateFallback with RandomizationCapability =>
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
