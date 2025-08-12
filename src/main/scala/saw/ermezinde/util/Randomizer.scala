package saw.ermezinde.util

import saw.ermezinde.game.domain.card.Deck
import saw.ermezinde.game.domain.player.PlayerModel.{Color, PlayerModelId}

import scala.util.Random

object Randomizer {
  def randomizePlayers(players: List[PlayerModelId]): List[PlayerModelId] = {
    // Random.shuffle(players)
    List(Color.BLUE, Color.RED)
  }

  def shuffleDeck(deck: Deck): Deck = Deck(
    Random.shuffle(deck.cards)
  )

}
