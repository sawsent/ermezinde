package saw.ermezinde.game.domain.game.state

import saw.ermezinde.game.domain.game.model.InCountingGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.game.state.InCountingGameState.RevealPhase._
import saw.ermezinde.game.domain.game.state.inplay.{DiscardPhaseGameState, InPlayGameState}
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.result.ResultTable


object InCountingGameState {
  object RevealPhase {
    type RevealPhase = String
    val REVEAL_DISCARDED: RevealPhase = "REVEAL_DISCARDED"
    val REVEAL_MEDALS: RevealPhase = "REVEAL_MEDALS"
    val REVEAL_MISSIONS: RevealPhase = "REVEAL_MISSIONS"
    val REVEAL_HAND: RevealPhase = "REVEAL_HAND"
    val ALL_REVEALED: RevealPhase = "ALL_REVEALED"
  }

  def init(state: DiscardPhaseGameState): InCountingGameState = {
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
