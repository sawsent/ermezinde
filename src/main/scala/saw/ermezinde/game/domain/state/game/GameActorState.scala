package saw.ermezinde.game.domain.state.game

sealed trait GameActorState
case object GameNoState extends GameActorState

sealed trait GameState extends GameActorState {
  val game: GameModel
  val gameStep: GameStep = game.step
}

case class NotStartedGameState(game: NotStartedGameModel) extends GameState
case class InPreparationGameState(game: InPreparationGameModel) extends GameState
sealed trait InPlayGameState extends GameState {
  override val game: InPlayGameModel
}
case class PreparationPhaseGameState(game: PreparationPhaseGameModel) extends InPlayGameState
case class PlacePhaseGameState(game: PlacePhaseGameModel) extends InPlayGameState
case class ResolvePhaseGameState(game: ResolvePhaseGameModel) extends InPlayGameState
case class DiscardPhaseGameState(game: DiscardPhaseGameModel) extends InPlayGameState

case class InCountingGameState(game: InCountingGameModel) extends GameState
case class FinishedGameState(game: FinishedGameModel) extends GameState

