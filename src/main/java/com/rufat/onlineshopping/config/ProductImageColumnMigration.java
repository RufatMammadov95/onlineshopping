package com.rufat.onlineshopping.config;

import javax.sql.DataSource;
import java.sql.Connection;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Converts the legacy varchar(255) image column to a type that can hold
 * uploaded images.
 */
@Component
public class ProductImageColumnMigration {
	private final DataSource dataSource;

	public ProductImageColumnMigration(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void migrate() {
		try {
			String database;
			try (Connection connection = dataSource.getConnection()) {
				database = connection.getMetaData().getDatabaseProductName().toLowerCase();
			}
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			if (database.contains("postgres")) {
				jdbc.execute("ALTER TABLE products ALTER COLUMN image_url TYPE TEXT");
			} else if (database.contains("h2")) {
				jdbc.execute("ALTER TABLE products ALTER COLUMN image_url SET DATA TYPE CLOB");
			}
		} catch (Exception ignored) {
			// The migration is idempotent; an unavailable/unchanged schema must not stop
			// startup.
		}
	}
}
