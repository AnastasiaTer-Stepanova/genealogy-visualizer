package genealogy.visualizer.randomizer;

import genealogy.visualizer.entity.enums.DateRangeType;
import genealogy.visualizer.entity.model.DateInfo;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.randomizers.misc.BooleanRandomizer;
import org.jeasy.random.randomizers.misc.EnumRandomizer;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;

public class DateInfoRandomizer implements Randomizer<DateInfo> {

    private Integer year;

    @Override
    public DateInfo getRandomValue() {
        BooleanRandomizer booleanRandomizer = new BooleanRandomizer();
        IntegerRangeRandomizer integerYearRandomizer = new IntegerRangeRandomizer(1500, 1980);
        StringBuilder dateString = new StringBuilder();
        dateString.append(year != null ? year : integerYearRandomizer.getRandomValue());
        if (booleanRandomizer.getRandomValue()) {
            IntegerRangeRandomizer integerMonthRandomizer = new IntegerRangeRandomizer(1, 12);
            dateString.insert(0, integerMonthRandomizer.getRandomValue().toString() + "-");
            if (booleanRandomizer.getRandomValue()) {
                IntegerRangeRandomizer integerDayRandomizer = new IntegerRangeRandomizer(1, 28);
                dateString.insert(0, integerDayRandomizer.getRandomValue().toString() + "-");
            }
        }
        EnumRandomizer<DateRangeType> enumRandomizer = new EnumRandomizer<>(DateRangeType.class);
        return new DateInfo(dateString.toString(), enumRandomizer.getRandomValue());
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
