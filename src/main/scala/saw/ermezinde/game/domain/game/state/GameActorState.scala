package saw.ermezinde.game.domain.game.state

import saw.ermezinde.game.domain.board.BoardPosition
import saw.ermezinde.game.domain.game.model._
import saw.ermezinde.game.domain.game.state.GameActorState.{DiceRoll, PlayerId, Timestamp}
import saw.ermezinde.game.domain.game.state.InCountingGameState.RevealPhase._
import saw.ermezinde.game.domain.game.state.NotStartedGameState.NotStartedPlayerModel
import saw.ermezinde.game.domain.player.Color
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.result.ResultTable
import saw.ermezinde.game.syntax.DiceRollSyntax.DiceRollSyntax
import saw.ermezinde.util.Randomizer

import scala.math.pow

object GameActorState {
  type PlayerId = String
  type Timestamp = Long
  type DiceRoll = (Int, Int)
}
trait GameActorState {
  val id: String
}
case object GameNoState extends GameActorState {
  override val id: PlayerId = ""
}

trait GameState extends GameActorState {
  val id: String
  val ownerId: String
  val gameStartTime: Option[Timestamp]
  val players: Map[PlayerId, PlayerModelId]
  val game: GameModel
}
