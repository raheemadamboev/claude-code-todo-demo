// variant-a.jsx — Variation A: Expressive Material You
// Vibrant teal primary, large M3-Expressive shape vocabulary,
// pill-shaped FAB, soft surface containers.

const A_THEME_LIGHT = {
  primary: '#0B6E4F',
  onPrimary: '#FFFFFF',
  primaryContainer: '#9BF0CF',
  onPrimaryContainer: '#002115',
  surface: '#F5FBF7',
  surfaceContainer: '#E9F2EC',
  surfaceContainerHigh: '#E0EBE3',
  onSurface: '#172019',
  onSurfaceVariant: '#3F4943',
  outline: '#6F7A73',
  secondary: '#4C6358',
  secondaryContainer: '#CEE9DA',
  tertiary: '#3D6373',
  tertiaryContainer: '#C0E8FB',
  error: '#BA1A1A',
  errorContainer: '#FFDAD6',
};
const A_THEME_DARK = {
  primary: '#80D5B4',
  onPrimary: '#003824',
  primaryContainer: '#005138',
  onPrimaryContainer: '#9BF0CF',
  surface: '#0F1511',
  surfaceContainer: '#1B2520',
  surfaceContainerHigh: '#252F2A',
  onSurface: '#DEE5DF',
  onSurfaceVariant: '#BFC9C1',
  outline: '#89938C',
  secondary: '#B2CCBF',
  secondaryContainer: '#344B40',
  tertiary: '#A4CCDF',
  tertiaryContainer: '#234C5B',
  error: '#FFB4AB',
  errorContainer: '#93000A',
};

function VariantA({ dark = false, screen = 'list', setScreen, selectedId, setSelectedId }) {
  const T = dark ? A_THEME_DARK : A_THEME_LIGHT;
  const tagColors = dark ? TAG_COLORS_DARK : TAG_COLORS_LIGHT;
  const todoStore = window.__variantA_store ||
    (window.__variantA_store = { current: null });
  // Persist a single store across rerenders for this variant
  if (!todoStore.current) todoStore.current = SAMPLE_TODOS;
  const [todos, setTodos] = React.useState(todoStore.current);
  React.useEffect(() => { todoStore.current = todos; }, [todos]);

  const toggle = (id) => setTodos(ts => ts.map(t => t.id === id ? { ...t, done: !t.done } : t));
  const remove = (id) => setTodos(ts => ts.filter(t => t.id !== id));
  const update = (id, patch) => setTodos(ts => ts.map(t => t.id === id ? { ...t, ...patch } : t));

  let body;
  if (screen === 'onboarding') body = <A_Onboarding T={T} onDone={() => setScreen('list')} />;
  else if (screen === 'list') body = <A_List T={T} tagColors={tagColors} todos={todos} toggle={toggle} remove={remove} setTodos={setTodos}
    onOpen={(id) => { setSelectedId(id); setScreen('detail'); }}
    onSupport={() => setScreen('support')} />;
  else if (screen === 'detail') {
    const todo = todos.find(t => t.id === selectedId) || todos[0];
    body = <A_Detail T={T} tagColors={tagColors} todo={todo} update={update}
      onBack={() => setScreen('list')} onDelete={() => { remove(todo.id); setScreen('list'); }}
      onToggle={() => toggle(todo.id)} />;
  } else if (screen === 'support') body = <A_Support T={T} onBack={() => setScreen('list')} />;

  return (
    <PhoneShell width={360} height={740} bg={T.surface} fg={T.onSurface} navBg={T.surface} border={dark ? '#23211f' : '#1f1f24'}>
      {body}
    </PhoneShell>
  );
}

