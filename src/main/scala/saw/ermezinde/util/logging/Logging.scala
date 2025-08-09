package saw.ermezinde.util.logging

trait Logging {
  val ClassName: String = this.getClass.getSimpleName

  def log(message: String): Unit = println(s"[$ClassName] $message")
}
