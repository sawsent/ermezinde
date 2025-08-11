package saw.ermezinde.game.domain.game

sealed trait GamePhase {
  val isPreparation: Boolean = false
  val isPlace: Boolean = false
  val isResolve: Boolean = false
  val isDiscard: Boolean = false
}
object GamePhase {
  case object PREPARATION extends GamePhase {
    override val isPreparation: Boolean = true
  }
  case object PLACE extends GamePhase {
    override val isPlace: Boolean = true
  }
  case object RESOLVE extends GamePhase {
    override val isResolve: Boolean = true
  }
  case object DISCARD extends GamePhase {
    override val isDiscard: Boolean = true
  }
}