package saw.ermezinde.util

import saw.ermezinde.game.domain.card.Deck
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.player.Color

import scala.util.Random

object Randomizer extends Deterministic {
  this: Randomization =>
}

trait Randomization {
  def randomizePlayers(players: List[PlayerModelId]): List[PlayerModelId]
  def shuffleDeck(deck: Deck): Deck

  def rollDice(): (Int, Int)
}

trait TrueRandom extends Randomization {
  override def randomizePlayers(players: List[PlayerModelId]): List[PlayerModelId] = {
    Random.shuffle(players)
  }

  override def shuffleDeck(deck: Deck): Deck = Deck(
    Random.shuffle(deck.cards)
  )

  override def rollDice(): (Int, Int) = (Random.between(1, 7), Random.between(1, 7))
}

trait Deterministic extends Randomization {
  override def shuffleDeck(deck: Deck): Deck = Deck(Random.shuffle(deck.cards))
  override def randomizePlayers(players: List[PlayerModelId]): List[PlayerModelId] = List(Color.BLUE, Color.RED)

  override def rollDice(): (Int, Int) = (1, 1)
}
