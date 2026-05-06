// todo-state.jsx — Shared todo data + hooks used by all 3 variations.

const SAMPLE_TODOS = [
  { id: 't1', title: 'Design review with Kiera', notes: 'Bring the v3 mocks and the spec doc. Focus on the empty states.', tag: 'Work', tagColor: 'work', priority: 'high', due: 'Today, 4:00 PM', done: false },
  { id: 't2', title: 'Pick up dry cleaning', notes: 'Two shirts, the navy suit. Closes at 7.', tag: 'Errand', tagColor: 'errand', priority: 'medium', due: 'Today', done: false },
  { id: 't3', title: 'Reply to Marco about the lease', notes: '', tag: 'Personal', tagColor: 'personal', priority: 'high', due: 'Today, 6:00 PM', done: false },
  { id: 't4', title: 'Read chapter 4 of Designing Data-Intensive Apps', notes: 'Page 96 onwards.', tag: 'Learn', tagColor: 'learn', priority: 'low', due: 'Tomorrow', done: false },
  { id: 't5', title: 'Book the dentist', notes: 'Try Dr. Patel — friend recommended.', tag: 'Health', tagColor: 'health', priority: 'medium', due: 'Fri, May 9', done: false },
  { id: 't6', title: 'Water the monstera', notes: '', tag: 'Home', tagColor: 'home', priority: 'low', due: 'Sat', done: true },
  { id: 't7', title: 'Submit Q2 expenses', notes: 'Concur — receipts in Drive folder.', tag: 'Work', tagColor: 'work', priority: 'medium', due: 'Mon, May 12', done: true },
];

const TAG_COLORS_LIGHT = {
  work:     { bg: '#E8DEF8', fg: '#21005D' },
  errand:   { bg: '#FFE0B2', fg: '#3F2E00' },
  personal: { bg: '#D6E4FF', fg: '#001A41' },
  learn:    { bg: '#D7F5DC', fg: '#002107' },
  health:   { bg: '#FFD8E4', fg: '#3F001E' },
  home:     { bg: '#FFF0C4', fg: '#3D2E00' },
};
const TAG_COLORS_DARK = {
  work:     { bg: '#4F378B', fg: '#EADDFF' },
  errand:   { bg: '#5C4326', fg: '#FFDDB1' },
  personal: { bg: '#284879', fg: '#D5E3FF' },
  learn:    { bg: '#1F4327', fg: '#B6F0BF' },
  health:   { bg: '#633B48', fg: '#FFD8E4' },
  home:     { bg: '#5A4214', fg: '#FFE08C' },
};

function useTodos(initial = SAMPLE_TODOS) {
  const [todos, setTodos] = React.useState(initial);
  const toggle = (id) => setTodos(ts => ts.map(t => t.id === id ? { ...t, done: !t.done } : t));
  const remove = (id) => setTodos(ts => ts.filter(t => t.id !== id));
  const update = (id, patch) => setTodos(ts => ts.map(t => t.id === id ? { ...t, ...patch } : t));
  const reorder = (fromIdx, toIdx) => setTodos(ts => {
    const next = ts.slice();
    const [m] = next.splice(fromIdx, 1);
    next.splice(toIdx, 0, m);
    return next;
  });
  const add = (todo) => setTodos(ts => [{ id: 'n' + Date.now(), done: false, ...todo }, ...ts]);
  return { todos, toggle, remove, update, reorder, add, setTodos };
}

// Animated checkbox — circular, fills with the brand color and draws a check.
function AnimatedCheck({ checked, onChange, color = '#6750A4', size = 24, ring = 'rgba(0,0,0,0.4)' }) {
  return (
    <button
      onClick={(e) => { e.stopPropagation(); onChange(!checked); }}
      aria-pressed={checked}
      style={{
        appearance: 'none', border: 'none', padding: 0, background: 'transparent',
        width: size + 12, height: size + 12, borderRadius: '50%',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        cursor: 'pointer', flexShrink: 0,
      }}
    >
      <span style={{
        width: size, height: size, borderRadius: '50%',
        background: checked ? color : 'transparent',
        border: `2px solid ${checked ? color : ring}`,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        transition: 'background .22s ease, border-color .22s ease, transform .22s cubic-bezier(.34,1.56,.64,1)',
        transform: checked ? 'scale(1.0)' : 'scale(1.0)',
      }}>
        <svg width={size * 0.6} height={size * 0.6} viewBox="0 0 24 24" fill="none"
          style={{
            opacity: checked ? 1 : 0,
            transform: checked ? 'scale(1)' : 'scale(0.5)',
            transition: 'opacity .18s ease, transform .22s cubic-bezier(.34,1.56,.64,1)',
          }}>
          <path d="M5 12.5 L10 17.5 L19 7.5" stroke="#fff" strokeWidth="3"
            strokeLinecap="round" strokeLinejoin="round" />
        </svg>
      </span>
    </button>
  );
}

