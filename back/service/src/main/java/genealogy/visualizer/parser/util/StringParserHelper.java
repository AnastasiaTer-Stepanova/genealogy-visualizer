package genealogy.visualizer.parser.util;

import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.enums.LocalityType;
import genealogy.visualizer.entity.model.FullName;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static genealogy.visualizer.parser.util.ParserUtils.HYPHEN;
import static org.apache.commons.lang3.StringUtils.contains;

public class StringParserHelper {

    private static final Set<String> RELATIVE = Set.of("муж", "отец", "брат");
    private static final Set<String> SETTLEMENT = Set.of("слободы", "уезда", "округа");
    private static final Set<String> TOWN_LOCATION = Set.of("г.", "города");
    private static final Set<String> VILLAGE_LOCATION = Set.of("с.", "село", "села");
    private static final Set<String> HAMLET_LOCATION = Set.of("д.", "деревня", "деревни");
    private static final String LOCATION_EXCLUDE = "того села";
    private static final Set<String> EXCLUDE_STATUS = Set.of("его ", "\\...", "\\(", "\\)");
    private static final String TWIN = "близнец";

    private static final Set<String> PHRASES = Set.of("не указан", "церковника дочь", "церковникова дочь", "незаконнорожденные близнецы",
            "того града", "того округа", "нижнего земского суда", "из стрельцов", "живущая крестьянка", "незаконнорожденный сын",
            "у солдатки", "у нее", "незаконнорожденый сын", "у пашенного солдата", "водва(ец)", "церковникова жена",
            "дьякона жена", "дьячкова дочь", "государственный мещанин", "отставной солдат", "отставной конюх", "ученик философии",
            "отставной пономарь", "священницкая девка", "пахотный солдат", "не разборчиво", "скопинский мещанин", "скопинская мещанка",
            "крепостной работник", "первым браком", "вторым браком", "третим браком", "чертвертым браком", "сноха их",
            "сын ее", "дочь ее", "внук ее", "внучка ее", "внук их", "внучка их", "от 1 брака", "от 2 брака", "от 3 брака", "от 4 брака", "от 5 брака",
            "по 1 браку", "по 2 браку", "по 3 браку", "по 4 браку", "по 5 браку", "их мачеха", "от 1 мужа", "от 2 мужа", "от 3 мужа",
            "от 4 мужа", "от 5 мужа", "от 1 жены", "от 2 жены", "от 3 жены", "от 4 жены", "от 5 жены", "сводный брат");
    private static final Set<String> ARRANGED_WIFE = Set.of("1-я жена", "2-я жена", "3-я жена", "4-я жена", "5-я жена",
            "1 жена", "2 жена", "3 жена", "4 жена", "5 жена");
    private static final Set<String> RELATIONSHIPS = Set.of("падчерица", "племянница", "шурин", "жена", "брат", "сестра",
            "племянник", "сноха", "двоюродный", "двоюродная", "свекровь", "приемный", "теща", "троюродный", "троюродная",
            "другая жена", "дочь", "внук", "внучка", "тесть", "второбрачная", "зять", "тетя", "мачеха", "тетка", "девица",
            "приемыш", "сват", "племяник");
    private static final Set<String> ARRANGED_MARRIAGE = Set.of("по 1бр", "по 2бр", "по 3бр", "по 4бр", "по 5бр",
            "от 1го брака", "от 2го брака", "от 3го брака", "от 4го брака", "от 5го брака", "от 1бр", "от 2бр", "от 3бр",
            "от 4бр", "от 5бр", "от 1 бр", "от 2 бр", "от 3 бр", "от 4 бр", "от 5 бр", "от1бр", "от2бр", "от3бр", "от4бр", "от5бр");
    private static final Set<String> WIDOWS = Set.of("вд1", "вд2", "вд3", "вд4", "вд5", "водва(ец)", "вдова", "вдовец",
            "вд 1", "вд 2", "вд 3", "вд 4", "вд 5", "вд.");
    private static final Set<String> FEMININE_COUNTER = Set.of("другая", "первая", "вторая", "третья", "четвертая",
            "пятая", "шестая", "седьмая", "восьмая", "девятая", "десятая", "старшая", "младшая");
    private static final Set<String> MASCULINE_COUNTER = Set.of("другой", "первый", "второй", "третий", "четвертый",
            "пятый", "шестой", "седьмой", "восьмой", "девятый", "десятый", "старший", "старшой", "младший", "младшой", "меньшой");
    private static final Set<String> ANOTHER = Set.of("умер", "дьякон ", "солдат", "пономарь", "иерей", "церковник", "мещанин",
            "дьячек", "прапорщик", "купец", "кр-нин", "священник", "крестьянин", "служитель", "конюх", "капитан", "вдов", "сын");
    private static final Set<String> ANOTHER_WITH_SUFFIX = Set.of("умерший", "солдатка", "солдатки", "незаконнорожденый",
            "незаконнорожденая", "дьяконица", "дьякононица", "протопица", "мещанка", "дьяконщица", "попадья", "живущая",
            "крестьянка", "скопинский", "скопинская", "купчиха", "дьячиха", "пятницкий", "государственный", "пономариха", "мещанская",
            "священницкая", "девка", "отставной", "дьяконова", "капитан", "вознесенский", "незаконнорожденный",
            "незаконнорожденная", "пападья", "дьяек", "помещик", "пасынок", "пассынок");

