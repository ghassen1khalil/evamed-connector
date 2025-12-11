package fr.has.evamed.connector.config;

import fr.has.evamed.connector.domain.UserTypeDto;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToUserTypeConverter stringToUserTypeConverter;

    public WebConfig(StringToUserTypeConverter stringToUserTypeConverter) {
        this.stringToUserTypeConverter = stringToUserTypeConverter;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToUserTypeConverter);
    }

    @Component
    static class StringToUserTypeConverter implements Converter<String, UserTypeDto> {

        @Override
        public UserTypeDto convert(String source) {
            if (source == null) {
                return null;
            }
            // Optionnel : normaliser la casse
            String value = source.trim().toLowerCase();

            return UserTypeDto.fromValue(value); // utilise la méthode générée par OpenAPI
        }
    }
}
