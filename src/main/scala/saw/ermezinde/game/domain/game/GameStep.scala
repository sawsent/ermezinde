package saw.ermezinde.game.domain.game

sealed trait GameStep {
  val isNotStarted: Boolean = false
  val isPreparation: Boolean = false
  val isInPlay: Boolean = false
  val isCounting: Boolean = false
  val isFinished: Boolean = false
}
object GameStep {
  case object NOT_STARTED extends GameStep {
    override val isNotStarted: Boolean = true
  }
  case object PREPARATION extends GameStep {
    override val isPreparation: Boolean = true
  }
  case object IN_PLAY extends GameStep {
    override val isInPlay: Boolean = true
  }
  case object COUNTING extends GameStep {
    override val isCounting: Boolean = true
  }
  case object FINISHED extends GameStep {
    override val isFinished: Boolean = true
  }
}


