package genealogy.visualizer;

import genealogy.visualizer.config.DaoConfig;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static genealogy.visualizer.config.EasyRandomParamsBuilder.getGeneratorParams;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = DaoConfig.class)
public class JpaAbstractTest {

    @Autowired
    protected TestEntityManager entityManager;

    protected static EasyRandom generator;

    static {
        generator = new EasyRandom(getGeneratorParams());
    }

}
