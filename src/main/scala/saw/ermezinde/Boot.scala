package saw.ermezinde

import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout
import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse}
import saw.ermezinde.game.behaviour.FinishedBehaviour.GetResults
import saw.ermezinde.game.behaviour.InCountingBehaviour._
import saw.ermezinde.game.behaviour.InPreparationBehaviour.{GetReadyForInPlay, SelectMissionCard}
import saw.ermezinde.game.behaviour.NoStateBehaviour.CreateGameCommand
import saw.ermezinde.game.behaviour.NotStartedBehaviour.{PlayerJoinGame, PlayerReady, PlayerSelectColor, StartGame}
import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.card.MissionCard
import saw.ermezinde.game.domain.game.model.DiscardPhaseGameModel
import saw.ermezinde.game.domain.game.state.{DiscardPhaseGameState, InCountingGameState}
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.Color
import saw.ermezinde.game.domain.player.PlayerModel.Color.{BLUE, GREEN}

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt, MILLISECONDS}
import scala.util.{Failure, Success, Try}

object Boot extends App {
  println("####### Starting Ermezinde Server #######")

  private val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("ermezinde")

  private val testGameActor = system.actorOf(GameActor.props(GameConfig.default))
  implicit val timeout: Timeout = Timeout(100.millis)
  val duration = Duration(100, MILLISECONDS)

  def printResponse(cmd: GameActorCommand): Unit = {
    Try(Await.result(testGameActor ? cmd, duration).asInstanceOf[GameActorResponse]) match {
      case Failure(_) => println(s"Failure on cmd: $cmd")
      case Success(value) => println(value)
    }
  }

  def send(any: Any): Unit = {
    testGameActor ! any
  }


  val gameId = "123"
  val ownerId = "vicente"
  printResponse(CreateGameCommand(gameId, ownerId))

  printResponse(PlayerJoinGame("sebas"))
  printResponse(PlayerSelectColor("sebas", Color.GREEN))
  printResponse(PlayerSelectColor("vicente", Color.BLUE))

  printResponse(PlayerReady("vicente"))
  printResponse(PlayerReady("sebas"))

  printResponse(StartGame("vicente"))

  printResponse(SelectMissionCard(ownerId, 0))
  printResponse(SelectMissionCard("sebas", 0))
  printResponse(SelectMissionCard(ownerId, 0))
  printResponse(SelectMissionCard("sebas", 0))
  printResponse(SelectMissionCard(ownerId, 0))

  printResponse(GetReadyForInPlay("leonor"))
  printResponse(GetReadyForInPlay(ownerId))
  printResponse(GetReadyForInPlay(ownerId))
  printResponse(GetReadyForInPlay("sebas"))

  Thread.sleep(500)
  send("get")

  println()
  println("-------------------------------------")
  println()


  val inPlay = DiscardPhaseGameState(
    id = "1234",
    ownerId = ownerId,
    gameStartTime = Some(System.currentTimeMillis()),
    players = Map(ownerId -> BLUE, "sebas" -> GREEN),
    game = DiscardPhaseGameModel(
      GameConfig.default,
      round = 4,
      players = Map(BLUE -> PlayerModel.init(BLUE), GREEN -> PlayerModel.init(GREEN)),
      missionCards = MissionCard.defaultDeck
    )
  )

  printResponse(GetResults)

  printResponse(PlayerReadyToFinish(ownerId))
  printResponse(PlayerRevealDiscarded(ownerId))
  printResponse(PlayerRevealDiscarded(ownerId))
  printResponse(PlayerRevealDiscarded("sebas"))
  printResponse(PlayerRevealDiscarded(ownerId))

  printResponse(PlayerRevealMedals("sebas"))
  printResponse(PlayerRevealMedals(ownerId))
  printResponse(PlayerRevealMedals(ownerId))

  printResponse(PlayerRevealMissionPoints("sebas"))
  printResponse(PlayerRevealMissionPoints(ownerId))
  printResponse(PlayerRevealMissionPoints("sebas"))
  printResponse(PlayerRevealMissionPoints(ownerId))
  printResponse(PlayerRevealMissionPoints("sebas"))
  printResponse(PlayerRevealMissionPoints(ownerId))
  printResponse(PlayerRevealMissionPoints("sebas"))
  printResponse(PlayerRevealMissionPoints(ownerId))
  printResponse(PlayerRevealMissionPoints("sebas"))
  printResponse(PlayerRevealMissionPoints(ownerId))

  printResponse(PlayerRevealHand("sebas"))
  printResponse(PlayerRevealHand(ownerId))
  printResponse(PlayerRevealHand(ownerId))

  printResponse(PlayerReadyToFinish(ownerId))
  printResponse(PlayerReadyToFinish(ownerId))
  printResponse(PlayerReadyToFinish("sebas"))

  printResponse(PlayerReadyToFinish("sebas"))

  printResponse(GetResults)

}
