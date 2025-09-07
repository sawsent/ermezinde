package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.behaviour.inplay.PlacePhaseBehaviour.PlacePhaseCommand
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state.GameState
import saw.ermezinde.game.domain.game.state.inplay.PlacePhaseGameState

object PlacePhaseBehaviour {
  sealed trait PlacePhaseCommand extends InPlayGameCommand

  case class PlayerPlaceCard(playerId: PlayerId, cardId: String)
}
trait PlacePhaseBehaviour {
  this: GameActor with WrongStateFallback =>
  private implicit val BN: String = "PlacePhaseBehaviour"
  def placeBehaviour(state: GameState, cmd: PlacePhaseCommand): GameActorResponse = state match {
    case state: PlacePhaseGameState => processPlace(state, cmd)
    case _ => fallbackWrongStateWithReply(state, cmd)
  }

  def processPlace(state: PlacePhaseGameState, cmd: PlacePhaseCommand): GameActorResponse = cmd match {
    case _ => Left("Not Implemented")

  }

}
