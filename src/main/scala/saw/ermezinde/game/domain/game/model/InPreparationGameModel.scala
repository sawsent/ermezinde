package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.card.MissionCard
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.Color.{BLUE, GREEN}
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

import scala.util.Random

object InPreparationGameModel {
  def init(players: List[PlayerModelId]): InPreparationGameModel = {
    // val playerOrdering = Random.shuffle(model.players.keys.toList)
    val playerOrdering = List(BLUE, GREEN)

    InPreparationGameModel(
      players.map(p => p -> PlayerModel.init(p)).toMap,
      playerOrdering,
      List.empty,
      Random.shuffle(MissionCard.defaultDeck),
      currentPlayerIndex = 0
    )
  }
}
case class InPreparationGameModel(
                                   players: Map[PlayerModelId, PlayerModel],
                                   playerOrdering: List[PlayerModelId],
                                   missionCards: List[MissionCard],
                                   possibleMissionCards: List[MissionCard],
                                   currentPlayerIndex: Int
                                 ) extends GameModel {

  val currentPlayerId: PlayerModelId = playerOrdering(currentPlayerIndex)

  def selectMissionCard(cardIndex: Int): InPreparationGameModel = {
    val card = possibleMissionCards(cardIndex)
    val updatedPossibleMissionCards = possibleMissionCards.filter(_ == card)
    val updatedMissionCards = card +: missionCards

    copy(
      possibleMissionCards = updatedPossibleMissionCards,
      missionCards = updatedMissionCards
    )
  }

  def nextPlayer: InPreparationGameModel = {
    copy(
      currentPlayerIndex = (currentPlayerIndex + 1) % players.toList.length
    )
  }
}
