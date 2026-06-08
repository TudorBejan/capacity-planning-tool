<script>
  import { onMount } from 'svelte';
  import { link } from 'svelte-spa-router';
  import { getTeam, getPersonsByTeam, createPerson, updatePerson, deletePerson, getTeamCapacity } from '../lib/api.js';
  import Modal from '../components/Modal.svelte';
  import CapacityBar from '../components/CapacityBar.svelte';

  export let params = {};
  const id = params.id;

  let team = null;
  let persons = [];
  let capacity = null;
  let loading = true;
  let error = null;
  let showModal = false;
  let editingPerson = null;

  const now = new Date();
  const qStart = new Date(now.getFullYear(), Math.floor(now.getMonth() / 3) * 3, 1);
  const qEnd   = new Date(now.getFullYear(), Math.floor(now.getMonth() / 3) * 3 + 3, 0);
  let startDate = qStart.toISOString().slice(0, 10);
  let endDate   = qEnd.toISOString().slice(0, 10);

  const emptyPerson = () => ({ teamId: id, name: '', role: '', availabilityPercentage: 100 });
  let personForm = emptyPerson();

  onMount(load);

  async function load() {
    loading = true; error = null;
    try {
      [team, persons] = await Promise.all([getTeam(id), getPersonsByTeam(id)]);
      await loadCapacity();
    } catch (e) { error = e.message; }
    finally { loading = false; }
  }

  async function loadCapacity() {
    try { capacity = await getTeamCapacity(id, startDate, endDate); }
    catch (e) { capacity = null; }
  }

  function openCreate() { personForm = emptyPerson(); editingPerson = null; showModal = true; }
  function openEdit(p) {
    personForm = { teamId: id, name: p.name, role: p.role || '', availabilityPercentage: p.availabilityPercentage };
    editingPerson = p;
    showModal = true;
  }

  async function savePerson() {
    try {
      if (editingPerson) await updatePerson(editingPerson.id, personForm);
      else await createPerson(personForm);
      showModal = false;
      persons = await getPersonsByTeam(id);
      await loadCapacity();
    } catch (e) { alert(e.message); }
  }

  async function removePerson(p) {
    if (!confirm(`Remove ${p.name} from this team?`)) return;
    try { await deletePerson(p.id); persons = await getPersonsByTeam(id); await loadCapacity(); }
    catch (e) { alert(e.message); }
  }

  $: availPct = capacity
    ? ((Number(capacity.netCapacityWeeks)) / (Number(capacity.workingWeeksInPeriod) * Number(capacity.totalFte)) * 100)
    : 0;
</script>

