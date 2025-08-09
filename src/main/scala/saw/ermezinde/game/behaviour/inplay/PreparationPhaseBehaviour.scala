package saw.ermezinde.game.behaviour.inplay

import org.apache.pekko.actor.Actor.Receive
import saw.ermezinde.game.domain.state.game.GameState

trait PreparationPhaseBehaviour {
  def preparationBehaviour(state: GameState): Receive = _ => ()

}
