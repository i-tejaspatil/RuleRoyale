# RuleRoyale

**A Kotlin Multiplatform exploration of emergent complexity through user-configurable rule systems**

RuleRoyale is a cross-platform mobile application that demonstrates how simple rules can generate complex, unpredictable patterns. Built with Kotlin Multiplatform and Compose Multiplatform, it runs natively on both Android and iOS with 95%+ shared codebase.

---

## Overview

RuleRoyale transforms abstract mathematical concepts into interactive experiences. Users experiment with cellular automata and ecosystem dynamics by adjusting fundamental rules, witnessing how microscopic changes produce macroscopic emergence. The application bridges education and competition through three distinct simulation modes.

---

## Core Philosophy

**From Rules to Reality**: Every complex system—from biological evolution to social dynamics—emerges from simple underlying rules. RuleRoyale makes this principle tangible by letting users manipulate these rules and observe outcomes in real-time.

**Competitive Discovery**: Beyond passive observation, RuleRoyale introduces Battle Mode—a novel competitive framework where players compete to discover optimal rule configurations, transforming scientific experimentation into strategic gameplay.

---

## Features

### 1. Life From Rules Arena

A generalized implementation of Conway's Game of Life with fully configurable survival and reproduction rules.

**What Makes It Unique:**
- **Three adjustable parameters** instead of fixed rules:
  - **Underpopulation threshold**: Cells die if neighbors < N
  - **Overpopulation threshold**: Cells die if neighbors > N
  - **Reproduction count**: Dead cells revive with exactly N neighbors
  
- **Real-time pattern analysis** detecting five distinct states:
  - **Stable**: Pattern reaches equilibrium
  - **Oscillating**: Repeating cycles detected
  - **Extinct**: All cells die
  - **Saturated**: Grid fills beyond 90%
  - **Running**: Evolution continues

- **Interactive grid manipulation**: Click cells to add/remove during pause
- **Time controls**: Play, pause, step forward, rewind through history
- **Visual feedback**: Clean monochrome grid with clear alive/dead states

**Technical Implementation:**
- Immutable grid operations ensure deterministic behavior
- Bounded history tracking for oscillation detection (6-generation window)
- O(n²) neighbor counting with boundary checking

---

### 2. Ecosystem Arena

A predator-prey-grass simulation with configurable interaction rules and two distinct behavioral modes.

**What Makes It Unique:**

**Dual Rule Modes:**
- **Normal Mode**: Traditional food chain (Grass ← Prey ← Predator)
- **Inverted Mode**: Reversed dynamics (Grass → Prey → Predator)
  - Predators consume grass directly
  - Prey hunt predators
  - Demonstrates how rule inversion creates entirely different ecosystem behaviors

**Configurable Density:**
- Independent sliders for grass, prey, and predator initial populations
- Real-time density adjustment (None / Low / Medium / High)

**Deterministic vs. Random Simulation:**
- **Deterministic**: Seeded RNG for reproducible experiments
- **Random**: True randomness for varied outcomes

**Live Status Detection:**
- **Balanced**: Healthy population equilibrium
- **Prey Starvation**: Insufficient grass
- **Predator Starvation**: Insufficient prey
- **Extinction Events**: Species die out
- **Empty World**: Complete collapse

**Advanced Simulation Mechanics:**
- Seven-phase tick cycle: Eating → Decay → Death → Movement → Reproduction → Regrowth → Aging
- Energy-based survival system with decay rates
- Age-dependent reproduction with minimum thresholds
- Movement AI: Entities seek food or explore randomly
- Collision-free movement and reproduction (two-phase commit pattern)
- Independent grass regrowth cycle

**Visual Design:**
- Color-coded entities: Green (grass), White (prey), Red (predator)
- Grid-based spatial representation
- Real-time population dynamics

---

### 3. Battle Mode

The application's most innovative feature: offline competitive pattern discovery.

**The Concept:**
Two players receive identical initial grid configurations via shared 6-character codes. Each independently experiments with rule configurations, then compares outcomes to determine the winner.

**How It Works:**

