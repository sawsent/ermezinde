package saw.ermezinde.adapter.config

import com.typesafe.config.Config
import saw.ermezinde.game.domain.board.{Board, PlacePhaseBoardPower, ResolvePhaseBoardPower, Slot, SlotPosition}

import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Try

object Config2Board {
  def fromConfig(boardConfig: Config): Try[Board] = Try(
    Board(
      id = boardConfig.getString("id"),
      resolveOrderNumber = boardConfig.getInt("resolve-order-number"),
      placePhaseBoardPower = config2PlacePhaseBoardPower(boardConfig.getString("place-phase-board-power-id")),
      resolvePhaseBoardPower = config2ResolvePhaseBoardPower(boardConfig.getString("place-phase-board-power-id")),
      slots = boardConfig.getConfigList("slots").asScala.toList.map(config2BoardSlot).toMap
    )
  )

  private def config2PlacePhaseBoardPower(boardPowerId: String): Option[PlacePhaseBoardPower] = boardPowerId match {
    case _ => None
  }

  private def config2ResolvePhaseBoardPower(boardPowerId: String): Option[ResolvePhaseBoardPower] = boardPowerId match {
    case _ => None
  }

  private def config2BoardSlot(slotConfig: Config): (SlotPosition, Slot) = {
    config2SlotPosition(slotConfig.getString("position")) -> Slot()
  }

  private def config2SlotPosition(slotPosition: String): SlotPosition = slotPosition match {
    case "TOP_LEFT" => SlotPosition.TOP_LEFT
    case "TOP_RIGHT" => SlotPosition.TOP_RIGHT
    case "BOTTOM_LEFT" => SlotPosition.BOTTOM_LEFT
    case "BOTTOM_RIGHT" => SlotPosition.BOTTOM_RIGHT
    case "MIDDLE" => SlotPosition.MIDDLE
  }

}
