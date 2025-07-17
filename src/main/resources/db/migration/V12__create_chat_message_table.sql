-- Tabla de mensajes de chat entre cliente y profesional
CREATE TABLE chat_message (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_appointment_chat FOREIGN KEY (appointment_id) REFERENCES appointment(id) ON DELETE CASCADE,
    CONSTRAINT fk_sender_chat FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_receiver_chat FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE
); 