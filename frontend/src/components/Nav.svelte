<script>
  import { link, location } from 'svelte-spa-router';

  const navItems = [
    { href: '/',            label: 'Dashboard',   icon: '▦' },
    { href: '/initiatives', label: 'Initiatives', icon: '◈' },
    { href: '/teams',       label: 'Teams',       icon: '◎' },
  ];
</script>

<nav class="sidebar">
  <div class="logo">
    <span class="logo-icon">⬡</span>
    <span class="logo-text">CapPlan</span>
  </div>

  <ul class="nav-list">
    {#each navItems as item}
      <li>
        <a
          href={item.href}
          use:link
          class="nav-link"
          class:active={$location === item.href}
        >
          <span class="nav-icon">{item.icon}</span>
          {item.label}
        </a>
      </li>
    {/each}
  </ul>

  <div class="nav-footer">
    <span class="version">v1.0 · Prototype</span>
  </div>
</nav>

<style>
  .sidebar {
    width: 220px;
    min-width: 220px;
    background: var(--surface);
    border-right: 1px solid var(--border);
    display: flex;
    flex-direction: column;
    padding: 1.5rem 0;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0 1.25rem 1.5rem;
    border-bottom: 1px solid var(--border);
    margin-bottom: 1rem;
  }
  .logo-icon { font-size: 1.4rem; color: var(--accent); }
  .logo-text { font-size: 1.1rem; font-weight: 700; color: var(--text); }

  .nav-list { list-style: none; flex: 1; }

  .nav-link {
    display: flex;
    align-items: center;
    gap: 0.65rem;
    padding: 0.6rem 1.25rem;
    color: var(--text-muted);
    font-weight: 500;
    border-radius: 0;
    transition: background 0.15s, color 0.15s;
  }
  .nav-link:hover { background: var(--surface2); color: var(--text); }
  .nav-link.active { background: rgba(99,102,241,0.15); color: var(--accent); border-right: 2px solid var(--accent); }

  .nav-icon { font-size: 1rem; width: 1.2rem; text-align: center; }

  .nav-footer {
    padding: 1rem 1.25rem 0;
    border-top: 1px solid var(--border);
  }
  .version { font-size: 0.72rem; color: var(--text-muted); }
</style>
