package za.ac.nwu.logic.config;

import za.ac.nwu.Translator.config.TranslatorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Import({TranslatorConfig.class})
@Configuration
@ComponentScan(basePackages = {"za.ac.nwu.logic.impl"})
public class logicConfig
{
    @Bean
    public PasswordEncoder encoder()
    {
        return new BCryptPasswordEncoder();
    }
}
