package saw.ermezinde.game.domain

import saw.ermezinde.game.domain.board.Board
import saw.ermezinde.game.domain.card.Card

object GameConfig {
  def default: GameConfig = GameConfig(
    nrOfMissionCards = 4,
    medalsPerMissionCard = 6,
    cards = List.empty,
    boards = List.empty
  )
}
case class GameConfig(
                     nrOfMissionCards: Int,
                     medalsPerMissionCard: Int,
                     cards: List[Card],
                     boards: List[Board]
                     )
