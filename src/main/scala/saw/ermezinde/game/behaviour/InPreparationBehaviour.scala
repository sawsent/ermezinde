package saw.ermezinde.game.behaviour

import saw.ermezinde.game.GameActor
import saw.ermezinde.game.GameActor.{GameActorCommand, GameActorResponse, GameFailureResponse}
import saw.ermezinde.game.behaviour.InPreparationBehaviour.{GetReadyForInPlay, InPreparationGameCommand, SelectMissionCard}
import saw.ermezinde.game.behaviour.fallback.WrongStateFallback
import saw.ermezinde.game.domain.state.game.GameActorState.PlayerId
import saw.ermezinde.game.domain.state.game.{GameActorState, InPreparationGameState}
import saw.ermezinde.game.syntax.Validate
import saw.ermezinde.game.validation.PlayerIdValidation.PlayerIdValidation
import saw.ermezinde.util.logging.BehaviourLogging


object InPreparationBehaviour {
  sealed trait InPreparationGameCommand extends GameActorCommand {
    val playerId: PlayerId
  }

  case class SelectMissionCard(playerId: PlayerId, cardIndex: Int) extends InPreparationGameCommand
  case class GetReadyForInPlay(playerId: PlayerId) extends InPreparationGameCommand
}
trait InPreparationBehaviour extends BehaviourLogging {
  this: GameActor with WrongStateFallback =>
  private implicit val BN: String = "InPreparationBehaviour"

  def inPreparationBehaviour(state: GameActorState): Receive = {
    case cmd: InPreparationGameCommand => state match {
      case s: InPreparationGameState =>
        val response = Validate(
          cmd.playerId.notBlank,
          cmd.playerId.inGame(s)
        ).flatMap {
          processInPreparation(s, cmd)
        }
        sender() ! response
      case _: GameActorState => fallbackWrongState(state, cmd)
    }
  }

  def processInPreparation(state: InPreparationGameState, cmd: InPreparationGameCommand): GameActorResponse = {
    cmd match {
      case SelectMissionCard(playerId, cardIndex) => Validate(
        state.isCardIndexAllowed(cardIndex),
        state.notAllMissionCardsSelected,
        state.isPlayerMove(playerId)
      ).map {
        val updatedState = state.copy(
          game = state.game
            .selectMissionCard(cardIndex)
            .nextPlayer
        )
        context.become(behaviour(updatedState))
        s"Player $playerId selected missionCard ${updatedState.game.missionCards.head}"
      }

      case GetReadyForInPlay(playerId) => Validate(
        state.allMissionCardsSelected,
        state.playerNotReady(playerId)
      ).map {
        val updatedState = state.copy(
          playersReady = state.playersReady + playerId
        )
        context.become(behaviour(updatedState))
        if (updatedState.allPlayersReady) moveToInPlay(updatedState)

        s"Player $playerId is ready for inPlay"
      }
    }
  }

  private def moveToInPlay(state: InPreparationGameState): Unit = {
    println(s"Game ${state.id} moving to InPlay")
  }

  implicit class InPreparationStateValidation(state: InPreparationGameState) {
    def isCardIndexAllowed(cardIndex: Int): Either[GameFailureResponse, Unit] =
      Either.cond(
        state.game.possibleMissionCards.length > cardIndex && cardIndex >= 0,
        (),
        "Card not available"
      )

    def notAllMissionCardsSelected: Either[GameFailureResponse, Unit] =
      Either.cond(state.game.missionCards.length < 4, (), "4 mission cards are already selected")

    def allMissionCardsSelected: Either[GameFailureResponse, Unit] =
      Either.cond(state.game.missionCards.length == 4, (), "Not all mission cards are selected")

    def isPlayerMove(playerId: PlayerId): Either[GameFailureResponse, Unit] =
      Either.cond(state.currentPlayer == playerId, (), s"It's not $playerId's move")

    def playerNotReady(playerId: PlayerId): Either[GameFailureResponse, Unit] =
      Either.cond(!state.playersReady.contains(playerId), (), s"Player $playerId's move is already ready")

    def allPlayersReady: Boolean = state.playersReady == state.players.keys.toSet
  }

}