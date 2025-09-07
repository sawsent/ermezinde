package saw.ermezinde.game.domain

import saw.ermezinde.game.domain.board.BoardPosition
import saw.ermezinde.game.domain.card.Deck
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

object GameRandomizer {
  object KeepOrder extends GameRandomizer {
    override def randomizePlayers(players: List[PlayerModelId]): List[PlayerModelId] = players
    override def shuffleDeck(deck: Deck): Deck = deck
    override def randomTablePosition: BoardPosition = BoardPosition.TL
  }
}
trait GameRandomizer {
  def randomizePlayers(players: List[PlayerModelId]): List[PlayerModelId]
  def shuffleDeck(deck: Deck): Deck
  def randomTablePosition: BoardPosition
}
