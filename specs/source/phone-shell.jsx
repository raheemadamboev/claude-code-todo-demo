// phone-shell.jsx — Lightweight Android phone frame used by all 3 variations.
// Replaces the starter frame so we can theme status/nav bar to each variant.

function PhoneShell({
  children,
  width = 360,
  height = 740,
  bg = '#FEF7FF',
  fg = '#1D1B20',
  showStatus = true,
  showNav = true,
  navBg,
  borderRadius = 36,
  border = '#1f1f24',
}) {
  return (
    <div style={{
      width, height, borderRadius, overflow: 'hidden',
      background: bg, color: fg,
      border: `6px solid ${border}`,
      boxShadow: '0 24px 60px rgba(0,0,0,0.18), 0 4px 12px rgba(0,0,0,0.08)',
      position: 'relative',
      fontFamily: '"Roboto", "Google Sans", system-ui, -apple-system, sans-serif',
      boxSizing: 'border-box',
      display: 'flex', flexDirection: 'column',
    }}>
      {showStatus && <PhoneStatus fg={fg} bg={bg} />}
      <div style={{ flex: 1, position: 'relative', overflow: 'hidden' }}>
        {children}
      </div>
      {showNav && <PhoneNav fg={fg} bg={navBg || bg} />}
    </div>
  );
}

function PhoneStatus({ fg = '#1D1B20', bg = '#FEF7FF' }) {
  return (
    <div style={{
      height: 32, padding: '0 18px',
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      background: bg, color: fg, position: 'relative', flexShrink: 0,
      fontSize: 13, fontWeight: 500, letterSpacing: 0.1,
    }}>
      <span>9:41</span>
      <div style={{
        position: 'absolute', left: '50%', top: 6, transform: 'translateX(-50%)',
        width: 18, height: 18, borderRadius: 100, background: '#0a0a0a',
      }} />
      <div style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
        <svg width="14" height="11" viewBox="0 0 14 11" fill={fg}><path d="M.5 9.5h2v1.5h-2zM3.5 7.5h2V11h-2zM6.5 5h2v6h-2zM9.5 2.5h2V11h-2z"/></svg>
        <svg width="14" height="11" viewBox="0 0 14 11" fill="none"><path d="M7 4.2c2 0 3.7.7 5 2L7 11.5 2 6.2c1.3-1.3 3-2 5-2zM7 1c3 0 5.6 1.1 7.5 3L13 5.5C11.5 4 9.4 3 7 3S2.5 4 1 5.5L-.5 4C1.4 2.1 4 1 7 1z" fill={fg}/></svg>
        <div style={{ width: 22, height: 11, border: `1px solid ${fg}`, borderRadius: 3, padding: 1, position: 'relative', boxSizing: 'border-box' }}>
          <div style={{ width: '78%', height: '100%', background: fg, borderRadius: 1 }} />
          <div style={{ position: 'absolute', right: -3, top: 3, width: 1.5, height: 3, background: fg, borderRadius: 1 }} />
        </div>
      </div>
    </div>
  );
}

function PhoneNav({ fg = '#1D1B20', bg = '#FEF7FF' }) {
  return (
    <div style={{
      height: 22, display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: bg, flexShrink: 0,
    }}>
      <div style={{
        width: 110, height: 4, borderRadius: 2,
        background: fg, opacity: 0.85,
      }} />
    </div>
  );
}

Object.assign(window, { PhoneShell, PhoneStatus, PhoneNav });
