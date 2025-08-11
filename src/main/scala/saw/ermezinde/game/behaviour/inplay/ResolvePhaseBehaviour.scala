package saw.ermezinde.game.behaviour.inplay

import org.apache.pekko.actor.Actor.Receive
import saw.ermezinde.game.domain.game.GameState

trait ResolvePhaseBehaviour {
  def resolveBehaviour(state: GameState): Receive = _ => ()

}
