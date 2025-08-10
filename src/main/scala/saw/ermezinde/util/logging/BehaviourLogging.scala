package saw.ermezinde.util.logging

trait BehaviourLogging extends Logging {
  implicit class BehaviourLoggingOps(behaviourName: String) {
    def ||(msg: String): String = s"($behaviourName) $msg"
  }
}
