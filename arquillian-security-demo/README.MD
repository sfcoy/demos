Demonstration code for testing secured access to EJBs on WildFly 8.1.0

Please add the following to your security sub-system configuration (in standalone.xml) before attempting to run
these tests:

    <subsystem xmlns="urn:jboss:domain:security:1.2">
        <security-domains>
            <security-domain name="demo" cache-type="default">
                <authentication>
                    <login-module code="UsersRoles" flag="sufficient"/>
                </authentication>
            </security-domain>
            <security-domain name="other" cache-type="default">
            ...

