package pt.ipvc.kiosks.web.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
    basePackages = {"pt.ipvc.kiosks"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = pt.ipvc.kiosks.KiosksApplication.class
    )
)
@EntityScan(basePackages = "pt.ipvc.kiosks.dal.entities")
@EnableJpaRepositories(basePackages = "pt.ipvc.kiosks.dal.repository")
public class WebApp {
    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }
}
