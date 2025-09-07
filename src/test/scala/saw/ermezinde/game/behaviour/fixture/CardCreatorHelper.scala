package saw.ermezinde.game.behaviour.fixture

import saw.ermezinde.game.domain.card.{Card, CardNationality, CardSex}

import scala.util.Random

trait CardCreatorHelper {
  lazy val CardCreator = new CardCreator
  class CardCreator {
    var _id: Int = -1
    def nextId(): Int = {
      _id = _id + 1
      _id
    }
    def randomStrength: Int = Random.between(0, 6)
    def randomNationality: CardNationality = CardNationality.all(Random.between(0, CardNationality.all.length))
    def randomSex: CardSex = CardSex.all(Random.between(0, CardSex.all.length))
    def randomMedals: Int = Random.between(0, 6)

    def randomCard = Card(
      id = nextId().toString,
      strength = randomStrength,
      nationality = randomNationality,
      powers = List.empty,
      sex = randomSex,
      medals = randomMedals
    )
  }
}
