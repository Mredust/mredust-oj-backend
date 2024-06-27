package com.mredust.oj.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.mredust.oj.constant.MqConstant.*;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Configuration
public class RabbitMQConfig {
    
    // 判题
    @Bean("judgeExchange")
    public FanoutExchange judgeExchange() {
        return new FanoutExchange(JUDGE_EXCHANGE);
    }
    
    @Bean("judgeQueue")
    public Queue judgeQueue() {
        Queue queue = new Queue(JUDGE_QUEUE);
        queue.addArgument("x-dead-letter-exchange", DL_EXCHANGE);
        queue.addArgument("x-dead-letter-routing-key", DL_JUDGE_QUEUE_ROUTING_KEY);
        return queue;
    }
    
    @Bean
    public Binding bindingQueueWithExchange(@Qualifier("judgeQueue") Queue queue,
                                            @Qualifier("judgeExchange") FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queue).to(fanoutExchange);
    }
    
    // 死信
    @Bean("dlExchange")
    public DirectExchange dlExchange() {
        return new DirectExchange(DL_EXCHANGE);
    }
    
    @Bean("dlQueue")
    public Queue dlQueue() {
        return new Queue(DL_QUEUE);
    }
    
    @Bean
    public Binding bindingDLQueueWithDLExchange(@Qualifier("dlQueue") Queue queue,
                                                @Qualifier("dlExchange") DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(DL_JUDGE_QUEUE_ROUTING_KEY);
    }
    
}
