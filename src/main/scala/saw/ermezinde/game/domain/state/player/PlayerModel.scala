package saw.ermezinde.game.domain.state.player

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
}
case class PlayerModel(
                      id: PlayerModelId
                      )




