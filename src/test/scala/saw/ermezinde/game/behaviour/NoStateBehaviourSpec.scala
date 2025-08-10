package saw.ermezinde.game.behaviour

import org.apache.pekko.actor.{ActorRef, ActorSystem, Props}
import org.apache.pekko.pattern.ask
import org.scalatest.flatspec.AnyFlatSpecLike
import saw.ermezinde.game.GameActor
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.domain.state.game.{GameActorState, GameNoState, NotStartedGameModel, NotStartedGameState}
import org.apache.pekko.testkit.{TestKit, TestProbe}
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar.verify
import org.scalatest.matchers.should.Matchers
import saw.ermezinde.game.GameActor.{GameFailureResponse, GameSuccessResponse}
import saw.ermezinde.game.behaviour.NoStateBehaviour.CreateGameCommand
import saw.ermezinde.game.domain.state.player.PlayerModel.Color.UNSET

import scala.concurrent.Await


class NoStateBehaviourSpec extends TestKit(ActorSystem("NoStateBehaviour")) with AnyFlatSpecLike with Matchers {

  trait NoStateBehaviourFixture extends BehaviourFixture {
    class Victim() extends GameActor with NoStateBehaviour with DoNothingNotStartedBehaviour with DoNothingInPreparationBehaviour with DoNothingInPlayBehaviour
      with DoNothingInCountingBehaviour with DoNothingFinishedBehaviour with TestWrongStateFallback with TestDebugBehaviour {

      override protected def gameBehaviour(state: GameActorState): Receive = noStateBehaviour(state)
    }

    val victim: ActorRef = system.actorOf(Props(new Victim()))

  }

  "NoStateBehaviour" should "create a game with ID and OWNER" in new NoStateBehaviourFixture {
    victim ! TestDebugBehaviour.SetState(GameNoState)
    private val gameId = "102931"
    private val ownerId = "hello"

    val probe = TestProbe()

    probe.send(victim, CreateGameCommand(gameId, ownerId))
    probe.expectMsg(Right(s"Game created with gameId: $gameId and ownerId $ownerId"))

    val state: GameActorState = Await.result(victim ? TestDebugBehaviour.GetState, awaitDuration).asInstanceOf[GameActorState]

    state shouldBe NotStartedGameState(
      gameId,
      ownerId,
      players = Map(ownerId -> UNSET),
      game = NotStartedGameModel()
    )
  }

  it should "respond with failure if gameId or ownerId are blank" in new NoStateBehaviourFixture {
    victim ! TestDebugBehaviour.SetState(GameNoState)

    private val blankGameIdResponse = Await.result(victim ? CreateGameCommand("", "hello"), awaitDuration).asInstanceOf[Either[GameFailureResponse, GameSuccessResponse]]
    blankGameIdResponse shouldBe a[Left[_, _]]
    stateShouldBe(victim, GameNoState)

    private val blankOwnerIdResponse = Await.result(victim ? CreateGameCommand("hi", "  "), awaitDuration).asInstanceOf[Either[GameFailureResponse, GameSuccessResponse]]
    blankOwnerIdResponse shouldBe a[Left[_, _]]
    stateShouldBe(victim, GameNoState)
  }

  it should "use fallback wrong state when it has another state" in new NoStateBehaviourFixture {
    case object TestState extends GameActorState

    victim ! TestDebugBehaviour.SetState(TestState)
    val command = CreateGameCommand("hello", "world")

    victim ! command

    verify(mockFallback).apply(TestState, command)
    resetMocks()

  }
}
