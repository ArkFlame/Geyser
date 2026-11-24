# Wave 26 — final pass on 16 upstream "Confirmed Bug" issues still UNFIXED

Scope: worktree /tmp/opencode/w26-confirmed, branch wave26/confirmed. `upstream/master` is an ancestor of HEAD; no upstream commit or merged PR fixes any of these issues (`git log --all --grep` found only #5755 in the leash batch `817a95c29`, and that commit's diff is a leash-timing fix, not a teleport fix). No Gradle, live tests, API bump, submodule edit or default-behaviour change was made. Every issue was read with `gh issue view`/`gh api` (body + all comments) and mapped to a concrete fork code path. Result: no safe, reproducible fork-local root cause exists for any of the 16; each is externally constrained or lacks the evidence needed to change code without guessing.

---

## #5755 — "The player's movements are strange." — UNFIXED, externally constrained
Timeline: reporter C3 says the problem disappeared after lowering Paper's anti-xray from level 2 to 1 ("We are not sure if this is a bug"); maintainer C4 only proposes a general improvement to batched Bedrock teleports, no repro or fix. Code: `GeyserSession.confirmTeleport()` (GeyserSession.java:2285-2310) re-sends `MoveAbsolute` until the client confirms; the "moved too quickly" log is `BedrockMovePlayer.java:255`. Cross-world speed after world switch was fixed separately by `f7d4dcb9` (#6903/#6927, attribute reset in `JavaRespawnTranslator`/`BedrockPlayerActionTranslator`) — different symptom. **Missing evidence:** clean-server reproduction with debug on, plus client `MovePlayerPacket` teleport-mode / server teleport-id trace showing which teleport is never confirmed. Relaxing the resend loop would regress teleport reliability. No change.

## #5584 — Anvil "Required Experience" field cannot be bypassed — BLOCKED (Bedrock client)
C1: "unfortunately a bedrock client issue"; C2: maybe workable; C4: the Java plugin is private under NDA. Code: `AnvilInventoryTranslator.java:45-117` maps the Java anvil cost property to Bedrock's anvil container property; Bedrock always renders the cost field. **Missing evidence:** the plugin's identity or a dump/packet capture of the packet that suppresses the Java field. No workable target to port. No change.

## #5017 — Sitting players float in Bedrock POV — UNFIXED, no repro detail
Zero comments; only user video/images. Plugins (GSit/Citizens) seat entities via an invisible vehicle and a passenger offset. Code: `JavaSetPassengersTranslator.java:54-88` and `EntityUtils.updateMountOffset()` (EntityUtils.java:182+). **Missing evidence:** which seat entity/offset the plugins use (packet capture of SetPassengers + passenger metadata), and whether the Java client applies a client-only offset. Guessing an offset would change all mount seating. No change.

## #4855 — Crafted items land in crafting-grid slot 0 (mobile Pocket UI) — BLOCKED (client UI)
Maintainer C3 reproduces only on mobile; C5 gives classic-UI workaround. Code: `CraftingInventoryTranslator` maps CREATED_OUTPUT→java slot 0; `InventoryTranslator.translateRequests()` handles the request normally. **Missing evidence:** the Pocket-UI `ItemStackRequest` action sequence (source/destination slot types) for a tapped result; without it any redirect would break normal crafting on Windows/console. No change.

## #4832 — Item bounce animation on honey block — BLOCKED
No comments, no dump, no Geyser version. Code: `ItemEntity` forwards Java entity motion. **Missing evidence:** item-motion packet trace on honey block, and confirmation that Bedrock does not simulate item bouncing client-side. No change.

## #4453 — Weird slow crafting of large stacks — UNFIXED
C1 confirms across platforms; no cause identified. Code inspection: `InventoryTranslator.java:271-304` has a 750 ms `mustDelay` but only for the first request batch after opening a container (close-inventory guard for #5258), not per craft. **Missing evidence:** timing trace of ItemStackRequest/ItemStackResponse while shift-crafting hay bales, identifying which upstream delay or server throttle dominates. No change.

## #4102 — Villager trade selection always returns first trade (mobile) — UNFIXED
C3 says "seems to have been merged in a previous pr", but `git log --all --grep=4102` and GitHub PR search found no fix commit/PR. Code: `MerchantInventoryTranslator.java:126-137` derives the trade index from `recipeNetworkId - 1`; `JavaMerchantOffersTranslator.java:104-126` assigns `netId = i + 1` and appends a hidden extra recipe. **Missing evidence:** the actual netId sent by the mobile client for trades 3-5, and the PR the commenter refers to. No change.

## #4092 — Trading in the End rejects custom trades — UNFIXED
C1 gives a reproducible `/summon villager` command (custom SkullOwner player-head trade); sound plays but the trade is rejected. Root cause not identified; most likely custom NBT item mismatch (cf #3580), not dimension-specific. Code: `JavaMerchantOffersTranslator.getItemTag()` and `MerchantInventoryTranslator.handleTrade()`. **Missing evidence:** server log / packet trace of the rejected `ServerboundSelectTradePacket` and the item-NBT comparison; no explanation of why the End matters. No change.

## #3937 — Fast-build glass panes leave holes — UNFIXED
C1: "Geyser is missing some block placements", still on latest build. Code: `BedrockInventoryTransactionTranslator.java:224-248` rejects any `SIMULATION_TICK` repeat within 110 ms, including at different block positions — this is deliberate (fix for #6901) and cannot be relaxed blindly. **Missing evidence:** packet capture distinguishing legitimate fast placements from held-button repeats. No change.

## #3910 — Map player location missing in Nether/End — UNFIXED
No comments. Code: `JavaMapItemDataTranslator.java:47-60` sets `dimensionId` from the session; history shows that was an intentional change (`6e0bad3c4`, "Fix #4837 by not hardcoding dimension IDs"). **Missing evidence:** a captured Bedrock `ClientboundMapItemDataPacket` (dimensionId, origin, trackedEntityIds) matched to the Java MapData center for a Nether/End map, and Bedrock's undocumented dimension scaling for the self icon. No change.

## #3580 — AttributeModifiers items stuck in cursor — BLOCKED (third-party NBT rewrite)
Body has a packet-level description (two window clicks; the PLACE_ALL click is rejected because the item no longer matches). C1: likely ViaVersion/ProtocolLib pre-1.17 NBT mutation, "can't see an easy fix"; C2 lists version-dependent behaviour. **Missing evidence:** which plugin rewrites the item and the exact NBT diff between the two clicks. The damage-lore half needs Java tooltip data not sent in the map/item packet (wave-17 precedent for #5692). No change.

## #3480 — Offhand interactions not sent to the event handler — BLOCKED by design
C1: a proper fix requires replicating Java item/block behaviours on the client; low priority. Code: `BedrockInteractTranslator` and `GeyserSession.attemptToBlock()` (GeyserSession.java:1789-1813) handle the offhand shield only; no general offhand-use translation exists. **Missing evidence:** the per-interaction packet contract Java sends for both hands. No change.

## #3407 — Movement rebound on Spigot/Paper while climbing — BLOCKED (external)
Maintainer C3 points to the Hurricane wiki workaround (remove server-side collision for bamboo/dripstone). Root cause is Bedrock-vs-Java collision/anticheat disagreement, not a Geyser packet bug. **Missing evidence:** server version/config and "moved too quickly" logs with coordinates. No change.

## #2879 — LibsDisguises falling→solid→falling disguise not reverted — BLOCKED (plugin-specific)
Only "+1" comments; C6 narrowed it to AddEntityPacket behaviour for disguised falling blocks. Code: `JavaSetPassengersTranslator`/`EntityCache` entity-ID reuse. **Missing evidence:** a plugin-free minimal repro and the exact packet sequence when LibsDisguises reuses an entity id with a new type. No change.

## #2787 — Bridging possible with allow-bedrock-bridging=false — PARTIALLY already fixed; remainder BLOCKED
Sub-issue 1 (reverse/touch bridging, duplicate of #2769) is already fixed in the fork: upstream PR #6220's interact-rotation check at `BedrockInventoryTransactionTranslator.java:198-215`, commit `40c0cf638` (merged `7598a5870`, wave 18). Remaining sub-issues (double-click forward bridging, ghost blocks/walking on air) have no identified Geyser-side cause. **Missing evidence:** packet capture of the placements, using the existing gated `debugRejectedPlacement()` diagnostics, to identify which guard (if any) fires. No change.

## #1782 — Shift+plant crops eats food (with offhand shield) — UNFIXED, no safe fix
C1 initially called it expected, retracted; C3 says behaviour matches Java without a shield and differs only with a shield; C4 reporter later doubted shift is required. Code: `GeyserSession.attemptToBlock()` sends `useItem(OFF_HAND)` on sneak; `BedrockInventoryTransactionTranslator` case 1 sends `useItem(MAIN_HAND)`, which calls `disableBlocking()`. **Missing evidence:** the exact Bedrock transaction sequence (case 0 vs case 1) and the matching Java client packets; a blanket suppression of main-hand use while sneaking would break crouch-shooting/eating. No change.

---

## Summary
- Promotions: none. No issue was promoted to FIXED/UPSTREAM_FIXED/DUPLICATE — no ancestor fix commit with a fork `file:line` exists for any of the 16.
- Fixed numbers: none (see per-issue missing evidence above).
- Files changed in the repository: none (the tracker docs were not edited). This audit record is written to `/tmp/confirmed26.md` as requested.
- Checks: read-only verification only — `git log --all --grep` per issue, `gh issue view`/`gh api` timelines, GitHub PR search, and careful source reading. Gradle and live tests were not run per the brief.
