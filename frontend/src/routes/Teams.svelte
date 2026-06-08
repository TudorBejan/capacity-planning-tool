<script>
  import { onMount } from 'svelte';
  import { link } from 'svelte-spa-router';
  import { getTeams, createTeam, updateTeam, deleteTeam } from '../lib/api.js';
  import Modal from '../components/Modal.svelte';

  let teams = [];
  let loading = true;
  let error = null;
  let showModal = false;
  let editing = null;

  const empty = () => ({ name: '', description: '', overheadPercentage: 25 });
  let form = empty();

  onMount(load);

  async function load() {
    loading = true; error = null;
    try { teams = await getTeams(); }
    catch (e) { error = e.message; }
    finally { loading = false; }
  }

  function openCreate() { form = empty(); editing = null; showModal = true; }
  function openEdit(t) {
    form = { name: t.name, description: t.description || '', overheadPercentage: t.overheadPercentage };
    editing = t;
    showModal = true;
  }

  async function save() {
    try {
      if (editing) await updateTeam(editing.id, form);
      else await createTeam(form);
      showModal = false;
      await load();
    } catch (e) { alert(e.message); }
  }

  async function remove(t) {
    if (!confirm(`Delete team "${t.name}"?`)) return;
    try { await deleteTeam(t.id); await load(); }
    catch (e) { alert(e.message); }
  }
</script>

<div class="page">
  <div class="page-header">
    <h1>Teams</h1>
    <button class="btn-primary" on:click={openCreate}>+ New team</button>
  </div>

  {#if loading}
    <p class="loading">Loading…</p>
  {:else if error}
    <p style="color:var(--red)">{error}</p>
  {:else if teams.length === 0}
    <div class="card empty">No teams yet.</div>
  {:else}
    <div class="teams-grid">
      {#each teams as team}
        <div class="card team-card">
          <div class="team-card-top">
            <div>
              <a href="/teams/{team.id}" use:link class="team-name">{team.name}</a>
              {#if team.description}
                <p class="team-desc">{team.description}</p>
              {/if}
            </div>
            <div style="display:flex;gap:0.4rem">
              <button class="btn-ghost btn-sm" on:click={() => openEdit(team)}>Edit</button>
              <button class="btn-danger btn-sm" on:click={() => remove(team)}>Delete</button>
            </div>
          </div>

          <div class="team-stats">
            <div class="stat">
              <span class="stat-val">{team.persons?.length ?? 0}</span>
              <span class="stat-label">Members</span>
            </div>
            <div class="stat">
              <span class="stat-val">{team.overheadPercentage}%</span>
              <span class="stat-label">Overhead</span>
            </div>
            <div class="stat">
              <span class="stat-val">
                {team.persons
                  ? (team.persons.reduce((s, p) => s + Number(p.availabilityPercentage), 0) / 100).toFixed(1)
                  : '—'}
              </span>
              <span class="stat-label">Total FTE</span>
            </div>
          </div>

          <a href="/teams/{team.id}" use:link class="btn-ghost" style="display:block;text-align:center;padding:0.4rem;border-radius:6px;border:1px solid var(--border);font-size:0.8rem;color:var(--text-muted)">
            View detail →
          </a>
        </div>
      {/each}
    </div>
  {/if}
</div>

{#if showModal}
  <Modal title={editing ? 'Edit team' : 'New team'} onClose={() => showModal = false}>
    <div class="form-group">
      <label>Team name *</label>
      <input bind:value={form.name} placeholder="e.g. Backend" />
    </div>
    <div class="form-group">
      <label>Description</label>
      <textarea bind:value={form.description} placeholder="What does this team own?"></textarea>
    </div>
    <div class="form-group">
      <label>Overhead % (meetings, support, on-call) *</label>
      <input type="number" min="0" max="100" bind:value={form.overheadPercentage} />
    </div>
    <div style="display:flex;justify-content:flex-end;gap:0.5rem;margin-top:1rem">
      <button class="btn-ghost" on:click={() => showModal = false}>Cancel</button>
      <button class="btn-primary" on:click={save}>{editing ? 'Save changes' : 'Create'}</button>
    </div>
  </Modal>
{/if}

<style>
  .teams-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1rem;
  }
  .team-card { display: flex; flex-direction: column; gap: 1rem; }
  .team-card-top { display: flex; justify-content: space-between; align-items: flex-start; }
  .team-name { font-size: 1.05rem; font-weight: 600; color: var(--text); }
  .team-name:hover { color: var(--accent); }
  .team-desc { font-size: 0.8rem; color: var(--text-muted); margin-top: 4px; }
  .team-stats { display: flex; gap: 1rem; }
  .stat { display: flex; flex-direction: column; align-items: center; flex: 1;
          background: var(--surface2); border-radius: 6px; padding: 0.5rem; }
  .stat-val { font-size: 1.2rem; font-weight: 700; }
  .stat-label { font-size: 0.7rem; color: var(--text-muted); margin-top: 2px; }
</style>
