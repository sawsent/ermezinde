package saw.ermezinde.game.domain

object GameConfig {
  def default: GameConfig = GameConfig(
    nrOfMissionCards = 4,
    medalsPerMissionCard = 6
  )
}
case class GameConfig(
                     nrOfMissionCards: Int,
                     medalsPerMissionCard: Int
                     )
