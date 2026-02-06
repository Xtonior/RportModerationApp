package kz.lab.moderationapp.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import kz.lab.moderationapp.model.ReportEvent;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Value("${kafka.bootstrap-server}")
    private String kafkaServer;

    @Value("${kafka.topics.topic-1}")
    private String topic1;

    @Value("${kafka.topics.topic-2}")
    private String topic2;

    private final ObjectMapper objectMapper;

    public KafkaConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler((record, exception) -> {
            System.err.println("Kafka error");
            System.err.println("Data: " + record.value());
            System.err.println("Reason: " + exception.getMessage());
            exception.printStackTrace();
        }, new FixedBackOff(1000L, 2)));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        JacksonJsonDeserializer<Object> deserializer = new JacksonJsonDeserializer<>();
        deserializer.addTrustedPackages("kz.lab.moderationapp.*");

        deserializer.setTypeResolver((topic, data, headers) -> {
            if (topic.equals(topic1) || topic.equals(topic2)) {
                return objectMapper.getTypeFactory().constructType(ReportEvent.class);
            }
            return objectMapper.getTypeFactory().constructType(Object.class);
        });

        ErrorHandlingDeserializer<Object> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(deserializer);

        return new DefaultKafkaConsumerFactory<>(
                consumerProps(),
                new StringDeserializer(),
                errorHandlingDeserializer);
    }

    private Map<String, Object> consumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "moderator-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(senderProps());
    }

    private Map<String, Object> senderProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        props.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, true);

        return props;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
