CREATE TABLE patients (
    id UUID PRIMARY KEY,
    cabinet_id UUID NOT NULL REFERENCES cabinets(id),
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_patients_cabinet ON patients(cabinet_id);
CREATE INDEX idx_patients_created_at ON patients(created_at);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    actor_id UUID REFERENCES users(id),
    actor_name VARCHAR(170),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    description VARCHAR(500) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_occurred_at ON audit_logs(occurred_at DESC);
