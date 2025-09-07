package saw.ermezinde.game.domain.game.state.inplay

import saw.ermezinde.game.domain.game.model.DiscardPhaseGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.game.state.{GameState, InCountingGameState}
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

object DiscardPhaseGameState {
  def init(state: ResolvePhaseGameState, enigmaOwner: Option[PlayerId]): DiscardPhaseGameState = DiscardPhaseGameState(
    id = state.id,
    ownerId = state.ownerId,
    gameStartTime = state.gameStartTime,
    players = state.players,
    game = DiscardPhaseGameModel.init(state.game, enigmaOwner.map(state.players(_))),
    playersDiscarded = List.empty
  )
}
case class DiscardPhaseGameState(
                                  id: String,
                                  ownerId: String,
                                  gameStartTime: Option[Timestamp],
                                  players: Map[PlayerId, PlayerModelId],
                                  game: DiscardPhaseGameModel,
                                  playersDiscarded: List[PlayerId]
                                ) extends InPlayGameState {

  def allPlayersDiscarded: Boolean = playersDiscarded.toSet == players.keys.toSet

  def playerDiscardCards(playerId: PlayerId, cards: List[String]): GameState = {
    val cardsToDiscard = game.players(players(playerId)).hand.filter(card => cards.contains(card.id))
    val updatedModel = game.playerDiscardCards(players(playerId), cardsToDiscard)

    copy(
      game = updatedModel,
      playersDiscarded = playersDiscarded :+ playerId
    ).verifications
  }

  def verifications: GameState = {
    if (allPlayersDiscarded) {
      moveToNextPhase
    } else {
      copy()
    }
  }

  def moveToNextPhase: GameState = {
    game.round match {
      case game.config.nrOfRounds => InCountingGameState.init(this)
      case _ => PreparationPhaseGameState.newRound(this)
    }
  }
}
