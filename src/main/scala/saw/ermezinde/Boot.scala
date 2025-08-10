package saw.ermezinde

import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.ActorSystem
import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.DebugSetState
import saw.ermezinde.game.behaviour.FinishedBehaviour.FinishedGameCommand
import saw.ermezinde.game.behaviour.InCountingBehaviour.InCountingGameCommand
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.InPreparationBehaviour.InPreparationGameCommand
import saw.ermezinde.game.behaviour.NotStartedBehaviour.NotStartedGameCommand
import saw.ermezinde.game.domain.state.game.{NotStartedGameModel, NotStartedGameState}

object Boot extends App {
  println("####### Starting Ermezinde Server #######")

  private val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("ermezinde")

  private val testGameActor = system.actorOf(GameActor.props)

  private val gamestate = NotStartedGameState(game = NotStartedGameModel(Set.empty))

  // testGameActor ! DebugSetState(gamestate)

  testGameActor ! new NotStartedGameCommand {}
  testGameActor ! new InPreparationGameCommand {}
  testGameActor ! new InPlayGameCommand {}
  testGameActor ! new InCountingGameCommand {}
  testGameActor ! new FinishedGameCommand {}

}
