databaseChangeLog = {

	changeSet(author: "nando (generated)", id: "1401398517038-1") {
		addColumn(tableName: "token_restriction") {
			column(name: "number_of_response", type: "int4")
		}
	}
}
