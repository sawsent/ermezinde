package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.inplay.PreparationPhaseBehaviour.PreparationPhaseCommand
import saw.ermezinde.game.domain.game.state.PreparationPhaseGameState

object PreparationPhaseBehaviour {
  sealed trait PreparationPhaseCommand extends InPlayGameCommand


}
trait PreparationPhaseBehaviour {
  def preparationBehaviour(state: PreparationPhaseGameState, cmd: PreparationPhaseCommand): GameActorResponse =
    Left("Not Implemented")

}