<div class="page">
  <div style="margin-bottom:1rem">
    <a href="/teams" use:link style="color:var(--text-muted);font-size:0.85rem">← Teams</a>
  </div>

  {#if loading}
    <p class="loading">Loading…</p>
  {:else if error}
    <p style="color:var(--red)">{error}</p>
  {:else if team}
    <div class="page-header">
      <div>
        <h1>{team.name}</h1>
        {#if team.description}
          <p style="color:var(--text-muted);margin-top:0.4rem;font-size:0.9rem">{team.description}</p>
        {/if}
      </div>
    </div>

    <!-- Capacity section -->
    <h2 style="margin-bottom:1rem">Capacity</h2>
    <div class="capacity-section">
      <div class="date-range">
        <input type="date" bind:value={startDate} style="width:140px"/>
        <span style="color:var(--text-muted)">→</span>
        <input type="date" bind:value={endDate} style="width:140px"/>
        <button class="btn-primary" on:click={loadCapacity}>Calculate</button>
      </div>

      {#if capacity}
        <div class="cap-cards">
          <div class="card cap-card">
            <div class="cap-val">{Number(capacity.totalFte).toFixed(1)}</div>
            <div class="cap-label">Total FTE</div>
          </div>
          <div class="card cap-card">
            <div class="cap-val">{capacity.overheadPercentage}%</div>
            <div class="cap-label">Overhead</div>
          </div>
          <div class="card cap-card">
            <div class="cap-val">{Number(capacity.workingWeeksInPeriod).toFixed(1)}</div>
            <div class="cap-label">Working weeks</div>
          </div>
          <div class="card cap-card highlight">
            <div class="cap-val">{Number(capacity.netCapacityWeeks).toFixed(1)}</div>
            <div class="cap-label">Net capacity (weeks)</div>
          </div>
        </div>
        <div class="card" style="margin-top:1rem;padding:1rem">
          <p style="font-size:0.8rem;color:var(--text-muted);margin-bottom:0.5rem">
            Formula: Σ(availability%) × (1 − overhead%) × working weeks
          </p>
          <CapacityBar utilization={100 - team.overheadPercentage} />
          <p style="font-size:0.72rem;color:var(--text-muted);margin-top:0.4rem">
            {100 - team.overheadPercentage}% of raw FTE-weeks are available for project work
          </p>
        </div>
      {/if}
    </div>

    <!-- Members section -->
    <div class="section-header" style="margin-top:2rem;margin-bottom:1rem">
      <h2>Members ({persons.length})</h2>
      <button class="btn-primary" on:click={openCreate}>+ Add member</button>
    </div>

    {#if persons.length === 0}
      <div class="card empty">No members yet.</div>
    {:else}
      <div class="card" style="padding:0;overflow:hidden">
        <table>
          <thead>
            <tr>
              <th>Name</th>
              <th>Role</th>
              <th>Availability</th>
              <th>FTE contribution</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {#each persons as p}
              <tr>
                <td style="font-weight:600">{p.name}</td>
                <td style="color:var(--text-muted)">{p.role || '—'}</td>
                <td>{p.availabilityPercentage}%</td>
                <td>{(Number(p.availabilityPercentage) / 100).toFixed(2)} FTE</td>
                <td>
                  <div style="display:flex;gap:0.4rem">
                    <button class="btn-ghost btn-sm" on:click={() => openEdit(p)}>Edit</button>
                    <button class="btn-danger btn-sm" on:click={() => removePerson(p)}>Remove</button>
                  </div>
                </td>
              </tr>
            {/each}
          </tbody>
        </table>
      </div>
    {/if}
  {/if}
</div>

{#if showModal}
  <Modal title={editingPerson ? 'Edit member' : 'Add member'} onClose={() => showModal = false}>
    <div class="form-group">
      <label>Name *</label>
      <input bind:value={personForm.name} placeholder="e.g. Alice Müller" />
    </div>
    <div class="form-group">
      <label>Role</label>
      <input bind:value={personForm.role} placeholder="e.g. Senior Engineer" />
    </div>
    <div class="form-group">
      <label>Availability % (100 = full-time)</label>
      <input type="number" min="1" max="100" bind:value={personForm.availabilityPercentage} />
    </div>
    <div style="display:flex;justify-content:flex-end;gap:0.5rem;margin-top:1rem">
      <button class="btn-ghost" on:click={() => showModal = false}>Cancel</button>
      <button class="btn-primary" on:click={savePerson}>{editingPerson ? 'Save' : 'Add'}</button>
    </div>
  </Modal>
{/if}

<style>
  .date-range { display:flex; align-items:center; gap:0.5rem; margin-bottom:1rem; }
  .cap-cards { display:grid; grid-template-columns:repeat(auto-fill,minmax(150px,1fr)); gap:1rem; }
  .cap-card { text-align:center; }
  .cap-card.highlight { border-color:var(--accent); }
  .cap-val { font-size:1.8rem; font-weight:700; }
  .cap-label { font-size:0.75rem; color:var(--text-muted); margin-top:4px; }
  .section-header { display:flex; justify-content:space-between; align-items:center; }
  .capacity-section { margin-bottom:1.5rem; }
</style>
