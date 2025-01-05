package keyhub.sample.common;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
	@Value("${spring.datasource.main.username}")
	private String username;
	@Value("${spring.datasource.main.password}")
	private String password;
	@Value("${spring.datasource.main.jdbc-url}")
	private String jdbcUrl;
	@Value("${spring.datasource.main.driver-class-name}")
	private String driverClassName;
	@Value("${spring.datasource.main.maximum-pool-size}")
	private int maximumPoolSize;
	@Value("${spring.datasource.main.minimum-idle}")
	private int minimumIdle;

	@Primary
	@Bean(name = "dataSource")
	public DataSource mainDataSource() {
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setUsername(username);
		hikariConfig.setPassword(password);
		hikariConfig.setJdbcUrl(jdbcUrl);
		hikariConfig.setDriverClassName(driverClassName);
		hikariConfig.setMaximumPoolSize(maximumPoolSize);
		hikariConfig.setMinimumIdle(minimumIdle);
		return new HikariDataSource(hikariConfig);
	}
}