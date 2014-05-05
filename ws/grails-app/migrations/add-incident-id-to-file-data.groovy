databaseChangeLog = {

	changeSet(author: "nando (generated)", id: "1398843713507-1") {
		addColumn(tableName: "file_data") {
			column(name: "incident_id", type: "int4")
		}
	}
}
