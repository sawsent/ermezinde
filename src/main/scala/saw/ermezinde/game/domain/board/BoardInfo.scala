package saw.ermezinde.game.domain.board

import saw.ermezinde.game.domain.slot.SlotInfo

import scala.util.Random

case class BoardInfo(
                      id: String,
                      resolveOrderNumber: ResolveOrderNumberType,
                      placePhaseBoardPower: Option[PlacePhaseBoardPower],
                      resolvePhaseBoardPower: Option[ResolvePhaseBoardPower],
                      slots: List[SlotInfo]
                    )

sealed trait ResolveOrderNumberType {
  def get: Int
}
object ResolveOrderNumberType {
  case class PreSet(n: Int) extends ResolveOrderNumberType {
    override def get: Int = n
  }
  case class ViaDice(min: Int, max: Int) extends ResolveOrderNumberType {
    override def get: Int = Random.between(min, max + 1)
  }
}