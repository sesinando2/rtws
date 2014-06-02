databaseChangeLog = {

	changeSet(author: "nando (generated)", id: "1401428111701-1") {
		createTable(tableName: "member_token") {
			column(name: "id", type: "int8") {
				constraints(nullable: "false", primaryKey: "true", primaryKeyName: "member_tokenPK")
			}

			column(name: "version", type: "int8") {
				constraints(nullable: "false")
			}

			column(name: "member_id", type: "int4") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(author: "nando (generated)", id: "1401428111701-2") {
		createTable(tableName: "member_token_rest_token") {
			column(name: "member_token_tokens_id", type: "int8")

			column(name: "rest_token_id", type: "int8")
		}
	}

	changeSet(author: "nando (generated)", id: "1401428111701-3") {
		addForeignKeyConstraint(baseColumnNames: "member_token_tokens_id", baseTableName: "member_token_rest_token", constraintName: "FK1EE33ED9A63D57A1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "member_token", referencesUniqueColumn: "false")
	}

	changeSet(author: "nando (generated)", id: "1401428111701-4") {
		addForeignKeyConstraint(baseColumnNames: "rest_token_id", baseTableName: "member_token_rest_token", constraintName: "FK1EE33ED9AA3A239E", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "rest_token", referencesUniqueColumn: "false")
	}
}
