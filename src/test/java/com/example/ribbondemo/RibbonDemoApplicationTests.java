package com.example.ribbondemo;

import com.netflix.client.ClientException;
import com.netflix.client.ClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.client.http.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RibbonDemoApplicationTests {

    @Test
    public void contextLoads() throws IOException, InterruptedException, URISyntaxException, ClientException {
        ConfigurationManager.loadPropertiesFromResources("ribbon.properties");  // 1
        System.out.println(ConfigurationManager.getConfigInstance().getProperty("sample-client.ribbon.listOfServers"));
        RestClient client = (RestClient) ClientFactory.getNamedClient("sample-client");  // 2
        HttpRequest request = HttpRequest.newBuilder().uri(new URI("/")).build(); // 3
        for (int i = 0; i < 20; i++)  {
            HttpResponse response = client.executeWithLoadBalancer(request); // 4
            System.out.println("Status code for " + response.getRequestedURI() + "  :" + response.getStatus());
        }
        BaseLoadBalancer lb = (BaseLoadBalancer) client.getLoadBalancer();
        System.out.println(lb.getLoadBalancerStats());
        for (int i = 0; i < 20; i++)  {
            Server server = lb.chooseServer("sample-client");
            System.out.println("choose server:" + server.getHost()+":"+server.getPort());
        }
        ConfigurationManager.getConfigInstance().setProperty(
                "sample-client.ribbon.listOfServers", "www.microsoft.com:80,www.baosight.com:80,localhost:8080"); // 5
        System.out.println("changing servers ...");
        Thread.sleep(3000); // 6
        for (int i = 0; i < 20; i++)  {
            HttpResponse response = null;
            try {
                response = client.executeWithLoadBalancer(request);
                System.out.println("Status code for " + response.getRequestedURI() + "  : " + response.getStatus());
            } catch(Exception e){
                e.printStackTrace();
            }
            finally {
                if (response != null) {
                    response.close();
                }
            }
        }
        for (int i = 0; i < 20; i++)  {
            Server server = lb.chooseServer("sample-client");
            System.out.println("choose server:" + server.getHost()+":"+server.getPort());
        }
        System.out.println(lb.getLoadBalancerStats()); // 7
    }

}

