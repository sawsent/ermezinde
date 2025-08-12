package saw.ermezinde.game.domain.card

case class Card(
               id: String,
               strength: Int,
               nationality: CardNationality,
               powers: List[CardPower],
               sex: CardSex,
               medals: Int
               ) {
  override def toString: String =
    s"{$id, $strength, $powers, $sex, $medals}"
}
