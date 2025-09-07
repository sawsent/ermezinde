package saw.ermezinde.game.domain.board

object BoardPosition {
  case object TL extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(TM, BL)
  }
  case object TM extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(TL, TR, BM)
  }
  case object TR extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(TM, BR)
  }
  case object BL extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(TL, BM)
  }
  case object BM extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(BL, BR, TM)
  }
  case object BR extends BoardPosition {
    override val adjacents: List[BoardPosition] = List(BM, TR)
  }

  def all: Set[BoardPosition] = Set(TL, TM, TR, BL, BM, BR)
}
sealed trait BoardPosition {
  val adjacents: List[BoardPosition]
}

