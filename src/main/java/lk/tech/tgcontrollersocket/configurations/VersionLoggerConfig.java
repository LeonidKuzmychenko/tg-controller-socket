package lk.tech.tgcontrollersocket.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.security.CodeSource;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

@Configuration
public class VersionLoggerConfig {

    private static final Logger log = LoggerFactory.getLogger(VersionLoggerConfig.class);

    @Bean
    public ApplicationRunner versionLoggerRunner(ApplicationContext context) {
        return args -> {
            log.info("--- 📦 STARTING DEPENDENCY VERSION AUDIT ---");
            
            // Получаем все загруженные классы, которые могут быть библиотеками
            ClassLoader classLoader = getClass().getClassLoader();
            
            // Для примера, логируем версию самого Spring Boot
            logSpringCoreVersions(context);

            // Логируем основные компоненты, такие как Reactor Netty (если вы используете WebFlux)
            logSpecificVersion("Reactor Netty HTTP Server", "reactor.netty.http.server.HttpServer");
            
            // Добавьте логику для обхода всех JAR-файлов, если нужно, 
            // но это очень ресурсоемко. Лучше логировать ключевые библиотеки по имени.

            log.info("--- 🏁 DEPENDENCY VERSION AUDIT COMPLETE ---");
        };
    }

    /**
     * Логирует ключевые версии Spring.
     */
    private void logSpringCoreVersions(ApplicationContext context) {
        // 1. Spring Boot Version
        String bootVersion = context.getClass().getPackage().getImplementationVersion();
        log.info("✅ Spring Boot Version: {}", bootVersion != null ? bootVersion : "N/A");

        // 2. Spring Framework Version
        String frameworkVersion = org.springframework.core.SpringVersion.getVersion();
        log.info("✅ Spring Framework Version: {}", frameworkVersion != null ? frameworkVersion : "N/A");
    }

    /**
     * Пытается найти и залогировать версию конкретного класса/модуля по его имени.
     */
    private void logSpecificVersion(String moduleName, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            String version = getVersionFromManifest(clazz);
            log.info("🌐 {} Version: {}", moduleName, version);
        } catch (ClassNotFoundException e) {
            log.warn("❌ {} Class not found: {}", moduleName, className);
        } catch (Exception e) {
            log.error("⚠️ Failed to read version for {}", moduleName, e);
        }
    }

    /**
     * Извлекает версию из манифеста JAR-файла, содержащего класс.
     */
    private String getVersionFromManifest(Class<?> clazz) throws Exception {
        CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            URL url = codeSource.getLocation();
            if (url != null) {
                // Попытка получить JAR-файл и его манифест
                URL manifestUrl = new URL("jar:" + url.toExternalForm() + "!/META-INF/MANIFEST.MF");
                try (java.io.InputStream is = manifestUrl.openStream()) {
                    Manifest manifest = new Manifest(is);
                    Attributes attributes = manifest.getMainAttributes();
                    String implVersion = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                    if (implVersion != null) {
                        return implVersion;
                    }
                }
            }
        }
        return "Unknown (Manifest N/A)";
    }
}