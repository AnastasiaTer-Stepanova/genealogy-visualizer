package genealogy.visualizer.randomizer;

import genealogy.visualizer.entity.enums.AgeType;
import genealogy.visualizer.entity.model.Age;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.randomizers.range.BigDecimalRangeRandomizer;

import java.util.List;
import java.util.Random;

public class AgeRandomizer implements Randomizer<Age> {

    private static final List<AgeType> SUFFIX = List.of(AgeType.DAY, AgeType.WEEK, AgeType.MONTH, AgeType.YEAR);

    @Override
    public Age getRandomValue() {
        return new Age(new BigDecimalRangeRandomizer(Double.valueOf(0.0), Double.valueOf(99.9), Integer.valueOf(1)).getRandomValue(),
                SUFFIX.get(new Random().nextInt(SUFFIX.size())));
    }
}
