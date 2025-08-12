package saw.ermezinde.game.domain.card

sealed trait CardSex {
  override def toString: String = this.getClass.getSimpleName
}
case object MALE extends CardSex
case object FEMALE extends CardSex

