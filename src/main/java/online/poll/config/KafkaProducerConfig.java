package online.poll.config;

import online.poll.web.dto.KafkaMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    String kafkaServer;

    @Bean
    public KafkaTemplate<String, KafkaMessage> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<String, KafkaMessage> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProducerConfiguration());
    }

    @Bean
    public Map<String, Object> kafkaProducerConfiguration() {
        return Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class, // key, value 직렬화를 위해 사용, key 는 메시지를 보내면 토픽의 파티션이 지정될 때 쓰인다.
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
    }
}
