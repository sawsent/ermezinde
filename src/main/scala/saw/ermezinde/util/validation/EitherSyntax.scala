package saw.ermezinde.util.validation

import scala.language.implicitConversions

object EitherSyntax {
  implicit def toEither[L](toVerify: => (Boolean, L)): Either[L, Unit] = Either.cond(toVerify._1, (), toVerify._2)
}
