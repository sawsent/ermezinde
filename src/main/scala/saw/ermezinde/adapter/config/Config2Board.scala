package saw.ermezinde.adapter.config

import com.typesafe.config.Config
import saw.ermezinde.game.domain.board.{Board, BoardPower, NoPower, Slot, SlotPosition}

import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Try

object Config2Board {
  def fromConfig(boardConfig: Config): Try[Board] = Try(
    Board(
      id = boardConfig.getString("id"),
      resolveOrderNumber = boardConfig.getInt("resolve-order-number"),
      boardPower = config2BoardPower(boardConfig.getString("board-power-id")),
      slots = boardConfig.getConfigList("slots").asScala.toList.map(config2BoardSlot).toMap
    )
  )

  private def config2BoardPower(boardPowerId: String): BoardPower = boardPowerId match {
    case _ => NoPower
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
