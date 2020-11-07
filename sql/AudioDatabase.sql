BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS `AudioDatabase` (
	`database_id`	INTEGER NOT NULL DEFAULT 0 PRIMARY KEY AUTOINCREMENT,
	`track_name`	TEXT NOT NULL DEFAULT 'Unknown Track',
	`track_number`	INTEGER NOT NULL DEFAULT 0,
	`artist_name`	TEXT NOT NULL DEFAULT 'Unknown Artist',
	`album_artist_name`	TEXT NOT NULL DEFAULT 'Unknown Album Artist',
	`album_name`	TEXT NOT NULL DEFAULT 'Unknown Album',
	`file_path`	TEXT NOT NULL UNIQUE,
	`is_invalid`	INTEGER NOT NULL DEFAULT 0
);
COMMIT;
