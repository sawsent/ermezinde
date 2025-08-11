package saw.ermezinde.game.behaviour

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse}
import saw.ermezinde.game.behaviour.InCountingBehaviour.InCountingGameCommand
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.domain.game.GameActorState.PlayerId
import saw.ermezinde.game.domain.game.{GameActorState, InCountingGameState}
import saw.ermezinde.game.syntax.Validate
import saw.ermezinde.util.logging.BehaviourLogging
import saw.ermezinde.game.validation.PlayerIdValidation.PlayerIdValidation

object InCountingBehaviour {
  sealed trait InCountingGameCommand extends GameActorCommand {
    val playerId: PlayerId
  }
}
trait InCountingBehaviour extends BehaviourLogging {
  this: GameActor with WrongStateFallback =>
  private implicit val BN: String = "InCountingBehaviour"

  def inCountingBehaviour(state: GameActorState): Receive = {
    case cmd: InCountingGameCommand => state match {
      case s: InCountingGameState =>
        val response = Validate(
          cmd.playerId.inGame(s)
        ).flatMap(processInCounting(s, cmd))
        sender() ! response
      case _ => fallbackWrongState(state, cmd)
    }
      processInCounting(state, cmd)
  }

  def processInCounting(state: GameActorState, cmd: InCountingGameCommand): GameActorResponse = {
    logger.debug(BN || s"Processing Command $cmd")
    Left("Not Implemented")
  }
}
