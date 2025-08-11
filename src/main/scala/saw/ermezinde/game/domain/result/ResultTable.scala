package saw.ermezinde.game.domain.result

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
case class ResultTable(map: Map[PlayerId, PlayerResults])
