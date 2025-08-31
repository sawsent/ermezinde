package saw.ermezinde.util.validation

sealed trait Validatable[L, R] {
  def map[O](fn: R => O): Either[L, O]
  def map[O](out: => O): Either[L, O]
  def flatMap[O](fn: R => Either[L, O]): Either[L, O]
  def flatMap[O](out: => Either[L, O]): Either[L, O]
}
case class Validated[L, R](args: R) extends Validatable[L, R] {
  override def map[O](fn: R => O): Either[L, O] = Right(fn(args))
  override def map[O](out: => O): Either[L, O] = Right(out)

  override def flatMap[O](fn: R => Either[L, O]): Either[L, O] = fn(args)
  override def flatMap[O](out: => Either[L, O]): Either[L, O] = out
}
case class Invalid[L, R](error: L) extends Validatable[L, R] {
  override def map[O](fn: R => O): Either[L, O] = Left(error)
  override def map[O](out: => O): Either[L, O] = Left(error)

  override def flatMap[O](fn: R => Either[L, O]): Either[L, O] = Left(error)
  override def flatMap[O](out: => Either[L, O]): Either[L, O] = Left(error)
}

object Validate {
  def apply[L, R1](e1: Either[L, R1]): Validatable[L, R1] = e1 match {
    case Left(errorMessage) => Invalid(errorMessage)
    case Right(value) => Validated(value)
  }

  def apply[L, R1, R2](e1: Either[L, R1], e2: Either[L, R2]): Validatable[L, (R1, R2)] =
    (e1, e2) match {
      case (Right(e1v), Right(e2v)) => Validated((e1v, e2v))
      case _ => Invalid(collectErrors(e1, e2))
    }

  def apply[L, R1, R2, R3](e1: Either[L, R1], e2: Either[L, R2], e3: Either[L, R3]): Validatable[L, (R1, R2, R3)] =
    (e1, e2, e3) match {
      case (Right(e1v), Right(e2v), Right(e3v)) => Validated((e1v, e2v, e3v))
      case _ => Invalid(collectErrors(e1, e2, e3))
    }

  def apply[L, R1, R2, R3, R4](e1: Either[L, R1], e2: Either[L, R2], e3: Either[L, R3], e4: Either[L, R4]): Validatable[L, (R1, R2, R3, R4)] =
    (e1, e2, e3, e4) match {
      case (Right(e1v), Right(e2v), Right(e3v), Right(e4v)) => Validated((e1v, e2v, e3v, e4v))
      case _ => Invalid(collectErrors(e1, e2, e3, e4))
    }

  def apply[L, R1, R2, R3, R4, R5](e1: Either[L, R1], e2: Either[L, R2], e3: Either[L, R3],
                                   e4: Either[L, R4], e5: Either[L, R5]): Validatable[L, (R1, R2, R3, R4, R5)] =
    (e1, e2, e3, e4, e5) match {
      case (Right(e1v), Right(e2v), Right(e3v), Right(e4v), Right(e5v)) => Validated((e1v, e2v, e3v, e4v, e5v))
      case _ => Invalid(collectErrors(e1, e2, e3, e4, e5))
    }

  def apply[L, R1, R2, R3, R4, R5, R6](e1: Either[L, R1], e2: Either[L, R2], e3: Either[L, R3],
                                       e4: Either[L, R4], e5: Either[L, R5], e6: Either[L, R6]): Validatable[L, (R1, R2, R3, R4, R5, R6)] =
    (e1, e2, e3, e4, e5, e6) match {
      case (Right(e1v), Right(e2v), Right(e3v), Right(e4v), Right(e5v), Right(e6v)) => Validated((e1v, e2v, e3v, e4v, e5v, e6v))
      case _ => Invalid(collectErrors(e1, e2, e3, e4, e5, e6))
    }

  def apply[L, R1, R2, R3, R4, R5, R6, R7](e1: Either[L, R1], e2: Either[L, R2], e3: Either[L, R3],
                                           e4: Either[L, R4], e5: Either[L, R5], e6: Either[L, R6],
                                           e7: Either[L, R7]): Validatable[L, (R1, R2, R3, R4, R5, R6, R7)] =
    (e1, e2, e3, e4, e5, e6, e7) match {
      case (Right(e1v), Right(e2v), Right(e3v), Right(e4v), Right(e5v), Right(e6v), Right(e7v)) => Validated((e1v, e2v, e3v, e4v, e5v, e6v, e7v))
      case _ => Invalid(collectErrors(e1, e2, e3, e4, e5, e6, e7))
    }

  def apply[L, R1, R2, R3, R4, R5, R6, R7, R8](e1: Either[L, R1], e2: Either[L, R2], e3: Either[L, R3],
                                               e4: Either[L, R4], e5: Either[L, R5], e6: Either[L, R6],
                                               e7: Either[L, R7], e8: Either[L, R8]): Validatable[L, (R1, R2, R3, R4, R5, R6, R7, R8)] =
    (e1, e2, e3, e4, e5, e6, e7, e8) match {
      case (Right(e1v), Right(e2v), Right(e3v), Right(e4v), Right(e5v), Right(e6v), Right(e7v), Right(e8v)) => Validated((e1v, e2v, e3v, e4v, e5v, e6v, e7v, e8v))
      case _ => Invalid(collectErrors(e1, e2, e3, e4, e5, e6, e7, e8))
    }

  private def collectErrors[L](errors: Either[L, Any]*): L = {
    errors
      .filter(_.isLeft)
      .map(_.swap)
      .map(_.toOption.get)
      .toList
      .head
  }
}