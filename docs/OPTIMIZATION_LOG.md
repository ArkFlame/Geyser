
## Batch 1 (10 optimizations; Gradle :core:test green, evidence work/evidence/perf-batch1*.log)
| # | File | Hot path | Change | Parity test | Measured |
|---|------|----------|--------|-------------|----------|
| 1 | JavaLevelChunkWithLightTranslator | chunk load | hoist per-section registry size lookups | REVERTED: needs a full session + registries, cannot be parity-tested offline | none |
| 2 | Entity.moveRelativeRaw | entity move | compute bedrockPosition() once | EntityMoveRelativeParityTest (400 random moves, offsets, signed-zero deltas; full MoveEntityDeltaPacket equality) PASS | not claimed |
| 3 | JavaTeleportEntityTranslator | teleport | one pass over relatives instead of 9 contains() | TeleportRelativesParityTest (all 512 relative subsets, old contains-logic vs translator, teleport/moveRelative args and motion) PASS | not claimed |
| 4-5 | JavaMoveEntityPos(Rot)Translator | entity move | doubles instead of Vector3d | MoveEntityStepAccumulationParityTest (500 random step lists each, signed zeros, bit-exact vs old Vector3d sum) PASS | not claimed |
| 6 | JavaSectionBlocksUpdateTranslator | block updates | hoist lookups per entry | REVERTED: BlockState.of needs initialised registries, cannot be parity-tested offline | none |
| 7 | SoundUtils.toSoundEvent | sounds | single pass + map lookup, no exception on miss | PerfParityTest.soundEventParity (20k random + all names) | miss path alloc 54,880,000 -> 2,880,000 B per 20k calls (~19x less) |
| 8 | MessageTranslator | no-colour convert | constant replace target | NoColorMessageParityTest (constant == old concatenation; no-colour output) PASS | not claimed |
| 9 | ItemTranslator | item translate | skip identifier String when no custom block overrides (helper moved to CustomBlockItemTranslator.resolveForItem so it is testable offline) | CustomBlockOverrideGuardParityTest (empty and populated registry vs direct resolve) PASS | not claimed |
| 10 | BedrockPlayerAuthInputTranslator | per-tick input | EnumSet instead of HashSet copy | PerfParityTest.inputDataLeftoverSetParity | not claimed |

Only #7 has a measured gain. #1 and #6 were reverted (untestable offline); every remaining edit has a parity test.

## Batch 2 (in progress; guard eager debug-message building on hot unhandled paths)
Pattern: `logger.debug("..." + packet)` builds the string (including a full packet toString) even when debug is off. Guard with `isDebug()`.
| # | File | Change | Parity test | Measured |
|---|------|--------|-------------|----------|
| 11 | JavaEntityEventTranslator (unhandled entity event) | `isDebug()` guard | UnhandledPacketDebugGuardTest: debug off = packet toString called 0 times (old: 1), debug on = identical message logged | toString calls per unhandled event 1 -> 0 (deterministic count, not a CPU claim) |
| 12 | SoundUtils.readSoundEvent (unexpected type) | `isDebug()` guard | DebugConcatGuardTest: same shape, 0 vs 1 toString, identical message when on | same |
Attempted and REVERTED (cannot be tested offline: `Registries.PARTICLES`/`SOUNDS` throw "Registry has not been loaded yet!", ItemTranslator static init fails): Particles, StopSound, LivingEntity particle, ItemTranslator air-mapping, and the Registries-dependent SoundUtils.playSound/translatePlaySound guards. Animate and LevelEvent guards dropped as no gain (enum concat only).
Evidence: work/evidence/perf-batch2*.log, full :core:test green.
Full :core:test re-run green after #13-14 (work/evidence/perf-batch2-full3.log).
| 13 | level/chunk/BlockStorage.writeToNetwork | pre-size buffer with `ensureWritable(estimateNetworkSize(), false)` (non-throwing form), indexed palette loop instead of a capturing lambda | ChunkWritePresizeParityTest.storageBytesIdentical (10 palette sizes x 5 initial capacities, byte-identical to the old writer) | 12 storages x 200 writes into default `Unpooled.buffer()`: 14,458,096 -> 8,217,600 B allocated (1.76x less) |
| 14 | level/chunk/GeyserChunkSection.writeToNetwork | pre-size once for all layers | ChunkWritePresizeParityTest.sectionBytesIdentical | covered by #13 measurement (same root cause) |
Rejected (no change): skipping the per-tick empty `sendPacketsImmediately` in GeyserSession.tick. `BedrockPeer.sendPacketsImmediately` with an empty array performs `channel.flush()`, which flushes other queued packets; removing it changes send timing.
