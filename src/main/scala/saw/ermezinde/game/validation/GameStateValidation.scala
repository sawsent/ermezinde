package saw.ermezinde.game.validation

import saw.ermezinde.game.GameActor.GameFailureResponse
import saw.ermezinde.game.domain.game.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.GameState
import saw.ermezinde.game.domain.player.PlayerModel.Color
import saw.ermezinde.game.domain.player.PlayerModel.Color.UNSET

object GameStateValidation {
  implicit class StateValidation(state: GameState) {
    def colorIsNotSelected(color: Color): Either[GameFailureResponse, Unit] =
      Either.cond(!state.players.values.toList.contains(color), (), s"Color ${color.toString} is already selected")


  }


}
