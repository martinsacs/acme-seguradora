package com.acme.seguro.cotacoes.producer;


import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CotacaoKafkaProducer {

    private static final String TOPIC = "topico-cotacoes_seguro";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        kafkaTemplate.send(TOPIC, message);
        System.out.println("Mensagem enviada para o tópico " + TOPIC + ": " + message);
    }
}

