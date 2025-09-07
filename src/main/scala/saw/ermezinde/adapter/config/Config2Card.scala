package saw.ermezinde.adapter.config

import com.typesafe.config.Config
import saw.ermezinde.game.domain.card.{Card, CardNationality, CardPower, CardSex}

import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Try

object Config2Card {
  def fromConfig(cardConfig: Config): Try[Card] = Try(
    Card(
      id = cardConfig.getString("id"),
      strength = cardConfig.getInt("strength"),
      nationality = config2Nationality(cardConfig.getString("nationality")),
      powers = cardConfig.getStringList("powers").asScala.map(config2CardPower).toList,
      sex = config2CardSex(cardConfig.getString("sex")),
      medals = cardConfig.getInt("medals")
    )
  )

  private def config2Nationality(str: String): CardNationality = str match {
    case "PT" => CardNationality.PT
    case "US" => CardNationality.US
    case "UK" => CardNationality.UK
    case "IT" => CardNationality.IT
    case "DE" => CardNationality.DE
    case "YU" => CardNationality.YU
    case "SU" => CardNationality.SU
    case "ES" => CardNationality.ES
    case "FR" => CardNationality.FR
    case "HU" => CardNationality.HU
    case "JP" => CardNationality.JP
    case "PL" => CardNationality.PL
    case "RO" => CardNationality.RO
    case "SE" => CardNationality.SE
    case nationality => throw new RuntimeException(s"Unknown card nationality: $nationality")
  }


  private def config2CardPower(power: String): CardPower = power match {
    case "PISTOL" => CardPower.PISTOL
    case "SEDUCTION" => CardPower.SEDUCTION
    case "NATIONALISM" => CardPower.NATIONALISM
    case "IMMUNITY" => CardPower.IMMUNITY
    case "CONSPIRE" => CardPower.CONSPIRE
    case "DOUBLE_AGENT" => CardPower.DOUBLE_AGENT
    case power => throw new RuntimeException(s"Unknown card power: $power")
  }

  private def config2CardSex(sex: String): CardSex = sex match {
    case "MALE" => CardSex.MALE
    case "FEMALE" => CardSex.FEMALE
    case sex => throw new RuntimeException(s"Unknown card sex: $sex")
  }
}
