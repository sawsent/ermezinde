package saw.ermezinde.game.domain.state.game

import saw.ermezinde.game.domain.state.GameConfig
import saw.ermezinde.game.domain.state.board.{PlacePhaseBoardModel, PreparationPhaseBoardModel, ResolvePhaseBoardModel}
import saw.ermezinde.game.domain.state.card.MissionCard
import saw.ermezinde.game.domain.state.player.PlayerModel
import saw.ermezinde.game.domain.state.player.PlayerModel.Color.{BLUE, GREEN}
import saw.ermezinde.game.domain.state.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.state.result.PlayerResults

import scala.math.ceil
import scala.util.Random

sealed trait GameModel {
  val players: Map[PlayerModelId, PlayerModel]
  val config: GameConfig = GameConfig.default
}

case class NotStartedGameModel() extends GameModel {
  override val players: Map[PlayerModelId, PlayerModel] = Map.empty
}

object InPreparationGameModel {
  def init(players: List[PlayerModelId]): InPreparationGameModel = {
    // val playerOrdering = Random.shuffle(model.players.keys.toList)
    val playerOrdering = List(BLUE, GREEN)

    InPreparationGameModel(
      players.map(p => p -> PlayerModel.init(p)).toMap,
      playerOrdering,
      List.empty,
      Random.shuffle(MissionCard.defaultDeck),
      currentPlayerIndex = 0
    )
  }
}
case class InPreparationGameModel(
                                   players: Map[PlayerModelId, PlayerModel],
                                   playerOrdering: List[PlayerModelId],
                                   missionCards: List[MissionCard],
                                   possibleMissionCards: List[MissionCard],
                                   currentPlayerIndex: Int
                                 ) extends GameModel {

  val currentPlayerId: PlayerModelId = playerOrdering(currentPlayerIndex)

  def selectMissionCard(cardIndex: Int): InPreparationGameModel = {
    val card = possibleMissionCards(cardIndex)
    val updatedPossibleMissionCards = possibleMissionCards.filter(_ == card)
    val updatedMissionCards = card +: missionCards

    copy(
      possibleMissionCards = updatedPossibleMissionCards,
      missionCards = updatedMissionCards
    )
  }

  def nextPlayer: InPreparationGameModel = {
    copy(
      currentPlayerIndex = (currentPlayerIndex + 1) % players.toList.length
    )
  }
}

sealed trait InPlayGameModel extends GameModel {
  override val players: Map[PlayerModelId, PlayerModel]

  val phase: GamePhase
  val round: Int
  val missionCards: List[MissionCard]
}
case class PreparationPhaseGameModel(
                                      round: Int,
                                      players: Map[PlayerModelId, PlayerModel],
                                      missionCards: List[MissionCard],
                                      diceRolls: Map[PlayerModel, Int],
                                      board: PreparationPhaseBoardModel
                                    ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PREPARATION
}

case class PlacePhaseGameModel(
                                round: Int,
                                players: Map[PlayerModelId, PlayerModel],
                                missionCards: List[MissionCard],
                                turn: Int,
                                currentPlayer: PlayerModel,
                                board: PlacePhaseBoardModel
                              ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.PLACE
}

case class ResolvePhaseGameModel(
                                  round: Int,
                                  players: Map[PlayerModelId, PlayerModel],
                                  missionCards: List[MissionCard],
                                  board: ResolvePhaseBoardModel
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.RESOLVE
}

case class DiscardPhaseGameModel(
                                  round: Int,
                                  players: Map[PlayerModelId, PlayerModel],
                                  missionCards: List[MissionCard],
                                ) extends InPlayGameModel {
  override val phase: GamePhase = GamePhase.DISCARD
}


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
        val toAward = ceil(config.medalsPerMissionCard / playerToTopResults.values.toList.length).toInt
        missionCard -> playerToTopResults.map { case (id, _) =>
          id -> toAward
        }
      }

    players.map { case (id, model) =>
      id -> PlayerResults.hidden(
        discardedAmount = model.discarded.length,
        medals = model.medalsInHand,
        missionCardPoints = missionCards.map(mc => mc -> mc.pointsInHand(model.hand)).toMap,
        missionCardAwards = missionCards.map(mc => missionCardWinners(mc).getOrElse(id, 0)).sum
      )
    }


  }
}

case class FinishedGameModel(
                              players: Map[PlayerModelId, PlayerModel],
                              missionCards: List[MissionCard]
                            ) extends GameModel
