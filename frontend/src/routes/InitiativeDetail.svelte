<script>
  import { onMount } from 'svelte';
  import { link, push } from 'svelte-spa-router';
  import { getInitiative, getTeams, createEpic, updateEpic, deleteEpic } from '../lib/api.js';
  import Modal from '../components/Modal.svelte';

  export let params = {};
  const id = params.id;

  let initiative = null;
  let teams = [];
  let loading = true;
  let error = null;
  let showModal = false;
  let editingEpic = null;

  const emptyEpic = () => ({
    initiativeId: id,
    teamId: '',
    name: '',
    description: '',
    status: 'PLANNED',
    estimatedWeeks: 2,
    startDate: '',
    dueDate: '',
  });
  let epicForm = emptyEpic();

  onMount(load);

  async function load() {
    loading = true; error = null;
    try { [initiative, teams] = await Promise.all([getInitiative(id), getTeams()]); }
    catch (e) { error = e.message; }
    finally { loading = false; }
  }

  function openCreate() { epicForm = emptyEpic(); editingEpic = null; showModal = true; }
  function openEdit(epic) {
    epicForm = {
      initiativeId: id,
      teamId: epic.teamId,
      name: epic.name,
      description: epic.description || '',
      status: epic.status,
      estimatedWeeks: epic.estimatedWeeks,
      startDate: epic.startDate || '',
      dueDate: epic.dueDate || '',
    };
    editingEpic = epic;
    showModal = true;
  }

  async function saveEpic() {
    if (!epicForm.teamId) { alert('Please select a team.'); return; }
    try {
      if (editingEpic) await updateEpic(editingEpic.id, epicForm);
      else await createEpic(epicForm);
      showModal = false;
      await load();
    } catch (e) { alert(e.message); }
  }

  async function removeEpic(epic) {
    if (!confirm(`Delete epic "${epic.name}"?`)) return;
    try { await deleteEpic(epic.id); await load(); }
    catch (e) { alert(e.message); }
  }

  function teamName(tid) {
    return teams.find(t => t.id === tid)?.name ?? tid?.slice(0, 8) + '…';
  }

  function statusBadge(s) {
    return { IN_PROGRESS: 'badge-green', PLANNED: 'badge-blue', COMPLETED: 'badge-gray', CANCELLED: 'badge-gray' }[s] || 'badge-gray';
  }
  function priorityBadge(p) {
    return { CRITICAL: 'badge-red', HIGH: 'badge-yellow', MEDIUM: 'badge-blue', LOW: 'badge-gray' }[p] || 'badge-gray';
  }
  function fmt(d) { return d ? new Date(d).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' }) : '—'; }

  $: totalEstimated = initiative?.epics?.reduce((s, e) => s + Number(e.estimatedWeeks), 0) ?? 0;
</script>

<div class="page">
  <div style="margin-bottom:1rem">
    <a href="/initiatives" use:link style="color:var(--text-muted);font-size:0.85rem">← Initiatives</a>
  </div>

  {#if loading}
    <p class="loading">Loading…</p>
  {:else if error}
    <p style="color:var(--red)">{error}</p>
  {:else if initiative}
    <div class="page-header">
      <div>
        <h1>{initiative.name}</h1>
        <div style="display:flex;gap:0.5rem;margin-top:0.5rem;align-items:center">
          <span class="badge {priorityBadge(initiative.priority)}">{initiative.priority}</span>
          <span class="badge {statusBadge(initiative.status)}">{initiative.status}</span>
          {#if initiative.targetDate}
            <span style="font-size:0.8rem;color:var(--text-muted)">Target: {fmt(initiative.targetDate)}</span>
          {/if}
        </div>
        {#if initiative.description}
          <p style="color:var(--text-muted);margin-top:0.5rem;font-size:0.9rem">{initiative.description}</p>
        {/if}
      </div>
      <button class="btn-primary" on:click={openCreate}>+ Add epic</button>
    </div>

    <!-- Summary cards -->
    <div class="summary-row">
      <div class="card mini-card">
        <div class="mini-val">{initiative.epics?.length ?? 0}</div>
        <div class="mini-label">Epics</div>
      </div>
      <div class="card mini-card">
        <div class="mini-val">{totalEstimated.toFixed(1)}</div>
        <div class="mini-label">Estimated weeks total</div>
      </div>
      <div class="card mini-card">
        <div class="mini-val">{[...new Set(initiative.epics?.map(e => e.teamId))].length}</div>
        <div class="mini-label">Teams involved</div>
      </div>
    </div>

    <!-- Epics table -->
    <h2 style="margin-bottom:1rem">Epics</h2>
    {#if !initiative.epics || initiative.epics.length === 0}
      <div class="card empty">No epics yet. Add one to break down the work.</div>
    {:else}
      <div class="card" style="padding:0;overflow:hidden">
        <table>
          <thead>
            <tr>
              <th>Epic</th>
              <th>Team</th>
              <th>Status</th>
              <th>Estimated</th>
              <th>Start</th>
              <th>Due</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {#each initiative.epics as epic}
              <tr>
                <td>
                  <div style="font-weight:600">{epic.name}</div>
                  {#if epic.description}
                    <div style="font-size:0.78rem;color:var(--text-muted);margin-top:2px">{epic.description.slice(0,50)}{epic.description.length > 50 ? '…' : ''}</div>
                  {/if}
                </td>
                <td>
                  <a href="/teams/{epic.teamId}" use:link style="color:var(--accent);font-size:0.85rem">{teamName(epic.teamId)}</a>
                </td>
                <td><span class="badge {statusBadge(epic.status)}">{epic.status.replace('_', ' ')}</span></td>
                <td>{epic.estimatedWeeks}w</td>
                <td>{fmt(epic.startDate)}</td>
                <td>{fmt(epic.dueDate)}</td>
                <td>
                  <div style="display:flex;gap:0.4rem">
                    <button class="btn-ghost btn-sm" on:click={() => openEdit(epic)}>Edit</button>
                    <button class="btn-danger btn-sm" on:click={() => removeEpic(epic)}>Delete</button>
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
  <Modal title={editingEpic ? 'Edit epic' : 'Add epic'} onClose={() => showModal = false}>
    <div class="form-group">
      <label>Epic name *</label>
      <input bind:value={epicForm.name} placeholder="e.g. Auth & SSO Revamp" />
    </div>
    <div class="form-group">
      <label>Description</label>
      <textarea bind:value={epicForm.description} placeholder="What will be delivered?"></textarea>
    </div>
    <div class="form-row">
      <div class="form-group">
        <label>Team *</label>
        <select bind:value={epicForm.teamId}>
          <option value="">Select team…</option>
          {#each teams as t}
            <option value={t.id}>{t.name}</option>
          {/each}
        </select>
      </div>
      <div class="form-group">
        <label>Status *</label>
        <select bind:value={epicForm.status}>
          <option>PLANNED</option>
          <option>IN_PROGRESS</option>
          <option>COMPLETED</option>
          <option>CANCELLED</option>
        </select>
      </div>
    </div>
    <div class="form-row">
      <div class="form-group">
        <label>Estimated weeks *</label>
        <input type="number" min="0.5" step="0.5" bind:value={epicForm.estimatedWeeks} />
      </div>
      <div class="form-group"><!-- spacer --></div>
    </div>
    <div class="form-row">
      <div class="form-group">
        <label>Start date</label>
        <input type="date" bind:value={epicForm.startDate} />
      </div>
      <div class="form-group">
        <label>Due date</label>
        <input type="date" bind:value={epicForm.dueDate} />
      </div>
    </div>
    <div style="display:flex;justify-content:flex-end;gap:0.5rem;margin-top:1rem">
      <button class="btn-ghost" on:click={() => showModal = false}>Cancel</button>
      <button class="btn-primary" on:click={saveEpic}>{editingEpic ? 'Save changes' : 'Add epic'}</button>
    </div>
  </Modal>
{/if}

<style>
  .summary-row { display:flex; gap:1rem; margin-bottom:1.5rem; flex-wrap:wrap; }
  .mini-card { text-align:center; min-width:120px; }
  .mini-val { font-size:1.6rem; font-weight:700; }
  .mini-label { font-size:0.78rem; color:var(--text-muted); margin-top:0.2rem; }
</style>
