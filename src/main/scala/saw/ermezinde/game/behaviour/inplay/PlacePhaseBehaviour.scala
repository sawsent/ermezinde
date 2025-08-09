package saw.ermezinde.game.behaviour.inplay

import org.apache.pekko.actor.Actor.Receive
import saw.ermezinde.game.domain.state.game.GameState

trait PlacePhaseBehaviour {
  def placeBehaviour(state: GameState): Receive = _ => ()

}
