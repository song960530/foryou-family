package com.foryou.matchingservice.global.config;

import com.foryou.matchingservice.api.dto.request.PaymentRequestMessage;
import com.foryou.matchingservice.api.dto.response.MatchingResultMessage;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> makeConfigProps() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return configProps;
    }

    @Bean
    public ProducerFactory<String, MatchingResultMessage> producerFactory() {
        return new DefaultKafkaProducerFactory<>(makeConfigProps());
    }

    @Bean
    public ProducerFactory<String, PaymentRequestMessage> producerPaymentRequestFactory() {
        return new DefaultKafkaProducerFactory<>(makeConfigProps());
    }

    @Bean
    @Qualifier("KafkaTemplateMatchingResult")
    public KafkaTemplate<String, MatchingResultMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    @Qualifier("KafkaTemplatePaymentRequest")
    public KafkaTemplate<String, PaymentRequestMessage> kafkaPaymentRequestTemplate() {
        return new KafkaTemplate<>(producerPaymentRequestFactory());
    }
}
