package saw.ermezinde.game.domain.game.state

import saw.ermezinde.game.domain.game.model._
import saw.ermezinde.game.domain.game.state.GameActorState.{PlayerId, Timestamp}
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId

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
  def rPlayers: Map[PlayerModelId, PlayerId] = players.map(kv => kv._2 -> kv._1)
}
