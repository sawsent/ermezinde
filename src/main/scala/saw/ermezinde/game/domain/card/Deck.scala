package saw.ermezinde.game.domain.card

case class Deck(
               cards: List[Card]
               ) {
  override def toString: String = s"Deck: [${cards.map(_.id)}]"
}
