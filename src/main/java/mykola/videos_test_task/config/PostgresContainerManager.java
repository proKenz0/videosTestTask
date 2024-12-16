package mykola.videos_test_task.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

@Component
@Profile("linux")
public class PostgresContainerManager {
    @PostConstruct
    public void setupPostgresContainer() {
        try {
            String scriptPath = "./start_postgres.sh";
            File scriptFile = new File(scriptPath);

            if (!scriptFile.exists()) {
                throw new RuntimeException("Script not found at path: " + scriptPath);
            }

            if (!scriptFile.canExecute()) {
                System.out.println("Granting execute permission to script...");
                boolean success = scriptFile.setExecutable(true);
                if (!success) {
                    throw new RuntimeException("Failed to grant execute permission to script: " + scriptPath);
                }
            }

            Process process = Runtime.getRuntime().exec(new String[]{"bash", scriptPath});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            System.out.println("Running start_postgres.sh...");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("start_postgres.sh exited with code " + exitCode);
            }

            System.out.println("start_postgres.sh completed successfully.");

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to execute script.", e);
        }
    }
}
