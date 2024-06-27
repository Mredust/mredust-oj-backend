package com.mredust.oj.rabbitmq;

import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.enums.problem.JudgeInfoEnum;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;
import com.mredust.oj.service.ProblemSubmitService;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.mredust.oj.constant.MqConstant.DL_QUEUE;


/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Slf4j
@Component
public class DLMessageConsumer {
    @Resource
    private ProblemSubmitService problemSubmitService;
    
    /**
     * 监听死信队列
     *
     * @param message     消息
     * @param channel     通道
     * @param deliveryTag 传送标签
     */
    @SneakyThrows
    @RabbitListener(queues = DL_QUEUE)
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("死信队列接受到的消息：{}", message);
        if (StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "消息为空");
        }
        long problemSubmitId = Long.parseLong(message);
        ProblemSubmit problemSubmit = problemSubmitService.getById(problemSubmitId);
        if (problemSubmit == null) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "提交的题目信息不存在");
        }
        // 把提交题目标为失败
        problemSubmit.setStatus(ProblemSubmitStatusEnum.FAILED.getCode());
        problemSubmit.setMessage(JudgeInfoEnum.WRONG_ANSWER.getText());
        boolean update = problemSubmitService.updateById(problemSubmit);
        if (!update) {
            log.info("处理死信队列消息失败,对应提交的题目id为:{}", problemSubmit.getId());
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "处理死信队列消息失败");
        }
        // 确认消息
        channel.basicAck(deliveryTag, false);
    }
}
