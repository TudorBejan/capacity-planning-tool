<script>
  import { onMount } from 'svelte';
  import { link } from 'svelte-spa-router';
  import { getCapacitySummary, getInitiatives, getTeams } from '../lib/api.js';
  import CapacityBar from '../components/CapacityBar.svelte';

  // Demo presets — each maps to a date range that produces a distinct capacity scenario
  const PRESETS = [
    {
      label: 'All clear',
      icon: '✓',
      start: '2025-04-01', end: '2025-06-30',
      hint: 'Q2 2025 · Data 39%, Frontend 23%, Backend 9%',
      color: 'green',
    },
    {
      label: 'Q1 overview',
      icon: '◑',
      start: '2025-01-01', end: '2025-03-31',
      hint: 'Q1 2025 · 5 teams active, Platform leading at 72%',
      color: 'yellow',
    },
    {
      label: '1 team at risk',
      icon: '▲',
      start: '2025-04-01', end: '2025-04-30',
      hint: 'April 2025 · Data 114%',
      color: 'orange',
    },
    {
      label: '2 teams at risk',
      icon: '▲▲',
      start: '2025-01-06', end: '2025-01-31',
      hint: 'Jan sprint · Mobile 191%, Platform 115%',
      color: 'red',
    },
    {
      label: '3 teams at risk',
      icon: '▲▲▲',
      start: '2025-01-06', end: '2025-02-07',
      hint: 'Jan–Feb · Platform 153%, Mobile 152%, Backend 122%',
      color: 'red',
    },
  ];

  let activePreset = 'Q1 overview';

  function applyPreset(preset) {
    activePreset = preset.label;
    startDate = preset.start;
    endDate = preset.end;
    load();
  }

  let startDate = '2025-01-01';
  let endDate   = '2025-03-31';

  let summary = null;
  let initiatives = [];
  let teams = [];
  let loading = true;
  let error = null;

  onMount(load);

  async function load() {
    loading = true; error = null;
    try {
      [summary, initiatives, teams] = await Promise.all([
        getCapacitySummary(startDate, endDate),
        getInitiatives(),
        getTeams(),
      ]);
    } catch (e) {
      error = e.message;
    } finally {
      loading = false;
    }
  }

  $: activeInitiatives  = initiatives.filter(i => i.status === 'ACTIVE');
  $: criticalInitiatives = initiatives.filter(i => i.priority === 'CRITICAL' && i.status === 'ACTIVE');
  $: atRiskTeams = summary ? summary.teamAllocations.filter(t => t.utilizationPercentage >= 90) : [];

  function priorityBadge(p) {
    return { CRITICAL: 'badge-red', HIGH: 'badge-yellow', MEDIUM: 'badge-blue', LOW: 'badge-gray' }[p] || 'badge-gray';
  }
  function statusBadge(s) {
    return { ACTIVE: 'badge-green', DRAFT: 'badge-gray', COMPLETED: 'badge-blue', CANCELLED: 'badge-gray' }[s] || 'badge-gray';
  }
  function fmt(d) { return d ? new Date(d).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' }) : '—'; }
</script>

<div class="page">
  <div class="page-header">
    <h1>Dashboard</h1>
    <div class="date-range">
      <input type="date" bind:value={startDate} on:change={() => activePreset = null} />
      <span style="color:var(--text-muted)">→</span>
      <input type="date" bind:value={endDate} on:change={() => activePreset = null} />
      <button class="btn-primary" on:click={load}>Refresh</button>
    </div>
  </div>

  <!-- Demo preset buttons -->
  <div class="presets-bar">
    <span class="presets-label">Quick scenarios:</span>
    {#each PRESETS as preset}
      <button
        class="preset-btn preset-{preset.color}"
        class:preset-active={activePreset === preset.label}
        on:click={() => applyPreset(preset)}
        title={preset.hint}
      >
        <span class="preset-icon">{preset.icon}</span>
        {preset.label}
      </button>
    {/each}
  </div>

  {#if loading}
    <p class="loading">Loading…</p>
  {:else if error}
    <p style="color:var(--red)">{error} — make sure both services are running.</p>
  {:else}
    <!-- KPI cards -->
    <div class="kpi-grid">
      <div class="card kpi-card">
        <div class="kpi-value">{initiatives.length}</div>
        <div class="kpi-label">Total initiatives</div>
      </div>
      <div class="card kpi-card">
        <div class="kpi-value" style="color:var(--green)">{activeInitiatives.length}</div>
        <div class="kpi-label">Active initiatives</div>
      </div>
      <div class="card kpi-card">
        <div class="kpi-value" style="color:var(--red)">{criticalInitiatives.length}</div>
        <div class="kpi-label">Critical &amp; active</div>
      </div>
      <div class="card kpi-card">
        <div class="kpi-value" style="color:{atRiskTeams.length > 0 ? 'var(--red)' : 'var(--green)'}">{atRiskTeams.length}</div>
        <div class="kpi-label">Teams at risk (&gt;90%)</div>
      </div>
      <div class="card kpi-card">
        <div class="kpi-value">{teams.length}</div>
        <div class="kpi-label">Teams</div>
      </div>
    </div>

    <!-- Capacity section -->
    <div class="section-header">
      <h2>Team Capacity — {startDate} to {endDate}</h2>
    </div>

    {#if summary && summary.teamAllocations.length > 0}
      <div class="capacity-grid">
        {#each summary.teamAllocations as team}
          <div class="card capacity-card">
            <div class="team-card-header">
              <a href="/teams/{team.teamId}" use:link class="team-name">{team.teamName}</a>
              <span class="badge {team.utilizationPercentage >= 90 ? 'badge-red' : team.utilizationPercentage >= 70 ? 'badge-yellow' : 'badge-green'}">
                {team.utilizationPercentage.toFixed(0)}%
              </span>
            </div>
            <CapacityBar
              utilization={Number(team.utilizationPercentage)}
              capacityWeeks={team.capacityWeeks}
              allocatedWeeks={team.allocatedWeeks}
            />
          </div>
        {/each}
      </div>
    {:else}
      <div class="card empty">No epics overlap with this period.</div>
    {/if}

    <!-- Active initiatives table -->
    <div class="section-header" style="margin-top: 2rem;">
      <h2>Active Initiatives</h2>
      <a href="/initiatives" use:link class="btn-ghost" style="padding:0.35rem 0.75rem;border-radius:6px;font-size:0.8rem;border:1px solid var(--border);color:var(--text-muted)">View all →</a>
    </div>

    {#if activeInitiatives.length > 0}
      <div class="card" style="padding:0;overflow:hidden">
        <table>
          <thead>
            <tr>
              <th>Initiative</th>
              <th>Priority</th>
              <th>Target date</th>
              <th>Epics</th>
            </tr>
          </thead>
          <tbody>
            {#each activeInitiatives as ini}
              <tr>
                <td><a href="/initiatives/{ini.id}" use:link>{ini.name}</a></td>
                <td><span class="badge {priorityBadge(ini.priority)}">{ini.priority}</span></td>
                <td>{fmt(ini.targetDate)}</td>
                <td>{ini.epics?.length ?? 0}</td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    {:else}
      <div class="card empty">No active initiatives.</div>
    {/if}
  {/if}
</div>

<style>
  .date-range { display: flex; align-items: center; gap: 0.5rem; }
  .date-range input { width: 140px; }

  .presets-bar {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    flex-wrap: wrap;
    margin-bottom: 1.5rem;
    padding: 0.75rem 1rem;
    background: var(--surface);
    border: 1px solid var(--border);
    border-radius: var(--radius);
  }
  .presets-label {
    font-size: 0.75rem;
    color: var(--text-muted);
    font-weight: 500;
    margin-right: 0.25rem;
    white-space: nowrap;
  }
  .preset-btn {
    display: flex;
    align-items: center;
    gap: 0.35rem;
    padding: 0.3rem 0.75rem;
    border-radius: 99px;
    font-size: 0.78rem;
    font-weight: 500;
    border: 1px solid transparent;
    cursor: pointer;
    transition: all 0.15s;
    white-space: nowrap;
  }
  .preset-icon { font-size: 0.7rem; }

  .preset-green  { background: #064e3b22; color: #6ee7b7; border-color: #065f46; }
  .preset-yellow { background: #78350f22; color: #fcd34d; border-color: #92400e; }
  .preset-orange { background: #7c2d1222; color: #fb923c; border-color: #9a3412; }
  .preset-red    { background: #7f1d1d22; color: #fca5a5; border-color: #991b1b; }

  .preset-green:hover  { background: #064e3b55; }
  .preset-yellow:hover { background: #78350f55; }
  .preset-orange:hover { background: #7c2d1255; }
  .preset-red:hover    { background: #7f1d1d55; }

  .preset-active {
    filter: brightness(1.2);
    box-shadow: 0 0 0 2px currentColor;
  }

  .kpi-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
  }
  .kpi-card { text-align: center; }
  .kpi-value { font-size: 2rem; font-weight: 700; margin-bottom: 0.25rem; }
  .kpi-label { font-size: 0.78rem; color: var(--text-muted); }

  .section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }

  .capacity-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
    gap: 1rem;
    margin-bottom: 2rem;
  }
  .capacity-card { display: flex; flex-direction: column; gap: 0.75rem; }
  .team-card-header { display: flex; justify-content: space-between; align-items: center; }
  .team-name { font-weight: 600; color: var(--text); }
  .team-name:hover { color: var(--accent); }
</style>
