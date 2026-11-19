# Memory optimization notes (ArkFlame fork)

Measured 2026-11-19. Same VeloFlame-Lite proxy, Zulu 27, -Xmx1G G1, no Bedrock client
connected, `jcmd GC.class_histogram` (forces a full GC) taken 10 s after Geyser logged `Done`.
Baseline is upstream `1760ffb`; optimized is the two commits on top of it.

| | Live heap after full GC | Objects |
|---|---|---|
| Upstream baseline | 67.0 MB | 1,499,753 |
| Optimized | 52.8 MB | 1,160,043 |
| Saved | 14.1 MB (21%) | 339,710 |

Largest reductions by class (baseline -> optimized):

| Class | Saved | Count |
|---|---|---|
| `byte[]` | 3.4 MB | 162,713 -> 127,479 |
| `LinkedHashMap$Entry` | 2.5 MB | 185,332 -> 107,408 |
| `LinkedHashMap` | 2.2 MB | 80,245 -> 41,287 |
| `NbtMap` | 1.6 MB | 80,953 -> 41,989 |
| `HashMap$Node[]` | 1.3 MB | 80,796 -> 41,842 |
| `String` | 0.9 MB | 156,730 -> 121,495 |

`GeyserBedrockBlock` stays at 64,147 in both: the palettes are unchanged, only duplicated
tag data was removed.

## Changes
1. Whole block `NbtMap`s are interned across the three Bedrock palette versions.
2. `BlockMappings.stateDefinitionMap` is released once the item registries are built;
   `getDefinition(NbtMap)` falls back to a palette scan.
3. `JAVA_BLOCK_STATE_IDENTIFIER_TO_ID` is trimmed to the keys the sound mappings still use.

## CPU
No per-packet or per-tick cost. Startup does one extra hash per block. Not measured during
active Bedrock play yet.

## Caveats
Measured with no Bedrock client connected, and the baseline run could not bind UDP 19132
(port in use by another server), which does not affect the registries measured here.
