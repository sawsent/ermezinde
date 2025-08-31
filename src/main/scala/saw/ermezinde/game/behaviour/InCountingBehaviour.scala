package saw.ermezinde.game.behaviour

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse, GameFailureResponse}
import saw.ermezinde.game.behaviour.InCountingBehaviour.{InCountingGameCommand, PlayerReadyToFinish, PlayerRevealDiscarded, PlayerRevealHand, PlayerRevealMedals, PlayerRevealMissionPoints, RevealCommand}
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.util.validation.EitherSyntax.toEither
import saw.ermezinde.game.domain.game.state.{FinishedGameState, GameActorState, InCountingGameState}
import saw.ermezinde.game.domain.game.state.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.state.InCountingGameState.RevealPhase.{ALL_REVEALED, REVEAL_DISCARDED, REVEAL_HAND, REVEAL_MEDALS, REVEAL_MISSIONS, RevealPhase}
import saw.ermezinde.util.logging.BehaviourLogging
import saw.ermezinde.game.validation.PlayerIdValidation.PlayerIdValidation
import saw.ermezinde.util.validation.Validate

object InCountingBehaviour {
  sealed trait InCountingGameCommand extends GameActorCommand {
    val playerId: PlayerId
  }
  sealed trait RevealCommand extends InCountingGameCommand {
    val revealPhase: RevealPhase
  }

  case class PlayerRevealDiscarded(playerId: PlayerId) extends RevealCommand {
    override val revealPhase: RevealPhase = REVEAL_DISCARDED
  }

  case class PlayerRevealMedals(playerId: PlayerId) extends RevealCommand {
    override val revealPhase: RevealPhase = REVEAL_MEDALS
  }

  case class PlayerRevealMissionPoints(playerId: PlayerId) extends RevealCommand {
    override val revealPhase: RevealPhase = REVEAL_MISSIONS
  }

  case class PlayerRevealHand(playerId: PlayerId) extends RevealCommand {
    override val revealPhase: RevealPhase = REVEAL_HAND
  }

  case class PlayerReadyToFinish(playerId: PlayerId) extends InCountingGameCommand
}
trait InCountingBehaviour extends BehaviourLogging {
  this: GameActor with WrongStateFallback =>
  private implicit val BN: String = "InCountingBehaviour"

  def inCountingBehaviour(state: GameActorState): Receive = {
    case cmd: InCountingGameCommand => state match {
      case s: InCountingGameState =>
        val response = Validate(
          cmd.playerId.notBlank,
          cmd.playerId.inGame(s)
        ).flatMap(processInCounting(s, cmd))
        sender() ! response
      case _ => fallbackWrongState(state, cmd)
    }
  }

  def processInCounting(state: InCountingGameState, cmd: InCountingGameCommand): GameActorResponse = {
    cmd match {
      case command: RevealCommand => Validate(
        state.isCorrectRevealPhase(command.revealPhase)
      ).flatMap(processRevealCommand(state, command))

      case PlayerReadyToFinish(playerId) => Validate(
        state.isCorrectRevealPhase(ALL_REVEALED),
        state.playerNotReadyToFinish(playerId)
      ).map {
        val u = state.setPlayerReadyToFinish(playerId)
        val updateState = if (u.allPlayersReadyToFinish) {
          println("Moving to FinishedGameState")
          FinishedGameState.init(u)
        } else u
        context.become(behaviour(updateState))
        s"Player $playerId is set to ready for finish"
      }
    }
  }

  def processRevealCommand(state: InCountingGameState, cmd: RevealCommand): GameActorResponse = {
    cmd match {
      case PlayerRevealDiscarded(playerId) =>
        val updateState = state.playerRevealDiscarded(playerId)
        context.become(behaviour(updateState))
        Right(s"Player $playerId revealed discarded")

      case PlayerRevealMedals(playerId) =>
        val updateState = state.playerRevealMedals(playerId)
        context.become(behaviour(updateState))
        Right(s"Player $playerId revealed medals")

      case PlayerRevealMissionPoints(playerId) =>
        val updateState = state.playerRevealMissionCardPoints(playerId)
        context.become(behaviour(updateState))
        Right(s"Player $playerId revealed")

      case PlayerRevealHand(playerId) =>
        val updateState = state.playerRevealHand(playerId)
        context.become(behaviour(updateState))
        Right(s"Player $playerId revealed discarded")
    }
  }

  implicit class InCountingStateValidations(state: InCountingGameState) {
    def isCorrectRevealPhase(revealPhase: RevealPhase): Either[GameFailureResponse, Unit] =
      (state.revealPhase == revealPhase) -> s"Game is in revealPhase ${state.revealPhase}, not $revealPhase"

    def playerNotReadyToFinish(playerId: PlayerId): Either[GameFailureResponse, Unit] =
      !state.playersReadyToFinish.contains(playerId) -> s"Player $playerId is already ready to finish."

    def allPlayersReadyToFinish: Boolean = state.playersReadyToFinish == state.players.keys.toSet
  }
}
