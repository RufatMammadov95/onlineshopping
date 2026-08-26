package com.rufat.onlineshopping.config;

import javax.sql.DataSource;
import java.sql.Connection;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderCancellationColumnMigration {
	private final DataSource dataSource;

	public OrderCancellationColumnMigration(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void migrate() {
		try (Connection connection = dataSource.getConnection()) {
			String database = connection.getMetaData().getDatabaseProductName().toLowerCase();
			JdbcTemplate jdbc = new JdbcTemplate(dataSource);
			if (database.contains("postgres")) {
				jdbc.execute(
						"ALTER TABLE orders ADD COLUMN IF NOT EXISTS cancellation_requested BOOLEAN NOT NULL DEFAULT FALSE");
				jdbc.execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS cancellation_reason VARCHAR(1000)");
				jdbc.execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS cancellation_requested_at TIMESTAMP");
			} else if (database.contains("h2")) {
				jdbc.execute(
						"ALTER TABLE orders ADD COLUMN IF NOT EXISTS cancellation_requested BOOLEAN DEFAULT FALSE NOT NULL");
				jdbc.execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS cancellation_reason VARCHAR(1000)");
				jdbc.execute("ALTER TABLE orders ADD COLUMN IF NOT EXISTS cancellation_requested_at TIMESTAMP");
			}
		} catch (Exception ignored) {
			// Existing columns are harmless; startup must continue if migration is already
			// applied.
		}
	}
}
