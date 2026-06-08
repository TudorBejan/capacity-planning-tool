<script>
  export let utilization = 0; // 0-100+
  export let capacityWeeks = null;
  export let allocatedWeeks = null;

  $: pct = Math.min(utilization, 100);
  $: color = utilization >= 90 ? 'var(--red)'
           : utilization >= 70 ? 'var(--yellow)'
           : 'var(--green)';
  $: label = utilization >= 90 ? 'At risk' : utilization >= 70 ? 'Busy' : 'Healthy';
</script>

<div class="capacity-bar-wrap">
  <div class="bar-track">
    <div class="bar-fill" style="width:{pct}%; background:{color}"></div>
  </div>
  <div class="bar-meta">
    <span style="color:{color}" class="pct-label">{utilization.toFixed(0)}% {label}</span>
    {#if capacityWeeks != null && allocatedWeeks != null}
      <span class="weeks">{Number(allocatedWeeks).toFixed(1)} / {Number(capacityWeeks).toFixed(1)} wks</span>
    {/if}
  </div>
</div>

<style>
  .capacity-bar-wrap { width: 100%; }
  .bar-track {
    height: 6px; border-radius: 99px;
    background: var(--surface2);
    overflow: hidden;
    margin-bottom: 4px;
  }
  .bar-fill { height: 100%; border-radius: 99px; transition: width 0.4s ease; }
  .bar-meta { display: flex; justify-content: space-between; font-size: 0.75rem; }
  .pct-label { font-weight: 600; }
  .weeks { color: var(--text-muted); }
</style>
