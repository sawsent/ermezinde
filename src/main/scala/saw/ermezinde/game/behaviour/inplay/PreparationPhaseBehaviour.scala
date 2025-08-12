package saw.ermezinde.game.behaviour.inplay

import saw.ermezinde.game.GameActor.GameActorResponse
import saw.ermezinde.game.behaviour.InPlayBehaviour.InPlayGameCommand
import saw.ermezinde.game.behaviour.inplay.PreparationPhaseBehaviour.PreparationPhaseCommand
import saw.ermezinde.game.domain.board.{BoardPosition, BoardRotation}
import saw.ermezinde.game.domain.game.model.PreparationPhaseGameModel
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state.PreparationPhaseGameState

object PreparationPhaseBehaviour {
  sealed trait PreparationPhaseCommand extends InPlayGameCommand {
    val action: PreparationPhaseGameModel.PreparationAction
  }

  case class SelectBoard(playerId: PlayerId, boardIndex: Int, boardPosition: BoardPosition, boardRotation: BoardRotation)


}
trait PreparationPhaseBehaviour {
  def preparationBehaviour(state: PreparationPhaseGameState, cmd: PreparationPhaseCommand): GameActorResponse =
    Left("Not Implemented")

}
