package saw.ermezinde.game.domain.card

sealed trait CardNationality {
  override def toString: String = this.getClass.getSimpleName
}

case object PT extends CardNationality
case object US extends CardNationality
case object UK extends CardNationality
case object IT extends CardNationality
case object DE extends CardNationality
case object YU extends CardNationality
case object SU extends CardNationality
case object ES extends CardNationality
case object FR extends CardNationality
case object HU extends CardNationality
case object JP extends CardNationality
case object PL extends CardNationality
case object RO extends CardNationality
case object SE extends CardNationality