// Tiny SVG icon helpers (Material symbols-style, but original strokes)
const Icon = {
  arrow_back: (c) => <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M19 12H5M5 12l6-6M5 12l6 6" stroke={c} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>,
  more: (c) => <svg width="22" height="22" viewBox="0 0 24 24" fill={c}><circle cx="12" cy="5" r="2"/><circle cx="12" cy="12" r="2"/><circle cx="12" cy="19" r="2"/></svg>,
  search: (c) => <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><circle cx="11" cy="11" r="7" stroke={c} strokeWidth="2"/><path d="m20 20-3.5-3.5" stroke={c} strokeWidth="2" strokeLinecap="round"/></svg>,
  filter: (c) => <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M4 6h16M7 12h10M10 18h4" stroke={c} strokeWidth="2" strokeLinecap="round"/></svg>,
  plus: (c) => <svg width="24" height="24" viewBox="0 0 24 24" fill="none"><path d="M12 5v14M5 12h14" stroke={c} strokeWidth="2.5" strokeLinecap="round"/></svg>,
  flag: (c) => <svg width="16" height="16" viewBox="0 0 24 24" fill={c}><path d="M5 3v18M5 4h11l-2 4 2 4H5"/></svg>,
  clock: (c) => <svg width="14" height="14" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke={c} strokeWidth="2"/><path d="M12 7v5l3 2" stroke={c} strokeWidth="2" strokeLinecap="round"/></svg>,
  phone: (c) => <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M5 4h4l2 5-2.5 1.5a11 11 0 0 0 5 5L15 13l5 2v4a2 2 0 0 1-2 2A15 15 0 0 1 3 6a2 2 0 0 1 2-2z" stroke={c} strokeWidth="2" strokeLinejoin="round"/></svg>,
  mail: (c) => <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><rect x="3" y="5" width="18" height="14" rx="2" stroke={c} strokeWidth="2"/><path d="m4 7 8 6 8-6" stroke={c} strokeWidth="2" strokeLinejoin="round"/></svg>,
  chev_right: (c) => <svg width="20" height="20" viewBox="0 0 24 24" fill="none"><path d="m9 6 6 6-6 6" stroke={c} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>,
  drag: (c) => <svg width="18" height="18" viewBox="0 0 24 24" fill={c}><circle cx="9" cy="6" r="1.5"/><circle cx="15" cy="6" r="1.5"/><circle cx="9" cy="12" r="1.5"/><circle cx="15" cy="12" r="1.5"/><circle cx="9" cy="18" r="1.5"/><circle cx="15" cy="18" r="1.5"/></svg>,
  refresh: (c) => <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M4 12a8 8 0 0 1 14-5.3L20 9M20 4v5h-5" stroke={c} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>,
  check: (c) => <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M5 12.5 10 17.5 19 7.5" stroke={c} strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round"/></svg>,
  trash: (c) => <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path d="M4 7h16M9 7V5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v2M6 7l1 13a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2l1-13" stroke={c} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>,
  list: (c) => <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><path d="M8 6h13M8 12h13M8 18h13M3.5 6h.01M3.5 12h.01M3.5 18h.01" stroke={c} strokeWidth="2" strokeLinecap="round"/></svg>,
  help: (c) => <svg width="22" height="22" viewBox="0 0 24 24" fill="none"><circle cx="12" cy="12" r="9" stroke={c} strokeWidth="2"/><path d="M9 9a3 3 0 0 1 6 0c0 2-3 2-3 4M12 17h.01" stroke={c} strokeWidth="2" strokeLinecap="round"/></svg>,
};

Object.assign(window, {
  SAMPLE_TODOS, TAG_COLORS_LIGHT, TAG_COLORS_DARK,
  useTodos, AnimatedCheck, Icon,
});
