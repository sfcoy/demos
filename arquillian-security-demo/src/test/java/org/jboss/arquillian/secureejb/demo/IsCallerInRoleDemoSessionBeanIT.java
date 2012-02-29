/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.arquillian.secureejb.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.security.PrivilegedAction;
import java.util.Set;

import javax.ejb.EJB;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.secureejb.JBossLoginContextFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests demonstrating access to user roles.
 */
@RunWith(Arquillian.class)
public class IsCallerInRoleDemoSessionBeanIT {

    @EJB
    private IsCallerInRoleDemoSessionBean sut;

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addClasses(JBossLoginContextFactory.class, IsCallerInRoleDemoSessionBean.class)
                .addAsResource("users.properties")
                .addAsResource("roles.properties");
    }

    @Test
    public void testDiscoverAllRoles() throws LoginException {
        LoginContext loginContext = JBossLoginContextFactory.createLoginContext("user1", "password");
        loginContext.login();
        try {
            Set<String> foundRoles = Subject.doAs(loginContext.getSubject(), new PrivilegedAction<Set<String>>() {

                @Override
                public Set<String> run() {
                    return sut.discoverRoles();
                }

            });
            assertEquals(3, foundRoles.size());
            assertTrue(foundRoles.contains("role1"));
            assertTrue(foundRoles.contains("role2"));
            assertTrue(foundRoles.contains("role3"));
        } finally {
            loginContext.logout();
        }
    }

    @Test
    public void testDiscoverFewerRoles() throws LoginException {
        LoginContext loginContext = JBossLoginContextFactory.createLoginContext("user2", "foobar");
        loginContext.login();
        try {
            Set<String> foundRoles = Subject.doAs(loginContext.getSubject(), new PrivilegedAction<Set<String>>() {

                @Override
                public Set<String> run() {
                    return sut.discoverRoles();
                }

            });
            assertEquals(2, foundRoles.size());
            assertTrue(foundRoles.contains("role2"));
            assertTrue(foundRoles.contains("role3"));
        } finally {
            loginContext.logout();
        }
    }

}
