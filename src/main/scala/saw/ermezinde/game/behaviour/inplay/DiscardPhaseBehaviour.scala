package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.inplay.DiscardPhaseBehaviour.DiscardPhaseCommand
import saw.ermezinde.game.domain.game.state.GameState

object DiscardPhaseBehaviour {
  sealed trait DiscardPhaseCommand extends InPlayGameCommand
}
trait DiscardPhaseBehaviour {
  def discardBehaviour(state: GameState, cmd: DiscardPhaseCommand): GameActorResponse =
    Left("Not Implemented")
}
