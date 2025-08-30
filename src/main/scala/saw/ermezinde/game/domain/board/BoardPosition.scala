package saw.ermezinde.game.domain.board

object BoardPosition {
  case object TOP_LEFT extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(TOP_MIDDLE, BOTTOM_LEFT)
  }
  case object TOP_MIDDLE extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(TOP_LEFT, TOP_RIGHT, BOTTOM_MIDDLE)
  }
  case object TOP_RIGHT extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(TOP_MIDDLE, BOTTOM_RIGHT)
  }
  case object BOTTOM_LEFT extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(TOP_LEFT, BOTTOM_MIDDLE)
  }
  case object BOTTOM_MIDDLE extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(BOTTOM_LEFT, BOTTOM_RIGHT, TOP_MIDDLE)
  }
  case object BOTTOM_RIGHT extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(BOTTOM_MIDDLE, TOP_RIGHT)
  }

  object X {
    def fromString(str: String): X = str match {
      case "LEFT" => LEFT
      case "MIDDLE" => MIDDLE
      case "RIGHT" => RIGHT
    }
  }
  trait X
  case object LEFT extends X
  case object MIDDLE extends X
  case object RIGHT extends X

  object Y {
    def fromString(str: String): Y = str match {
      case "TOP" => TOP
      case "BOTTOM" => BOTTOM
    }
  }
  trait Y
  case object TOP extends Y
  case object BOTTOM extends Y

  def apply(x: X, y: Y): BoardPosition = (y, x) match {
    case (BOTTOM, LEFT) => BOTTOM_LEFT
    case (BOTTOM, MIDDLE) => BOTTOM_MIDDLE
    case (BOTTOM, RIGHT) => BOTTOM_RIGHT
    case (TOP, LEFT) => TOP_LEFT
    case (TOP, MIDDLE) => TOP_MIDDLE
    case (TOP, RIGHT) => TOP_RIGHT
  }

  def all: Set[BoardPosition] = Set(TOP_LEFT, TOP_MIDDLE, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_MIDDLE, BOTTOM_RIGHT)
}
sealed trait BoardPosition {
  val adjacents: List[BoardPosition]
}

