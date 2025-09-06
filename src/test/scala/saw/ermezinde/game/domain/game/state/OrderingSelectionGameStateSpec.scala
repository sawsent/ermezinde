package saw.ermezinde.game.domain.game.state

import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.flatspec.AnyFlatSpecLike
import saw.ermezinde.game.domain.GameConfig
import saw.ermezinde.game.domain.card.Deck
import saw.ermezinde.game.domain.game.model.PreparationPhaseGameModel
import saw.ermezinde.game.domain.game.state.inplay.{BoardSelectionGameState, OrderingSelectionGameState}
import saw.ermezinde.game.domain.player.Color.{BLUE, GREEN, RED, YELLOW}
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.table.PreparationPhaseTableModel
import saw.ermezinde.util.Randomization

class OrderingSelectionGameStateSpec extends AnyFlatSpecLike {

  "OrderingSelectionGameState" should "attributeSpots" in {
    val randomizerMock = mock[Randomization]
    when(randomizerMock.rollDice()).thenReturn((1, 2))

    val result = new OrderingSelectionGameState(
      underlying = BoardSelectionGameState("test", "test", None, Map("t1" -> RED, "t2" -> BLUE, "t3" -> GREEN, "t4" -> YELLOW), PreparationPhaseGameModel(GameConfig.default, round = 0, players = Map(RED -> PlayerModel.init(RED), BLUE -> PlayerModel.init(BLUE)), playerOrdering = List(RED, BLUE), currentPlayerIndex = 0, availableBoards = List.empty, missionCards = List.empty, deck = Deck(List.empty), table = PreparationPhaseTableModel(Map.empty))),
      currentPlayerIndex = 0,
      playersContesting = List("t1", "t2", "t3", "t4"),
      contestingForSpots = 0,
      diceRolls = List.empty,
      results = Map.empty,
      currentRoundDiceRolls = Map.empty,
      currentRollRound = 0
    )
      .playerRollDice("t1", (1, 1)).asInstanceOf[OrderingSelectionGameState]
      .playerRollDice("t2", (1, 1)).asInstanceOf[OrderingSelectionGameState]
      .playerRollDice("t3", (2, 2)).asInstanceOf[OrderingSelectionGameState]
      .playerRollDice("t4", (2, 2)).asInstanceOf[OrderingSelectionGameState]
      .playerRollDice("t1", (6, 6)).asInstanceOf[OrderingSelectionGameState]
      .playerRollDice("t2", (6, 5)).asInstanceOf[OrderingSelectionGameState]
      .playerRollDice("t3", (1, 2)).asInstanceOf[OrderingSelectionGameState]
      .playerRollDice("t4", (2, 2))

    println(result)

  }

}
