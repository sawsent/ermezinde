package saw.ermezinde.game.domain.board

import saw.ermezinde.game.domain.board.dto.PFUseBoardPowerDTO
import saw.ermezinde.game.domain.card.Card

sealed trait PFUseBoardPower
object PFUseBoardPower {
  case class UseRotateBoardPower(rotation: BoardRotation) extends PFUseBoardPower with PFUseBoardPowerDTO
  case class UseChangeResolveOrderBoardPower(change: Int) extends PFUseBoardPower with PFUseBoardPowerDTO
  case class UseRouletteBoardPower(card: Card) extends PFUseBoardPower
}

