<script>
  import { onMount } from 'svelte';
  import { link } from 'svelte-spa-router';
  import { getInitiatives, createInitiative, updateInitiative, deleteInitiative } from '../lib/api.js';
  import Modal from '../components/Modal.svelte';

  let initiatives = [];
  let loading = true;
  let error = null;
  let showModal = false;
  let editing = null;

  const empty = () => ({ name: '', description: '', status: 'DRAFT', priority: 'MEDIUM', targetDate: '' });
  let form = empty();

  onMount(load);

  async function load() {
    loading = true; error = null;
    try { initiatives = await getInitiatives(); }
    catch (e) { error = e.message; }
    finally { loading = false; }
  }

  function openCreate() { form = empty(); editing = null; showModal = true; }
  function openEdit(ini) {
    form = { name: ini.name, description: ini.description || '', status: ini.status, priority: ini.priority, targetDate: ini.targetDate || '' };
    editing = ini;
    showModal = true;
  }

  async function save() {
    try {
      if (editing) await updateInitiative(editing.id, form);
      else await createInitiative(form);
      showModal = false;
      await load();
    } catch (e) { alert(e.message); }
  }

  async function remove(ini) {
    if (!confirm(`Delete "${ini.name}"?`)) return;
    try { await deleteInitiative(ini.id); await load(); }
    catch (e) { alert(e.message); }
  }

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
    <h1>Initiatives</h1>
    <button class="btn-primary" on:click={openCreate}>+ New initiative</button>
  </div>

  {#if loading}
    <p class="loading">Loading…</p>
  {:else if error}
    <p style="color:var(--red)">{error}</p>
  {:else if initiatives.length === 0}
    <div class="card empty">No initiatives yet. Create one to get started.</div>
  {:else}
    <div class="card" style="padding:0;overflow:hidden">
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Status</th>
            <th>Priority</th>
            <th>Target date</th>
            <th>Epics</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {#each initiatives as ini}
            <tr>
              <td>
                <a href="/initiatives/{ini.id}" use:link style="font-weight:600">{ini.name}</a>
                {#if ini.description}
                  <div style="font-size:0.78rem;color:var(--text-muted);margin-top:2px">{ini.description.slice(0,60)}{ini.description.length > 60 ? '…' : ''}</div>
                {/if}
              </td>
              <td><span class="badge {statusBadge(ini.status)}">{ini.status}</span></td>
              <td><span class="badge {priorityBadge(ini.priority)}">{ini.priority}</span></td>
              <td>{fmt(ini.targetDate)}</td>
              <td>{ini.epics?.length ?? 0}</td>
              <td>
                <div style="display:flex;gap:0.4rem">
                  <button class="btn-ghost btn-sm" on:click={() => openEdit(ini)}>Edit</button>
                  <button class="btn-danger btn-sm" on:click={() => remove(ini)}>Delete</button>
                </div>
              </td>
            </tr>
          {/each}
        </tbody>
      </table>
    </div>
  {/if}
</div>

{#if showModal}
  <Modal title={editing ? 'Edit initiative' : 'New initiative'} onClose={() => showModal = false}>
    <div class="form-group">
      <label>Name *</label>
      <input bind:value={form.name} placeholder="e.g. Customer Portal v2" />
    </div>
    <div class="form-group">
      <label>Description</label>
      <textarea bind:value={form.description} placeholder="What is this initiative about?"></textarea>
    </div>
    <div class="form-row">
      <div class="form-group">
        <label>Status *</label>
        <select bind:value={form.status}>
          <option>DRAFT</option>
          <option>ACTIVE</option>
          <option>COMPLETED</option>
          <option>CANCELLED</option>
        </select>
      </div>
      <div class="form-group">
        <label>Priority *</label>
        <select bind:value={form.priority}>
          <option>LOW</option>
          <option>MEDIUM</option>
          <option>HIGH</option>
          <option>CRITICAL</option>
        </select>
      </div>
    </div>
    <div class="form-group">
      <label>Target date</label>
      <input type="date" bind:value={form.targetDate} />
    </div>
    <div style="display:flex;justify-content:flex-end;gap:0.5rem;margin-top:1rem">
      <button class="btn-ghost" on:click={() => showModal = false}>Cancel</button>
      <button class="btn-primary" on:click={save}>{editing ? 'Save changes' : 'Create'}</button>
    </div>
  </Modal>
{/if}
