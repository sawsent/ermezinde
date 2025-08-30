package saw.ermezinde.game.domain.player

import PlayerModel.PlayerModelId
import saw.ermezinde.game.domain.card.Card

object PlayerModel {
  type PlayerModelId = Color

  def init(id: PlayerModelId): PlayerModel = PlayerModel(id)
}
case class PlayerModel(
                      id: PlayerModelId,
                      hand: List[Card] = List.empty,
                      discarded: List[Card] = List.empty
                      ) {
  val medalsInHand: Int = hand.map(_.medals).sum
}




