package saw.ermezinde.game.domain.slot

import saw.ermezinde.game.domain.card.Card
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

object PFSlot {
  def fromInfo(info: SlotInfo): PFSlot = info match {
    case i: NormalSlotInfo => createNormal(i)
    case i: PrizeSlotInfo => createPrize(i)
  }

  def createNormal(info: NormalSlotInfo): PFNormalSlot = PFNormalSlot(
    slotInfo = info,
    card = None,
    placedBy = None
  )

  def createPrize(info: PrizeSlotInfo): PFPrizeSlot = PFPrizeSlot(
    slotInfo = info,
    cards = List.empty
  )
}
sealed trait PFSlot {
  val slotInfo: SlotInfo
  val isPrize: Boolean
  def place(card: Card): PFSlot
  def setPlacedBy(playerModelId: PlayerModelId): PFSlot
}
case class PFNormalSlot(
                         slotInfo: NormalSlotInfo,
                         card: Option[Card],
                         placedBy: Option[PlayerModelId]
                       ) extends PFSlot {
  override val isPrize: Boolean = false

  def place(card: Card): PFNormalSlot = copy(
    card = Some(card),
  )

  override def setPlacedBy(playerModelId: PlayerModelId): PFSlot = copy(
    placedBy = Some(playerModelId)
  )
}
case class PFPrizeSlot(
                        slotInfo: PrizeSlotInfo,
                        cards: List[Card]
                      ) extends PFSlot {
  override val isPrize: Boolean = true

  override def place(card: Card): PFSlot = copy(
    cards = cards :+ card
  )

  override def setPlacedBy(playerModelId: PlayerModelId): PFSlot = copy()
}