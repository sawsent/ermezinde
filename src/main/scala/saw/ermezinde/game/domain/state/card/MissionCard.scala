package saw.ermezinde.game.domain.state.card

object MissionCard {
  def defaultDeck: List[MissionCard] = List(new MissionCard {}, new MissionCard {}, new MissionCard {}, new MissionCard {})
}
trait MissionCard
