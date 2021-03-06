/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.modeshape.jcr;

import static junit.framework.Assert.fail;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.modeshape.common.util.FileUtil;
import org.modeshape.jcr.value.binary.infinispan.InfinispanTestUtil;

public class InfinispanUtilTest {

    private static DefaultCacheManager cacheManager;

    private static Configuration LOCAL;
    private static Configuration LOCAL_STORE;
    private static Configuration DIST;
    private static Configuration DIST_STORE;
    private static Configuration DIST_STORE_UNSHARED;

    @BeforeClass
    public static void beforeClass() throws Exception {
        cacheManager = InfinispanTestUtil.beforeClassStartup(true);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.clustering().cacheMode(CacheMode.LOCAL);
        LOCAL = configurationBuilder.build();
        configurationBuilder.clustering().cacheMode(CacheMode.DIST_SYNC);
        DIST = configurationBuilder.build();

        // store config
        File dir = new File(System.getProperty("java.io.tmpdir"), "InfinispanLocalBinaryStoreWithPersistenceTest");
        if (dir.exists()) FileUtil.delete(dir);
        dir.mkdirs();
        configurationBuilder.loaders().shared(true);
        configurationBuilder.loaders().addFileCacheStore().purgeOnStartup(true).location(dir.getAbsolutePath());

        configurationBuilder.clustering().cacheMode(CacheMode.LOCAL);
        LOCAL_STORE = configurationBuilder.build();
        configurationBuilder.clustering().cacheMode(CacheMode.DIST_SYNC);
        DIST_STORE = configurationBuilder.build();
        configurationBuilder.loaders().shared(false);
        DIST_STORE_UNSHARED = configurationBuilder.build();

        // define caches
        cacheManager.defineConfiguration("LOCAL", LOCAL);
        cacheManager.defineConfiguration("LOCAL_STORE", LOCAL_STORE);
        cacheManager.defineConfiguration("DIST", DIST);
        cacheManager.defineConfiguration("DIST_STORE", DIST_STORE);
        cacheManager.defineConfiguration("DIST_STORE_UNSHARED", DIST_STORE_UNSHARED);
        cacheManager.start();
    }

    @AfterClass
    public static void afterClass() {
        InfinispanTestUtil.afterClassShutdown(cacheManager);
        org.infinispan.test.TestingUtil.recursiveFileRemove(new File(System.getProperty("java.io.tmpdir"), "InfinispanTestUtil"));
    }

    private void checkSequence( InfinispanUtil.Sequence<String> sequence,
                                String... expected ) throws Exception {
        int index = 0;
        List<String> keys = new ArrayList<String>(expected.length);
        while (true) {
            String key = sequence.next();
            if (key == null) {
                break;
            }
            keys.add(key);
        }

        if (keys.size() != expected.length) {
            fail("Sequence contains wrong elements. Expected: " + expected.length + " Contained: " + index);
        }
        for (String key : expected) {
            if (!keys.contains(key)) {
                fail("Missing key in sequence: " + key);
            }
        }
    }

    private void standardTest( String cacheName ) throws Exception {
        Cache<String, String> cache = cacheManager.getCache(cacheName);
        cache.put("Foo", "Bar");

        checkSequence(InfinispanUtil.getAllKeys(cache), "Foo");
    }

    @Test
    public void testLocal() throws Exception {
        standardTest("LOCAL");
    }

    @Test
    public void testLocalStore() throws Exception {
        standardTest("LOCAL_STORE");
    }

    @Test
    public void testDist() throws Exception {
        standardTest("DIST");
    }

    @Test
    public void testDistStore() throws Exception {
        standardTest("DIST_STORE");
    }

    @Test
    public void testDistStoreUnshared() throws Exception {
        standardTest("DIST_STORE_UNSHARED");
    }
}