**Battle Setup:**
1. **Create Battle**: Generate a unique 6-character code (e.g., "ABC123")
2. **Share Code**: Send to friend via any channel (text, chat, voice)
3. **Join Battle**: Friend enters code to get identical starting grid
4. **Configure Rules**: Each player independently adjusts three rules
5. **Run Simulation**: Both execute until pattern stabilizes
6. **Compare Results**: Manually compare final state and tick count

**Winning Criteria (Clear Ranking System):**
1. **First Place**: STABLE - Pattern stops changing
2. **Second Place**: OSCILLATING - Pattern repeats in cycles
3. **Third Place**: EXTINCT or SATURATED - Complete death or overpopulation

**Tie-Breaker**: Lower tick count wins (faster convergence = better rules)

**Technical Innovation:**

**Deterministic Seeding:**
```
Code "ABC123" → Seed 12345678 → Identical 20×20 grid (100 cells alive)
Same code on different devices = Same initial pattern
```

**Fair Competition:**
- No internet required (offline-first design)
- Same starting conditions guaranteed
- No Step/Rewind controls (prevents cheating)
- Automatic pause on terminal state
- Results displayed with status + tick count for manual comparison

**Why This Is Novel:**

Most cellular automata apps are passive viewers. Battle Mode transforms exploration into competition:
- **No opponent needed in real-time**: Asynchronous gameplay
- **No server infrastructure**: Completely offline
- **Cross-platform fairness**: Android vs iOS = identical grids
- **Strategic depth**: Players must understand emergence to optimize rules
- **Social aspect**: Friends compete to find "best" rules

This bridges the gap between:
- Educational tools (learning cellular automata)
- Competitive games (ranking and winning)
- Social experiences (sharing codes and comparing)

**Example Battle Scenario:**

```
Player A: Rules (2, 4, 3) → STABLE at tick 187
Player B: Rules (1, 3, 2) → OSCILLATING at tick 94

Result: Player A wins (STABLE ranks higher than OSCILLATING)
```

---

## Technical Architecture

### Kotlin Multiplatform Structure

**Code Sharing:**
- **95%+ shared code** across Android and iOS
- Platform-specific code: <5% (only MainActivity and MainViewController)

**Module Organization:**

```
commonMain/
├── engine/
│   ├── lifefromrules/     # Cellular automaton engine
│   │   ├── LifeFromRulesEngine.kt
│   │   ├── LifeRules.kt
│   │   ├── Grid.kt
│   │   └── LifeFromRulesAnalyzer.kt
│   └── ecosystem/
│       ├── engine/         # Multi-agent simulation
│       │   ├── EcosystemEngine.kt
│       │   └── TickProcessor.kt
│       ├── model/          # Entity system
│       ├── rules/          # Interaction rules
│       └── rng/            # Deterministic RNG
├── ui/
│   ├── theme/              # Material3 dark theme
│   ├── home/               # Navigation hub
│   ├── lifefromrules/      # Life arena UI
│   ├── ecosystem/          # Ecosystem arena UI
│   └── battle/             # Battle mode UI
│       ├── BattleSetupScreen.kt
│       ├── BattleArenaScreen.kt
│       ├── BattleCode.kt
│       └── BattleResultCard.kt
└── platform/
    └── HapticFeedback.kt   # expect/actual pattern demo

androidMain/
├── MainActivity.kt
└── HapticFeedback.android.kt

iosMain/
├── MainViewController.kt
└── HapticFeedback.ios.kt
```

**Shared Components:**
- **All simulation engines**: Pure Kotlin, platform-agnostic
- **All UI screens**: Compose Multiplatform
- **All business logic**: State management, navigation, analysis
- **Theme system**: Material3 dark theme with custom colors

**Platform-Specific:**
- Entry points only (MainActivity, MainViewController)
- Haptic feedback implementations (demonstrates expect/actual pattern)

---

## Design Decisions

### Visual Design

**Dark Theme Throughout:**
- Reduces eye strain during extended experimentation
- Grid cells more visible against dark background
- Consistent with scientific/technical aesthetic

