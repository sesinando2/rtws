databaseChangeLog = {

	changeSet(author: "nando (generated)", id: "1399293877740-1") {
		addColumn(tableName: "file_data") {
			column(name: "thumb_data", type: "bytea")
		}
	}

	changeSet(author: "nando (generated)", id: "1399293877740-2") {
		addColumn(tableName: "file_data") {
			column(name: "thumb_extension", type: "varchar(255)")
		}
	}
}
