package saw.ermezinde.adapter.config

import com.typesafe.config.Config
import saw.ermezinde.game.domain.board.ResolveOrderNumberType.{PreSet, ViaDice}
import saw.ermezinde.game.domain.board.{Board, BoardInfo, PlacePhaseBoardPower, ResolveOrderNumberType, ResolvePhaseBoardPower}
import saw.ermezinde.game.domain.slot._

import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Try

object Config2Board {
  def fromConfig(boardConfig: Config): Try[BoardInfo] = Try(
    BoardInfo(
      id = boardConfig.getString("id"),
      resolveOrderNumber = config2BoardResolveOrderNumberType(boardConfig.getConfig("resolve-order-number")),
      placePhaseBoardPower = config2PlacePhaseBoardPower(boardConfig.getString("place-phase-board-power")),
      resolvePhaseBoardPower = config2ResolvePhaseBoardPower(boardConfig.getString("place-phase-board-power")),
      slots = boardConfig.getConfigList("slots").asScala.toList.map(config2BoardSlotInfo)
    )
  )

  private def config2BoardResolveOrderNumberType(resolveOrderNumberConfig: Config): ResolveOrderNumberType = {
    val t = resolveOrderNumberConfig.getString("type")
    t match {
      case "PRESET" => PreSet(resolveOrderNumberConfig.getInt("value"))
      case "VIA_DICE" =>
        val min = resolveOrderNumberConfig.getInt("min")
        val max = resolveOrderNumberConfig.getInt("max")
        ViaDice(min, max)
    }
  }

  private def config2PlacePhaseBoardPower(boardPowerId: String): Option[PlacePhaseBoardPower] = boardPowerId match {
    case _ => None
  }

  private def config2ResolvePhaseBoardPower(boardPowerId: String): Option[ResolvePhaseBoardPower] = boardPowerId match {
    case _ => None
  }

  private def config2BoardSlotInfo(slotConfig: Config): SlotInfo = {
    val isPrize = slotConfig.getBoolean("is-prize")
    val position = config2SlotPosition(slotConfig.getString("position"))
    val topSecret = slotConfig.getBoolean("top-secret")

    if (isPrize) {
      PrizeSlotInfo(
        position = position,
        topSecret = topSecret,
        prizeAmount = slotConfig.getInt("prize-amount")
      )
    } else {
      NormalSlotInfo(
        position = position,
        topSecret = topSecret,
        visionLevel = config2VisionLevel(slotConfig.getString("vision-level")),
        resolveOrderNumber = slotConfig.getInt("resolve-order-number")
      )
    }
  }

  private def config2VisionLevel(visionLevel: String): VisionLevel = visionLevel match {
    case "NOTHING" => VisionLevel.NOTHING
    case "SAME_BOARD" => VisionLevel.SAME_BOARD
    case "ADJACENT" => VisionLevel.ADJACENT_BOARDS
    case "ALL" => VisionLevel.ALL_BOARDS
    case "SAME_BOARD_TWICE" => VisionLevel.SAME_BOARD_TWICE
  }

  private def config2SlotPosition(slotPosition: String): SlotPosition = slotPosition match {
    case "TOP_LEFT" => SlotPosition.TL
    case "TOP_RIGHT" => SlotPosition.TR
    case "BOTTOM_LEFT" => SlotPosition.BL
    case "BOTTOM_RIGHT" => SlotPosition.BR
    case "MIDDLE" => SlotPosition.MIDDLE
  }
}
