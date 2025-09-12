package saw.ermezinde.game.domain.game.state.inplay

import saw.ermezinde.game.domain.board.BoardPosition
import saw.ermezinde.game.domain.game.model.{InPlayGameModel, PreparationPhaseGameModel}
import saw.ermezinde.game.domain.game.state.GameActorState.{DiceRoll, PlayerId, Timestamp}
import saw.ermezinde.game.domain.game.state.InCountingGameState.RevealPhase.RevealPhase
import saw.ermezinde.game.domain.player.PlayerModel.PlayerModelId
import saw.ermezinde.game.syntax.DiceRollSyntax.DiceRollSyntax

import scala.math.pow

object PreparationPhaseGameState {
  def newRound(state: DiscardPhaseGameState): PreparationPhaseGameState = BoardSelectionGameState(
    id = state.id,
    ownerId = state.ownerId,
    gameStartTime = state.gameStartTime,
    players = state.players,
    game = PreparationPhaseGameModel.newRound(state.game),
  )
}
sealed trait PreparationPhaseGameState extends InPlayGameState
case class BoardSelectionGameState(
                                    id: String,
                                    ownerId: String,
                                    gameStartTime: Option[Timestamp],
                                    players: Map[PlayerId, PlayerModelId],
                                    game: PreparationPhaseGameModel
                                  ) extends PreparationPhaseGameState {
  def checkMoveOnToDiceRolls: PreparationPhaseGameState = if (game.table.isFull) {
    OrderingSelectionGameState.init(copy())
  } else copy()
}

object OrderingSelectionGameState {
  def init(state: BoardSelectionGameState): OrderingSelectionGameState = {
    val players = state.players.keys.toList
    OrderingSelectionGameState(
      currentPlayerIndex = 0,
      underlying = state,
      playersContesting = players,
      currentRoundDiceRolls = Map.empty,
      diceRolls = List.empty
    )
  }
}
case class OrderingSelectionGameState(
                                       underlying: BoardSelectionGameState,
                                       currentPlayerIndex: Int,
                                       playersContesting: List[PlayerId],
                                       currentRoundDiceRolls: Map[PlayerId, DiceRoll],
                                       diceRolls: List[Map[PlayerId, DiceRoll]],
                                     ) extends PreparationPhaseGameState {
  override val game: InPlayGameModel = underlying.game
  override val id: RevealPhase = underlying.id
  override val ownerId: RevealPhase = underlying.ownerId
  override val gameStartTime: Option[Timestamp] = underlying.gameStartTime
  override val players: Map[PlayerId, PlayerModelId] = underlying.players

  private val allContestingPlayersRolled: Boolean = playersContesting.toSet == currentRoundDiceRolls.keys.toSet

  def playerRollDice(playerId: PlayerId, roll: DiceRoll): InPlayGameState = {
    val updatedDiceRolls = currentRoundDiceRolls + (playerId -> roll)

    copy(
      currentRoundDiceRolls = updatedDiceRolls,
      currentPlayerIndex = (currentPlayerIndex + 1) % playersContesting.length
    )
      .verifications
  }

  private def verifications: InPlayGameState = {
    if (allContestingPlayersRolled) {
      copy(
        diceRolls = diceRolls :+ currentRoundDiceRolls,
      ).checkTies
    } else {
      copy()
    }
  }

  private def checkTies: InPlayGameState = {
    val valueRolls = currentRoundDiceRolls.map { case (id, diceRoll) => id -> diceRoll.value }

    val ties = valueRolls.filter { case (_, diceRoll) => valueRolls.toList.map(_._2).count(_ == diceRoll) != 1 }

    if (ties.isEmpty) {
      moveToNextPhase
    } else {
      copy(
        playersContesting = ties.keys.toList,
        currentRoundDiceRolls = Map.empty
      )
    }
  }

  private def moveToNextPhase: InPlayGameState = {
    val playerOrdering =
      diceRolls.map(map => map.map(kv => kv._1 -> kv._2.value)).reverse
        .zipWithIndex
        .map { case (diceRolls, idx) => diceRolls.map { case (id, diceRoll) => id -> (diceRoll * pow(12, idx)).toInt } }
        .foldLeft(Map.empty[PlayerId, Int]) { case (acc, map) => map.map {
          case (id, value) => id -> (value + acc.getOrElse(id, 0))
        }
        }
        .toList
        .map(kv => kv._2 -> kv._1)
        .sorted((x: (Int, PlayerId), y: (Int, PlayerId)) => y._1 - x._1)
        .map(_._2)

    EnigmaPlacementGameState.init(
      boardSelectionGameState = underlying,
      playerOrdering = playerOrdering
    )
  }


}
object EnigmaPlacementGameState {
  def init(
            boardSelectionGameState: BoardSelectionGameState,
            playerOrdering: List[PlayerId],
          ): InPlayGameState = {

    if (boardSelectionGameState.game.enigmaOwner.isEmpty) {
      EnigmaPlacementGameState(
        underlying = boardSelectionGameState,
        playerOrdering = playerOrdering,
        enigmaPlacement = Some(boardSelectionGameState.game.config.randomizer.randomTablePosition)
      ).moveToPlacement
    } else {
      EnigmaPlacementGameState(
        underlying = boardSelectionGameState,
        playerOrdering = playerOrdering,
        enigmaPlacement = None
      )
    }

  }

}
case class EnigmaPlacementGameState(
                                     underlying: BoardSelectionGameState,
                                     playerOrdering: List[PlayerId],
                                     enigmaPlacement: Option[BoardPosition]
                                   ) extends PreparationPhaseGameState {
  override val game: InPlayGameModel = underlying.game
  override val id: RevealPhase = underlying.id
  override val ownerId: RevealPhase = underlying.ownerId
  override val gameStartTime: Option[Timestamp] = underlying.gameStartTime
  override val players: Map[PlayerId, PlayerModelId] = underlying.players

  val enigmaOwner: Option[PlayerId] = underlying.game.enigmaOwner.map(rPlayers(_))

  def setEnigmaPlacement(bp: BoardPosition): EnigmaPlacementGameState = copy(enigmaPlacement = Some(bp))
  def moveToPlacement: InPlayGameState = PlacePhaseGameState.init(underlying, playerOrdering, enigmaPlacement.get)
}
