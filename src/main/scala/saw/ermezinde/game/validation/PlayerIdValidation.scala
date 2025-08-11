package saw.ermezinde.game.validation

import saw.ermezinde.game.GameActor.GameFailureResponse
import saw.ermezinde.game.domain.game.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.GameState

object PlayerIdValidation {
  implicit class PlayerIdValidation(playerId: PlayerId) {
    def notBlank: Either[GameFailureResponse, Unit] =
      Either.cond(!playerId.isBlank, (), "PlayerId cannot be blank.")

    def inGame(state: GameState): Either[GameFailureResponse, Unit] =
      Either.cond(state.players.contains(playerId), (), s"Player $playerId is not in the game")

    def notInGame(state: GameState): Either[GameFailureResponse, Unit] =
      Either.cond(!state.players.contains(playerId), (), s"Player $playerId is already in the game")

    def isOwner(state: GameState): Either[GameFailureResponse, Unit] =
      Either.cond(state.ownerId == playerId, (), s"Player $playerId is not game owner")
  }
}
