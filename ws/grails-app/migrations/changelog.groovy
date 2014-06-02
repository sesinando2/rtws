databaseChangeLog = {

	changeSet(author: "nando (generated)", id: "1398841353240-1") {
		createTable(tableName: "file_data") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "file_dataPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "content_type", type: "varchar(255)")

			column(name: "data", type: "bytea") {
				constraints(nullable: "false")
			}

			column(name: "filename", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "class", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "location", type: "varchar(255)")

			column(name: "timestamp", type: "timestamp")
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-2") {
		createTable(tableName: "file_data_log") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "file_data_logPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "action", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "file_data_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "token_id", type: "int8")

			column(name: "token_value", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-3") {
		createTable(tableName: "persistent_logins") {
			column(name: "series", type: "varchar(64)") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "persistent_loPK")
			}

			column(name: "last_used", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "token", type: "varchar(64)") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "varchar(64)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-4") {
		createTable(tableName: "rest_token") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "rest_tokenPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "login", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "token", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-5") {
		createTable(tableName: "role") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "rolePK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "authority", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-6") {
		createTable(tableName: "token_restriction") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "token_restricPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "token_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "class", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "number_of_files", type: "int4")

			column(name: "file_data_id", type: "int8")

			column(name: "number_of_access", type: "int4")
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-7") {
		createTable(tableName: "user_role") {
			column(name: "role_id", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "user_id", type: "int8") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-8") {
		createTable(tableName: "user_table") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "user_tablePK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "account_expired", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "account_locked", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "enabled", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "password", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "password_expired", type: "bool") {
				constraints(nullable: "false")
			}

			column(name: "username", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-9") {
		addPrimaryKey(columnNames: "role_id, user_id", constraintName: "user_rolePK", tableName: "user_role")
	}

	changeSet(author: "nando (generated)", id: "1398841353240-16") {
		createIndex(indexName: "authority_uniq_1398841353158", tableName: "role", unique: "true") {
			column(name: "authority")
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-17") {
		createIndex(indexName: "username_uniq_1398841353165", tableName: "user_table", unique: "true") {
			column(name: "username")
		}
	}

	changeSet(author: "nando (generated)", id: "1398841353240-18") {
		createSequence(sequenceName: "hibernate_sequence")
	}

	changeSet(author: "nando (generated)", id: "1398841353240-10") {
		addForeignKeyConstraint(baseColumnNames: "file_data_id", baseTableName: "file_data_log", constraintName: "FKD19384924B9E81FF", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "file_data", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1398841353240-11") {
		addForeignKeyConstraint(baseColumnNames: "token_id", baseTableName: "file_data_log", constraintName: "FKD19384928EC18EC8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "rest_token", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1398841353240-12") {
		addForeignKeyConstraint(baseColumnNames: "file_data_id", baseTableName: "token_restriction", constraintName: "FK9AFE864B9E81FF", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "file_data", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1398841353240-13") {
		addForeignKeyConstraint(baseColumnNames: "token_id", baseTableName: "token_restriction", constraintName: "FK9AFE868EC18EC8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "rest_token", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1398841353240-14") {
		addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "user_role", constraintName: "FK143BF46A12329598", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "role", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1398841353240-15") {
		addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", constraintName: "FK143BF46AB75D5978", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user_table", referencesUniqueColumn: "false")
	}

	include file: 'add-incident-id-to-file-data.groovy'
    include file: 'add-date-created-on-file-data.groovy'
    include file: 'add-video-file-data.groovy'
    include file: 'changed-thumb-extension-to-thumb-type.groovy'
	include file: 'add-messaging-domain.groovy'

	include file: 'add-number-of-response-to-message-restriction.groovy'

	include file: 'added-token-member-mapping.groovy'
}
