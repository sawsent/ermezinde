package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.behaviour.inplay.DiscardPhaseBehaviour.{DiscardPhaseCommand, PlayerDiscardCards}
import saw.ermezinde.game.domain.card.Card
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state.GameState
import saw.ermezinde.game.domain.game.state.inplay.DiscardPhaseGameState
import saw.ermezinde.game.domain.player.PlayerModel
import saw.ermezinde.util.validation.Validate
import saw.ermezinde.util.validation.EitherSyntax.toEither
import math.min

object DiscardPhaseBehaviour {
  sealed trait DiscardPhaseCommand extends InPlayGameCommand
  case class PlayerDiscardCards(playerId: PlayerId, cards: List[String]) extends DiscardPhaseCommand
}
trait DiscardPhaseBehaviour {
  this: GameActor with WrongStateFallback =>
  private implicit val BN: String = "DiscardPhaseBehaviour"
  def discardBehaviour(state: GameState, cmd: DiscardPhaseCommand): GameActorResponse = (state, cmd) match {
    case (s: DiscardPhaseGameState, c: PlayerDiscardCards) => val stx = DiscardPhaseGameStateValidations(s, c); import stx._
      Validate(
        c.cards.forall(cId => hand.exists(c => c.id == cId))  -> s"Player ${c.playerId} doesn't have all the cards he's trying to discard",
        !playersDiscarded.contains(c.playerId)                -> s"Player ${c.playerId} has already discarded",
        (min(cardsInHand, 6) == cardsInHand - c.cards.length) -> s"Player ${c.playerId} with $cardsInHand cards must discard ${math.max(cardsInHand - 6, 0)} cards"
      ).map {
        val updatedState = s.playerDiscardCards(c.playerId, c.cards)
        context.become(behaviour(updatedState))
        s"Player ${c.playerId} discarded cards ${c.cards}"
      }
    case _ => fallbackWrongStateWithReply(state, cmd)
  }

  private case class DiscardPhaseGameStateValidations(state: DiscardPhaseGameState, cmd: PlayerDiscardCards) {
    private val playerModel: PlayerModel = state.game.players(state.players(cmd.playerId))
    val hand: List[Card] = playerModel.hand
    val cardsInHand: Int = hand.length
    val playersDiscarded: List[PlayerId] = state.playersDiscarded
  }
}
