package saw.ermezinde.game.syntax

import saw.ermezinde.game.GameActor.GameFailureResponse

object GameResponseSyntax {
  implicit class EitherExtensionOps[R](either: Either[GameFailureResponse, R]) {
    def filter(cf: (R => Boolean, GameFailureResponse)): Either[GameFailureResponse, R] = cf match {
      case (condition, failure) =>
        either.filterOrElse(!condition(_), failure)

    }
  }


}
