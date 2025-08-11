package saw.ermezinde.game.behaviour

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse, GameFailureResponse}
import saw.ermezinde.game.behaviour.NotStartedBehaviour._
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state.NotStartedGameState.NotStartedPlayerModel
import saw.ermezinde.game.domain.game.state.{GameActorState, InPreparationGameState, NotStartedGameState}
import saw.ermezinde.game.domain.player.PlayerModel.Color
import saw.ermezinde.game.domain.player.PlayerModel.Color.UNSET
import saw.ermezinde.game.syntax.Validate
import saw.ermezinde.game.validation.GameStateValidation.StateValidation
import saw.ermezinde.game.validation.PlayerIdValidation.PlayerIdValidation
import saw.ermezinde.util.logging.BehaviourLogging

object NotStartedBehaviour {
  sealed trait NotStartedGameCommand extends GameActorCommand {
    val playerId: PlayerId
  }

  case class PlayerJoinGame(playerId: PlayerId) extends NotStartedGameCommand
  case class PlayerLeaveGame(playerId: PlayerId) extends NotStartedGameCommand

  case class PlayerSelectColor(playerId: PlayerId, color: Color) extends NotStartedGameCommand
  case class PlayerUnselectColor(playerId: PlayerId) extends NotStartedGameCommand

  case class PlayerReady(playerId: PlayerId) extends NotStartedGameCommand
  case class PlayerUnready(playerId: PlayerId) extends NotStartedGameCommand

  case class StartGame(playerId: PlayerId) extends NotStartedGameCommand
}

trait NotStartedBehaviour extends BehaviourLogging {
  this: GameActor with WrongStateFallback =>
  private implicit val BN: String = "NotStartedBehaviour"

  def notStartedBehaviour(state: GameActorState): Receive = {
    case cmd: NotStartedGameCommand => state match {
      case s: NotStartedGameState =>
        val response: GameActorResponse = Validate(
          cmd.playerId.notBlank,
        ).flatMap(
          processNotStarted(s, cmd)
        )
        sender() ! response

      case s: GameActorState => fallbackWrongState(s, cmd)
    }
  }

  def processNotStarted(game: NotStartedGameState, cmd: NotStartedGameCommand): GameActorResponse = {
    cmd match {
      case PlayerJoinGame(playerId) => Validate(
        playerId.notInGame(game),
        game.isNotFull
      ).map {
        val updatedState = game.copy(
          waitingPlayers = game.waitingPlayers + (playerId -> NotStartedPlayerModel(UNSET, ready = false))
        )
        context.become(behaviour(updatedState))
        s"Player $playerId joined the game"
      }

      case PlayerLeaveGame(playerId) => Validate(
        playerId.inGame(game)
      ).map {
        val updatedState = game.copy(
          waitingPlayers = game.waitingPlayers - playerId
        )
        context.become(behaviour(updatedState))
        s"Player $playerId joined the game"
      }

      case PlayerSelectColor(playerId, color) => Validate(
        playerId.inGame(game),
        game.colorIsNotSelected(color)
      ).map {
        val updatedState = game.copy(
          waitingPlayers = game.waitingPlayers + (playerId -> game.waitingPlayers(playerId).copy(color = color))
        )
        context.become(behaviour(updatedState))
        s"Player $playerId selected color: $color"
      }

      case PlayerUnselectColor(playerId) => Validate(
        playerId.inGame(game),
        game.playerHasColorSelected(playerId),
        game.playerIsNotReady(playerId)
      ).map {
        val updatedState = game.copy(
          waitingPlayers = game.waitingPlayers + (playerId -> game.waitingPlayers(playerId).copy(color = UNSET))
        )
        context.become(behaviour(updatedState))
        s"Player $playerId has unselected their color."
      }

      case PlayerReady(playerId) => Validate(
        playerId.inGame(game),
        game.playerHasColorSelected(playerId),
        game.playerIsNotReady(playerId)
      ).map {
        val updatedState = game.copy(
          waitingPlayers = game.waitingPlayers + (playerId -> game.waitingPlayers(playerId).copy(ready = true))
        )
        context.become(behaviour(updatedState))
        s"Player $playerId is ready"
      }

      case PlayerUnready(playerId) => Validate(
        playerId.inGame(game),
        game.playerHasColorSelected(playerId),
        game.playerIsReady(playerId)
      ).map {
        val updatedState = game.copy(
          waitingPlayers = game.waitingPlayers + (playerId -> game.waitingPlayers(playerId).copy(ready = false))
        )
        context.become(behaviour(updatedState))
        s"Player $playerId is not ready"
      }

      case StartGame(playerId) => Validate(
        playerId.isOwner(game),
        game.allPlayersReady
      ).map {
        val startedTimestamp = System.currentTimeMillis()

        logger.info(s"Starting game ${game.id}")
        val updatedState = InPreparationGameState.init(game, startedTimestamp)
        context.become(behaviour(updatedState))

        s"Game (${game.id}) started"
      }
    }
  }

  implicit class NotStartedStateValidation(state: NotStartedGameState) {
    def playerHasColorSelected(playerId: PlayerId): Either[GameFailureResponse, Unit] =
      Either.cond(state.waitingPlayers(playerId).color != UNSET, (), s"Player $playerId does not have a selected color")

    def playerIsNotReady(playerId: PlayerId): Either[GameFailureResponse, Unit] =
      Either.cond(state.waitingPlayers.get(playerId).exists(!_.ready), (), s"Player $playerId is not ready")

    def playerIsReady(playerId: PlayerId): Either[GameFailureResponse, Unit] =
      Either.cond(state.waitingPlayers.get(playerId).exists(_.ready), (), s"Player $playerId is ready")

    def allPlayersReady: Either[GameFailureResponse, Unit] =
      Either.cond(state.waitingPlayers.values.toList.forall(_.ready), (), "Not all players are ready")

    def isNotFull: Either[GameFailureResponse, Unit] =
      Either.cond(state.players.toList.length <= 4, (), "Game is full")
  }
}
