databaseChangeLog = {

	changeSet(author: "nando (generated)", id: "1400725302992-1") {
		createTable(tableName: "abstract_log") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "abstract_logPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "token_id", type: "int8")

			column(name: "token_value", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "class", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "action", type: "varchar(255)")

			column(name: "message_id", type: "int8")

			column(name: "file_data_id", type: "int8")
		}
	}

	changeSet(author: "nando (generated)", id: "1400725302992-2") {
		createTable(tableName: "canned_message_canned_message_response") {
			column(name: "canned_message_responses_id", type: "int8")

			column(name: "canned_message_response_id", type: "int8")
		}
	}

	changeSet(author: "nando (generated)", id: "1400725302992-3") {
		createTable(tableName: "canned_message_response") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "canned_messagPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "message_responses_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "value", type: "varchar(255)") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "nando (generated)", id: "1400725302992-4") {
		createTable(tableName: "message") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "messagePK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "date_created", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "from_agent_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "from_member_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "incident_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "instance_id", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "last_updated", type: "timestamp") {
				constraints(nullable: "false")
			}

			column(name: "message_content", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "message_type", type: "int4") {
				constraints(nullable: "false")
			}

			column(name: "class", type: "varchar(255)") {
				constraints(nullable: "false")
			}

			column(name: "response_type_id", type: "int4")
		}
	}

	changeSet(author: "nando (generated)", id: "1400725302992-5") {
		addColumn(tableName: "token_restriction") {
			column(name: "message_id", type: "int8")
		}
	}

	changeSet(author: "nando (generated)", id: "1400725302992-6") {
		dropForeignKeyConstraint(baseTableName: "file_data_log", baseTableSchemaName: "public", constraintName: "fkd19384924b9e81ff")
	}

	changeSet(author: "nando (generated)", id: "1400725302992-7") {
		dropForeignKeyConstraint(baseTableName: "file_data_log", baseTableSchemaName: "public", constraintName: "fkd19384928ec18ec8")
	}

	changeSet(author: "nando (generated)", id: "1400725302992-14") {
		dropTable(tableName: "file_data_log")
	}

	changeSet(author: "nando (generated)", id: "1400725302992-8") {
		addForeignKeyConstraint(baseColumnNames: "file_data_id", baseTableName: "abstract_log", constraintName: "FKD2DDE1A74B9E81FF", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "file_data", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1400725302992-9") {
		addForeignKeyConstraint(baseColumnNames: "message_id", baseTableName: "abstract_log", constraintName: "FKD2DDE1A7E4E9B4E0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "message", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1400725302992-10") {
		addForeignKeyConstraint(baseColumnNames: "token_id", baseTableName: "abstract_log", constraintName: "FKD2DDE1A78EC18EC8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "rest_token", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1400725302992-11") {
		addForeignKeyConstraint(baseColumnNames: "canned_message_response_id", baseTableName: "canned_message_canned_message_response", constraintName: "FKC94C310134681C10", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "canned_message_response", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1400725302992-12") {
		addForeignKeyConstraint(baseColumnNames: "canned_message_responses_id", baseTableName: "canned_message_canned_message_response", constraintName: "FKC94C3101723C3632", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "message", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1400725302992-13") {
		addForeignKeyConstraint(baseColumnNames: "message_id", baseTableName: "token_restriction", constraintName: "FK9AFE86E4E9B4E0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "message", referencesUniqueColumn: "false")
	}
}
