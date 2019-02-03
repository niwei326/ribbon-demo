package com.example.ribbondemo;

import com.example.ribbondemo.ping.MyPing;
import com.netflix.client.ClientException;
import com.netflix.client.ClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.PingUrl;
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
public class RibbonPingTests {

    @Test
    public void contextLoads() throws IOException, InterruptedException, URISyntaxException, ClientException {
        ConfigurationManager.loadPropertiesFromResources("ribbon.properties");  // 1
        System.out.println(ConfigurationManager.getConfigInstance().getProperty("sample-client.ribbon.listOfServers"));
        RestClient client = (RestClient) ClientFactory.getNamedClient("sample-client");  // 2
        Thread.sleep(60000);
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
        MyPing.mockDead = false;
        Thread.sleep(10000); // 6
        for (int i = 0; i < 20; i++)  {
            Server server = lb.chooseServer("sample-client");
            System.out.println("choose server2:" + server.getHost()+":"+server.getPort());
        }
        MyPing.mockDead = true;
        Thread.sleep(10000); // 6
        for (int i = 0; i < 20; i++)  {
            Server server = lb.chooseServer("sample-client");
            System.out.println("choose server3:" + server.getHost()+":"+server.getPort());
        }

        System.out.println(lb.getLoadBalancerStats()); // 7
        Thread.sleep(30000);
    }

    @Test
    public void testPingUrl(){
        PingUrl p = new PingUrl(false,"/PlatDemo/alive");
        p.setExpectedContent("true");
        Server s = new Server("localhost", 8080);
        boolean isAlive = p.isAlive(s);
        System.out.println("isAlive:" + isAlive);
    }
}

