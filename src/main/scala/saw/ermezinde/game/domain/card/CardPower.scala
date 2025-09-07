package saw.ermezinde.game.domain.card

sealed trait CardPower {
  override def toString: String = this.getClass.getSimpleName
}
object CardPower {
  object PISTOL extends CardPower
  object SEDUCTION extends CardPower
  object NATIONALISM extends CardPower
  object IMMUNITY extends CardPower
  object CONSPIRE extends CardPower
  object DOUBLE_AGENT extends CardPower

  val all: List[CardPower] = List(PISTOL, SEDUCTION, NATIONALISM, IMMUNITY, CONSPIRE, DOUBLE_AGENT)
}


