package saw.ermezinde.game.domain.game.state

import saw.ermezinde.game.domain.game.model._
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.game.state.InCountingGameState.RevealPhase.{ALL_REVEALED, REVEAL_DISCARDED, REVEAL_HAND, REVEAL_MEDALS, REVEAL_MISSIONS, RevealPhase}
import saw.ermezinde.game.domain.game.state.NotStartedGameState.NotStartedPlayerModel
import saw.ermezinde.game.domain.player.PlayerModel.{Color, PlayerModelId}
import saw.ermezinde.game.domain.result.ResultTable

object GameActorState {
  type PlayerId = String
  type Timestamp = Long
}
trait GameActorState {
  val id: String
}
case object GameNoState extends GameActorState {
  override val id: PlayerId = ""
}

sealed trait GameState extends GameActorState {
  val id: String
  val ownerId: String
  val gameStartTime: Option[Timestamp]
  val players: Map[PlayerId, PlayerModelId]
  val game: GameModel
}

object NotStartedGameState {
  case class NotStartedPlayerModel(color: Color, ready: Boolean)
}
case class NotStartedGameState(
                                id: String,
                                ownerId: String,
                                waitingPlayers: Map[PlayerId, NotStartedPlayerModel],
                                game: NotStartedGameModel
                              ) extends GameState {
  override val players: Map[PlayerId, PlayerModelId] = waitingPlayers.map { case (playerId, playerModel) => playerId -> playerModel.color}
  override val gameStartTime: Option[Timestamp] = None
}

object InPreparationGameState {
  def init(state: NotStartedGameState, startTime: Timestamp): InPreparationGameState = {
    InPreparationGameState(
      id = state.id,
      ownerId = state.ownerId,
      gameStartTime = Some(startTime),
      players = state.players,
      game = InPreparationGameModel.init(state.game, state.players.values.toList),
      playersReady = Set.empty
    )
  }
}
case class InPreparationGameState(
                                   id: String,
                                   ownerId: String,
                                   gameStartTime: Option[Timestamp],
                                   players: Map[PlayerId, PlayerModelId],
                                   playersReady: Set[PlayerId],
                                   game: InPreparationGameModel
                                 ) extends GameState {
  val currentPlayer: PlayerId = players.find { case (_, playerModelId) => game.currentPlayerId == playerModelId}.map(_._1).get
  def moveToInPlayIfReady: GameState = if (playersReady == players.keys.toSet) InPlayGameState.init(this) else this
}

object InPlayGameState {
  def init(state: InPreparationGameState): InPlayGameState = PreparationPhaseGameState(
    id = state.id,
    ownerId = state.ownerId,
    gameStartTime = state.gameStartTime,
    players = state.players,
    game = InPlayGameModel.init(state.game)
  )
}
sealed trait InPlayGameState extends GameState {
  override val game: InPlayGameModel
}
case class PreparationPhaseGameState(
                                      id: String,
                                      ownerId: String,
                                      gameStartTime: Option[Timestamp],
                                      players: Map[PlayerId, PlayerModelId],
                                      game: PreparationPhaseGameModel
                                    ) extends InPlayGameState

case class PlacePhaseGameState(
                                id: String,
                                ownerId: String,
                                gameStartTime: Option[Timestamp],
                                players: Map[PlayerId, PlayerModelId],
                                game: PlacePhaseGameModel
                              ) extends InPlayGameState

case class ResolvePhaseGameState(
                                  id: String,
                                  ownerId: String,
                                  gameStartTime: Option[Timestamp],
                                  players: Map[PlayerId, PlayerModelId],
                                  game: ResolvePhaseGameModel
                                ) extends InPlayGameState

case class DiscardPhaseGameState(
                                  id: String,
                                  ownerId: String,
                                  gameStartTime: Option[Timestamp],
                                  players: Map[PlayerId, PlayerModelId],
                                  game: DiscardPhaseGameModel
                                ) extends InPlayGameState


object InCountingGameState {
  object RevealPhase {
    type RevealPhase = String
    val REVEAL_DISCARDED: RevealPhase = "REVEAL_DISCARDED"
    val REVEAL_MEDALS: RevealPhase = "REVEAL_MEDALS"
    val REVEAL_MISSIONS: RevealPhase = "REVEAL_MISSIONS"
    val REVEAL_HAND: RevealPhase = "REVEAL_HAND"
    val ALL_REVEALED: RevealPhase = "ALL_REVEALED"
  }

