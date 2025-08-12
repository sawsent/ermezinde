package saw.ermezinde.adapter.config

import com.typesafe.config.Config
import saw.ermezinde.game.domain.card.{CONSPIRE, Card, CardNationality, CardPower, CardSex, DE, DOUBLE_AGENT, ES, FEMALE, FR, HU, IMMUNITY, IT, JP, MALE, NATIONALISM, PISTOL, PL, PT, RO, SE, SEDUCTION, SU, UK, US, YU}

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
    case "PT" => PT
    case "US" => US
    case "UK" => UK
    case "IT" => IT
    case "DE" => DE
    case "YU" => YU
    case "SU" => SU
    case "ES" => ES
    case "FR" => FR
    case "HU" => HU
    case "JP" => JP
    case "PL" => PL
    case "RO" => RO
    case "SE" => SE
    case nationality => throw new RuntimeException(s"Unknown card nationality: $nationality")
  }


  private def config2CardPower(power: String): CardPower = power match {
    case "PISTOL" => PISTOL
    case "SEDUCTION" => SEDUCTION
    case "NATIONALISM" => NATIONALISM
    case "IMMUNITY" => IMMUNITY
    case "CONSPIRE" => CONSPIRE
    case "DOUBLE_AGENT" => DOUBLE_AGENT
    case power => throw new RuntimeException(s"Unknown card power: $power")
  }

  private def config2CardSex(sex: String): CardSex = sex match {
    case "MALE" => MALE
    case "FEMALE" => FEMALE
    case sex => throw new RuntimeException(s"Unknown card sex: $sex")
  }
}
