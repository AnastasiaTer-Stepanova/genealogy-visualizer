package genealogy.visualizer.randomizer;

import org.jeasy.random.api.Randomizer;

import java.util.List;
import java.util.Random;

public class PersonStatusRandomizer implements Randomizer<String> {
    private static final List<String> RELATIONSHIPS = List.of(
            "жена", "брат", "сестра", "племянник", "сноха", "двоюродный", "двоюродная", "свекровь",
            "теща", "троюродный", "троюродная", "другая жена", "дочь", "сын", "пассынок", "внук", "внучка",
            "тесть", "вдова", "второбрачная", "зять", "тетя", "мачеха", "тетка");

    private static final List<String> ANOTHER = List.of("умер", "не указан", "умерший", "солдатка", "незаконнорожденый",
            "незаконнорожденая");

    @Override
    public String getRandomValue() {
        return RELATIONSHIPS.get(new Random().nextInt(RELATIONSHIPS.size())) + " " +
                ANOTHER.get(new Random().nextInt(ANOTHER.size()));
    }
}
