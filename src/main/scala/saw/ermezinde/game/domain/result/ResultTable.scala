package saw.ermezinde.game.domain.result

import saw.ermezinde.game.domain.card.MissionCard
import saw.ermezinde.game.domain.game.GameActorState.PlayerId
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

object ResultTable {
  def fromGameResults(playerMap: Map[PlayerId, PlayerModelId], gameResults: Map[PlayerModelId, PlayerResults]): ResultTable = {
    val map = playerMap
      .map { case (playerId, playerModelId) =>
        playerId -> gameResults(playerModelId).hideAll
      }
    ResultTable(map)
  }
}
case class ResultTable(map: Map[PlayerId, PlayerResults]) {
  private def update(playerId: PlayerId, playerResults: PlayerResults => PlayerResults): ResultTable = copy(
    map = updateMap(playerId, playerResults)
  )
  private def updateMap(playerId: PlayerId, playerResults: PlayerResults => PlayerResults): Map[PlayerId, PlayerResults] = {
    map + (playerId -> playerResults(map(playerId)))
  }

  def playerRevealDiscarded(playerId: PlayerId): ResultTable = update(playerId, _.revealDiscarded)
  def playerRevealMedals(playerId: PlayerId): ResultTable = update(playerId, _.revealMedals)
  def playerRevealMissionCardPoints(playerId: PlayerId, missionCard: MissionCard): ResultTable =
    update(playerId, _.revealMissionPoints(missionCard))
  def playerRevealMissionAwards(playerId: PlayerId): ResultTable = update(playerId, _.revealMissionCardAwards)
  def playerRevealHand(playerId: PlayerId): ResultTable = update(playerId, _.revealHand)
  def playerRevealTotal(playerId: PlayerId): ResultTable = update(playerId, _.revealTotal)

  def allPlayersRevealedMission(missionCard: MissionCard): Boolean =
    map.map { case (id, results) => id -> results.missionCardPoints(missionCard)}.forall(_._2.isRevealed)


}
