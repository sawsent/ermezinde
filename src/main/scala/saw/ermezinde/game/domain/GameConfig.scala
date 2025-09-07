package saw.ermezinde.game.domain

import saw.ermezinde.game.domain.board.BoardInfo
import saw.ermezinde.game.domain.card.Card

object GameConfig {
  def default: GameConfig = GameConfig(
    randomizer = GameRandomizer.KeepOrder,
    nrOfMissionCards = 4,
    nrOfRounds = 4,
    cards = List.empty,
    boards = List.empty,
    enigmaPointsAwarded = 2
  )
}
case class GameConfig(
                     randomizer: GameRandomizer,
                     nrOfMissionCards: Int,
                     nrOfRounds: Int,
                     enigmaPointsAwarded: Int,
                     cards: List[Card],
                     boards: List[BoardInfo]
                     ) {
  override def toString: String =
    s"GameConfig: [missionCards: $nrOfMissionCards, cards: ${cards.length}, boards: ${boards.length}]"
}