  def init(state: InPlayGameState): InCountingGameState = {
    val updateGame: InCountingGameModel = InCountingGameModel.init(state.game)
    val resultTable = ResultTable.fromGameResults(state.players, updateGame.result)
    InCountingGameState(
      id = state.id,
      ownerId = state.ownerId,
      gameStartTime = state.gameStartTime,
      players = state.players,
      game = updateGame,
      resultTable,
      revealPhase = REVEAL_DISCARDED,
      playersReadyToFinish = Set.empty,
      currentResolvingMissionCardIndex = 0
    )
  }
}
case class InCountingGameState(
                                id: String,
                                ownerId: String,
                                gameStartTime: Option[Timestamp],
                                players: Map[PlayerId, PlayerModelId],
                                game: InCountingGameModel,
                                resultTable: ResultTable,
                                revealPhase: RevealPhase,
                                playersReadyToFinish: Set[PlayerId],
                                currentResolvingMissionCardIndex: Int
                              ) extends GameState {

  def setPlayerReadyToFinish(playerId: PlayerId): InCountingGameState = copy(
    playersReadyToFinish = playersReadyToFinish + playerId
  )

  def nextPhase: InCountingGameState = {
    val nextPhase = revealPhase match {
      case REVEAL_DISCARDED => REVEAL_MEDALS
      case REVEAL_MEDALS => REVEAL_MISSIONS
      case REVEAL_MISSIONS => REVEAL_HAND
      case REVEAL_HAND => ALL_REVEALED
      case ALL_REVEALED => ALL_REVEALED
    }
    copy(revealPhase = nextPhase)
  }
  def isResolvingLastMission: Boolean = currentResolvingMissionCardIndex == game.missionCards.length - 1

  def playerRevealDiscarded(playerId: PlayerId): InCountingGameState = {
    val u = copy(resultTable = resultTable.playerRevealDiscarded(playerId))
    if (u.resultTable.map.forall(results => results._2.discardedAmount.isRevealed)) {
      u.nextPhase
    } else u
  }

  def playerRevealMedals(playerId: PlayerId): InCountingGameState = {
    val u = copy(resultTable = resultTable.playerRevealMedals(playerId))
    if (u.resultTable.map.forall(results => results._2.medals.isRevealed)) {
      u.nextPhase
    } else u
  }

  def playerRevealMissionCardPoints(playerId: PlayerId): InCountingGameState = {
    val currentMission = game.missionCards(currentResolvingMissionCardIndex)
    val u = copy(resultTable = resultTable.playerRevealMissionCardPoints(playerId, currentMission))
    if (u.resultTable.allPlayersRevealedMission(currentMission)) {
      if (u.isResolvingLastMission) {
        u.revealAllPlayersMissionAwards
      } else {
        u.copy(currentResolvingMissionCardIndex = currentResolvingMissionCardIndex + 1)
      }
    } else u
  }

  def revealAllPlayersMissionAwards: InCountingGameState = {
    players
      .keys
      .foldLeft(this)((state, player) => state.playerRevealMissionAwards(player))
      .nextPhase
  }

  def playerRevealMissionAwards(playerId: PlayerId): InCountingGameState = copy(resultTable = resultTable.playerRevealMissionAwards(playerId))

  def playerRevealHand(playerId: PlayerId): InCountingGameState = {
    val u = copy(resultTable = resultTable.playerRevealHand(playerId).playerRevealTotal(playerId))
    if (u.resultTable.map.forall(results => results._2.total.isRevealed)) {
      u.nextPhase
    } else u
  }
}

object FinishedGameState {
  def init(state: InCountingGameState): FinishedGameState = {
    val endTime = System.currentTimeMillis()
    FinishedGameState(
      id = state.id,
      ownerId = state.ownerId,
      gameStartTime = state.gameStartTime,
      gameEndTime = Some(endTime),
      players = state.players,
      results = state.resultTable,
      game = FinishedGameModel.init(state.game)
    )
  }
}
case class FinishedGameState(
                              id: String,
                              ownerId: String,
                              gameStartTime: Option[Timestamp],
                              gameEndTime: Option[Timestamp],
                              players: Map[PlayerId, PlayerModelId],
                              results: ResultTable,
                              game: FinishedGameModel
                            ) extends GameState {
  val gameDurationMillis: Option[Long] = gameStartTime.flatMap(start => gameEndTime.map(end => end - start))
}