**Color Scheme:**
- **Primary**: Rule Yellow (#FFC107) - Energetic, attention-grabbing
- **Background**: True black (#000000) - Maximum contrast
- **Surface**: Dark gray (#1E1E1E) - Subtle elevation
- **Grid lines**: Very dark gray (#1F1F1F) - Minimal distraction

**Typography:**
- Material3 defaults with clear hierarchy
- Monospace for battle codes (visual clarity)
- Large touch targets (48dp minimum)

### UX Patterns

**Consistent Controls:**
- Play/Pause/Reset in all modes
- 32dp bottom padding (prevents navigation bar interference)
- Top bar with back button and status display
- Scrollable middle content, fixed controls at bottom

**Feedback Mechanisms:**
- Status text updates (Running, Stable, Extinct, etc.)
- Tick counters for progress tracking
- Visual state changes in real-time
- Color-coded ecosystem status

**iOS-Specific Adaptations:**
- 44dp top padding for Dynamic Island and status bar
- Safe area handling for notched devices
- Centered layouts prevent clipping

---

## Battle Mode Code System

**Code Format:**
- Length: 6 characters
- Character set: A-Z, 2-9 (no 0/O, 1/I confusion)
- Example: "ABC123", "XYZ789"

**Code Generation:**
```kotlin
fun generateBattleCode(): String {
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // 32 chars
    return (1..6).map { chars.random() }.joinToString("")
}
```

**Seed Conversion:**
```kotlin
fun battleCodeToSeed(code: String): Long {
    // Each character mapped to 0-31, packed into Long
    // Guarantees: Same code = same seed = same grid
}
```

**Grid Generation:**
```kotlin
fun createSeededGrid(size: Int, seed: Long): Grid {
    val random = Random(seed)
    return Grid(size, size) { row, col ->
        if (random.nextFloat() < 0.25f) ALIVE else DEAD
    }
}
```

**Why 25% Density:**
- Not too sparse (avoids quick extinction)
- Not too dense (avoids immediate saturation)
- Creates interesting, unpredictable patterns
- Approximately 100 alive cells in 20×20 grid

**Life From Rules uses 18% for learning balance, Battle Mode uses 25% for competitive complexity**

---

## Engine Implementation Details

### Life From Rules Engine

**Core Algorithm:**
```kotlin
for each cell (r, c):
    aliveNeighbors = count 8-neighborhood alive cells
    
    if cell is ALIVE:
        if aliveNeighbors < underPopulation: → DEAD
        if aliveNeighbors > overPopulation: → DEAD
        else: → ALIVE
    
    if cell is DEAD:
        if aliveNeighbors == reproduction: → ALIVE
        else: → DEAD
```

**Pattern Detection:**
- **Stable**: Grid matches previous generation
- **Oscillating**: Grid matches any of last 6 generations
- **Extinct**: Zero alive cells
- **Saturated**: >90% cells alive

**Performance:**
- O(rows × cols) per generation
- Immutable grid approach (functional style)
- History capped at 6 generations for oscillation detection

### Ecosystem Engine

**Tick Processing Order:**
1. **Eating**: Adjacent food consumed, energy gained
2. **Energy Decay**: Eaters/victims lose energy per tick
3. **Death**: Zero energy or max age → removal
4. **Movement**: Seek food or explore (two-phase collision prevention)
5. **Reproduction**: Minimum energy/age → spawn offspring (age=0, no collision)
6. **Grass Regrowth**: Independent cycle every N ticks
7. **Age Increment**: All entities age by 1

**Collision Prevention:**

**Movement (Two-Phase Commit):**
```kotlin
Phase 1: Calculate all intended moves
Phase 2: Detect conflicts
         If multiple entities target same cell → all stay put
         If single entity targets cell → move succeeds
```

**Reproduction:**
```kotlin
Check: spawnPosition !in occupiedMap
Reserve position immediately before continuing
Prevents: Two parents spawning at same position
```

**Energy System:**
```kotlin
Energy decay clamped: maxOf(0, energy - decay)
Prevents negative energy states
```

**Why These Fixes Matter:**
- **Determinism**: Critical for Battle Mode fairness
- **Stability**: Prevents ecosystem collapse edge cases
- **Predictability**: Same inputs = same outputs

---

## User Experience Flow

### First Launch

1. **Home Screen**: Three arena cards with descriptions
2. **Clear hierarchy**: Life From Rules → Ecosystem → Battle Mode
3. **Visual consistency**: All cards styled identically with yellow borders

### Life From Rules Experience

1. Start with default rules (2, 3, 3) - Conway's original
2. Adjust sliders to experiment
3. Paint initial pattern or use random seed
4. Play → Observe → Pause → Rewind
5. Learn how rules affect emergence

### Ecosystem Experience

1. Choose mode: Normal or Inverted
2. Set simulation type: Deterministic or Random
3. Adjust initial densities
4. Play → Watch populations evolve
5. Observe status changes (balanced, starvation, extinction)

### Battle Mode Experience

**As Host:**
1. Tap "Battle Mode" → "Create Battle"
2. See generated code (e.g., "PQR456")
3. Share code with friend (any channel)
4. Adjust rules experimentally
5. Press "Start Battle"
6. Note final status + tick count
7. Compare with friend

**As Joiner:**
1. Receive code from friend
2. Tap "Join Battle"
3. Enter code
4. Same initial grid appears
5. Adjust rules independently
6. Press "Join Battle"
7. Note final status + tick count
8. Compare with friend

---

## Why This Project Stands Out

### 1. Genuine Innovation

**Battle Mode** is not an obvious extension of cellular automata. Most implementations focus on:
- Watching pre-defined patterns (gliders, spaceships)
- Learning about Conway's Game of Life
- Passive observation

RuleRoyale adds:
- **Active competition** without requiring simultaneous play
- **Strategic thinking** about rule optimization
- **Social dimension** through code sharing

### 2. Educational Value

Users learn:
- **Emergence**: How complexity arises from simplicity
- **Parameter sensitivity**: Small rule changes = large outcome differences
- **Scientific method**: Hypothesis (rules) → Experiment (simulate) → Result (pattern)
- **Optimization thinking**: What rule set produces "best" outcome?

### 3. Technical Excellence

- **Robust engines**: Fixed all collision bugs, negative energy states
- **Deterministic behavior**: Critical for Battle Mode fairness
- **Clean architecture**: Clear separation of engine, UI, platform
- **95%+ code sharing**: Demonstrates KMP mastery
- **Production quality**: Proper error handling, state management

### 4. User-Centric Design

- **No tutorial needed**: Interface is self-explanatory
- **Instant feedback**: See results immediately
- **Reversible actions**: Rewind, reset, try again
- **Offline-first**: No internet dependency (even for Battle Mode)
- **Cross-platform**: Android user vs iOS user = fair battle

---

## Technical Highlights

### Kotlin Multiplatform Usage

**Shared Logic (95%+):**
- Complete engine implementations
- All UI via Compose Multiplatform
- State management
- Navigation system
- Battle code generation/validation
- Pattern analysis algorithms

**Platform-Specific (<5%):**
- Entry points (MainActivity, MainViewController)
- Haptic feedback (demonstrates expect/actual)

**Why This Matters:**
- Single source of truth for business logic
- Identical behavior across platforms
- Faster development and testing
- Easier maintenance

### Compose Multiplatform

**Benefits Demonstrated:**
- Material3 theming works identically on both platforms
- Complex layouts (grids, sliders, tabs) shared completely
- Animations and state management unified
- Navigation handled in common code

**No Platform-Specific UI Code:**
- All screens in commonMain
- Same composables render on Android and iOS
- Theme applies automatically

### Design Patterns

**Immutability:**
- Grid operations never mutate, always return new instances
- Prevents race conditions
- Makes history/rewind trivial

**State Management:**
- Compose state hoisting
- ViewModel-like controllers for Ecosystem
- Deterministic state updates

**Separation of Concerns:**
- Engine layer: Pure logic, no UI dependencies
- UI layer: Composables, no business logic
- Platform layer: Entry points only

---

## Future Potential

### Possible Extensions

**Advanced Battle Features:**
- Leaderboards (most stable patterns per code)
- Pattern sharing (export grid states)
- Tournament mode (bracket-style competitions)

**Enhanced Analytics:**
- Pattern classification (identify gliders, oscillators)
- Statistical analysis (average lifespan, stability rate)
- Historical tracking (previous experiments)

**Educational Content:**
- Guided tutorials for famous patterns
- Challenges (achieve stable with specific rules)
- Explanations of emergence theory

**Additional Simulations:**
- Langton's Ant
- Brian's Brain
- Wireworld (logic gates)

**Multiplayer Evolution:**
- Real-time co-op pattern building
- Competitive pattern destruction
- Synchronized battles with live updates

---

## Building and Running

### Prerequisites

- JDK 17 or higher
- Android Studio Hedgehog or later
- Xcode 15+ (for iOS builds, macOS only)
- Kotlin 1.9.20+

### Android

**Option 1: Using Gradle (Command Line)**
```bash
./gradlew :composeApp:assembleDebug
```

**Option 2: Using Android Studio**
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Select an Android device/emulator from the device dropdown
4. Click Run (green play button) or press `Ctrl+R`

### iOS (macOS Required)

**Option 1: Using Xcode (Recommended)**

1. **Open the iOS project:**
   ```bash
   cd iosApp
   open iosApp.xcodeproj
   ```
   Or double-click `iosApp/iosApp.xcodeproj` in Finder

2. **Select a simulator/device:**
   - Click the device selector at the top (next to the scheme)
   - Choose an iPhone simulator (e.g., "iPhone 15 Pro")
   - Or connect a physical iPhone and select it

3. **Run the app:**
   - Click the Run button (▶️) in the toolbar
   - Or press `Cmd+R`
   - First build may take 2-3 minutes

**Option 2: Using Command Line**
```bash
# Run on simulator
cd iosApp
xcodebuild -scheme iosApp -configuration Debug -destination 'platform=iOS Simulator,name=iPhone 15 Pro' build

# Or use xcodebuild to build and run
xcodebuild -scheme iosApp -configuration Debug -sdk iphonesimulator -destination 'platform=iOS Simulator,name=iPhone 15 Pro'
```

**Running on Physical iPhone:**

1. **Connect your iPhone** via USB cable

2. **Trust your Mac** (if prompted on iPhone)

3. **In Xcode:**
   - Select your connected iPhone from device selector
   - Click Run button
   - If prompted about Developer Mode:
     - On iPhone: Settings → Privacy & Security → Developer Mode → Enable
     - Restart iPhone if needed

4. **Signing (if first time):**
   - Xcode may show "Signing for iosApp requires a development team"
   - Click the project name in the left navigator
   - Select the iosApp target
   - Go to "Signing & Capabilities" tab
   - Select your Apple ID team from the dropdown
   - Xcode will automatically provision the app

5. **Trust Developer Certificate** (first time only):
   - On iPhone: Settings → General → VPN & Device Management
   - Tap your developer certificate
   - Tap "Trust [Your Name]"
   - Return to home screen and launch RuleRoyale

**Troubleshooting iOS Build:**

If you encounter build errors:

```bash
# Clean build folder
cd iosApp
xcodebuild clean -scheme iosApp

# Or in Xcode: Product → Clean Build Folder (Cmd+Shift+K)
```

If pods are outdated:
```bash
cd iosApp
pod install --repo-update
```

---


## Conclusion

RuleRoyale demonstrates that Kotlin Multiplatform can deliver complex, interactive applications with massive code reuse. By combining educational value with competitive gameplay, it shows how abstract mathematical concepts can become engaging mobile experiences.

The project's innovation lies not in inventing new algorithms, but in **reimagining how users interact with cellular automata**—transforming passive observation into active competition. Battle Mode creates a new category: **asynchronous competitive scientific experimentation**.

This is more than a Game of Life clone. It's a platform for discovering how rules shape reality, one pattern at a time.

---

**Built with Kotlin Multiplatform** | **Compose Multiplatform** | **Material3**

*Project created for Kotlin Multiplatform Contest 2026*

