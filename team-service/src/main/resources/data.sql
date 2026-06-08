-- Sample teams
INSERT INTO teams (id, name, description, overhead_percentage, created_at) VALUES
  ('a1000000-0000-0000-0000-000000000001', 'Platform',   'Core infrastructure and DevOps', 30, NOW()),
  ('a1000000-0000-0000-0000-000000000002', 'Frontend',   'Web UI and design system',       25, NOW()),
  ('a1000000-0000-0000-0000-000000000003', 'Backend',    'API and business logic',         25, NOW()),
  ('a1000000-0000-0000-0000-000000000004', 'Data',       'Analytics and data pipelines',   20, NOW()),
  ('a1000000-0000-0000-0000-000000000005', 'Mobile',     'iOS and Android apps',           30, NOW());

-- Sample persons
INSERT INTO persons (id, team_id, name, role, availability_percentage, created_at) VALUES
  -- Platform team
  ('b1000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000001', 'Alice Müller',    'Tech Lead',         100, NOW()),
  ('b1000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000001', 'Bob Andersen',   'Senior Engineer',   100, NOW()),
  ('b1000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001', 'Clara Johansson','Engineer',           80, NOW()),
  -- Frontend team
  ('b1000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000002', 'David Kim',      'Tech Lead',         100, NOW()),
  ('b1000000-0000-0000-0000-000000000005', 'a1000000-0000-0000-0000-000000000002', 'Elena Rossi',    'Senior Engineer',   100, NOW()),
  ('b1000000-0000-0000-0000-000000000006', 'a1000000-0000-0000-0000-000000000002', 'Frank Dubois',   'Engineer',          100, NOW()),
  ('b1000000-0000-0000-0000-000000000007', 'a1000000-0000-0000-0000-000000000002', 'Grace O''Brien', 'Engineer',           60, NOW()),
  -- Backend team
  ('b1000000-0000-0000-0000-000000000008', 'a1000000-0000-0000-0000-000000000003', 'Henry Chen',     'Tech Lead',         100, NOW()),
  ('b1000000-0000-0000-0000-000000000009', 'a1000000-0000-0000-0000-000000000003', 'Ingrid Patel',   'Senior Engineer',   100, NOW()),
  ('b1000000-0000-0000-0000-000000000010', 'a1000000-0000-0000-0000-000000000003', 'James Wilson',   'Engineer',          100, NOW()),
  ('b1000000-0000-0000-0000-000000000011', 'a1000000-0000-0000-0000-000000000003', 'Karen Nguyen',   'Engineer',          100, NOW()),
  ('b1000000-0000-0000-0000-000000000012', 'a1000000-0000-0000-0000-000000000003', 'Leo Martinez',   'Junior Engineer',    80, NOW()),
  -- Data team
  ('b1000000-0000-0000-0000-000000000013', 'a1000000-0000-0000-0000-000000000004', 'Mia Larsson',    'Tech Lead',         100, NOW()),
  ('b1000000-0000-0000-0000-000000000014', 'a1000000-0000-0000-0000-000000000004', 'Noah Fischer',   'Data Engineer',     100, NOW()),
  ('b1000000-0000-0000-0000-000000000015', 'a1000000-0000-0000-0000-000000000004', 'Olivia Brown',   'Data Scientist',    100, NOW()),
  -- Mobile team
  ('b1000000-0000-0000-0000-000000000016', 'a1000000-0000-0000-0000-000000000005', 'Paul Schmidt',   'Tech Lead',         100, NOW()),
  ('b1000000-0000-0000-0000-000000000017', 'a1000000-0000-0000-0000-000000000005', 'Quinn Taylor',   'iOS Engineer',      100, NOW()),
  ('b1000000-0000-0000-0000-000000000018', 'a1000000-0000-0000-0000-000000000005', 'Rachel Lee',     'Android Engineer',  100, NOW());
