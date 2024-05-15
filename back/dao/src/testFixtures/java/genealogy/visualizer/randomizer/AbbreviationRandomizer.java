package genealogy.visualizer.randomizer;

import genealogy.visualizer.entity.enums.ArchiveDocumentType;
import org.jeasy.random.api.Randomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;

import java.util.List;
import java.util.Random;

public class AbbreviationRandomizer implements Randomizer<String> {

    private static final List<ArchiveDocumentType> PREFIX = List.of(ArchiveDocumentType.CB, ArchiveDocumentType.CS,
            ArchiveDocumentType.IC, ArchiveDocumentType.RL, ArchiveDocumentType.PR);

    @Override
    public String getRandomValue() {
        return PREFIX.get((new Random().nextInt(PREFIX.size()))).getName() + new StringRandomizer(5).getRandomValue();
    }
}