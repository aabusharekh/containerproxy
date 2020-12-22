/**
 * ContainerProxy
 *
 * Copyright (C) 2016-2020 Open Analytics
 *
 * ===========================================================================
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Apache License as published by
 * The Apache Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Apache License for more details.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/>
 */
package eu.openanalytics.containerproxy.test.e2e.app_recovery;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.json.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TestAppRecovery {

    @Test
    public void simple_recover_single_app_after_shutdown() throws IOException, InterruptedException {
        ShinyProxyClient shinyProxyClient = new ShinyProxyClient("demo", "demo");
        List<ShinyProxyInstance> instances = new ArrayList<>();
        try {
            // 1. create the instance
            ShinyProxyInstance instance1 = new ShinyProxyInstance("1", "application-app-recovery_docker.yml");
            instances.add(instance1);
            Assertions.assertTrue(instance1.start());

            // 2. create a proxy
            String id = shinyProxyClient.startProxy("01_hello");
            Assertions.assertNotNull(id);

            // 3. get defined proxies
            HashSet<JsonObject> originalProxies = shinyProxyClient.getProxies();
            Assertions.assertNotNull(originalProxies);

            // 4. stop the instance
            instance1.stop();

            // 5. start the instance again
            ShinyProxyInstance instance2 = new ShinyProxyInstance("2", "application-app-recovery_docker.yml");
            instances.add(instance2);
            Assertions.assertTrue(instance2.start());

            // 6. get defined proxies
            HashSet<JsonObject> newProxies = shinyProxyClient.getProxies();
            Assertions.assertNotNull(newProxies);

            // 7. assert that the responses are equal
            Assertions.assertEquals(originalProxies, newProxies);

            // 8. stop the proxy
            Assertions.assertTrue(shinyProxyClient.stopProxy(id));

            // 9. stop the instance
            instance2.stop();
        } finally {
            instances.forEach(ShinyProxyInstance::stop);
        }
    }

    @Test
    public void complex_recover_multiple_apps_after_shutdown() throws IOException, InterruptedException {
        ShinyProxyClient shinyProxyClient1 = new ShinyProxyClient("demo", "demo");
        ShinyProxyClient shinyProxyClient2 = new ShinyProxyClient("demo2", "demo2");
        ShinyProxyClient shinyProxyClient3 = new ShinyProxyClient("demo3", "demo3");

        List<ShinyProxyInstance> instances = new ArrayList<>();
        try {
            // 1. create the instance
            ShinyProxyInstance instance1 = new ShinyProxyInstance("1", "application-app-recovery_docker.yml");
            instances.add(instance1);
            Assertions.assertTrue(instance1.start());


            // 2. create two proxies for user demo
            String id1 = shinyProxyClient1.startProxy("01_hello");
            Assertions.assertNotNull(id1);

            String id2 = shinyProxyClient1.startProxy("02_hello");
            Assertions.assertNotNull(id2);

            // 3. get defined proxies
            HashSet<JsonObject> originalProxies1 = shinyProxyClient1.getProxies();
            Assertions.assertNotNull(originalProxies1);


            // 4. create two proxies for user demo
            String id3 = shinyProxyClient2.startProxy("01_hello");
            Assertions.assertNotNull(id3);

            String id4 = shinyProxyClient2.startProxy("02_hello");
            Assertions.assertNotNull(id4);

            // 5. get defined proxies
            HashSet<JsonObject> originalProxies2 = shinyProxyClient2.getProxies();
            Assertions.assertNotNull(originalProxies2);


            // 6. create two proxies for user demo
            String id5 = shinyProxyClient3.startProxy("01_hello");
            Assertions.assertNotNull(id5);

            String id6 = shinyProxyClient3.startProxy("02_hello");
            Assertions.assertNotNull(id6);

            // 7. get defined proxies
            HashSet<JsonObject> originalProxies3 = shinyProxyClient3.getProxies();
            Assertions.assertNotNull(originalProxies3);


            // 8. stop the instance
            instance1.stop();

            // 9. start the instance again
            ShinyProxyInstance instance2 = new ShinyProxyInstance("2", "application-app-recovery_docker.yml");
            instances.add(instance2);
            Assertions.assertTrue(instance2.start());

            // 10. get defined proxies for user demo
            HashSet<JsonObject> newProxies1 = shinyProxyClient1.getProxies();
            Assertions.assertNotNull(newProxies1);

            // 11. assert that the responses are equal
            Assertions.assertEquals(originalProxies1, newProxies1);


            // 12. get defined proxies for user demo2
            HashSet<JsonObject> newProxies2 = shinyProxyClient2.getProxies();
            Assertions.assertNotNull(newProxies2);

            // 13. assert that the responses are equal
            Assertions.assertEquals(originalProxies2, newProxies2);


            // 14. get defined proxies for user demo2
            HashSet<JsonObject> newProxies3 = shinyProxyClient3.getProxies();
            Assertions.assertNotNull(newProxies3);

            // 15. assert that the responses are equal
            Assertions.assertEquals(originalProxies3, newProxies3);


            // 16. stop the proxies
            Assertions.assertTrue(shinyProxyClient1.stopProxy(id1));
            Assertions.assertTrue(shinyProxyClient1.stopProxy(id2));
            Assertions.assertTrue(shinyProxyClient2.stopProxy(id3));
            Assertions.assertTrue(shinyProxyClient2.stopProxy(id4));
            Assertions.assertTrue(shinyProxyClient3.stopProxy(id5));
            Assertions.assertTrue(shinyProxyClient3.stopProxy(id6));

            // 17. stop the instance
            instance2.stop();
        } finally {
            instances.forEach(ShinyProxyInstance::stop);
        }
    }

    @Test
    public void simple_recover_multiple_instances() throws IOException, InterruptedException {
        ShinyProxyClient shinyProxyClient1 = new ShinyProxyClient("demo", "demo", 7583);
        ShinyProxyClient shinyProxyClient2 = new ShinyProxyClient("demo", "demo", 7584);
        List<ShinyProxyInstance> instances = new ArrayList<>();
        try {
            // 1. create the first instance
            ShinyProxyInstance instance1 = new ShinyProxyInstance("1", "application-app-recovery_docker.yml", 7583);
            instances.add(instance1);
            Assertions.assertTrue(instance1.start());

            // 1. create the second instance
            ShinyProxyInstance instance2 = new ShinyProxyInstance("2", "application-app-recovery_docker_2.yml", 7584);
            instances.add(instance2);
            Assertions.assertTrue(instance2.start());

            // 2. create a proxy on both instances
            String id1 = shinyProxyClient1.startProxy("01_hello");
            Assertions.assertNotNull(id1);

            String id2 = shinyProxyClient2.startProxy("01_hello");
            Assertions.assertNotNull(id2);

            // 3. get defined proxies
            HashSet<JsonObject> originalProxies1 = shinyProxyClient1.getProxies();
            Assertions.assertNotNull(originalProxies1);

            HashSet<JsonObject> originalProxies2 = shinyProxyClient2.getProxies();
            Assertions.assertNotNull(originalProxies2);

            // 4. stop both instances
            instance1.stop();
            instance2.stop();

            // 5. start both instances again
            ShinyProxyInstance instance3 = new ShinyProxyInstance("3", "application-app-recovery_docker.yml", 7583);
            instances.add(instance3);
            Assertions.assertTrue(instance3.start());

            ShinyProxyInstance instance4 = new ShinyProxyInstance("4", "application-app-recovery_docker_2.yml", 7584);
            instances.add(instance4);
            Assertions.assertTrue(instance4.start());

            // 6. get defined proxies
            HashSet<JsonObject> newProxies1 = shinyProxyClient1.getProxies();
            Assertions.assertNotNull(newProxies1);

            HashSet<JsonObject> newProxies2 = shinyProxyClient2.getProxies();
            Assertions.assertNotNull(newProxies2);

            // 7. assert that the responses are equal
            Assertions.assertEquals(originalProxies1, newProxies1);
            Assertions.assertEquals(originalProxies2, newProxies2);

            // 8. stop the proxy
            Assertions.assertTrue(shinyProxyClient1.stopProxy(id1));
            Assertions.assertTrue(shinyProxyClient2.stopProxy(id2));

            // 9. stop the instance
            instance3.stop();
            instance4.stop();
        } finally {
            instances.forEach(ShinyProxyInstance::stop);
        }

    }

}
