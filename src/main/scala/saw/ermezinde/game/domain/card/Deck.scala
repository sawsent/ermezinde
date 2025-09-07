package saw.ermezinde.game.domain.card

object Deck {
  def empty: Deck = Deck(List.empty)
}
case class Deck(
               cards: List[Card]
               ) {
  override def toString: String = s"Deck: [${cards.map(_.id)}]"
}
