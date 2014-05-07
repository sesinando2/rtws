databaseChangeLog = {

	changeSet(author: "nando (generated)", id: "1399501951127-1") {
		addColumn(tableName: "file_data") {
			column(name: "thumb_content_type", type: "varchar(255)")
		}
	}

	changeSet(author: "nando (generated)", id: "1399501951127-2") {
		dropColumn(columnName: "thumb_extension", tableName: "file_data")
	}
}
