package keyhub.sample.common;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class JpaConfig {
	private final JpaProperties jpaProperties;

	public JpaConfig(JpaProperties jpaProperties) {
		this.jpaProperties = jpaProperties;
		jpaProperties.setOpenInView(false);
	}

	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder,
		DataSource dataSource) {
		Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
		return builder
			.dataSource(dataSource)
			.properties(properties)
			.packages("keyhub.sample")
			.build();
	}
}
