package de.slimecloud.april.features.april;

import de.slimecloud.april.config.ConfigCategory;
import de.slimecloud.april.config.engine.ConfigField;
import de.slimecloud.april.config.engine.ConfigFieldType;
import lombok.Getter;

@Getter
public class AprilConfig extends ConfigCategory {
	@ConfigField(name = "Lösungs-Satz", command = "message", description = "Der notwendige Lösungssatz", type = ConfigFieldType.STRING, required = true)
	private String message;
}
