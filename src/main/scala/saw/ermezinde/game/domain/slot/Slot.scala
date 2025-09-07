package saw.ermezinde.game.domain.slot

object PFSlot {
  def fromInfo(info: SlotInfo): PFSlot = info match {
    case i: NormalSlotInfo => createNormal(i)
    case i: PrizeSlotInfo => createPrize(i)
  }

  def createNormal(info: NormalSlotInfo): PFNormalSlot = PFNormalSlot(
    slotInfo = info
  )

  def createPrize(info: PrizeSlotInfo): PFPrizeSlot = PFPrizeSlot(
    slotInfo = info
  )
}
sealed trait PFSlot {
  val slotInfo: SlotInfo
  val isPrize: Boolean
}
case class PFNormalSlot(
                     slotInfo: NormalSlotInfo
                     ) extends PFSlot {
  override val isPrize: Boolean = false
}
case class PFPrizeSlot(
                    slotInfo: PrizeSlotInfo
                    ) extends PFSlot {
  override val isPrize: Boolean = true
}