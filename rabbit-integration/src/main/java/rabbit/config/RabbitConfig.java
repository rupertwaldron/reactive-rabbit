package rabbit.config;


import org.reactivestreams.Publisher;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.amqp.dsl.AmqpInboundChannelAdapterSMLCSpec;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.rsocket.RSocketRequester;
import rabbit.models.PersonDto;
import rabbit.service.PersonService;
import rabbit.transformers.MessageConverter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Executor;

@Configuration
public class RabbitConfig {
    private static final String INCOMING_MESSAGE_CHANNEL = "incoming_message_channel";
private static final String ENRICHMENT_MESSAGE_CHANNEL = "enrichment_message_channel";
    private static int personCount;

    @Autowired
    PersonService personService;

    @Autowired
    MessageConverter messageConverter;

//    @Autowired
//    ConnectionFactory rabbitConnectionFactory;

    @Bean(name = INCOMING_MESSAGE_CHANNEL)
    public MessageChannel getMessageChannel() {
        return MessageChannels.direct().get();
    }


//    @Bean
//    public IntegrationFlow amqpInbound() {
//        return IntegrationFlows.from(Amqp.inboundAdapter(workListenerContainer()))
//                .log(LoggingHandler.Level.INFO)
//                .transform(messageConverter, "extractObject")
//                .log(LoggingHandler.Level.INFO)
//                .channel(INCOMING_MESSAGE_CHANNEL)
//                .get();
//    }

    @Bean
    public Publisher<Message<PersonDto>> amqpInbound(ConnectionFactory rabbitConnectionFactory) {
        return IntegrationFlows.from(Amqp.inboundAdapter(rabbitConnectionFactory, "aName"))
                .transform(messageConverter, "wrapHeaders")
                .transform(messageConverter, "extractPerson")
                .log()
                .toReactivePublisher();

//                .log(LoggingHandler.Level.INFO)
//                .channel(INCOMING_MESSAGE_CHANNEL)
//                .get();
    }


    @Bean
    public IntegrationFlow processMessage() {
        return IntegrationFlows.from(INCOMING_MESSAGE_CHANNEL)
                .transform(Message.class, m -> {
                    ((PersonDto) m.getPayload()).setAge(20);
                    return m;
                })
                .transform(messageConverter, "changeAge")
                .channel(ENRICHMENT_MESSAGE_CHANNEL)
//                .transform(messageConverter, "enrichObject") //need to run Chunky on 8088
//                .handle(m -> personService.addPerson((Person) m.getPayload()))
                .get();
    }

    @Bean
    public IntegrationFlow enrichmentFlow() {
        return IntegrationFlows.from(ENRICHMENT_MESSAGE_CHANNEL)
                .transform(messageConverter, "enrichObject")
                .log(LoggingHandler.Level.INFO)
                .handle(m -> personService.addPerson((PersonDto) m.getPayload()))
                .get();
    }




    @Bean(name = ENRICHMENT_MESSAGE_CHANNEL)
    public ExecutorChannel enrichmentChannel() {
        return new ExecutorChannel(getAsyncExecutor());
    }

    @Bean
    public Executor getAsyncExecutor() {
        return new SimpleAsyncTaskExecutor();
    }


//    @Bean
//    public SimpleMessageListenerContainer workListenerContainer() {
//        SimpleMessageListenerContainer container =
//                new SimpleMessageListenerContainer(rabbitConnectionFactory);
//        container.setQueues(worksQueue());
//        //container.setPrefetchCount(1);
//        //container.setConcurrentConsumers(2);
//        container.setDefaultRequeueRejected(false);
////        container.setAdviceChain(new Advice[]{interceptor()});
//        return container;
//    }

    @Bean
    public Queue worksQueue() {
        return QueueBuilder.durable("aName")
                .build();
    }

}
