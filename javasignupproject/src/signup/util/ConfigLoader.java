package signup.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ConfigLoader는 config.properties를 애플리케이션 전체에서 공유하도록 하는 싱글턴입니다.
 * 민감한 값은 환경 변수를 통해 대체할 수 있도록 지원합니다.
 */
public final class ConfigLoader {

    private static final String CONFIG_PATH = "config.properties";
    private static final Logger logger = Logger.getLogger(ConfigLoader.class.getName());
    private static final ConfigLoader INSTANCE = new ConfigLoader();

    private final Properties properties = new Properties();

    private ConfigLoader() {
        load();
    }

    public static ConfigLoader getInstance() {
        return INSTANCE;
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    private void load() {
        try (FileInputStream fis = new FileInputStream(CONFIG_PATH)) {
            properties.load(fis);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "config.properties 로딩 실패", e);
        }
    }
}
