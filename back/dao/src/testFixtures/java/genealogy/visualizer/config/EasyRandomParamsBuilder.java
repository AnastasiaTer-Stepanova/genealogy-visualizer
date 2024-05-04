package genealogy.visualizer.config;

import genealogy.visualizer.entity.model.Age;
import genealogy.visualizer.entity.model.AnotherNameInRevision;
import genealogy.visualizer.randomizer.AgeRandomizer;
import genealogy.visualizer.randomizer.AnotherNameInRevisionRandomizer;
import genealogy.visualizer.randomizer.PersonStatusRandomizer;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.LongRangeRandomizer;
import org.jeasy.random.randomizers.range.ShortRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;

import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

public class EasyRandomParamsBuilder {

    public static EasyRandomParameters getGeneratorParams() {
        return new EasyRandomParameters()
                .stringLengthRange(20, 50)
                .collectionSizeRange(3, 10)
                .randomize(named("id").and(ofType(Long.class)),
                        () -> new LongRangeRandomizer(1L, Long.MAX_VALUE).getRandomValue())
                .randomize(named("year").and(ofType(Short.class)),
                        () -> new ShortRangeRandomizer((short) 1300, (short) 2030).getRandomValue())
                .randomize(ofType(String.class).and(named("status")),
                        () -> new PersonStatusRandomizer().getRandomValue())
                .randomize((named("familyRevisionNumber").or(named("previousFamilyRevisionNumber")
                                .or(named("nextFamilyRevisionNumber")).or(named("listNumber"))))
                                .and(ofType(Short.class)),
                        () -> new ShortRangeRandomizer((short) 1, Short.MAX_VALUE).getRandomValue())
                .randomize((named("catalog").or(named("fund")).or(named("instance"))).and(ofType(String.class)),
                        () -> new StringRandomizer(10).getRandomValue())
                .randomize(AnotherNameInRevision.class, new AnotherNameInRevisionRandomizer())
                .randomize(Age.class, new AgeRandomizer());
    }
}
