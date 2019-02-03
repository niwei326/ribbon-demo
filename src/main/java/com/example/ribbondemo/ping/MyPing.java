package com.example.ribbondemo.ping;

import com.netflix.loadbalancer.IPing;

import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MyPing implements IPing{
    private Logger logger = LoggerFactory.getLogger(MyPing.class);
    public static boolean mockDead = true;

    public boolean isAlive(Server server){
        if(80 == server.getPort()) {
            logger.info("isAlive " + server.getHostPort());
            return true;
        }else if(mockDead){
            logger.info("isDead " + server.getHostPort());
            return false;
        }else{
            logger.info("isAlive " + server.getHostPort());
            return true;
        }

    }

}