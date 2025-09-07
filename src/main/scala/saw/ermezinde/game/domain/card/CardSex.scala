package saw.ermezinde.game.domain.card

sealed trait CardSex {
  override def toString: String = this.getClass.getSimpleName
}
object CardSex {
  case object MALE extends CardSex
  case object FEMALE extends CardSex

  val all: List[CardSex] = List(MALE, FEMALE)
}

