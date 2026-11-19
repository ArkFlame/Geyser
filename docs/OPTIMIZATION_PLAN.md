# Geyser CPU / allocation optimization plan (round 3)

Evidence (spark CPU AqBla6hFO4, spark alloc KPQq8QMKE2, docs/GEYSER_HEAP_PRESSURE.txt). Goal: near-zero heap pressure at massive player counts, CPU not higher than baseline.

## Where the cost is
| Thread | Share | Hot spots | Strategy |
|---|---|---|---|
| NetherNet player thread | 46% of allocation | byte[] allocateArray 34% (packet/chunk buffers), BitStorage.<init> 18%, BitArrayVersion.createArray 10%, Entity.moveRelativeRaw, GeyserSession.tick 8.9% of its samples | A: chunk/bit arrays. B: per-tick/per-entity garbage. C: buffer copies |
| localSession (Java decode) | 22.6% of allocation | MinecraftTypes.readByteArray 36%, DirectByteBuffer duplicate/slice | D: avoid copies, slice not copy where lifetime allows |
| NetherNet signaling | 19% CPU / 15% alloc | native datachannel+SSL (not fixable in Java); duplicate/slice 63% of its allocation | E: nethernet package: fewer/bigger messages, no nioBuffers |
| Backend worker | 7% CPU | itable/vtable dispatch, fireChannelRead | F: nothing cheap; skip |

## Rules for every change
1. Only hot paths: per packet, per tick, per entity, per chunk, per inventory click. Skip startup, config, commands, forms.
2. Allowed: hoist invariants and repeated getters into locals; avoid streams/Optional/boxing/varargs/String.format/concat in loops; EnumSet/static immutable constants instead of per-call new; presize ArrayList/HashMap; fastutil primitive maps; reuse immutable Vector3f/Vector3i; early-return when nothing changed; avoid defensive copies of collections that are only iterated.
3. Forbidden: changing public/protected signatures, wire output, event order, or thread-safety (no unsynchronised sharing of mutable state across threads); caching that can go stale; unbounded caches.
4. If you are not sure a change is behavior-identical, do not make it. Zero changes is a valid result.

## Units
100 units, one file each (see unit files). Each unit names its FILE and an AREA below with specific things to look for.
- AREA-A chunk/world (level, session/cache/Chunk*, World*): bit arrays, palettes, per-section lists.
- AREA-B tick/entity (entity/**, EntityCache, session tick): per-tick packet/vector/collection allocation, metadata maps, passenger/attribute updates.
- AREA-C inventory/items (translator/inventory, translator/item, util/InventoryUtils, inventory): ItemData/NBT rebuilds, stream/collect, click plan allocations.
- AREA-D java->bedrock packet translators (translator/protocol/java/**): per-packet allocation, repeated registry lookups, list copies.
- AREA-E bedrock->java translators (translator/protocol/bedrock/**).
- AREA-F nethernet (network/bedrock/nethernet): buffer copies, nioBuffers, per-message objects.
- AREA-G utils/session caches (util/**, session/**): hot helpers called per packet.

Verification (orchestrator): ./gradlew :core:compileJava, shadowJar, class-level review of each diff before commit.
