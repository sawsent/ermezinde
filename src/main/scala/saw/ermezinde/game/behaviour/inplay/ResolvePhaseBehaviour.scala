package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.inplay.ResolvePhaseBehaviour.ResolvePhaseCommand
import saw.ermezinde.game.domain.game.state.GameState

object ResolvePhaseBehaviour {
  sealed trait ResolvePhaseCommand extends InPlayGameCommand
}
trait ResolvePhaseBehaviour {
  def resolveBehaviour(state: GameState, cmd: ResolvePhaseCommand): GameActorResponse =
    Left("Not Implemented")

}
