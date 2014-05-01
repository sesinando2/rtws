databaseChangeLog = {

	changeSet(author: "nando (generated)", id: "1398897472757-1") {
		addColumn(tableName: "file_data") {
			column(name: "date_created", type: "timestamp")
		}

        grailsChange {
            change {
                sql.execute("UPDATE file_data SET date_created = NOW()")
            }
            rollback {
            }
        }

        addNotNullConstraint(tableName: "file_data", columnName: "date_created")
	}
}
