package saw.ermezinde.game.syntax

import saw.ermezinde.game.domain.game.state.GameActorState.DiceRoll

object DiceRollSyntax {
  implicit class DiceRollSyntax(diceRoll: DiceRoll) {
    val value: Int = diceRoll._1 + diceRoll._2
  }

}
