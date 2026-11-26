
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