// ─── Onboarding ────────────────────────────────────────────────
const A_FEATURES = [
  {
    title: 'Capture every thought',
    body: 'Quick-add tasks with tags, due dates and notes — without breaking your flow.',
    color: 'primaryContainer',
    fg: 'onPrimaryContainer',
    art: (T) => <A_Art_Capture T={T} />,
  },
  {
    title: 'Stay on top of your day',
    body: 'Swipe to complete, drag to reorder, pull to refresh. The list adapts to you.',
    color: 'tertiaryContainer',
    fg: 'onSurface',
    art: (T) => <A_Art_Sort T={T} />,
  },
  {
    title: 'Focus when it matters',
    body: 'Priorities, categories and clean details so the next step is always obvious.',
    color: 'secondaryContainer',
    fg: 'onSurface',
    art: (T) => <A_Art_Focus T={T} />,
  },
];

function A_Onboarding({ T, onDone }) {
  const [idx, setIdx] = React.useState(0);
  const startX = React.useRef(null);
  const [drag, setDrag] = React.useState(0);

  const onStart = (e) => { startX.current = (e.touches?.[0]?.clientX ?? e.clientX); setDrag(0); };
  const onMove = (e) => {
    if (startX.current == null) return;
    const x = (e.touches?.[0]?.clientX ?? e.clientX);
    setDrag(x - startX.current);
  };
  const onEnd = () => {
    if (Math.abs(drag) > 50) {
      if (drag < 0 && idx < A_FEATURES.length - 1) setIdx(i => i + 1);
      if (drag > 0 && idx > 0) setIdx(i => i - 1);
    }
    setDrag(0); startX.current = null;
  };

  const last = idx === A_FEATURES.length - 1;

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: T.surface }}>
      <div
        onMouseDown={onStart} onMouseMove={onMove} onMouseUp={onEnd} onMouseLeave={onEnd}
        onTouchStart={onStart} onTouchMove={onMove} onTouchEnd={onEnd}
        style={{ flex: 1, position: 'relative', overflow: 'hidden', cursor: 'grab' }}
      >
        <div style={{
          position: 'absolute', inset: 0, display: 'flex',
          transform: `translateX(calc(${-idx * 100}% + ${drag}px))`,
          transition: drag === 0 ? 'transform .35s cubic-bezier(.2,.7,.3,1)' : 'none',
        }}>
          {A_FEATURES.map((f, i) => (
            <div key={i} style={{
              minWidth: '100%', height: '100%',
              padding: '20px 24px 0', boxSizing: 'border-box',
              display: 'flex', flexDirection: 'column',
            }}>
              <div style={{
                flex: 1, borderRadius: 32, background: T[f.color],
                position: 'relative', overflow: 'hidden',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                {f.art(T)}
              </div>
              <div style={{ padding: '28px 8px 0' }}>
                <div style={{ fontSize: 28, fontWeight: 500, lineHeight: 1.15, letterSpacing: -0.3, color: T.onSurface }}>
                  {f.title}
                </div>
                <div style={{ marginTop: 12, fontSize: 14.5, lineHeight: 1.55, color: T.onSurfaceVariant }}>
                  {f.body}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Footer: dots + button */}
      <div style={{ padding: '20px 24px 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', gap: 8 }}>
          {A_FEATURES.map((_, i) => (
            <div key={i} style={{
              height: 8, width: i === idx ? 24 : 8, borderRadius: 4,
              background: i === idx ? T.primary : T.outline + '55',
              transition: 'all .25s ease',
            }} />
          ))}
        </div>
        <button
          onClick={() => last ? onDone() : setIdx(i => i + 1)}
          style={{
            appearance: 'none', border: 'none', cursor: 'pointer',
            background: T.primary, color: T.onPrimary,
            borderRadius: 100, padding: '14px 24px',
            fontSize: 14.5, fontWeight: 500, letterSpacing: 0.1,
            boxShadow: '0 1px 2px rgba(0,0,0,.1)',
          }}
        >{last ? 'Get started' : 'Next'}</button>
      </div>
    </div>
  );
}

// Onboarding illustrations — abstract M3-shapes, no clip-art
function A_Art_Capture({ T }) {
  return (
    <svg viewBox="0 0 280 280" width="80%" height="80%">
      {/* Floating cards */}
      <g>
        <rect x="40" y="60" width="200" height="56" rx="20" fill={T.surface} opacity=".85"/>
        <rect x="58" y="76" width="100" height="10" rx="5" fill={T.onSurfaceVariant} opacity=".5"/>
        <rect x="58" y="92" width="60" height="8" rx="4" fill={T.onSurfaceVariant} opacity=".3"/>
        <circle cx="218" cy="88" r="10" fill={T.primary}/>
      </g>
      <g>
        <rect x="20" y="130" width="240" height="56" rx="20" fill={T.surface}/>
        <rect x="38" y="146" width="140" height="10" rx="5" fill={T.onSurface} opacity=".75"/>
        <rect x="38" y="162" width="80" height="8" rx="4" fill={T.onSurfaceVariant} opacity=".45"/>
        <circle cx="232" cy="158" r="12" stroke={T.primary} strokeWidth="2.4" fill="none"/>
      </g>
      <g transform="rotate(-3 140 220)">
        <rect x="40" y="200" width="200" height="48" rx="18" fill={T.surface} opacity=".7"/>
        <rect x="58" y="216" width="120" height="8" rx="4" fill={T.onSurfaceVariant} opacity=".4"/>
        <rect x="58" y="230" width="50" height="6" rx="3" fill={T.onSurfaceVariant} opacity=".3"/>
      </g>
    </svg>
  );
}
function A_Art_Sort({ T }) {
  return (
    <svg viewBox="0 0 280 280" width="80%" height="80%">
      <rect x="48" y="60" width="184" height="44" rx="14" fill={T.surface}/>
      <circle cx="68" cy="82" r="8" fill={T.primary}/>
      <rect x="86" y="78" width="100" height="8" rx="4" fill={T.onSurface} opacity=".7"/>

      <rect x="48" y="116" width="184" height="44" rx="14" fill={T.surface} transform="translate(20 0)"/>
      <circle cx="88" cy="138" r="8" stroke={T.primary} strokeWidth="2" fill="none"/>
      <rect x="106" y="134" width="120" height="8" rx="4" fill={T.onSurface} opacity=".7"/>

      <rect x="48" y="172" width="184" height="44" rx="14" fill={T.surface}/>
      <circle cx="68" cy="194" r="8" stroke={T.primary} strokeWidth="2" fill="none"/>
      <rect x="86" y="190" width="80" height="8" rx="4" fill={T.onSurface} opacity=".7"/>
      {/* arrow */}
      <path d="M236 116c0 56-100 56-100 0" stroke={T.primary} strokeWidth="3" fill="none" strokeLinecap="round" strokeDasharray="4 6"/>
      <path d="M138 110l-4 8 8 0z" fill={T.primary}/>
    </svg>
  );
}
function A_Art_Focus({ T }) {
  return (
    <svg viewBox="0 0 280 280" width="78%" height="78%">
      <circle cx="140" cy="140" r="92" fill={T.surface}/>
      <circle cx="140" cy="140" r="92" stroke={T.primary} strokeWidth="6" strokeDasharray="430 580" fill="none" strokeLinecap="round" transform="rotate(-90 140 140)"/>
      <text x="140" y="140" textAnchor="middle" dominantBaseline="middle" fill={T.onSurface} fontSize="38" fontWeight="500">3</text>
      <text x="140" y="172" textAnchor="middle" dominantBaseline="middle" fill={T.onSurfaceVariant} fontSize="12">of 5 today</text>
      <circle cx="220" cy="80" r="14" fill={T.tertiaryContainer}/>
      <circle cx="60" cy="200" r="10" fill={T.secondaryContainer}/>
    </svg>
  );
}

// ─── List Screen ────────────────────────────────────────────────
function A_List({ T, tagColors, todos, toggle, remove, setTodos, onOpen, onSupport }) {
  const [pulling, setPulling] = React.useState(0);
  const [refreshing, setRefreshing] = React.useState(false);
  const [swipeId, setSwipeId] = React.useState(null);
  const [swipeX, setSwipeX] = React.useState(0);
  const [dragId, setDragId] = React.useState(null);
  const [dragOverId, setDragOverId] = React.useState(null);
  const startY = React.useRef(0);
  const startX = React.useRef(0);
  const scrollRef = React.useRef(null);

  const open = todos.filter(t => !t.done);
  const done = todos.filter(t => t.done);

  const onContainerStart = (e) => {
    const y = e.touches?.[0]?.clientY ?? e.clientY;
    if (scrollRef.current?.scrollTop === 0) startY.current = y;
  };
  const onContainerMove = (e) => {
    if (!startY.current) return;
    const y = e.touches?.[0]?.clientY ?? e.clientY;
    const dy = y - startY.current;
    if (dy > 0) setPulling(Math.min(dy, 90));
  };
  const onContainerEnd = () => {
    if (pulling > 60) {
      setRefreshing(true);
      setTimeout(() => { setRefreshing(false); setPulling(0); }, 900);
    } else setPulling(0);
    startY.current = 0;
  };

  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: T.surface, position: 'relative' }}>
      {/* App bar */}
      <div style={{ padding: '12px 8px 4px 16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
          <button onClick={onSupport} style={iconBtn(T)} aria-label="Support">{Icon.help(T.onSurfaceVariant)}</button>
        </div>
        <div style={{ display: 'flex', gap: 4 }}>
          <button style={iconBtn(T)}>{Icon.search(T.onSurfaceVariant)}</button>
          <button style={iconBtn(T)}>{Icon.filter(T.onSurfaceVariant)}</button>
        </div>
      </div>

      <div style={{ padding: '4px 24px 12px' }}>
        <div style={{ fontSize: 32, fontWeight: 400, letterSpacing: -0.4, color: T.onSurface, lineHeight: 1.1 }}>
          Today
        </div>
        <div style={{ marginTop: 4, color: T.onSurfaceVariant, fontSize: 13.5 }}>
          Wed, May 6 · {open.length} open · {done.length} done
        </div>
      </div>

      {/* Scrolling list */}
      <div
        ref={scrollRef}
        onMouseDown={onContainerStart} onMouseMove={onContainerMove} onMouseUp={onContainerEnd} onMouseLeave={onContainerEnd}
        onTouchStart={onContainerStart} onTouchMove={onContainerMove} onTouchEnd={onContainerEnd}
        style={{ flex: 1, overflowY: 'auto', padding: '0 16px 100px', position: 'relative' }}
      >
        {/* Pull-to-refresh */}
        <div style={{
          height: pulling, display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: T.onSurfaceVariant, transition: pulling === 0 ? 'height .25s' : 'none',
        }}>
          <div style={{
            width: 40, height: 40, borderRadius: '50%', background: T.surfaceContainer,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            transform: `rotate(${pulling * 4}deg) scale(${Math.min(pulling / 60, 1)})`,
            opacity: pulling / 60,
          }}>
            <span style={{ animation: refreshing ? 'spin 1s linear infinite' : 'none' }}>
              {Icon.refresh(T.primary)}
            </span>
          </div>
        </div>

        {open.map((t, i) => (
          <A_TodoCard
            key={t.id} t={t} T={T} tagColors={tagColors}
            onToggle={() => toggle(t.id)} onOpen={() => onOpen(t.id)}
            swipeX={swipeId === t.id ? swipeX : 0}
            onSwipeStart={(x) => { setSwipeId(t.id); startX.current = x; setSwipeX(0); }}
            onSwipeMove={(x) => setSwipeX(x - startX.current)}
            onSwipeEnd={() => {
              if (swipeX < -110) remove(t.id);
              else if (swipeX > 110) toggle(t.id);
              setSwipeId(null); setSwipeX(0);
            }}
            isDragging={dragId === t.id}
            isDragOver={dragOverId === t.id}
            onDragStart={() => setDragId(t.id)}
            onDragOver={() => setDragOverId(t.id)}
            onDragEnd={() => {
              if (dragId && dragOverId && dragId !== dragOverId) {
                setTodos(curr => {
                  const a = curr.findIndex(x => x.id === dragId);
                  const b = curr.findIndex(x => x.id === dragOverId);
                  const next = curr.slice();
                  const [m] = next.splice(a, 1);
                  next.splice(b, 0, m);
                  return next;
                });
              }
              setDragId(null); setDragOverId(null);
            }}
          />
        ))}

        {done.length > 0 && (
          <div style={{ padding: '20px 8px 8px', fontSize: 11.5, fontWeight: 600,
            letterSpacing: 0.8, textTransform: 'uppercase', color: T.onSurfaceVariant }}>
            Completed · {done.length}
          </div>
        )}
        {done.map(t => (
          <A_TodoCard
            key={t.id} t={t} T={T} tagColors={tagColors} compact
            onToggle={() => toggle(t.id)} onOpen={() => onOpen(t.id)}
          />
        ))}
      </div>

      {/* FAB */}
      <button style={{
        position: 'absolute', right: 16, bottom: 16,
        height: 56, padding: '0 22px 0 18px', borderRadius: 18,
        background: T.primaryContainer, color: T.onPrimaryContainer,
        border: 'none', cursor: 'pointer',
        display: 'flex', alignItems: 'center', gap: 8,
        boxShadow: '0 4px 12px rgba(0,0,0,.18), 0 1px 3px rgba(0,0,0,.1)',
        fontSize: 14.5, fontWeight: 500,
      }}>
        {Icon.plus(T.onPrimaryContainer)} New task
      </button>
    </div>
  );
}

function A_TodoCard({ t, T, tagColors, onToggle, onOpen, compact,
  swipeX = 0, onSwipeStart, onSwipeMove, onSwipeEnd,
  isDragging, isDragOver, onDragStart, onDragOver, onDragEnd }) {
  const tag = tagColors[t.tagColor] || { bg: T.surfaceContainerHigh, fg: T.onSurface };
  const pri = t.priority;
  const priColor = pri === 'high' ? T.error : pri === 'medium' ? T.tertiary : T.outline;
  const startX = React.useRef(0);
  const tracking = React.useRef(false);

  const handleStart = (e) => {
    tracking.current = true;
    const x = e.touches?.[0]?.clientX ?? e.clientX;
    startX.current = x;
    onSwipeStart && onSwipeStart(x);
  };
  const handleMove = (e) => {
    if (!tracking.current) return;
    const x = e.touches?.[0]?.clientX ?? e.clientX;
    onSwipeMove && onSwipeMove(x);
  };
  const handleEnd = () => {
    tracking.current = false;
    onSwipeEnd && onSwipeEnd();
  };

  return (
    <div style={{
      position: 'relative', marginBottom: 8,
      transform: isDragOver && !isDragging ? 'translateY(6px)' : 'translateY(0)',
      transition: 'transform .18s ease',
    }}
      onMouseEnter={() => isDragging === false && onDragOver && onDragOver()}
    >
      {/* Swipe action bg */}
      <div style={{
        position: 'absolute', inset: 0, borderRadius: 22,
        display: 'flex', alignItems: 'center', justifyContent: swipeX < 0 ? 'flex-end' : 'flex-start',
        padding: '0 24px',
        background: swipeX < 0 ? T.errorContainer : T.primaryContainer,
        color: swipeX < 0 ? T.error : T.onPrimaryContainer,
        opacity: Math.min(Math.abs(swipeX) / 100, 1),
      }}>
        {swipeX < 0 ? Icon.trash(T.error) : Icon.check(T.onPrimaryContainer)}
        <span style={{ marginLeft: 8, fontWeight: 500, fontSize: 13 }}>
          {swipeX < 0 ? 'Delete' : 'Complete'}
        </span>
      </div>

      <div
        onClick={() => Math.abs(swipeX) < 6 && onOpen()}
        onMouseDown={onSwipeStart ? handleStart : undefined}
        onMouseMove={onSwipeMove ? handleMove : undefined}
        onMouseUp={handleEnd}
        onMouseLeave={handleEnd}
        onTouchStart={onSwipeStart ? handleStart : undefined}
        onTouchMove={onSwipeMove ? handleMove : undefined}
        onTouchEnd={handleEnd}
        style={{
          position: 'relative',
          background: compact ? T.surface : T.surfaceContainer,
          borderRadius: 22,
          padding: compact ? '10px 14px' : '12px 14px',
          display: 'flex', alignItems: 'flex-start', gap: 8,
          cursor: 'pointer',
          transform: `translateX(${swipeX}px) ${isDragging ? 'scale(1.02)' : ''}`,
          transition: tracking.current ? 'none' : 'transform .25s cubic-bezier(.2,.7,.3,1)',
          boxShadow: isDragging ? '0 10px 28px rgba(0,0,0,.18)' : 'none',
          border: compact ? `1px solid ${T.surfaceContainerHigh}` : 'none',
        }}
      >
        <AnimatedCheck checked={t.done} onChange={onToggle} color={T.primary} ring={T.outline} />
        <div style={{ flex: 1, minWidth: 0, paddingTop: 6 }}>
          <div style={{
            fontSize: 15, fontWeight: 500, color: T.onSurface,
            textDecoration: t.done ? 'line-through' : 'none',
            opacity: t.done ? 0.5 : 1,
            lineHeight: 1.35,
          }}>{t.title}</div>
          {!compact && (
            <div style={{ marginTop: 8, display: 'flex', alignItems: 'center', gap: 6, flexWrap: 'wrap' }}>
              <span style={{
                fontSize: 11.5, fontWeight: 500, padding: '3px 10px', borderRadius: 100,
                background: tag.bg, color: tag.fg,
              }}>{t.tag}</span>
              <span style={{ display: 'flex', alignItems: 'center', gap: 4, color: T.onSurfaceVariant, fontSize: 12 }}>
                {Icon.clock(T.onSurfaceVariant)} {t.due}
              </span>
              {pri === 'high' && (
                <span style={{
                  display: 'inline-flex', alignItems: 'center', gap: 3, fontSize: 11, fontWeight: 500,
                  color: priColor,
                }}>● High</span>
              )}
            </div>
          )}
        </div>
        {!compact && (
          <button
            onMouseDown={(e) => { e.stopPropagation(); onDragStart && onDragStart(); }}
            onMouseUp={(e) => { e.stopPropagation(); onDragEnd && onDragEnd(); }}
            style={{
              appearance: 'none', border: 'none', background: 'transparent', cursor: 'grab',
              padding: 4, marginTop: 4, opacity: 0.6,
            }}
            aria-label="Reorder"
          >{Icon.drag(T.onSurfaceVariant)}</button>
        )}
      </div>
    </div>
  );
}

// ─── Detail Screen ────────────────────────────────────────────────
function A_Detail({ T, tagColors, todo, update, onBack, onDelete, onToggle }) {
  const tag = tagColors[todo.tagColor] || { bg: T.surfaceContainerHigh, fg: T.onSurface };
  const pri = todo.priority;
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: T.surface }}>
      <div style={{ padding: '8px 8px 4px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <button onClick={onBack} style={iconBtn(T)} aria-label="Back">{Icon.arrow_back(T.onSurface)}</button>
        <div style={{ display: 'flex', gap: 4 }}>
          <button onClick={onDelete} style={iconBtn(T)} aria-label="Delete">{Icon.trash(T.onSurfaceVariant)}</button>
          <button style={iconBtn(T)}>{Icon.more(T.onSurfaceVariant)}</button>
        </div>
      </div>

      <div style={{ padding: '8px 24px 4px' }}>
        <span style={{
          fontSize: 11.5, fontWeight: 500, padding: '4px 12px', borderRadius: 100,
          background: tag.bg, color: tag.fg, display: 'inline-block',
        }}>{todo.tag}</span>
      </div>

      <div style={{ padding: '12px 24px 0' }}>
        <div style={{
          fontSize: 26, fontWeight: 500, lineHeight: 1.2,
          letterSpacing: -0.3, color: T.onSurface,
          textDecoration: todo.done ? 'line-through' : 'none',
          opacity: todo.done ? 0.55 : 1,
        }}>{todo.title}</div>
      </div>

      {/* Meta tile group */}
      <div style={{ padding: '20px 16px 0', display: 'grid', gap: 4 }}>
        <A_MetaRow T={T} label="Due" value={todo.due} icon={Icon.clock(T.onSurfaceVariant)} />
        <A_MetaRow T={T} label="Priority"
          value={
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6 }}>
              <span style={{
                width: 10, height: 10, borderRadius: 100,
                background: pri === 'high' ? T.error : pri === 'medium' ? T.tertiary : T.outline,
              }}/>
              {pri[0].toUpperCase() + pri.slice(1)}
            </span>
          }
          icon={Icon.flag(T.onSurfaceVariant)} />
        <A_MetaRow T={T} label="Category" value={todo.tag} icon={
          <div style={{ width: 14, height: 14, borderRadius: 4, background: tag.bg, border: `1px solid ${T.outline}55` }}/>
        } />
      </div>

      {/* Notes */}
      <div style={{ padding: '20px 24px 12px', flex: 1, overflowY: 'auto' }}>
        <div style={{ fontSize: 11.5, fontWeight: 600, letterSpacing: 0.8,
          textTransform: 'uppercase', color: T.onSurfaceVariant, marginBottom: 8 }}>Notes</div>
        <div style={{
          background: T.surfaceContainer, borderRadius: 18, padding: '14px 16px',
          minHeight: 80, fontSize: 14.5, color: T.onSurface, lineHeight: 1.55,
        }}>{todo.notes || <span style={{ color: T.onSurfaceVariant, fontStyle: 'italic' }}>No notes — tap to add.</span>}</div>
      </div>

      {/* Bottom action */}
      <div style={{ padding: '0 16px 16px' }}>
        <button onClick={onToggle} style={{
          width: '100%', height: 56, borderRadius: 18,
          background: todo.done ? T.surfaceContainerHigh : T.primary,
          color: todo.done ? T.onSurface : T.onPrimary,
          border: 'none', cursor: 'pointer',
          fontSize: 15, fontWeight: 500, letterSpacing: 0.1,
          display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
        }}>
          {todo.done ? 'Mark as not done' : <>{Icon.check(T.onPrimary)} Mark complete</>}
        </button>
      </div>
    </div>
  );
}
function A_MetaRow({ T, label, value, icon }) {
  return (
    <div style={{
      background: T.surfaceContainer, borderRadius: 14,
      padding: '14px 16px', display: 'flex', alignItems: 'center', gap: 14,
    }}>
      <div style={{
        width: 36, height: 36, borderRadius: 12, background: T.surface,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>{icon}</div>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 11.5, color: T.onSurfaceVariant, letterSpacing: 0.4 }}>{label}</div>
        <div style={{ fontSize: 14.5, color: T.onSurface, marginTop: 1 }}>{value}</div>
      </div>
    </div>
  );
}

