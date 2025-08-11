package saw.ermezinde.game.domain.state.player

import saw.ermezinde.game.domain.state.card.Card
import saw.ermezinde.game.domain.state.player.PlayerModel.PlayerModelId

object PlayerModel {
  type PlayerModelId = Color

  sealed trait Color {
    override def toString: String = this.getClass.getSimpleName
  }
  object Color {
    case object UNSET extends Color
    case object RED extends Color
    case object GREEN extends Color
    case object BLUE extends Color
    case object YELLOW extends Color
  }

  def init(id: PlayerModelId): PlayerModel = PlayerModel(id)
}
case class PlayerModel(
                      id: PlayerModelId,
                      hand: List[Card] = List.empty,
                      discarded: List[Card] = List.empty
                      ) {
  val medalsInHand: Int = hand.map(_.medals).sum
}




