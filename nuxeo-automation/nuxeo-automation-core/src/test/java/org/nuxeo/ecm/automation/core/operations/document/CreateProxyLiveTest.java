/*
 * Copyright (c) 2006-2013 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Benjamin JALON <bjalon@nuxeo.com>
 */
package org.nuxeo.ecm.automation.core.operations.document;

import static junit.framework.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;

/**
 * @since 5.7
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
public class CreateProxyLiveTest {

    @Inject
    CoreSession session;

    private DocumentModel doc;

    private DocumentModel folder;

    private CreateProxyLive operation;

    @Before
    public void setup() throws Exception {
        folder = session.createDocumentModel("/", "test", "Folder");
        folder = session.createDocument(folder);
        session.save();

        doc = session.createDocumentModel("/", "my-doc", "File");
        doc.setPropertyValue("dc:title", "Test1");
        doc = session.createDocument(doc);
        session.save();

        operation = new CreateProxyLive();
        operation.path = "/test";
        operation.session = session;
    }

    @Test
    public void shouldCreateProxyLive() throws ClientException {
        operation.run(doc);

        DocumentModelList docs = session.getChildren(folder.getRef());
        assertEquals(1, docs.size());
        DocumentModel proxy = docs.get(0);
        assertEquals("Test1", proxy.getTitle());

        proxy.setPropertyValue("dc:title", "Test2");
        proxy = session.saveDocument(proxy);
        session.save();

        doc = session.getDocument(doc.getRef());
        assertEquals("Test2", doc.getTitle());

        doc.setPropertyValue("dc:title", "Test3");
        doc = session.saveDocument(doc);
        session.save();

        proxy = session.getDocument(proxy.getRef());
        assertEquals("Test3", proxy.getTitle());

    }

}