    private static final String REGULAR_TAKE_NEXT_WORD = "\\s.+?(?=\\s|$)";
    private static final Set<String> CHECK_LIST = new HashSet<>();

    static {
        CHECK_LIST.addAll(RELATIONSHIPS);
        CHECK_LIST.addAll(ARRANGED_MARRIAGE);
        CHECK_LIST.addAll(WIDOWS);
        CHECK_LIST.addAll(FEMININE_COUNTER);
        CHECK_LIST.addAll(MASCULINE_COUNTER);
        CHECK_LIST.addAll(ANOTHER_WITH_SUFFIX);
        CHECK_LIST.add(LOCATION_EXCLUDE);
    }

    private FullName fullName = new FullName();
    private Locality locality = new Locality();

    private FullName relative = new FullName();
    private Locality relativeLocality = new Locality();

    public StringParserHelper(String parsingString) {
        if (parsingString == null || parsingString.isEmpty() || HYPHEN.equals(parsingString)) return;
        String fullNameString = parsingString;
        for (String relative : RELATIVE) {
            if (contains(parsingString, relative) && parsingString.matches("\\s" + relative + "\\s")) {
                String relativeString = relative + " " + StringUtils.substringAfter(parsingString, relative).trim();
                fullNameString = StringUtils.substringBefore(parsingString, relative).trim();
                this.updateRelativeInfo(relativeString);
                break;
            }
        }
        updateFullName(fullNameString);
    }

    public FullName getFullName() {
        return fullName;
    }

    public Locality getLocality() {
        return locality;
    }

    public FullName getRelative() {
        return relative;
    }

    public Locality getRelativeLocality() {
        return relativeLocality;
    }

    private void updateFullName(String fullNameString) {
        StringParserRecord<Locality> helperLocalityModel = parseLocality(fullNameString);
        if (helperLocalityModel != null) {
            this.locality = helperLocalityModel.value();
            fullNameString = helperLocalityModel.fullName();
        }
        StringParserRecord<FullName> helperFullNameModel = parseFullName(fullNameString);
        if (helperFullNameModel != null) {
            this.fullName = helperFullNameModel.value();
        } else {
            this.fullName = null;
        }
    }

    private void updateRelativeInfo(String relativeString) {
        StringParserRecord<Locality> helperLocalityModel = parseLocality(relativeString);
        if (helperLocalityModel != null) {
            this.relativeLocality = helperLocalityModel.value();
            relativeString = helperLocalityModel.fullName();
        }
        StringParserRecord<FullName> helperFullNameModel = parseFullName(relativeString);
        if (helperFullNameModel != null) {
            this.relative = helperFullNameModel.value();
        } else {
            this.relative = null;
        }
    }

