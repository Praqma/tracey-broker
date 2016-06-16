/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.praqma.tracey.broker.rabbitmq;

/**
 *
 * @author Mads
 */
public class TraceyRabbitMQConfig {
    public String host = "localhost";
    public String exchange = "tracey";
    public String queue = "default";
    public boolean durable = true;
}
