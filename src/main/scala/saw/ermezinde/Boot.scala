package saw.ermezinde

import com.typesafe.config.ConfigFactory
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.pattern.ask
import org.apache.pekko.util.Timeout
import saw.ermezinde.adapter.config.{Config2Board, Config2Card}
import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse}
import saw.ermezinde.game.behaviour.FinishedBehaviour.GetResults
import saw.ermezinde.game.behaviour.InCountingBehaviour._
import saw.ermezinde.game.behaviour.InPreparationBehaviour.{GetReadyForInPlay, SelectMissionCard}
import saw.ermezinde.game.behaviour.NoStateBehaviour.CreateGameCommand
import saw.ermezinde.game.behaviour.NotStartedBehaviour.{PlayerJoinGame, PlayerReady, PlayerSelectColor, StartGame}
import saw.ermezinde.game.behaviour.inplay.PreparationPhaseBehaviour.SelectBoard
import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.board.{BoardPosition, BoardRotation}
import saw.ermezinde.game.domain.card.MissionCard
import saw.ermezinde.game.domain.game.model.DiscardPhaseGameModel
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.Color
import saw.ermezinde.game.domain.player.Color.{BLUE, RED}
import saw.ermezinde.util.logging.Logging

import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt, MILLISECONDS}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.{Failure, Success, Try}

object Boot extends App with Logging {
  println("####### Starting Ermezinde Server #######")

  private val config = ConfigFactory.load()

  implicit val system: ActorSystem = ActorSystem("ermezinde")

  val deck = config.getConfigList("deck").asScala.toList.map(cardConfig => cardConfig.getString("id") -> Config2Card.fromConfig(cardConfig)).map { case (id, cardTry) =>
    cardTry match {
      case Failure(exception) =>
        logger.info(s"Failed to convert card: $id with exception $exception")
        Failure(exception)
      case Success(value) => Success(value)
    }
  }.filter(_.isSuccess).map(_.toOption.get)

  val boardDeck = config.getConfigList("boards").asScala.toList.map(boardConfig => boardConfig.getString("id") -> Config2Board.fromConfig(boardConfig)).map { case (id, boardTry) =>
    boardTry match {
      case Failure(exception) =>
        logger.info(s"Failed to convert board $id with exception $exception")
        Failure(exception)
      case Success(value) => Success(value)
    }
  }.filter(_.isSuccess).map(_.toOption.get)


  private val defaultGameConfig = GameConfig(
    nrOfMissionCards = 4,
    cards = deck,
    boards = boardDeck
  )

  private val testGameActor = system.actorOf(GameActor.props(defaultGameConfig))
  implicit val timeout: Timeout = Timeout(200.millis)
  val duration = Duration(200, MILLISECONDS)

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
  printResponse(PlayerSelectColor("sebas", Color.RED))
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

  printResponse(SelectBoard(ownerId, 6, BoardPosition.BOTTOM_RIGHT, BoardRotation.`0`))
  printResponse(SelectBoard("sebas", 0, BoardPosition.BOTTOM_MIDDLE, BoardRotation.`0`))
  printResponse(SelectBoard(ownerId, 4, BoardPosition.BOTTOM_LEFT, BoardRotation.`0`))
  printResponse(SelectBoard("sebas", 0, BoardPosition.TOP_LEFT, BoardRotation.`0`))
  printResponse(SelectBoard(ownerId, 0, BoardPosition.TOP_MIDDLE, BoardRotation.`0`))
  printResponse(SelectBoard("sebas", 0, BoardPosition.TOP_RIGHT, BoardRotation.`0`))
  printResponse(SelectBoard("sebas", 0, BoardPosition.TOP_RIGHT, BoardRotation.`0`))

  send("get")

  Thread.sleep(1000)
  /*
  println()
  println("-------------------------------------")
  println()


  val inPlay = DiscardPhaseGameState(
    id = "1234",
    ownerId = ownerId,
    gameStartTime = Some(System.currentTimeMillis()),
    players = Map(ownerId -> BLUE, "sebas" -> RED),
    game = DiscardPhaseGameModel(
      GameConfig.default,
      round = 4,
      players = Map(BLUE -> PlayerModel.init(BLUE), RED -> PlayerModel.init(RED)),
      missionCards = MissionCard.defaultDeck
    )
  )

  send(inPlay)

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


   */
}
