package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.inplay.PlacePhaseBehaviour.PlacePhaseCommand
import saw.ermezinde.game.domain.game.state.GameState

object PlacePhaseBehaviour {
  sealed trait PlacePhaseCommand extends InPlayGameCommand
}
trait PlacePhaseBehaviour {
  def placeBehaviour(state: GameState, cmd: PlacePhaseCommand): GameActorResponse =
    Left("Not Implemented")

}
