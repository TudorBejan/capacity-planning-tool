const BASE = import.meta.env.PROD ? '' : import.meta.env.VITE_TEAM_SERVICE_URL;
const PLANNING_BASE = import.meta.env.PROD ? '' : import.meta.env.VITE_PLANNING_SERVICE_URL;

async function req(url, options = {}) {
  const res = await fetch(url, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
    body: options.body ? JSON.stringify(options.body) : undefined,
  });
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
  if (res.status === 204) return null;
  return res.json();
}

// ── Teams ────────────────────────────────────────────────────────────────────
export const getTeams = () => req(`${BASE}/api/teams`);
export const getTeam  = (id) => req(`${BASE}/api/teams/${id}`);
export const createTeam = (data) => req(`${BASE}/api/teams`, { method: 'POST', body: data });
export const updateTeam = (id, data) => req(`${BASE}/api/teams/${id}`, { method: 'PUT', body: data });
export const deleteTeam = (id) => req(`${BASE}/api/teams/${id}`, { method: 'DELETE' });
export const getTeamCapacity = (id, start, end) =>
  req(`${BASE}/api/teams/${id}/capacity?startDate=${start}&endDate=${end}`);

// ── Persons ──────────────────────────────────────────────────────────────────
export const getPersonsByTeam = (teamId) => req(`${BASE}/api/persons/by-team/${teamId}`);
export const createPerson = (data) => req(`${BASE}/api/persons`, { method: 'POST', body: data });
export const updatePerson = (id, data) => req(`${BASE}/api/persons/${id}`, { method: 'PUT', body: data });
export const deletePerson = (id) => req(`${BASE}/api/persons/${id}`, { method: 'DELETE' });

// ── Initiatives ───────────────────────────────────────────────────────────────
export const getInitiatives = () => req(`${PLANNING_BASE}/api/initiatives`);
export const getInitiative  = (id) => req(`${PLANNING_BASE}/api/initiatives/${id}`);
export const createInitiative = (data) => req(`${PLANNING_BASE}/api/initiatives`, { method: 'POST', body: data });
export const updateInitiative = (id, data) => req(`${PLANNING_BASE}/api/initiatives/${id}`, { method: 'PUT', body: data });
export const deleteInitiative = (id) => req(`${PLANNING_BASE}/api/initiatives/${id}`, { method: 'DELETE' });

// ── Epics ────────────────────────────────────────────────────────────────────
export const getEpics = () => req(`${PLANNING_BASE}/api/epics`);
export const getEpicsByInitiative = (id) => req(`${PLANNING_BASE}/api/epics/by-initiative/${id}`);
export const createEpic = (data) => req(`${PLANNING_BASE}/api/epics`, { method: 'POST', body: data });
export const updateEpic = (id, data) => req(`${PLANNING_BASE}/api/epics/${id}`, { method: 'PUT', body: data });
export const deleteEpic = (id) => req(`${PLANNING_BASE}/api/epics/${id}`, { method: 'DELETE' });

// ── Capacity ──────────────────────────────────────────────────────────────────
export const getCapacitySummary = (start, end) =>
  req(`${PLANNING_BASE}/api/capacity/summary?startDate=${start}&endDate=${end}`);
