<script>
  import Router from 'svelte-spa-router';
  import Nav from './components/Nav.svelte';
  import Dashboard from './routes/Dashboard.svelte';
  import Initiatives from './routes/Initiatives.svelte';
  import InitiativeDetail from './routes/InitiativeDetail.svelte';
  import Teams from './routes/Teams.svelte';
  import TeamDetail from './routes/TeamDetail.svelte';

  const routes = {
    '/': Dashboard,
    '/initiatives': Initiatives,
    '/initiatives/:id': InitiativeDetail,
    '/teams': Teams,
    '/teams/:id': TeamDetail,
  };
</script>

<div class="layout">
  <Nav />
  <main class="main-content">
    <Router {routes} />
  </main>
</div>

<style>
  .layout {
    display: flex;
    min-height: 100vh;
  }
  .main-content {
    flex: 1;
    overflow-y: auto;
  }
</style>
