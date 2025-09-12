package saw.ermezinde.game.domain.card

object Deck {
  def empty: Deck = Deck(List.empty)
}
case class Deck(
               cards: List[Card]
               ) {
  def take(n: Int = 1): (Deck, List[Card]) = (copy(cards = cards.slice(n, cards.length)), cards.take(n))
  override def toString: String = s"Deck: [${cards.map(_.id)}]"
}