    private static String capitalizeAllWord(String input) {
        return Arrays.stream(input.split("\\s+"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

    private static StringParserRecord<Locality> parseLocality(String parsingString) {
        if (StringUtils.isEmpty(parsingString) || HYPHEN.equals(parsingString)) return null;
        Locality locality = new Locality();
        for (String settlement : SETTLEMENT) {
            if (contains(parsingString, settlement)) {
                String settlementInfo = getRegexString("((^|\\s).*)\\s" + settlement, parsingString);
                if (settlementInfo != null && !settlementInfo.isEmpty()) {
                    locality.setAddress(StringUtils.capitalize(settlementInfo.trim()));
                    parsingString = parsingString.replaceAll(settlementInfo, "").trim();
                }
            }
        }
        for (String location : TOWN_LOCATION) {
            String locationName = getLocationName(parsingString, location);
            if (locationName != null && !locationName.isEmpty()) {
                parsingString = parsingString.replaceAll(locationName, "");
                locality.setName(StringUtils.capitalize(locationName.replaceAll(location + " ", "")));
                locality.setType(LocalityType.TOWN);
            }
        }
        for (String location : VILLAGE_LOCATION) {
            String locationName = getLocationName(parsingString, location);
            if (locationName != null && !locationName.isEmpty()) {
                parsingString = parsingString.replaceAll(locationName, "");
                locality.setName(StringUtils.capitalize(locationName.replaceAll(location + " ", "")));
                locality.setType(LocalityType.VILLAGE);
            }
        }
        for (String location : HAMLET_LOCATION) {
            String locationName = getLocationName(parsingString, location);
            if (locationName != null && !locationName.isEmpty()) {
                parsingString = parsingString.replaceAll(locationName, "");
                locality.setName(StringUtils.capitalize(locationName.replaceAll(location + " ", "")));
                locality.setType(LocalityType.HAMLET);
            }
        }
        return new StringParserRecord<>(parsingString, locality);
    }

    private static String getLocationName(String parsingString, String locate) {
        if (parsingString.toLowerCase().contains(locate) && !parsingString.toLowerCase().contains(LOCATION_EXCLUDE)) {
            return getRegexString(locate + REGULAR_TAKE_NEXT_WORD, parsingString);
        }
        return null;
    }

    private static StringParserRecord<FullName> parseFullName(String parsingString) {
        if (parsingString == null || StringUtils.isEmpty(parsingString) || HYPHEN.equals(parsingString)) return null;
        parsingString = parsingString.toLowerCase();
        FullName fullName = new FullName();
        parsingString = parsingString.replaceAll("\\(|\\)$", "");
        StringParserRecord<List<String>> recordPhrases = removeSubstringAndGetStatuses(PHRASES, parsingString);
        List<String> statuses = new ArrayList<>(recordPhrases.value());
        parsingString = recordPhrases.fullName();
        for (String exclude : EXCLUDE_STATUS) {
            parsingString = parsingString.replaceAll(exclude, "");
        }
        if (contains(parsingString, TWIN)) {
            String twinName = getRegexString(TWIN + REGULAR_TAKE_NEXT_WORD, parsingString);
            if (twinName != null && !twinName.isEmpty()) {
                statuses.add(twinName);
                parsingString = parsingString.replaceAll(twinName, "");
            }
        }
        StringParserRecord<List<String>> recordArrangedWife = removeSubstringAndGetStatuses(ARRANGED_WIFE, parsingString);
        statuses.addAll(recordArrangedWife.value());
        parsingString = recordArrangedWife.fullName();
        StringParserRecord<List<String>> recordCheckList = removeSubstringAndGetStatuses(CHECK_LIST, parsingString);
        statuses.addAll(recordCheckList.value());
        parsingString = recordCheckList.fullName();
        StringParserRecord<List<String>> recordAnother = removeSubstringAndGetStatuses(ANOTHER, parsingString);
        statuses.addAll(recordAnother.value());
        parsingString = recordAnother.fullName();
        String[] separateFullName = StringUtils.split(parsingString.replaceAll("\\s+", " ").trim(), " ");
        Iterator<String> iterator = Arrays.stream(separateFullName).iterator();
        if (iterator.hasNext()) {
            fullName.setName(StringUtils.capitalize(iterator.next()));
        }
        if (iterator.hasNext()) {
            fullName.setSurname(StringUtils.capitalize(iterator.next()));
        }
        if (iterator.hasNext()) {
            fullName.setLastName(StringUtils.capitalize(iterator.next()));
        }
        if (iterator.hasNext()) {
            statuses.add(iterator.next());
        }
        if (!CollectionUtils.isEmpty(statuses)) {
            fullName.setStatus(StringUtils.join(statuses, ", "));
        }
        return new StringParserRecord<>(parsingString, fullName);
    }

    private static StringParserRecord<List<String>> removeSubstringAndGetStatuses(Set<String> subStrings, String parsingString) {
        List<String> statuses = new ArrayList<>();
        for (String subString : subStrings) {
            if (contains(parsingString, subString)) {
                statuses.add(subString);
                parsingString = StringUtils.remove(parsingString, subString).trim();
            }
        }
        return new StringParserRecord<>(parsingString, statuses);
    }

    private static String getRegexString(String regex, String input) {
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.toMatchResult().group();
        }
        return null;
    }
}
