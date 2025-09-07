package saw.ermezinde.game.behaviour.inplay

import org.apache.pekko.actor.{ActorRef, ActorSystem}
import org.apache.pekko.testkit.TestKit
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameFailureResponse, GameSuccessResponse}
import saw.ermezinde.game.behaviour.fixture.{BehaviourFixture, CardCreatorHelper}
import saw.ermezinde.game.behaviour.inplay.DiscardPhaseBehaviour.PlayerDiscardCards
import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.card.Deck
import saw.ermezinde.game.domain.game.model.DiscardPhaseGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state.InCountingGameState
import saw.ermezinde.game.domain.game.state.inplay.{DiscardPhaseGameState, PreparationPhaseGameState}
import saw.ermezinde.game.domain.player.Color.{BLUE, GREEN, RED}
import saw.ermezinde.game.domain.player.{Color, PlayerModel}
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.util.Randomization

class DiscardPhaseBehaviourSpec extends TestKit(ActorSystem("DiscardPhaseBehaviour")) with AnyFlatSpecLike with Matchers {
  trait DiscardPhaseBehaviourFixture extends BehaviourFixture with CardCreatorHelper {
    val gameConfig: GameConfig = GameConfig.default.copy(
      nrOfRounds = 4
    )
    val randomizer: Randomization = new Randomization {
      override def randomizePlayers(players: List[PlayerModelId]): List[PlayerModelId] = players
      override def randomizePlayerIds(playerIds: List[PlayerId]): List[PlayerId] = playerIds
      override def shuffleDeck(deck: Deck): Deck = deck
      override def rollDice(): (Int, Int) = (1, 1)
    }

    implicit val victim: ActorRef = system.actorOf(GameActor.props(gameConfig, randomizer))

    def getPopulatedModel(id: Color, cardsInHand: Int = 0, cardsDiscarded: Int = 0): PlayerModel = PlayerModel(
      id = id,
      hand = (1 to cardsInHand).toList.map(_ => CardCreator.randomCard),
      discarded = (1 to cardsDiscarded).toList.map(_ => CardCreator.randomCard),
    )

    val playerModels: Map[Color, PlayerModel] = Map(
      BLUE -> getPopulatedModel(BLUE, cardsInHand = 8),
      RED -> getPopulatedModel(RED, cardsInHand = 6),
      GREEN -> getPopulatedModel(GREEN, cardsInHand = 4)
    )

    def stateWithRound(round: Int): DiscardPhaseGameState = DiscardPhaseGameState(
      id = "",
      ownerId = "",
      gameStartTime = None,
      players = Map("p1" -> BLUE, "p2" -> RED, "p3" -> GREEN),
      game = DiscardPhaseGameModel(
        config = gameConfig,
        round = round,
        players = playerModels,
        enigmaOwner = None,
        missionCards = List.empty,
        deck = Deck.empty,
      ),
      playersDiscarded = List.empty,
    )

  }

  "DiscardPhaseBehaviour" should "work as expected for last round and move to in counting once done" in new DiscardPhaseBehaviourFixture {
    private val state = stateWithRound(4)
    send(state)

    askActor(PlayerDiscardCards("p1", List.empty)) shouldBe a[Left[GameFailureResponse, GameSuccessResponse]]
    askActor(PlayerDiscardCards("p1", List("1", "2", "3"))) shouldBe a[Left[GameFailureResponse, GameSuccessResponse]]
    askActor(PlayerDiscardCards("p1", List("100", "2", "3"))) shouldBe a[Left[GameFailureResponse, GameSuccessResponse]]
    askActor(PlayerDiscardCards("p1", List("0", "2"))) shouldBe a[Right[GameFailureResponse, GameSuccessResponse]]

    private val uState = askActor("get").asInstanceOf[DiscardPhaseGameState]
    uState.playersDiscarded shouldBe List("p1")
    uState.game.players(BLUE).hand.map(_.id) shouldBe List("1", "3", "4", "5", "6", "7")
    uState.game.players(BLUE).discarded.map(_.id) shouldBe List("0", "2")

    askActor(PlayerDiscardCards("p2", List("8"))) shouldBe a[Left[GameFailureResponse, GameSuccessResponse]]
    askActor(PlayerDiscardCards("p3", List("14"))) shouldBe a[Left[GameFailureResponse, GameSuccessResponse]]
    askActor(PlayerDiscardCards("p2", List.empty)) shouldBe a[Right[GameFailureResponse, GameSuccessResponse]]
    askActor(PlayerDiscardCards("p3", List.empty)) shouldBe a[Right[GameFailureResponse, GameSuccessResponse]]

    askActor("get") shouldBe a[InCountingGameState]
  }

  it should "work as expected for other rounds and move to in preparation once done" in new DiscardPhaseBehaviourFixture {
    send(stateWithRound(2))

    askActor(PlayerDiscardCards("p1", List("0", "2"))) shouldBe a[Right[GameFailureResponse, GameSuccessResponse]]
    askActor(PlayerDiscardCards("p2", List.empty)) shouldBe a[Right[GameFailureResponse, GameSuccessResponse]]
    askActor(PlayerDiscardCards("p3", List.empty)) shouldBe a[Right[GameFailureResponse, GameSuccessResponse]]

    private val s = askActor("get")
    s shouldBe a[PreparationPhaseGameState]
    private val state = s.asInstanceOf[PreparationPhaseGameState]

    state.game.round shouldBe 3
  }
}
