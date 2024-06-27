package com.mredust.oj.rabbitmq;

import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.judge.JudgeService;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.mredust.oj.constant.MqConstant.JUDGE_QUEUE;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Component
@Slf4j
public class JudgeMessageConsumer {
    
    @Resource
    private JudgeService judgeService;
    
    @SneakyThrows
    @RabbitListener(queues = JUDGE_QUEUE)
    public void handleJudgeMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receive judge message = {}", message);
        if (StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ResponseCode.FAIL, "消息为空");
        }
        long problemSubmitId = Long.parseLong(message);
        
        try {
            ProblemSubmit problemSubmit = judgeService.handleJudge(problemSubmitId);
            if (!ProblemSubmitStatusEnum.SUCCEED.getCode().equals(problemSubmit.getStatus())) {
                log.error("judge failed, problemSubmit = {}", problemSubmitId);
                throw new BusinessException(ResponseCode.FAIL, "判题失败");
            }
            log.info("judge success, problemSubmit = {}", problemSubmitId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
            throw new RuntimeException(e);
        }
    }
}
