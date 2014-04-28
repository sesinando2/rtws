package au.com.adtec.realtime.webservice.security

class User {

	transient springSecurityService

	String username
	String password
	boolean enabled = true
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static transients = ['springSecurityService', 'authorities']

	static constraints = {
		username blank: false, unique: true
		password blank: false
	}

	static mapping = {
        table 'user_table'
		password column: '`password`'
	}

	Set<Role> getAuthorities() {
        try {
            return UserRole.findAllByUser(this).collect { it.role } as Set
        } catch (all) {
            return []
        }

	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}
}
