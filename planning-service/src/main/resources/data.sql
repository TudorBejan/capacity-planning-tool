-- Sample initiatives
INSERT INTO initiatives (id, name, description, status, priority, target_date, created_at) VALUES
  ('c1000000-0000-0000-0000-000000000001', 'Customer Portal v2',        'Redesign the self-service portal with modern UX',              'ACTIVE',    'HIGH',     '2025-06-30', NOW()),
  ('c1000000-0000-0000-0000-000000000002', 'Data Platform Migration',   'Migrate legacy pipelines to the new data lakehouse',          'ACTIVE',    'CRITICAL', '2025-09-30', NOW()),
  ('c1000000-0000-0000-0000-000000000003', 'Mobile App Launch',         'First release of the companion mobile app',                   'ACTIVE',    'HIGH',     '2025-08-31', NOW()),
  ('c1000000-0000-0000-0000-000000000004', 'SOC2 Compliance',           'Achieve SOC2 Type II certification',                          'ACTIVE',    'CRITICAL', '2025-12-31', NOW()),
  ('c1000000-0000-0000-0000-000000000005', 'AI-Assisted Support',       'Integrate LLM-based tier-1 support chatbot',                  'DRAFT',     'MEDIUM',   '2026-03-31', NOW()),
  ('c1000000-0000-0000-0000-000000000006', 'Platform Observability',    'Full distributed tracing and SLO dashboards',                 'ACTIVE',    'MEDIUM',   '2025-07-31', NOW());

-- Sample epics
INSERT INTO epics (id, initiative_id, team_id, name, description, status, estimated_weeks, start_date, due_date, created_at) VALUES
  -- Customer Portal v2
  ('d1000000-0000-0000-0000-000000000001', 'c1000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000002', 'New Design System',           'Tokens, components, docs',           'IN_PROGRESS', 6,  '2025-01-06', '2025-02-14', NOW()),
  ('d1000000-0000-0000-0000-000000000002', 'c1000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000003', 'Auth & SSO Revamp',           'OIDC + SAML integration',            'IN_PROGRESS', 4,  '2025-01-13', '2025-02-07', NOW()),
  ('d1000000-0000-0000-0000-000000000003', 'c1000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000002', 'Dashboard Rebuild',           'Customer-facing analytics views',    'PLANNED',     8,  '2025-02-17', '2025-04-11', NOW()),
  ('d1000000-0000-0000-0000-000000000004', 'c1000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000003', 'API Gateway v2',              'REST + GraphQL layer',               'PLANNED',     5,  '2025-02-03', '2025-03-07', NOW()),

  -- Data Platform Migration
  ('d1000000-0000-0000-0000-000000000005', 'c1000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000004', 'Lakehouse Setup',             'Delta Lake + Spark cluster',         'IN_PROGRESS', 6,  '2025-01-06', '2025-02-14', NOW()),
  ('d1000000-0000-0000-0000-000000000006', 'c1000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000004', 'ETL Pipeline Migration',      'Port 40 legacy pipelines',           'PLANNED',     12, '2025-02-17', '2025-05-09', NOW()),
  ('d1000000-0000-0000-0000-000000000007', 'c1000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000003', 'Data API Layer',              'REST endpoints for data access',     'PLANNED',     4,  '2025-04-01', '2025-04-25', NOW()),

  -- Mobile App
  ('d1000000-0000-0000-0000-000000000008', 'c1000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000005', 'iOS Core App',                'Auth, navigation, core screens',     'IN_PROGRESS', 8,  '2025-01-13', '2025-03-07', NOW()),
  ('d1000000-0000-0000-0000-000000000009', 'c1000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000005', 'Android Core App',            'Parity with iOS build',              'IN_PROGRESS', 8,  '2025-01-13', '2025-03-07', NOW()),
  ('d1000000-0000-0000-0000-000000000010', 'c1000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000003', 'Mobile Backend APIs',         'Push notifications, mobile auth',    'PLANNED',     5,  '2025-01-20', '2025-02-21', NOW()),

  -- SOC2
  ('d1000000-0000-0000-0000-000000000011', 'c1000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000001', 'Secrets Management',          'Vault integration across services',  'IN_PROGRESS', 4,  '2025-01-06', '2025-01-31', NOW()),
  ('d1000000-0000-0000-0000-000000000012', 'c1000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000001', 'Audit Logging Pipeline',      'Immutable audit log infrastructure', 'PLANNED',     6,  '2025-02-03', '2025-03-14', NOW()),
  ('d1000000-0000-0000-0000-000000000013', 'c1000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000003', 'RBAC Overhaul',               'Fine-grained permission system',     'PLANNED',     8,  '2025-02-03', '2025-03-28', NOW()),

  -- Platform Observability
  ('d1000000-0000-0000-0000-000000000014', 'c1000000-0000-0000-0000-000000000006', 'a1000000-0000-0000-0000-000000000001', 'Distributed Tracing',         'OpenTelemetry + Jaeger setup',       'IN_PROGRESS', 5,  '2025-01-06', '2025-02-07', NOW()),
  ('d1000000-0000-0000-0000-000000000015', 'c1000000-0000-0000-0000-000000000006', 'a1000000-0000-0000-0000-000000000001', 'SLO Dashboard',               'Grafana dashboards per service',     'PLANNED',     3,  '2025-02-10', '2025-02-28', NOW());
