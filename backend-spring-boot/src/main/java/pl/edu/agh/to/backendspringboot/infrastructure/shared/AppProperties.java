package pl.edu.agh.to.backendspringboot.infrastructure.shared;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private int maxDaysInAdvance = 7; // Wartość domyślna

    public int getMaxDaysInAdvance() {
        return maxDaysInAdvance;
    }

    public void setMaxDaysInAdvance(int maxDaysInAdvance) {
        this.maxDaysInAdvance = maxDaysInAdvance;
    }
}