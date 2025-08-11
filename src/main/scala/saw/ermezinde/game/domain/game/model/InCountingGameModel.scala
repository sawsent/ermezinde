package saw.ermezinde.game.domain.game.model

import saw.ermezinde.game.domain.card.MissionCard
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.result.PlayerResults

import scala.math.ceil

object InCountingGameModel {

  def init(model: InPlayGameModel): InCountingGameModel = {
    InCountingGameModel(
      players = model.players,
      missionCards = model.missionCards
    )
  }
}
case class InCountingGameModel(
                                players: Map[PlayerModelId, PlayerModel],
                                missionCards: List[MissionCard]
                              ) extends GameModel {
  val result: Map[PlayerModelId, PlayerResults] = {
    val missionCardWinners = missionCards.map(missionCard => {
        missionCard -> players.map { case (id, player) =>
          (id, missionCard.pointsInHand(player.hand))
        }
      }).toMap
      .map { case (missionCard, playerToResultMap) =>
        val topScore = playerToResultMap.values.max
        missionCard -> playerToResultMap.filter(_._2 >= topScore)
      }
      .map { case (missionCard, playerToTopResults) =>
        val toAward = ceil(config.medalsPerMissionCard / playerToTopResults.toList.length).toInt
        missionCard -> playerToTopResults.map { case (id, _) =>
          id -> toAward
        }
      }

    players.map { case (id, model) =>
      id -> PlayerResults.hidden(
        discardedAmount = model.discarded.length,
        medals = model.medalsInHand,
        missionCardPoints = missionCards.map(mc => mc -> mc.pointsInHand(model.hand)).toMap,
        missionCardAwards = missionCards.map(mc => missionCardWinners(mc).getOrElse(id, 0)).sum,
        model.hand
      )
    }
  }
}
