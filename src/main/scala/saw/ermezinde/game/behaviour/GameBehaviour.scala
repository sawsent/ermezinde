package saw.ermezinde.game.behaviour

import saw.ermezinde.util.logging.BehaviourLogging

trait GameBehaviour extends BehaviourLogging {
  protected implicit val BehaviourName: String
}
