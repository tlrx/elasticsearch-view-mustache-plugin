/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.test.integration.views.mustache;

import com.github.tlrx.elasticsearch.test.EsSetup;
import org.elasticsearch.action.view.ViewAction;
import org.elasticsearch.action.view.ViewRequest;
import org.elasticsearch.action.view.ViewResponse;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.github.tlrx.elasticsearch.test.EsSetup.createIndex;
import static com.github.tlrx.elasticsearch.test.EsSetup.deleteAll;
import static com.github.tlrx.elasticsearch.test.EsSetup.fromClassPath;
import static junit.framework.Assert.assertEquals;

public class MustacheViewTests {

    EsSetup esSetup;

    @Before
    public void setUp() {

        // Instantiates a local node & client with few templates in config dir
        esSetup = new EsSetup(ImmutableSettings
                .settingsBuilder()
                .put("path.conf", "./target/test-classes/org/elasticsearch/test/integration/views/mustache/config/")
                .build());

        // Clean all and create some test data
        esSetup.execute(

                deleteAll(),

                createIndex("catalog")
                        .withMapping("product", fromClassPath("org/elasticsearch/test/integration/views/mustache/mappings/product.json"))
                        .withData(fromClassPath("org/elasticsearch/test/integration/views/mustache/data/products.json"))
        );
    }

    @Test
    public void testDefaultView() throws Exception {
        ViewResponse response = esSetup.client().execute(ViewAction.INSTANCE, new ViewRequest("catalog", "product", "1")).get();
        assertEquals("Rendering the document #1 in version 1 of type product from index catalog with Mustache", new String(response.content(), "UTF-8"));
    }

    @Test
    public void testFullView() throws Exception {
        ViewResponse response = esSetup.client().execute(ViewAction.INSTANCE, new ViewRequest("catalog", "product", "2").format("full")).get();
        assertEquals(fromClassPath("org/elasticsearch/test/integration/views/mustache/config/views/result.html").toString(), new String(response.content(), "UTF-8"));
    }

    @After
    public void tearDown() throws Exception {
        esSetup.terminate();
    }

}
