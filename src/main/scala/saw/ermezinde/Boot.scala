package saw.ermezinde

import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout
import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse}
import saw.ermezinde.game.behaviour.NoStateBehaviour.CreateGameCommand
import saw.ermezinde.game.behaviour.NotStartedBehaviour.{PlayerJoinGame, PlayerReady, PlayerSelectColor}
import saw.ermezinde.game.domain.state.player.PlayerModel.Color

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt, SECONDS}

object Boot extends App {
  println("####### Starting Ermezinde Server #######")

  private val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("ermezinde")

  private val testGameActor = system.actorOf(GameActor.props)
  implicit val timeout = Timeout(1.seconds)
  val duration = Duration(1, SECONDS)

  def printResponse(cmd: GameActorCommand): Unit = {
    val response = Await.result(testGameActor ? cmd, duration).asInstanceOf[GameActorResponse]
    println(response)
  }


  val gameId = "123"
  val ownerId = "vicente"
  printResponse(CreateGameCommand(gameId, ownerId))

  printResponse(PlayerJoinGame("sebas"))
  printResponse(PlayerSelectColor("sebas", Color.GREEN))
  printResponse(PlayerSelectColor("vicente", Color.BLUE))

  printResponse(PlayerReady("vicente"))
  printResponse(PlayerReady("sebas"))

  printResponse(PlayerJoinGame("leonor"))
  printResponse(PlayerSelectColor("leonor", Color.GREEN))
  printResponse(PlayerReady("leonor"))

}
