package saw.ermezinde.util.logging

trait BehaviourLogging {
  val ClassName: String = this.getClass.getSimpleName
  def log(message: String)(implicit behaviourName: String): Unit = println(s"[$ClassName@$behaviourName] $message")
}
