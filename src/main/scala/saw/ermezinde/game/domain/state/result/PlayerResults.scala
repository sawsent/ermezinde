package saw.ermezinde.game.domain.state.result

import saw.ermezinde.game.domain.state.card.MissionCard

object PlayerResults {
  def hidden(discardedAmount: Int, medals: Int, missionCardPoints: Map[MissionCard, Int], missionCardAwards: Int): PlayerResults =
    PlayerResults(
      Hidden(discardedAmount),
      Hidden(medals),
      missionCardPoints.map { case (mc, i) => mc -> Hidden(i) },
      Hidden(missionCardAwards),
      Hidden(discardedAmount + medals + missionCardAwards)
    )
}

case class PlayerResults(
                        discardedAmount: Result,
                        medals: Result,
                        missionCardPoints: Map[MissionCard, Result],
                        missionCardAwards: Result,
                        total: Result
                        ) {
  val hideAll: PlayerResults = copy(
    discardedAmount.hide,
    medals.hide,
    missionCardPoints.map(kv => kv._1 -> kv._2.hide),
    missionCardAwards.hide,
    total.hide
  )
}
