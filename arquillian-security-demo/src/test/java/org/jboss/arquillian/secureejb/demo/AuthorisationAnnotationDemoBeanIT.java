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

import java.security.PrivilegedAction;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.secureejb.JBossLoginContextFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests demonstrating {@link RolesAllowed}, {@link DenyAll} and {@link PermitAll} behaviour.
 */
@RunWith(Arquillian.class)
public class AuthorisationAnnotationDemoBeanIT {

    @EJB
    private AuthorisationAnnotationDemoBean sut;

    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addClasses(JBossLoginContextFactory.class, AuthorisationAnnotationDemoBean.class, SecurityRoles.class)
                .addAsWebInfResource("META-INF/ejb-jar.xml").addAsWebInfResource("META-INF/jboss-ejb3.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsResource("users.properties")
                .addAsResource("roles.properties");
        return webArchive;
    }

    @Test
    public void testAuthorisedSecureOperation() throws LoginException {
        LoginContext loginContext = JBossLoginContextFactory.createLoginContext("user1", "password");
        loginContext.login();
        try {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction<Void>() {

                @Override
                public Void run() {
                    sut.performSecureOperation();
                    return null;
                }

            });
        } finally {
            loginContext.logout();
        }
    }

    @Test(expected = EJBAccessException.class)
    public void testUnauthorisedSecureOperation() throws LoginException {
        LoginContext loginContext = JBossLoginContextFactory.createLoginContext("user2", "foobar");
        loginContext.login();
        try {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction<Void>() {

                @Override
                public Void run() {
                    sut.performSecureOperation();
                    return null;
                }

            });
        } finally {
            loginContext.logout();
        }
    }

    @Test(expected = EJBAccessException.class)
    public void testDenyAll() {
        sut.unauthorisedOperation();
    }

    @Test
    public void testPermitAll() {
        sut.performInsecureOperation();
    }

}
