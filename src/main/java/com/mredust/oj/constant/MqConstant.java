package com.mredust.oj.constant;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface MqConstant {
    /**
     * judge 交换机
     */
    String JUDGE_EXCHANGE = "judge.exchange";
    /**
     * judge 队列
     */
    String JUDGE_QUEUE = "judge.queue";
    
    /**
     * 判题队列路由键
     */
    String JUDGE_QUEUE_ROUTING_KEY = "judge.queue.key";
    
    /**
     * 死信交换机
     */
    String DL_EXCHANGE = "dl.exchange";
    /**
     * 死信队列
     */
    String DL_QUEUE = "dl.queue";
    
    /**
     * 死信队列路由键
     */
    String DL_JUDGE_QUEUE_ROUTING_KEY = "dl.judge.queue.key";
}
