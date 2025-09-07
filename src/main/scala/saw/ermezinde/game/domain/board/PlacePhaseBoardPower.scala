package saw.ermezinde.game.domain.board

import PFUseBoardPower.{UseChangeResolveOrderBoardPower, UseRotateBoardPower, UseRouletteBoardPower}
import saw.ermezinde.game.domain.card.Card


sealed trait PlacePhaseBoardPower {
  def use(dto: PFUseBoardPower, on: PFBoard): PFBoard
  def usesDto(dto: PFUseBoardPower): Boolean
}
object PlacePhaseBoardPower {
  case object RotateBoard extends PlacePhaseBoardPower {
    override def use(dto: PFUseBoardPower, on: PFBoard): PFBoard = on.rotate(dto.asInstanceOf[UseRotateBoardPower].rotation)

    override def usesDto(dto: PFUseBoardPower): Boolean = dto.isInstanceOf[UseRotateBoardPower]
  }

  case class Roulette(
                       cards: List[Card]
                     ) extends PlacePhaseBoardPower {
    override def use(dto: PFUseBoardPower, on: PFBoard): PFBoard = on.copy(
      power = Some(copy(cards = cards :+ dto.asInstanceOf[UseRouletteBoardPower].card))
    )

    override def usesDto(dto: PFUseBoardPower): Boolean = dto.isInstanceOf[UseRouletteBoardPower]
  }
  case object ChangeResolveOrderNumber extends PlacePhaseBoardPower {
    override def use(dto: PFUseBoardPower, on: PFBoard): PFBoard = on.copy(
      resolveOrderNumber = on.resolveOrderNumber + dto.asInstanceOf[UseChangeResolveOrderBoardPower].change
    )

    override def usesDto(dto: PFUseBoardPower): Boolean = dto.isInstanceOf[UseChangeResolveOrderBoardPower]
  }
}