// ─── Support Screen ────────────────────────────────────────────────
function A_Support({ T, onBack }) {
  return (
    <div style={{ height: '100%', display: 'flex', flexDirection: 'column', background: T.surface }}>
      <div style={{ padding: '8px 8px 4px', display: 'flex', alignItems: 'center' }}>
        <button onClick={onBack} style={iconBtn(T)}>{Icon.arrow_back(T.onSurface)}</button>
      </div>
      <div style={{ padding: '4px 24px 12px' }}>
        <div style={{ fontSize: 30, fontWeight: 400, letterSpacing: -0.3, color: T.onSurface, lineHeight: 1.15 }}>
          We're here<br/>to help.
        </div>
        <div style={{ marginTop: 10, fontSize: 14, color: T.onSurfaceVariant, lineHeight: 1.5 }}>
          Reach the developers directly. We usually answer within a day.
        </div>
      </div>

      <div style={{ padding: '12px 16px 0', display: 'flex', flexDirection: 'column', gap: 10 }}>
        <A_ContactCard T={T} icon={Icon.phone(T.onPrimaryContainer)}
          label="Call us"
          value="+1 (415) 555 — 0142"
          sub="Mon–Fri, 9am–5pm PT"
          accent={T.primaryContainer} accentFg={T.onPrimaryContainer} />
        <A_ContactCard T={T} icon={Icon.mail(T.onSurface)}
          label="Email support"
          value="hello@todoapp.dev"
          sub="Reply within 24 hours"
          accent={T.tertiaryContainer} accentFg={T.onSurface} />
      </div>

      <div style={{ padding: '24px 24px 8px', fontSize: 11.5, fontWeight: 600,
        letterSpacing: 0.8, textTransform: 'uppercase', color: T.onSurfaceVariant }}>FAQ</div>
      <div style={{ padding: '0 16px 16px', display: 'flex', flexDirection: 'column', gap: 4 }}>
        {['How do I sync across devices?', 'Can I export my tasks?', 'Where is my data stored?'].map((q, i) => (
          <div key={i} style={{
            background: T.surfaceContainer, borderRadius: 14,
            padding: '14px 16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between',
            cursor: 'pointer',
          }}>
            <span style={{ fontSize: 14, color: T.onSurface }}>{q}</span>
            {Icon.chev_right(T.onSurfaceVariant)}
          </div>
        ))}
      </div>
      <div style={{ flex: 1 }}/>
      <div style={{ padding: '8px 24px 16px', fontSize: 11.5, color: T.onSurfaceVariant, textAlign: 'center' }}>
        ToDo · v1.4.0 (build 2026.05)
      </div>
    </div>
  );
}
function A_ContactCard({ T, icon, label, value, sub, accent, accentFg }) {
  return (
    <button style={{
      appearance: 'none', border: 'none', background: T.surfaceContainer,
      borderRadius: 22, padding: 14, cursor: 'pointer',
      display: 'flex', alignItems: 'center', gap: 14, textAlign: 'left',
    }}>
      <div style={{
        width: 52, height: 52, borderRadius: 18,
        background: accent, color: accentFg,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
      }}>{icon}</div>
      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 11.5, color: T.onSurfaceVariant, letterSpacing: 0.4 }}>{label}</div>
        <div style={{ fontSize: 16, color: T.onSurface, marginTop: 1 }}>{value}</div>
        <div style={{ fontSize: 12, color: T.onSurfaceVariant, marginTop: 2 }}>{sub}</div>
      </div>
      {Icon.chev_right(T.onSurfaceVariant)}
    </button>
  );
}

// shared utilities for A
function iconBtn(T) {
  return {
    appearance: 'none', border: 'none', background: 'transparent',
    width: 40, height: 40, borderRadius: '50%', cursor: 'pointer',
    display: 'flex', alignItems: 'center', justifyContent: 'center',
  };
}

Object.assign(window, {
  VariantA, A_THEME_LIGHT, A_THEME_DARK,
  A_Art_Capture, A_Art_Sort, A_Art_Focus,
});
