package genealogy.visualizer.randomizer;

import genealogy.visualizer.entity.AnotherNameInRevision;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.randomizers.range.ByteRangeRandomizer;
import org.jeasy.random.randomizers.range.LongRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;

public class AnotherNameInRevisionRandomizer implements Randomizer<AnotherNameInRevision> {

    @Override
    public AnotherNameInRevision getRandomValue() {
        return new AnotherNameInRevision(
                new LongRangeRandomizer(1L, Long.MAX_VALUE).getRandomValue(),
                new ByteRangeRandomizer((byte) 1, Byte.MAX_VALUE).getRandomValue(),
                new StringRandomizer(10).getRandomValue(),
                null);
    }
}
