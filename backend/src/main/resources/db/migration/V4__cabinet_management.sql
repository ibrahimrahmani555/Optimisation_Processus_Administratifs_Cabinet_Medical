CREATE UNIQUE INDEX uk_cabinets_name_lower ON cabinets (LOWER(name));
CREATE INDEX idx_cabinets_active ON cabinets (active);
