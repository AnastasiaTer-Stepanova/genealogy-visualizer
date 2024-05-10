package genealogy.visualizer.config;

import genealogy.visualizer.entity.FamilyRevision;
import genealogy.visualizer.entity.Person;
import genealogy.visualizer.entity.model.Age;
import genealogy.visualizer.randomizer.AgeRandomizer;
import genealogy.visualizer.randomizer.PersonStatusRandomizer;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.ByteRangeRandomizer;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.range.ShortRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;

import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

public class EasyRandomParamsBuilder {

    public static EasyRandomParameters getGeneratorParams() {
        return new EasyRandomParameters()
                .stringLengthRange(20, 50)
                .collectionSizeRange(3, 10)
                .randomize(named("id").and(ofType(Long.class)), () -> null)
                .randomize(named("year").and(ofType(Short.class)),
                        () -> new ShortRangeRandomizer((short) 1300, (short) 2030).getRandomValue())
                .randomize(named("year").and(ofType(Integer.class)),
                        () -> new IntegerRangeRandomizer(1300, 2030).getRandomValue())
                .randomize(named("familyGeneration").and(ofType(Byte.class)),
                        () -> new ByteRangeRandomizer((byte) 1, (byte) 5).getRandomValue())
                .randomize(named("familyGeneration").and(ofType(Integer.class)),
                        () -> new IntegerRangeRandomizer(1, 5).getRandomValue())
                .randomize(named("listNumber").and(ofType(Short.class)),
                        () -> new ShortRangeRandomizer((short) 1, Short.MAX_VALUE).getRandomValue())
                .randomize(named("listNumber").and(ofType(Integer.class)),
                        () -> new IntegerRangeRandomizer(1, (int) Short.MAX_VALUE).getRandomValue())
                .randomize(ofType(String.class).and(named("status")),
                        () -> new PersonStatusRandomizer().getRandomValue())
                .randomize((named("familyRevisionNumber").or(named("previousFamilyRevisionNumber")
                                .or(named("nextFamilyRevisionNumber")).or(named("listNumber"))))
                                .and(ofType(Short.class)),
                        () -> new ShortRangeRandomizer((short) 1, Short.MAX_VALUE).getRandomValue())
                .randomize((named("familyRevisionNumber").or(named("previousFamilyRevisionNumber")
                                .or(named("nextFamilyRevisionNumber")).or(named("listNumber"))))
                                .and(ofType(Integer.class)),
                        () -> new IntegerRangeRandomizer(1, (int) Short.MAX_VALUE).getRandomValue())
                .randomize((named("catalog").or(named("fund")).or(named("instance"))).and(ofType(String.class)),
                        () -> new StringRandomizer(10).getRandomValue())
                .randomize(named("partner").and(ofType(FamilyRevision.class)), () -> null)
                .randomize(named("person").and(ofType(Person.class)), () -> null)
                .randomize(Age.class, new AgeRandomizer());
    }
}
