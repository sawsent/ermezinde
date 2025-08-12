package saw.ermezinde.game.domain

import saw.ermezinde.game.domain.board.Board
import saw.ermezinde.game.domain.card.Card

object GameConfig {
  def default: GameConfig = GameConfig(
    nrOfMissionCards = 4,
    cards = List.empty,
    boards = List.empty
  )
}
case class GameConfig(
                     nrOfMissionCards: Int,
                     cards: List[Card],
                     boards: List[Board]
                     ) {
  override def toString: String = s"GameConfig: [missionCards: $nrOfMissionCards, cards: ${cards.map(_.id)}, boards: ${boards.map(_.id)}]"
}
