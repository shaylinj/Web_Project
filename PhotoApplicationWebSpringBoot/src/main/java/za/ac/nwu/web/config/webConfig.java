package za.ac.nwu.web.config;

import za.ac.nwu.logic.config.logicConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
@Import({logicConfig.class})
@Configuration
@ComponentScan(basePackages = {"za.ac.nwu.web.controller"})
public class webConfig
{

}
