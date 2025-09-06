package saw.ermezinde.game.domain.game.state.inplay

import saw.ermezinde.game.domain.game.model.InPlayGameModel
import saw.ermezinde.game.domain.game.state.{GameState, InPreparationGameState}

object InPlayGameState {
  def init(state: InPreparationGameState): InPlayGameState = BoardSelectionGameState(
    id = state.id,
    ownerId = state.ownerId,
    gameStartTime = state.gameStartTime,
    players = state.players,
    game = InPlayGameModel.init(state.game)
  )
}
trait InPlayGameState extends GameState {
  override val game: InPlayGameModel
}





