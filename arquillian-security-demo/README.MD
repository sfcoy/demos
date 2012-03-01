Demonstration code for testing secured access to EJBs on JBossAS 7.1.x

Please add the following to your security sub-system configuration before attempting to run
these tests:

	<security-domain name="demo" cache-type="default">
		<authentication>
			<login-module code="UsersRoles" flag="sufficient"/>
		</authentication>
	</security-domain>
