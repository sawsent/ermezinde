package saw.ermezinde.game.domain.result

import saw.ermezinde.game.domain.card.{Card, MissionCard}

object PlayerResults {
  def hidden(discardedAmount: Int, medals: Int, missionCardPoints: Map[MissionCard, Int], missionCardAwards: Int, hand: List[Card]): PlayerResults =
    PlayerResults(
      Hidden(discardedAmount),
      Hidden(medals),
      missionCardPoints.map { case (mc, i) => mc -> Hidden(i) },
      Hidden(missionCardAwards),
      Hidden(discardedAmount + medals + missionCardAwards),
      Hidden(hand)
    )
}

case class PlayerResults(
                        discardedAmount: Result[Int],
                        medals: Result[Int],
                        missionCardPoints: Map[MissionCard, Result[Int]],
                        missionCardAwards: Result[Int],
                        total: Result[Int],
                        hand: Result[List[Card]]
                        ) {
  def hideAll: PlayerResults = copy(
    discardedAmount.hide,
    medals.hide,
    missionCardPoints.map(kv => kv._1 -> kv._2.hide),
    missionCardAwards.hide,
    total.hide
  )

  def revealDiscarded: PlayerResults = copy(discardedAmount = discardedAmount.reveal)
  def revealMedals: PlayerResults = copy(medals = medals.reveal)
  def revealMissionPoints(missionCard: MissionCard): PlayerResults = copy(
      missionCardPoints = missionCardPoints + (missionCard -> missionCardPoints(missionCard).reveal)
    )
  def revealMissionCardAwards: PlayerResults = copy(missionCardAwards = missionCardAwards.reveal)
  def revealHand: PlayerResults = copy(hand = hand.reveal)
  def revealTotal: PlayerResults = copy(total = total.reveal)

  val runningTotal: Int = discardedAmount.getOrElse(0) + medals.getOrElse(0) + missionCardAwards.getOrElse(0)
}
