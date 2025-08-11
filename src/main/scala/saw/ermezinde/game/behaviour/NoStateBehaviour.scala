package saw.ermezinde.game.behaviour

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse}
import saw.ermezinde.game.behaviour.NoStateBehaviour.CreateGameCommand
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.domain.game.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.{GameActorState, GameNoState, NotStartedGameState}
import saw.ermezinde.game.domain.game.NotStartedGameState.NotStartedPlayerModel
import saw.ermezinde.game.domain.game.model.NotStartedGameModel
import saw.ermezinde.game.domain.player.PlayerModel.Color
import saw.ermezinde.util.logging.BehaviourLogging

object NoStateBehaviour {
  sealed trait NoStateGameCommand extends GameActorCommand
  case class CreateGameCommand(id: String, ownerId: PlayerId) extends NoStateGameCommand

}
trait NoStateBehaviour extends BehaviourLogging {
  this: GameActor with WrongStateFallback =>

  private implicit val BehaviourName: String = "NoStateBehaviour"

  def noStateBehaviour(state: GameActorState): Receive = {
    case cmd @ CreateGameCommand(id, ownerId) => state match {
      case GameNoState =>
        val response = createGame(id, ownerId)
        sender() ! response
      case state: GameActorState => fallbackWrongState(state, cmd)
    }
  }

  def createGame(gameId: String, gameOwnerId: PlayerId): GameActorResponse = {
    Either.cond(!(gameId.isBlank || gameOwnerId.isBlank), (gameId, gameOwnerId), "GameId or GameOwnerId cannot be blank")
      .map { case (gameId, ownerId) =>
        val model = NotStartedGameModel()
        val updatedState = NotStartedGameState(
          gameId,
          ownerId,
          Map(gameOwnerId -> NotStartedPlayerModel(Color.UNSET, ready = false)),
          model
        )
        context.become(behaviour(updatedState))
        s"Game created with gameId: $gameId and ownerId $gameOwnerId"
      }
  }

}
