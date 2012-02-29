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

import static org.jboss.arquillian.secureejb.demo.SecurityRoles.ROLE1;
import static org.jboss.arquillian.secureejb.demo.SecurityRoles.ROLE2;
import static org.jboss.arquillian.secureejb.demo.SecurityRoles.ROLE3;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 * Session Bean demonstrating the various security authorisation annotations.
 */
@Stateless
@LocalBean
@DeclareRoles({ ROLE1, ROLE2, ROLE3 })
public class AuthorisationAnnotationDemoBean {

    private static final Logger logger = Logger.getLogger(AuthorisationAnnotationDemoBean.class.getName());

    @Resource
    private SessionContext sessionContext;

    @RolesAllowed(ROLE1)
    public void performSecureOperation() {
        logger.info("User " + sessionContext.getCallerPrincipal().getName() + " is doing something secret");
        if (sessionContext.isCallerInRole(ROLE1))
            logger.info("User is in correct role: " + ROLE1);
        else
            // should never get here
            logger.info("User is NOT in an allowed role");
    }
    
    @DenyAll
    public void unauthorisedOperation() {
        // should never get here
        logger.info("User " + sessionContext.getCallerPrincipal().getName()
                + " is doing something that should have been denied access");
    }
    
    @PermitAll
    public void performInsecureOperation() {
        logger.info("User " + sessionContext.getCallerPrincipal().getName()
                + " is doing something that all users may access");
    }
}
