# ArkFlame/Geyser — Upstream Issue/PR Tracker

_Generated at: 2026-11-24T22:13:34Z_

This tracker seeds the fork audit campaign by enumerating every issue and pull request in `GeyserMC/Geyser` (upstream) and `ArkFlame/Geyser` (fork). The machine-readable source of truth is [`arkflame-geyser-tracker.json`](arkflame-geyser-tracker.json); this document is a human-readable summary.

## Dynamically discovered counts

| Repo | Type | Open | Closed (unmerged) | Merged | Total |
| --- | --- | ---: | ---: | ---: | ---: |
| ArkFlame/Geyser | issue | 0 | 0 | 0 | 0 |
| ArkFlame/Geyser | pr | 0 | 0 | 0 | 0 |
| **ArkFlame/Geyser** | **all** | | | | **0** |
| GeyserMC/Geyser | issue | 291 | 4268 | 0 | 4559 |
| GeyserMC/Geyser | pr | 57 | 442 | 1760 | 2258 |
| **GeyserMC/Geyser** | **all** | | | | **6817** |
| **ALL REPOS** | **issue** | 291 | 4268 | 0 | 4559 |
| **ALL REPOS** | **pr** | 57 | 442 | 1760 | 2258 |
| **ALL REPOS** | **all** | | | | **6817** |

Unique records (deduplicated by numeric ID): **6817**. Note: closed issues and closed/merged PRs are intentionally not expanded here — see the JSON file for all of them.

## Status legend

| Status | Meaning |
| --- | --- |
| `UNFIXED` | Not fixed in the fork; requires an audit or an adoption decision. |
| `IN_PROGRESS` | A worker is actively porting the fix. |
| `FIXED` | Fix has been ported and verified in the fork. |
| `UPSTREAM_FIXED` | Fix is merged upstream and already present in the fork via shared upstream history. |
| `DUPLICATE` | Duplicate of another tracked record. |
| `BLOCKED` | Work cannot proceed until a prerequisite is resolved. |
| `UNSUPPORTED` | Not applicable/supported by the fork. |
| `NOT_APPLICABLE` | Record does not require any action. |
| `PARTIAL` | Part of the change is integrated; a documented remainder is still outstanding. |

## Aggregate status counts

| Status | Count |
| --- | ---: |
| `UNFIXED` | 822 |
| `IN_PROGRESS` | 0 |
| `FIXED` | 126 |
| `UPSTREAM_FIXED` | 2847 |
| `DUPLICATE` | 556 |
| `BLOCKED` | 46 |
| `UNSUPPORTED` | 598 |
| `NOT_APPLICABLE` | 1822 |
| `PARTIAL` | 1 |

## Mass audit (two passes)

A two-pass mass audit was applied to the `UNFIXED` inventory using the on-disk shard reports. The campaign expected 148 shard reports; 147 were present on disk (50 first-pass reports, `audit-results/shard-00.json` through `shard-49.json`, plus 97 deep-verification reports, `deep-results/deep-00.json` through `deep-97.json`; the `deep-34` report was absent). Every available report was reviewed, and all records that were still `UNFIXED` were eligible for reclassification.

### Method

- **Pass 1 — broad review:** first-pass shards classified closed inventory items against cached GitHub metadata (labels, state, merge reason).
- **Pass 2 — deep verification:** deep shards cross-checked each candidate against fork source, cached GitHub issue/PR bodies, and protocol documentation where applicable.
- Conflicting proposals for the same ID were resolved by priority `UPSTREAM_FIXED` > `DUPLICATE` > `UNSUPPORTED` > `NOT_APPLICABLE`.
- Evidence from every shard that proposed a disposition was joined with ` | ` and stored with a `MASS-AUDIT: ` prefix and a fresh timestamp in the JSON source of truth.
- Only `UNFIXED` records were touched. `FIXED`, `UPSTREAM_FIXED`, `BLOCKED`, `IN_PROGRESS`, and previously classified records were never modified.

### Dispositions applied

| Disposition | Records reclassified |
| --- | ---: |
| `UPSTREAM_FIXED` | 578 |
| `DUPLICATE` | 385 |
| `UNSUPPORTED` | 277 |
| `NOT_APPLICABLE` | 420 |
| **Total** | **1641** |

Roughly 5,000 closed records are now classified. **No runtime verification was performed** — these are static dispositions based on cached GitHub data and fork source inspection; protocol-document grounding was used where applicable. The mass-audit dispositions themselves were static; see Final integration state for the separate compile-only build run over the integrated fixes.

## Campaign audit gap

This pass did not audit the bulk of the inventory. Bulk closed issues and closed-unmerged PRs remain `UNFIXED`/unaudited and are retained only in the JSON. The actionable focus of this campaign is the 348-item open queue (291 open issues plus 57 open PRs). Worker patches have since been integrated into the canonical branch; see Final integration state below. A compile-only CI build passed, but no runtime verification has occurred and no fix in this tracker is claimed as runtime-verified.

## Final integration state

- Canonical branch: `integration/final-release`
- Final HEAD: `f63cd86ebe502502ee5c9654de5098e43351836f`
- 16 integration commits since the tracker baseline `33579e6c1bd6513bf83a30b731162b8120dffee6` (verified with `git log --oneline 33579e6c..f63cd86e`), covering the split-screen repair at the branch tip.
- `git diff --check`: clean.
- Build verification: `./gradlew build --no-daemon --no-parallel --max-workers=1 -Dorg.gradle.workers.max=1 -Dorg.gradle.jvmargs='-Xmx8G -XX:MaxMetaspaceSize=1G'` -> **BUILD SUCCESSFUL in 2m 15s** (exit 0). This is a **compile-only** build.
- **No runtime or gameplay verification was performed.** A successful compile does not establish that any fix in this tracker works in game.

## Tracker reconciliation

This pass reconciled the tracker with the commits actually present on `integration/final-release` at `f63cd86e`. Sixteen records were moved to `FIXED` using verified commit SHAs from that branch: the ten UNFIXED fixes #3678, #4325, #4911, #5746, #5915, #5994, #6663, #6675, #6695 and #6903; #4683 (from `UPSTREAM_FIXED`); #6419 and #750 (from `DUPLICATE`); #6844 (from `BLOCKED`, now covered by reworked commit `382165a9d`); and #6689 (from `IN_PROGRESS`). Related #6800 shares the #6903 root cause and is resolved by the same commit `f7d4dcb9`, with no separate fix claimed.

The task brief cited `4ee18fc525150ddc6953128fd8f8238004a8d6fa` as the new #6903 SHA. Git verification shows that commit is actually the #6695 Floodgate shutdown fix; the verified #6903 commit is `f7d4dcb9a81bde74e72a7cefaf22ce86b5818efa`. The tracker records the verified SHA. `status_counts` was recomputed from all 6817 records and still sums to 6817; all 6817 IDs remain unique.

**Merged upstream fix recorded after reconciliation — GeyserMC/Geyser#6937.** The upstream PR [Fix sendSkin for the session's own player and support mannequins (#6937)](https://github.com/GeyserMC/Geyser/pull/6937), fix commit `d2cf20058c6c875f7036deb6f6c59e0c39775199`, entered this branch through merge commit `4d3a072301090a1bd68f988bd1541134d1489efe` (`Merge upstream/master (d2cf20058) into integration/final-release`). This inventory has no record numbered 6937 and no record carrying that URL, so no JSON status was changed and `status_counts` remains unchanged (6817 total). Re-check CI: `./gradlew build --no-daemon --no-parallel --max-workers=1 -Dorg.gradle.workers.max=1 -Dorg.gradle.jvmargs='-Xmx8G -XX:MaxMetaspaceSize=1G'` -> **BUILD SUCCESSFUL in 5m 39s** (exit 0), compile-only. **No runtime or gameplay verification was performed.**

## Wave 1-2 integration

This pass recorded seven more dispositions against the green `integration/final-release` HEAD `0e79e51fa06dffe8c656ee5064f223c19692ac26`. Five records moved to `FIXED` with integrated commits present on that branch and two moved to `DUPLICATE` after git-verified root-cause checks.

| ID | Disposition | Integrated commit / reference |
| ---: | --- | --- |
| #6854 | `FIXED` | `c1c8705ad709f0694b2f5315fc0f9ef11743c314` — NetherNet packet batching; PR #6873 adopted only partially, usrsctp fork NOT adopted |
| #6876 | `FIXED` | `041292682b1d891b34a206e0c697825dd6ca72ef` — text display `see_through` occlusion |
| #6929 | `FIXED` | `c84074574bbdf96640ae8c35d008261b275a0fbb` — keep explicit italic:false item names and lore upright on Bedrock |
| #6893 | `FIXED` | `cadf12a952a7f74b05d1cb874d28f3b9b199a505` — entity metadata dirty-map thread safety |
| #6901 | `FIXED` | `0e79e51fa06dffe8c656ee5064f223c19692ac26` — held-input placement throttle |
| #6927 | `DUPLICATE` | of #6903; shared cause with #6800, fix `f7d4dcb9a81bde74e72a7cefaf22ce86b5818efa` (attribute reset in `JavaRespawnTranslator` / `BedrockPlayerActionTranslator`) |
| #6919 | `DUPLICATE` | of #6834; fixed upstream by PR #6843 / commit `64bb3993b4beef6743af0ab76ee5dfc8e67a0d21` (ancestor of HEAD via network 2.0.0.CR6 snapshot with CloudburstMC/Network#105) |

`status_counts` recomputed from all 6817 records (UNFIXED 3369 → 3362, FIXED 41 → 46, DUPLICATE 376 → 378); all IDs remain unique and every status is in the documented enum. Re-check CI at `0e79e51fa`: `./gradlew build --no-daemon --no-parallel --max-workers=1 -Dorg.gradle.workers.max=1 -Dorg.gradle.jvmargs='-Xmx8G -XX:MaxMetaspaceSize=1G'` -> **BUILD SUCCESSFUL in 1m 16s** (exit 0), **compile-only**. **No runtime or gameplay verification was performed.**

### Wave 2 reconciliation (continuous)

Wave-2 audit workers are still running; final dispositions will be reconciled continuously against new git and CI evidence. This pass records the verified wave-2 dispositions below. No status was set to `FIXED` without a merged commit present in `integration/final-release`: #6840 (`f88753d4c4bcb4967b2d38082f36e99dcfbed222`) and #6848 (`e91c8cd125fc9157ec99f0a090df9ae0487e03f8`) are verified ancestors of HEAD `a132a1f7a5f88585cd21ee3092f6b9cd7a2a90b1`. Compile-only CI at `0e79e51fa`: **BUILD SUCCESSFUL in 1m 16s** (exit 0); **no runtime or gameplay verification was performed**. References: https://github.com/GeyserMC/Geyser/pull/6840, https://github.com/GeyserMC/Geyser/issues/6848, https://github.com/GeyserMC/Geyser/issues/6903.

### Final reconciliation (wave 2 fixes)

Seven fixes are recorded as `FIXED` with integrated commits verified as ancestors of `integration/final-release` HEAD `0e1fe19b3656a6564604e798079236bb58d10029`: #6805 (`ae0bbc2dbaa215ca4659d7c132323250feb3c3ce`), #6715 (`51fc0081e5ed90c46828274a9b7897e5e5e36b10`), #6626 (`fd26af6ca70290a3d0b42f4e42da5139060deda6`), #6830 (`6c7fb2a45f1e63bc80875ad753e6072adc733b01`) and #6683 (`0e1fe19b3656a6564604e798079236bb58d10029`) #6570 (`c943be7b846933f460842e5c41ceb128290564bf`) and #6741 (`3814c3712ecc5a687a773e9a9d94a90c93bf70ba`). CI evidence is compile-only: earlier green build at `0e79e51fa` **BUILD SUCCESSFUL in 1m 16s** (exit 0); the final verification build at `0e1fe19b3` was still running when this update was written. **No runtime or gameplay verification was performed.** The remaining audited records received disposition-only updates with source URLs in their JSON evidence. References: https://github.com/GeyserMC/Geyser/issues/6903, https://github.com/GeyserMC/Geyser/issues/6570, https://github.com/GeyserMC/Geyser/issues/6675, https://github.com/GeyserMC/Geyser/issues/6883, https://github.com/GeyserMC/Cumulus/pull/15.

### Wave 3 audit — verified dispositions

Wave-3 audit dispositions were reconciled against upstream issue/PR state and the integrated source on `integration/final-release` @ `96a2e130095d4337c78cfc4ff69b5479446c6ad1`. For this batch of dispositions no new integration commit is claimed and no new SHA is recorded: #6248 is marked `FIXED` only because the implementation is already present in the integrated source at the file/line references below. No runtime or gameplay verification was performed; these are static dispositions. Reference: https://github.com/GeyserMC/Geyser/issues/6903.

| ID | Disposition | Evidence / reference |
| ---: | --- | --- |
| #6213 | `UNSUPPORTED` | PS5 Bedrock client crash CE-108255-1; maintainers explicitly said the crash is client/device-specific — https://github.com/GeyserMC/Geyser/issues/6213 |
| #6086 | `NOT_APPLICABLE` | Open skin/cape-as-`BufferedImage` feature request requiring a published-API design change — https://github.com/GeyserMC/Geyser/issues/6086 |
| #6129 | `UNFIXED` | Candidate https://github.com/GeyserMC/Geyser/pull/6176 closed unmerged; unconfirmed on PS5 with recurrence reported — https://github.com/GeyserMC/Geyser/issues/6129 |
| #6081 | `UNFIXED` | Open reloadable-mappings PR with a maintainer safety objection and no merge — https://github.com/GeyserMC/Geyser/pull/6081 |
| #6544 | `NOT_APPLICABLE` | Documentation-only request for an external wiki page, not in this repository — https://github.com/GeyserMC/Geyser/issues/6544 |
| #6248 | `FIXED` | Entity spectating implemented at base: `core/src/main/java/org/geysermc/geyser/entity/EntitySpectateHelper.java:46,59-84,112-120`, `core/src/main/java/org/geysermc/geyser/translator/protocol/java/entity/JavaSetCameraTranslator.java:39-45`, `core/src/main/java/org/geysermc/geyser/session/GeyserSession.java:1494`; upstream maintainer confirmed 26.2 builds; self-model-in-spectator intentionally declined — https://github.com/GeyserMC/Geyser/issues/6248 |
| #6489 | `NOT_APPLICABLE` | Open draft WIP scoreboard-allocation performance PR; conflicting and unreviewed — https://github.com/GeyserMC/Geyser/pull/6489 |

`status_counts` was recomputed from all 6817 records (UNFIXED 3321 → 3316, FIXED 55 → 56, UNSUPPORTED 290 → 291, NOT_APPLICABLE 420 → 423); every status is in the documented enum and all 6817 IDs remain unique. No build or test run was performed for this documentation pass.

#### Final wave-3 results (post-integration)

The final wave-3 audit was recorded against `integration/final-release` HEAD `239df55079f95eeeaa1c4225d23199cabec6413a`. #6248 remains `FIXED` as recorded above, with no change to its evidence. The single-thread 8G CI build started after the #6535 integration was still running when this update was written; no build result is claimed and no runtime or gameplay verification was performed for any disposition below. Reference: https://github.com/GeyserMC/Geyser/issues/6903.

| ID | Disposition | Evidence / reference |
| ---: | --- | --- |
| #6535 | `FIXED` | Integrated at `239df55079f95eeeaa1c4225d23199cabec6413a` (fix: accept custom Bedrock armor IDs in mount GUI); `core/src/main/java/org/geysermc/geyser/translator/protocol/java/inventory/JavaMountScreenOpenTranslator.java` now extends each mount slot's `acceptedItems` with Bedrock identifiers of custom items registered for its accepted Java items; CI single-thread 8G build running after integration, no result claimed — https://github.com/GeyserMC/Geyser/issues/6535 |
| #6558 | `UPSTREAM_FIXED` | Already fixed in base via upstream PR #6725; commit `777434cf9c287d459552ee9e0556821eb088608d` is an ancestor of HEAD. `core/src/main/java/org/geysermc/geyser/skin/SkinManager.java:117-135` (`sendSkinPacket`) routes through the player list (`PlayerListUtils.buildEntryManually` / `sendSkinUsingPlayerList`) instead of a `PlayerSkinPacket`; protocol grounding in `.vendor changelog_2168_07_07_26.md` — https://github.com/GeyserMC/Geyser/issues/6558 |
| #6532 | `UNSUPPORTED` | Kicks were caused by a third-party GrimAC anti-cheat; reporter confirmed the cause, so this is not a Geyser defect — https://github.com/GeyserMC/Geyser/issues/6532 |
| #6527 | `UNFIXED` | Enchanting translation matches upstream byte-for-byte in the integrated source, so there is no fork-specific divergence to correct; reporter runs a ViaVersion stack and the maintainer-contributor could not reproduce — https://github.com/GeyserMC/Geyser/issues/6527 |
| #6557 | `BLOCKED` | NSL FIFO queue-desync is plausible but unverified and requires runtime testing; upstream issue remains open with no linked PR — https://github.com/GeyserMC/Geyser/issues/6557 |

`status_counts` was recomputed from all 6817 records (UNFIXED 3316 → 3312, FIXED 56 → 57, UPSTREAM_FIXED 2339 → 2340, BLOCKED 7 → 8, UNSUPPORTED 291 → 292); every status is in the documented enum and all 6817 IDs remain unique. No build or test run was performed for this documentation pass.

#### Final wave-3 fix and dispositions (commit `b50035b2c`)

This batch was recorded against `integration/final-release` HEAD `b50035b2c472120ea64561d26859042950ecd575`. #6551 is `FIXED` at that commit (display entity teleport interpolation, upstream PR #6566); the single-thread 8G CI build finished **BUILD SUCCESSFUL in 6m 10s** (exit 0), compile/test only. The other three records received static dispositions with no new integration commit: #6556 `BLOCKED`, #6499 `UNSUPPORTED`, #6513 `NOT_APPLICABLE`. **No runtime or gameplay verification was performed.** Reference: https://github.com/GeyserMC/Geyser/issues/6903.

| ID | Disposition | Evidence / reference |
| ---: | --- | --- |
| #6551 | `FIXED` | Integrated commit `b50035b2c472120ea64561d26859042950ecd575` (display entity teleport interpolation, upstream PR #6566); clean single-thread 8G CI BUILD SUCCESSFUL in 6m 10s; compile/test only, no runtime verification — https://github.com/GeyserMC/Geyser/issues/6551 |
| #6556 | `BLOCKED` | No upstream diagnosis; armor-slot mapping fix requires design-level rerouting to player-inventory window; no repro — https://github.com/GeyserMC/Geyser/issues/6556 |
| #6499 | `UNSUPPORTED` | Already fixed by dependency NBT deserializer; sprite-to-icon mapping declined upstream — https://github.com/GeyserMC/Geyser/issues/6499 |
| #6513 | `NOT_APPLICABLE` | Unmerged draft PR with unresolved reviews; duplicates rejected PRs #4996/#6586 — https://github.com/GeyserMC/Geyser/pull/6513 |

`status_counts` was recomputed from all 6817 records (UNFIXED 3312 → 3308, FIXED 57 → 58, BLOCKED 8 → 9, UNSUPPORTED 292 → 293, NOT_APPLICABLE 423 → 424); every status is in the documented enum and all 6817 IDs remain unique.

### Wave 4 audit — verified dispositions (commit `ef6ca6736`)

This pass was recorded against `integration/final-release` HEAD `ef6ca6736238f9228d951076748a34566c713306`. #6443 is `FIXED` at that commit (dialogs no longer close the currently open Java-side inventory); the single-thread 8G CI build started after integration was still running when this update was written, so no build result is claimed. The other nine records received static dispositions with no new integration commit. **No runtime or gameplay verification was performed.** Reference: https://github.com/GeyserMC/Geyser/issues/6903.

| ID | Disposition | Evidence / reference |
| ---: | --- | --- |
| #6443 | `FIXED` | Integrated at `ef6ca6736238f9228d951076748a34566c713306` (dialogs no longer close the currently open Java-side inventory); CI single-thread 8G build running after integration, no result claimed — https://github.com/GeyserMC/Geyser/issues/6443 |
| #6398 | `UNFIXED` | Open upstream, no fix, PR or established root cause; floating-item desync during crafting is device-specific client rendering — https://github.com/GeyserMC/Geyser/issues/6398 |
| #6397 | `UNSUPPORTED` | Maintainer-confirmed Bedrock client-side chest visual/pairing bug, not a Geyser defect — https://github.com/GeyserMC/Geyser/issues/6397 |
| #6466 | `BLOCKED` | PS5 connection timeout produced no Geyser logs; root cause lies outside Geyser — https://github.com/GeyserMC/Geyser/issues/6466 |
| #6454 | `UNFIXED` | Ridden-mount velocity desync needs invasive client-prediction changes; PR #6551 excludes living entities — https://github.com/GeyserMC/Geyser/issues/6454 |
| #6420 | `DUPLICATE` | Of open #6246 / #6563 auto-crafting failures; unresolved TODOs, no minimal fix — https://github.com/GeyserMC/Geyser/issues/6420 |
| #6495 | `UNFIXED` | Unconfirmed upstream, maintainer cannot reproduce; mapping identical to upstream, third-party resource packs — https://github.com/GeyserMC/Geyser/issues/6495 |
| #6458 | `UPSTREAM_FIXED` | Furnace-recipe client crash #6351 / #6353 already addressed via recipe translation and newer protocol library in base — https://github.com/GeyserMC/Geyser/issues/6458 |
| #6408 | `NOT_APPLICABLE` | Armor rendering matches protocol; dismount issue belongs to unfixed #6283 — https://github.com/GeyserMC/Geyser/issues/6408 |
| #6485 | `UNSUPPORTED` | Minecart visual interpolation inherent; Bedrock has no equivalent packet, same as #1578 — https://github.com/GeyserMC/Geyser/issues/6485 |

`status_counts` was recomputed from all 6817 records (UNFIXED 3308 → 3301, FIXED 58 → 59, UPSTREAM_FIXED 2340 → 2341, DUPLICATE 385 → 386, BLOCKED 9 → 10, UNSUPPORTED 293 → 295, NOT_APPLICABLE 424 → 425); every status is in the documented enum and all 6817 IDs remain unique. No build or test run was performed for this documentation pass.

### Wave 5 audit — verified dispositions (HEAD `77e7e13ffc805e9e93f739617ad71df153c69bfa`)

Wave-5 audit dispositions were reconciled against upstream issue/PR state and the integrated source on `integration/final-release` @ `77e7e13ffc805e9e93f739617ad71df153c69bfa`. No new `FIXED` status was recorded: no new integration commit was produced for this batch, and the `UPSTREAM_FIXED` entries rely on fix commits already present as ancestors of HEAD.

| ID | Disposition | Evidence / reference |
| ---: | --- | --- |
| #6393 | `NOT_APPLICABLE` | open dirty/draft feature PR (Add proper combat sounds) with two unmerged prerequisite PRs and no maintainer reviews; not an actionable defect for this fork — https://github.com/GeyserMC/Geyser/pull/6393 |
| #6342 | `UNSUPPORTED` | item stacking is Bedrock client-side auto-place/stack prediction; Geyser can only accept or reject the resulting request packets, so there is no server-side translation to fix — https://github.com/GeyserMC/Geyser/issues/6342 |
| #6266 | `UNSUPPORTED` | maintainer-confirmed Java-only item attributes have no Bedrock equivalents and cannot be represented over the protocol — https://github.com/GeyserMC/Geyser/issues/6266 |
| #6239 | `UPSTREAM_FIXED` | Dolphin's Grace / Dolphin Tunnels already addressed by integrated upstream PR #6840 (https://github.com/GeyserMC/Geyser/pull/6840), commit f88753d4c4bcb4967b2d38082f36e99dcfbed222 (ancestor of HEAD); the remaining difference is Bedrock client physics — https://github.com/GeyserMC/Geyser/issues/6239 |
| #6278 | `UNFIXED` | intentional upstream custom-trim behavior introduced by PR #6218 (https://github.com/GeyserMC/Geyser/pull/6218); the fork's armor-trim translation is byte-identical to upstream, so there is no fork divergence to correct — https://github.com/GeyserMC/Geyser/issues/6278 |
| #6373 | `UNFIXED` | supplied log shows only normal inventory debug paths in a plugin-heavy setup; no Geyser inventory-translation defect identified — https://github.com/GeyserMC/Geyser/issues/6373 |
| #6302 | `UNFIXED` | custom-skull pipeline is intact with 27 skulls registered; the failure is specific to a third-party mod's skull data — https://github.com/GeyserMC/Geyser/issues/6302 |
| #6283 | `UNFIXED` | horse-riding desync on Bedrock protocol 944 (26.10/26.12); all upstream fixes cited in the report are already present in the base and the issue was deferred upstream — https://github.com/GeyserMC/Geyser/issues/6283 |
| #6368 | `UPSTREAM_FIXED` | self-player listed-state skin bug fixed upstream by PR #6675 (https://github.com/GeyserMC/Geyser/pull/6675), commit 7ba09fed1388c335de5bb8865914391c7e4178e8, and PR #6937 (https://github.com/GeyserMC/Geyser/pull/6937), commit d2cf20058c6c875f7036deb6f6c59e0c39775199; both are ancestors of HEAD — https://github.com/GeyserMC/Geyser/issues/6368 |
| #6320 | `UNFIXED` | unconfirmed upstream and console-only report; smelting and armor-trim recipe translation verified correct against the integrated source — https://github.com/GeyserMC/Geyser/issues/6320 |

`status_counts` was recomputed from all 6817 records (UNFIXED 3301 → 3296, UPSTREAM_FIXED 2341 → 2343, UNSUPPORTED 295 → 297, NOT_APPLICABLE 425 → 426; FIXED 59, DUPLICATE 386 and BLOCKED 10 unchanged); every status is in the documented enum and all 6817 IDs remain unique. No build or test run was performed for this documentation-only change.

### Wave 6 audit — verified dispositions (HEAD `037163fe9092b1caf7b40cc4269e6e1fcda76b09`)

Wave-6 re-verified closed upstream records against the integrated source on `integration/final-release` @ `037163fe9092b1caf7b40cc4269e6e1fcda76b09`. Every cited fix commit was checked as an ancestor of that HEAD with `git merge-base --is-ancestor`. No new source fix was introduced and no build or test was run for this documentation-only change; `FIXED` was recorded only where an integrated commit already exists.

| ID | Disposition | Evidence / reference |
| ---: | --- | --- |
| #6885 | `UPSTREAM_FIXED` | Riding a happy ghast riding a minecart console spam is fixed by PR #6887 (https://github.com/GeyserMC/Geyser/pull/6887), commit `25eda4141aa1a00ca95dca00af56a5b8dac4e20e` (ancestor of HEAD) guarding `ClientVehicle.shouldSimulateMovement` — https://github.com/GeyserMC/Geyser/issues/6885 |
| #6898 | `UPSTREAM_FIXED` | Stale teleport accepts after server transfer are fixed by PR #6917 (https://github.com/GeyserMC/Geyser/pull/6917), merge commit `440be82b0b2def7211504226d6ea5c37911a5643` (ancestor of HEAD); the teleport-accept fix is present in `JavaPlayerPositionTranslator.java` — https://github.com/GeyserMC/Geyser/issues/6898 |
| #6895 | `UPSTREAM_FIXED` | Sleeping-bed teleport-confirm spam is fixed by PR #6892 (https://github.com/GeyserMC/Geyser/pull/6892), merge commit `1955f5151e2af01a7b51db1679e4324345690e70` (ancestor of HEAD), which defers the teleport accept to a latency-ping callback in `JavaPlayerPositionTranslator.java`; the reporter's build predates the fix — https://github.com/GeyserMC/Geyser/issues/6895 |
| #6932 | `UPSTREAM_FIXED` | Block-registry resident-memory reduction: upstream PR #6932 (https://github.com/GeyserMC/Geyser/pull/6932) was closed unmerged upstream, but both of its commits `5a8d8d81bd166c1573d163b165fde73bc2ad3fba` and `4f8280665f740fe21ff9d868ada9f3c0a415d695` are ancestors of HEAD and the reduction is present in the fork, so the fix is already integrated despite the unmerged PR — https://github.com/GeyserMC/Geyser/pull/6932 |
| #6904 | `FIXED` | The movement-speed half shares the #6903 root cause and is resolved by integrated commit `f7d4dcb9a81bde74e72a7cefaf22ce86b5818efa` (ancestor of HEAD); the NetherNet/RakNet-transport half is a client-side platform decision, not a code defect — https://github.com/GeyserMC/Geyser/issues/6904 |
| #6935 | `DUPLICATE` | PR closed unmerged in favour of #6909 (https://github.com/GeyserMC/Geyser/pull/6909); the underlying #6929 fix `c84074574bbdf96640ae8c35d008261b275a0fbb` is already an ancestor of HEAD — https://github.com/GeyserMC/Geyser/pull/6935 |
| #6888 | `DUPLICATE` | NetherNet family duplicate of #6854 (https://github.com/GeyserMC/Geyser/issues/6854) and #6850 (https://github.com/GeyserMC/Geyser/issues/6850); batching fix `c1c8705ad709f0694b2f5315fc0f9ef11743c314` is an ancestor of HEAD and integrated — https://github.com/GeyserMC/Geyser/issues/6888 |
| #6930 | `NOT_APPLICABLE` | Closed invalid: wrong-repository feature request for a custom LAN/MCXboxBroadcast world-preview image; the maintainer ruled it impossible, so there is no code defect — https://github.com/GeyserMC/Geyser/issues/6930 |
| #6889 | `NOT_APPLICABLE` | Closed not_planned: login `DecoderException` stems from a proxy-protocol configuration mismatch (proxy emits PROXY protocol while Geyser is not configured to accept it), not a code defect — https://github.com/GeyserMC/Geyser/issues/6889 |
| #6877 | `NOT_APPLICABLE` | Closed Invalid with an empty issue template: no reproduction steps, dump, or diagnostics; no actionable defect identified — https://github.com/GeyserMC/Geyser/issues/6877 |

`status_counts` was recomputed from all 6817 records (UNFIXED 3296 → 3286, FIXED 59 → 60, UPSTREAM_FIXED 2343 → 2347, DUPLICATE 386 → 388, NOT_APPLICABLE 426 → 429; BLOCKED 10, UNSUPPORTED 297 and IN_PROGRESS 0 unchanged); every status is in the documented enum and all 6817 IDs remain unique. No build or test run was performed for this documentation-only change.

### Wave 7 audit — verified dispositions (HEAD `8e90a388cca4d93c4f3ba5894de4227d31b2f2bb`)

Wave-7 re-verified closed upstream records and one closed PR against the integrated source on `integration/final-release` @ `8e90a388cca4d93c4f3ba5894de4227d31b2f2bb`. Cited fix commits were checked as ancestors of that HEAD with `git merge-base --is-ancestor`. This is a documentation-only change: no source fix was introduced and no build or test was run. The final single-thread 8G CI build at `8e90a388` finished **BUILD SUCCESSFUL in 7m 59s** (exit 0), compile/test only; this result was verified before promotion to `master` in merge commit `4de63e5876b5c3ee438422d385c4dc31f72c4f02`. No runtime or gameplay verification is asserted for any disposition. Reference: https://github.com/GeyserMC/Geyser/issues/6903.

| ID | Disposition | Evidence / reference |
| ---: | --- | --- |
| #6857 | `FIXED` | Integrated at `8e90a388cca4d93c4f3ba5894de4227d31b2f2bb` — soft enums for mixed-case command literals, porting upstream #6619; PR #6857 ('Sync 6619') was closed by its author as a mistake, so the fix never landed through the PR. final single-thread 8G CI at that HEAD BUILD SUCCESSFUL in 7m 59s (exit 0), compile/test only; promoted to master in merge 4de63e5876b5c3ee438422d385c4dc31f72c4f02 — https://github.com/GeyserMC/Geyser/pull/6857 , https://github.com/GeyserMC/Geyser/pull/6619 |
| #6870 | `UPSTREAM_FIXED` | Missing Java entity_type definitions are handled gracefully by ancestor commit `b9d535a67cb6ef75a38140db0bd5ac2ccbe1e9ba` (Fix: gracefully handle missing entity type definitions in JavaAddEntityTranslator) — https://github.com/GeyserMC/Geyser/issues/6870 |
| #6852 | `UPSTREAM_FIXED` | Actionable NetherNet native diagnostics replace the misleading message via ancestor commits `64bb3993b4beef6743af0ab76ee5dfc8e67a0d21` (#6843) and `e91c8cd125fc9157ec99f0a090df9ae0487e03f8` (keep Geyser running when NetherNet libc detection fails, #6848) — https://github.com/GeyserMC/Geyser/issues/6852 , https://github.com/GeyserMC/Geyser/pull/6843 , https://github.com/GeyserMC/Geyser/issues/6848 |
| #6861 | `NOT_APPLICABLE` | Closed support/setup issue ('TERRACOTTA ERROR WONT LET ME JOIN' on a Fabric 26.3 / Geyser b1280 / Bedrock 26.60 setup); no code defect — https://github.com/GeyserMC/Geyser/issues/6861 |
| #6859 | `NOT_APPLICABLE` | Closed not_planned user setup issue (Persona/Terracotta initialization on Paper 26.3 with Termux/Android Bedrock) — https://github.com/GeyserMC/Geyser/issues/6859 |
| #6860 | `UNSUPPORTED` | Intentional warning about third-party custom-entity int properties; no Geyser defect — https://github.com/GeyserMC/Geyser/issues/6860 |
| #6856 | `NOT_APPLICABLE` | Closed not_planned wrong-port setup issue (Aternos Paper 26.3, NetherNet client connecting to the wrong port) — https://github.com/GeyserMC/Geyser/issues/6856 |
| #6855 | `NOT_APPLICABLE` | Reporter self-resolved an external Sonar antibot false positive (view distance 0 check); not a Geyser defect — https://github.com/GeyserMC/Geyser/issues/6855 |
| #6872 | `UNSUPPORTED` | Closed support redirect for an Xbox Bedrock account that could not join a Java server — https://github.com/GeyserMC/Geyser/issues/6872 |
| #6871 | `NOT_APPLICABLE` | Third-party extension incompatibility with intentional upstream cleanup that removed the `JAVA_ENTITY_TYPES` registry in commit `e87ad1475d629f58175f325f7b2ca5630fd231b8` (#6832) — https://github.com/GeyserMC/Geyser/issues/6871 , https://github.com/GeyserMC/Geyser/pull/6832 |

`status_counts` was recomputed from all 6817 records (UNFIXED 3286 → 3276, FIXED 60 → 61, UPSTREAM_FIXED 2347 → 2349, UNSUPPORTED 297 → 299, NOT_APPLICABLE 429 → 434; DUPLICATE 388, BLOCKED 10 and IN_PROGRESS 0 unchanged); every status is in the documented enum and all 6817 IDs remain unique. No priority-queue status cell references any of these closed records, so the open-issue and open-PR tables need no change.

## Waves 8-18 — open PR adoption and open issue fixes

This pass worked the *open* upstream queue: the 57 open pull requests and the open `UNFIXED` issue list, rather than the closed backlog. Work ran as isolated Git worktrees with one worker per non-overlapping file set, then merged into `master`. Every claim below is backed either by a commit on `master` or by a `file:line` reference in the current tree.

**Verification level.** Per-PR the workers ran `:api:compileJava`/`:core:compileJava` (plus `:spigot:compileJava`, `:velocity:compileJava` where relevant) and the related test packages. The integrated tree was then compiled as a whole: `./gradlew compileJava compileTestJava --no-daemon --no-parallel -Dorg.gradle.jvmargs='-Xmx4G -XX:MaxMetaspaceSize=1G'` -> **BUILD SUCCESSFUL in 5m 28s** (exit 0, 60 actionable tasks). No runtime or gameplay verification was performed, and the fixes that need hardware or a live proxy are listed as such.

### Adopted open pull requests

| PR | Change | Integrated commit | Verification |
| ---: | --- | --- | --- |
| #6938 | Hash heterogeneous NBT lists element-by-element (`HeterogeneousNbtList.tryUnwrap`) | `874d4bbef` | `item.hashing` tests + new `HeterogeneousNbtListHashTest` |
| #6905 | Material instances: texture variation + alpha-masked tint | `2475aebac` | `:api`/`:core` compile, item translator tests |
| #6643 | Model-specific item overrides for custom blocks | `1914a09f8` | `CustomBlockItemTranslatorTest` (5 tests) |
| #6694 | Keep the fishing bobber above lava | `be130b381` | entity tests (12) |
| #6686 | Recipe shift-click via `ServerboundPlaceRecipePacket` | `0a974ad9f` | inventory/entity tests |
| #5011 | Bedrock-aware max stack size | `5b713ffe0` | inventory/entity tests |
| #6586 | `/geyser debug` session tooling | `882bcc76a` | command + mappings tests (12) |
| #6081 | Reloadable mappings at runtime | `ac7e9fd29` | mappings tests |
| #6866 | Custom mappings from URLs and extensions | `72364edbc` | `MappingsConfigReaderTest` (8) |
| #6489 | Scoreboard allocation reduction (behaviour-neutral parts) | `bd92e4141` | 42/43 scoped tests in the worker worktree; the failure is a missing `mappings/item_data_components.json` in fresh worktrees and the full `:core:test` suite passes once the mappings submodule is checked out |
| #6916 | Register vanilla tools as custom items for correct break times | `25cad395b` | registry tests (10) |
| #6393 | Integrated-pack combat sound mappings | `a48771a9e` | `SoundRegistryLoaderTest` (2) — **PARTIAL**, see below |
| #6902 | Custom block geometry culling | `2141a478e` | `:api`/`:core`/`:spigot` compile |
| #6605 | Extensible server-authoritative custom block breaking | `eceb9d743` | cache/item tests, vanilla breaking unchanged without a provider |
| #6768 | Text displays via Bedrock primitive text | `994cd6a8b` | `MessageTranslatorTest` (24) + entity tests |
| #6833 | Command translation rework | `55a5db05f` | command/suggestion tests (13 in `ServerSuggestionCacheTest`) |
| #6928 | Server-side command suggestions on Bedrock | `a67f3221d` | `ServerSuggestionCacheTest` (13) + `NetherNetCommandTest` (3) |
| #6573 | `WebUtils` on `java.net.http` | `92c048209` | util/pack/text tests |
| #6578 | Download and read Java resource packs | `320ca65c1` | `JavaPackTest` (3) + `JavaPackStackTest` (3) |
| #6863 | Opt-in tooltip style markers | `9f81c354d` | `TooltipStyleMarkersTest` (4) |

Implementation notes that matter for future maintenance:

- #6393 is **PARTIAL**: the Java substitution, the mappings JSON and the fork-owned `core/src/main/resources/sound_mapping_overrides.json` are in, but `GeyserIntegratedPack.mcpack` is downloaded at build time and gitignored, so `geyseropt.entity.player.attack.{knockback,sweep}` will not resolve until a regenerated pack is published. The overrides were deliberately kept out of the `GeyserMC/mappings` submodule so its pin stays at `6e7d6c55`; the gitlink diff against the wave-7 baseline is empty.
- #6768 keeps the metadata nametag path as a fallback, gated on protocol >= 818 and `-DGeyser.DisablePrimitiveTextDisplays=true`.
- #6866's option lives in `GeyserConfig` `@Comment` annotations because this fork generates `config.yml` from annotations; there is no checked-in `config.yml`.
- #6605 uses `ServerboundPunchPacket` for continuous swing because `ServerboundSwingPacket` does not exist in the fork's MCProtocolLib `26.3-4`.
- #6833's gradle bumps were not applied (the fork stays on MCProtocolLib `26.3-4` and adapters `1.17`); its team-suggestion and `NOT_CHEAT` flag behaviour was kept, and the fork's soft-enum handling for mixed-case command literals from #6619 is preserved.
- #6578's translation overlay is process-wide because `MinecraftLocale` lookups are static; that is acceptable while `downloadJavaResourcePacks` defaults to off, but enabling it on a multi-locale network is the first thing to revisit.
- The wave-10 merge of #6578 into #6863's `GeyserConfig` needed one manual conflict resolution: both added an option to the same `@Comment` block. Both options are present and independently defaulted to `false`.

### Open PRs checked and not adopted

A read-only audit fetched each open PR's diff and grepped the fork source for the symbols it introduces.

**Already present in the fork (10, no action needed):** #6664 (`ResourcePackLoader.java:204,207`), #6724 (`CollisionRegistryLoader.java:100`, `SessionLoadResourcePacksEventImpl.java:230-242`), #6914's `ContainerType` renames (`InventoryTranslator.java:118-119`), #6896 (`LanLoginPolicy`, `LanBindRetry`, `UnsignedAnswerSignaling`), #6925 (`LivingEntity.java:213-237`), #6921 (`ItemFrameEntity.java:60-131`), #6890 (`DumpMessage`/`DumpMessageSource`), #6883 (`BlockBreakHandler.java:683`), #6667 (`JavaMerchantOffersTranslator.java:126-146`), #6566 (`Entity.interpolatesTeleports()`).

**Not adopted, with reasons:** #6914 needs the unmerged GeyserMC/MCProtocolLib#942 (`PotionContents.hasCustomColor()` does not exist in `26.3-4`; the patch fails to compile); #6690 is gated on Cumulus PR #15; #6811 is an unfinished draft whose cited leak exists only in its own new code; #6909 is covered through another route (`Item.java:236`, `MessageTranslator.java:463`) and its refactor was rejected upstream; #6936/#6916-style draft gating does not apply because #6916 was ported; #6873's production hunk is already integrated as `c1c8705ad`; #6829 is a duplicate of #6658.

### Open issue fixes

| Issue | Root cause | Fix | Verification |
| ---: | --- | --- | --- |
| #6920 | NetherNet child channel never closed; disconnect handler only refreshed provider status | `NetherNetServer` closes the channel; `NetherNetChannelInitialiser.releaseChannel` is idempotent and also runs on `channelInactive` — `5d6242bf3` | 87 network/entity tests; leak still needs a live profiling run |
| #6884 | Border collision entity only repositioned from `BedrockMovePlayer`, which is not sent while riding | `VehicleComponent` repositions it from the ridden vehicle, only on movement — `736273ff8` | 87 network/entity tests |
| #6842 | `BedrockPingHandler` produced a null virtual host for blank/bare-IP ping hosts | `GeyserVelocityPingPassthrough` falls back to the first forced host or join server — `2dbedd4d3` | `:velocity:compileJava` + 87 tests |
| #6584 | Server flying flag mirrored into the Bedrock `FLYING` ability while swimming | flag suppressed in fluid via `SWIMMING` + `collisionManager.isPlayerInWater()`, internal state preserved — `a3f374b21` | 87 network/entity tests |
| #6858, #6648 | Late `ContainerClosePacket` closing the wrong menu / clicks landing in the wrong container | already fixed by the container-id reservation at `Inventory.java:131-138` — no code change this wave | `file:line` evidence, retest on a current build |

### Open issues triaged as not Geyser defects

30 of the newest `UNFIXED` open issues were triaged against the fork source. Recorded as `NOT_APPLICABLE` with evidence: #6923 (no FreeBSD native in `libdatachannel-java-arch-detect`), #6915 (the reported local IPv4 is the negotiated ICE host candidate), #6894 (trace is inside `velocity-4.3.2-SNAPSHOT-48.jar`), #6881 (PROXY v1 is unsupported for UDP relays), #6650 (no anti-xray path; engine-mode 3 works), #6587 (ViaProxy chain), #6632 (third-party MCGateway). #6813 is `BLOCKED`: the duplicate `furnace_minecart` identifier lives in the pinned `GeyserMC/mappings` submodule.

14 issues remain `UNFIXED` because they need reproduction data rather than a code change: #6913, #6851, #6850, #6849, #6802, #6753, #6722, #6684, #6677, #6670, #6653, #6596, #6580, #6577. #6918 falls in the same category even though its root cause is narrowed to the local revert path.

Two code paths now carry gated diagnostics so the next report is actionable (`273f7c2d7`): #6918 logs which revert guard fired (repeat throttle, held-item mismatch or click-position bounds) with the transaction details, and #6684 logs each dropped recipe's id/type and cause. Both use the logger's debug flag for an early return and build their messages through suppliers, so nothing is allocated when debug is off, and neither changes control flow.


### Wave 13 — area sweep of the remaining open issues

Six area workers audited the 170 open issues that were still unresolved after waves 8-12, partitioned by code area (network, inventory, entity, world, chat/command, misc) so their changes could not collide. Each worker worked in its own worktree and could not build; every change below was compiled for the first time by a single central `./gradlew compileJava compileTestJava --no-daemon --no-parallel -Dorg.gradle.jvmargs='-Xmx4G -XX:MaxMetaspaceSize=1G'` -> **BUILD SUCCESSFUL in 1m 18s** (exit 0). At the user's request no tests, gametest or Spigot jar runtime verification was run for this wave: the verification is compile-only, and the wave-13 fixes are therefore **unverified at runtime**.

**13 issues fixed:**

| Issue | Root cause | Fix |
| ---: | --- | --- |
| #6850, #6557, #5371 | Bedrock `NetworkStackLatencyPacket` echoes were matched to queued replies **by position**, so one dropped or late echo shifted every later Java keepalive/ping/teleport-confirm id onto the previous one — the "kicked due to keepalive timeout" and invalid-movement disconnects after teleports and backend switches | replies are matched by the echoed timestamp (`GeyserSession.runLatencyPing`, `latencyPingCache` now a `LatencyPing` queue), and the unanswered queue is capped at 512 entries instead of growing for the connection's lifetime |
| #5123 | a leash link was dropped when the holder (fence knot or leash holder entity) was not tracked yet | the link is deferred and reapplied on spawn (`JavaSetEntityLinkTranslator`, `EntityCache`) |
| #1902 | Bedrock predicts both halves of a door from one placement, but only the clicked half was tracked, so a rejected prediction left a ghost upper half | both predicted halves are registered, mirroring the break-side handling |
| #6113 | partial takes from a furnace output slot were emulated with "take all, then right-click the rest back", which Java rejects (the result slot refuses placement) and which left a phantom cursor stack | only whole-stack and larger-half partial takes are emulated; other partials are rejected and resynced, and placement into the result slot is rejected |
| #6677 | cursor→hotbar SWAP used the number-key shortcut, which targeted the container's slot 0 because a cursor maps to slot 0; `Inventory.getItem` also overflowed on `slot == size`/negative, and a cartography `CONSUME` could mutate the shared `GeyserItemStack.EMPTY` | generic cursor-swap path, bounds-checked lookup, guarded consume |
| #6224 | signing a written book dereferenced `packet.getTitle()` unconditionally, but Bedrock omits the title when the book already carries a custom name | fall back to the item's custom name, then to an empty title |
| #5770 | advancement list buttons and the info title converted components with the default locale instead of the player's locale | locale is passed through (`AdvancementsCache:152,235`) |
| #5736 | `can_break`/`can_place_on` were read unconditionally, ignoring `tooltip_display.hidden_components` | both are gated on the hidden-components set like the other tooltip lines |
| #5318 | zipped pack folders (manifest at the root or in exactly one subfolder) were rejected | they are packed into a cached `.mcpack` under `cache/directory_packs`, rebuilt when the source changes, with macOS metadata skipped and a regression test |
| #5404 | the floor skull model started at y=0.5 while `FLOOR_BOX` and its collision start at y=0 | geometry moved to y=0 and the pack UUID bumped so clients re-download |
| #3456 | `connectionByUuid` only looked at registered sessions, so it missed sessions still in the Velocity `PlayerChooseInitialServerEvent` window | it now also matches pending sessions on their auth UUID (`SessionManager:97-113`) |

**Already fixed in the fork, confirmed with `file:line` evidence:** #5906 (vanilla tools as custom items, `25cad395b`), #3084 (flying break, `24167bf29`), #4588 (sign null messages, `cbd42b8a0`), #5227 (`JavaExplodeTranslator:73-79`), #6246 (`ServerboundPlaceRecipePacket`, `0a974ad9f`), #6134 (container-id reservation, `Inventory.java:131-138`).

**Blocked:** #6032 — the duplicate item-mapping collision lives in the pinned `GeyserMC/mappings` submodule, so it has to be fixed upstream.

**Reclassified as not a Geyser defect (53 issues, `NOT_APPLICABLE`)** with per-issue evidence: Bedrock client limits and crashes with no server-side path, third-party libraries (PacketEvents, ViaProxy, MCGateway), Java/server-side encoder limits, documented configuration trade-offs (`broadcast-port`, Rak rate limiting, `-DGeyser.RakRateLimitingDisabled`), and upstream feature requests that would need new public API surface. The remaining issues stay `UNFIXED` because they need a dump, a log or a hardware reproduction; each has its evidence recorded in the JSON.

### Wave 14 — last open PRs and closed-record backlog audit

This wave took the five open upstream PRs that were still unadopted after wave 13 and audited the closed backlog that earlier passes had left `UNFIXED`. As in wave 13, workers could not build; the only verification is the single compile-only pass on the merged tree. No live tests, gametest or Spigot jar runs were performed.

| PR | Change | Commit | State |
| ---: | --- | --- | --- |
| #5685 | Geyser Networking API: session/JavaState/ProtocolState/codec/channel API (38 new files), lifecycle hooks, inbound/outbound Bedrock+Java packet hooks, custom-payload dispatch, channel handshake, 3 network tests | `432836ef4` | FIXED — zero per-packet cost when no listener is registered; the upstream `LocalSession final -> non-final` edit was replaced with a `GeyserSessionAdapter` hook, preserving the NetherNet lifecycle |
| #5860 | Custom Translation API — `TranslationManager`, `GeyserLoadLocaleEvent`, `GeyserApi` accessors, default `CommandSource` helpers, extension locale loading | `a0a496936` | FIXED — purely additive; pack locale overrides are applied first so extensions have the final word |
| #6922 | `.geyserpack` archive loading — `GeyserAssetPack`, cached extraction with traversal/size/entry caps, `GEYSER_ASSET_PACKS` registry, pack resource-pack/mapping/locale overrides, opt-in config, regression test | `65de3c644` | FIXED — default off; existing manifest handling preserved |
| #5595 | Anvil cost refactor and small cleanups | `955914e` | FIXED — its item-name helpers were merged into one implementation with #5692 (both component- and stack-based overloads retained) |
| #5692 | Full Java tooltip parity | `6c77d08eb` | **PARTIAL** — Java hover names ported, but the 24-provider tooltip framework and registry rewrite depend on registry types absent from this fork (`JavaRegistryKey<T,MCPL>`, per-registry entry contexts), and the lore rewrite would change default formatting |

**Closed backlog audit.** Two read-only auditors reviewed the 800 most recent `UNFIXED` closed records (even and odd id shards) against cached GitHub metadata, maintainer closing comments, `state_reason`, and the fork source. They produced **239 evidence-backed dispositions** (`/tmp/backlogA.json` + `/tmp/backlogB.json`): 15 UPSTREAM_FIXED, 7 DUPLICATE, 96 UNSUPPORTED and 121 NOT_APPLICABLE. UPSTREAM_FIXED was only used when the fixing commit was confirmed as an ancestor of fork master with `git merge-base --is-ancestor` and the symbol was found in the tree (for example #6836 -> `8c61d7398` at `BlockTag.java:232`, #6797/#6779 -> `d14f2046`, #6457 -> 26.2 support present in the baseline). The remaining 561 reviewed records stayed `UNFIXED` because no admissible evidence was found; notably #5317 was deliberately not promoted because upstream commit `0416b55959` is not in master.

Verification level for this wave: `./gradlew compileJava compileTestJava --no-daemon --no-parallel -Dorg.gradle.jvmargs='-Xmx4G -XX:MaxMetaspaceSize=1G'` — compile-only. Runtime behaviour of all five PRs is **unverified**; the two merge conflicts that were resolved by hand (`MinecraftLocale` combining extension-locale and pack-locale hooks, and the overlapping #5692/#5595 item-name helpers) are included in that compile check.

### Wave 15 — gated PRs, PARTIAL remainders and backlog shards C/D/E

Six workers attacked the previously gated or partial PRs and three read-only auditors continued the closed-record backlog. All six PR branches merged cleanly; verification for this wave is a single compile-only pass (`compileJava compileTestJava`) on the merged tree — no tests, gametest or Spigot jar runs.

| PR | Outcome | Evidence |
| ---: | --- | --- |
| #6393 | FIXED | `b066aa7a8` — the two `integrated_playsound_mapping` entries now resolve to vanilla Bedrock sounds (`attack.strong`/`attack.critical`) via the fork-owned `sound_mapping_overrides.json`, so the build-downloaded pack binary is no longer required; Java-side substitution kept and `SoundRegistryLoaderTest` covers knockback |
| #6936 | FIXED | `10c207b42` — `GeyserSession.checkHeightLimit(Vector3f)` toggles vanilla `minecraft:fog_hell` by Y bounds from `BedrockMovePlayer`; the upstream integrated-pack dependency (#86) is cosmetic only; gated by `nether-roof-workaround`, default unchanged |
| #6690 | PARTIAL | `403b168d4` — config-gated NPC dialogue handling (`enableNpcDialogues`, default false), `BedrockNpcRequestTranslator`, session `npcId`, `FormCache.handleResponse` via existing Cumulus `FormDefinition`; the plugin-facing `NpcForm` opening path cannot be ported because that class does not exist in the fork's Cumulus `1.1.2` |
| #6811 | FIXED | `00bca39fd` — shared immutable session data: refcounted handles, per-Geyser holder (no static mutable global cache), raw-NBT keys instead of `hashCode`, opt-in stateless-reader allowlist; the draft's global static cache list and `RegistryCache` path were omitted |
| #5692 | PARTIAL | `3a6f61d4e` — provider framework plus 22 Java-parity providers, opt-in via `geyser.tooltipProviders` so default lore output is unchanged; per-registry entry contexts remain impossible without `JavaRegistryKey<T,MCPL>`, and the name-tooltip, map-tooltip, built-in potion-effect, spawner and can-break/can-place providers remain TODO |
| #6914 | BLOCKED (confirmed) | `javap` on `protocol-26.3-4` shows `PotionContents` has no presence flag: absent and present `-1` colors both decode to `-1`, and `writePotionContents` gates on `>= 0`, so the distinction is lost before hashing. Only a real MCProtocolLib bump (GeyserMC/MCProtocolLib#942) can fix it. The PR's `ContainerType` renames are already present at `InventoryTranslator.java:119-120` |

**Backlog audit C/D/E.** Three read-only auditors processed 1500 closed `UNFIXED` records and produced **744 evidence-backed dispositions**: 569 NOT_APPLICABLE, 72 UPSTREAM_FIXED, 59 UNSUPPORTED, 38 DUPLICATE and 6 BLOCKED. UPSTREAM_FIXED was asserted only when the fixing commit was verified as an ancestor of master (`git merge-base --is-ancestor` or `compare <sha>...master`) and a `file:line` was found — for example #6810 → `5a24ded` at `NetherNetServer.java:202-233`, #5493 → `a9c1da5` at `InventoryHolder.java:83`, #6760 → `12532288` at `gradle/libs.versions.toml:23`. Because this checkout is shallow, cited commits that could not be proven were deliberately kept `UNFIXED` (#6582 is one such case). The remaining 756 reviewed records stayed `UNFIXED` because no admissible evidence was found.

### Wave 16 — finishing PARTIALs, unblocking data records and backlog F/G/H/I

Six workers finished the two remaining PARTIAL PRs, attempted the BLOCKED records using only fork-local code, and audited another 1600 closed records. All changes merged cleanly; verification is a single compile-only pass on the merged tree (no runtime, gametest or Spigot jar runs).

| Record | Outcome | Evidence |
| --- | ---: | --- |
| #6690 | FIXED | `d6ac2517a` — the plugin-facing path is now present via a fork-owned additive `NpcDialogueForm` API wrapping Cumulus `SimpleForm`; `FormCache` branches NPC forms to `NpcDialoguePacket`, adapts responses, and closes safely; session permission handling and stale-`npcId` reset added; `NpcDialogueFormTest` plus a standalone `javac` and response smoke run |
| #6813, #6032 | FIXED | `4e4dc6aef` — the two mappings-submodule blockers were resolved with a fork-owned `item_mapping_overrides.json` merged by `ItemMappingOverrides.apply()` in `ItemRegistryPopulator`; furnace minecart gets a distinct `bedrock_data`, copper bars get an idempotent identifier/block-range guard; submodule pin untouched |
| #5692 | PARTIAL | `2ebee401a` — the concrete provider TODOs are implemented (name dispatch/gating, spawner, canBreak/canPlace, built-in potion effects, `filled_map.id`) with `TooltipProvidersTest` (5 tests); still impossible without protocol-library changes: MCPL per-registry entry contexts, map scale/level/locked lines (`MapItemSavedData` is not sent), and vanilla name providers that the `getName` path already covers |
| #6556, #6399, #6116, #5489, #6795 | BLOCKED (confirmed) | no compile-safe cause was found: armor slot mapping already matches upstream; bucket rotation fixes are already in the baseline and changing them risks known regressions; the enchanting translator matches upstream and repro links are dead; no reliable death-state flag exists for ~1 HP falling; #6795 has no repro or logs |

**Backlog audit F/G/H/I.** Four read-only auditors processed 1600 closed `UNFIXED` records and produced **509 evidence-backed dispositions**: 185 UPSTREAM_FIXED, 169 NOT_APPLICABLE, 83 DUPLICATE, 57 UNSUPPORTED and 15 BLOCKED. UPSTREAM_FIXED required a fix commit verified as an ancestor (via `compare <sha>...master` or `git merge-base`) plus a `file:line` in the fork. Shard G alone yielded 89 such records; shard I verified 67 closing PRs/commits. Because this checkout is shallow, unverifiable cited commits were deliberately left `UNFIXED` rather than asserted.

### Wave 17 — definitive parity decision and backlog J/K/L/M

One worker and four read-only auditors finished this wave. There were no source changes: the worker assigned to #5692 proved that the last claimed gap cannot be closed in this repository, and the other work was documentation-only. A final compile-only verification of the existing tree was run as described below.

**#5692 — definitively PARTIAL, not an unfinished port.** The tooltip provider framework, 22+ providers, all concrete TODOs and the opt-in flag are in place (`core/src/main/java/org/geysermc/geyser/item/tooltip/**` has zero TODO/FIXME markers). The only remaining item is the Java map `level` tooltip line:

```
javap ClientboundMapItemDataPacket (mcprotocollib 26.3-4):
  getMapId, getScale, getIcons, getData, isLocked   <- no getLevel
unzip -l protocol-26.3-4.jar | grep -ci MapItemSavedData  -> 0
JavaMapItemDataTranslator.java:49-60 reads scale/locked but caches nothing
GeyserSession has no map cache; upstream PR patch leaves MapTooltip as "// TODO"
```

`scale` alone is derivable, but adding a partial line would change the all-player map packet path without achieving parity, so no change was made. The other two reported gaps are already resolved: `Holder.ifId`/`ifCustom` provides behaviour equivalent to the upstream per-registry entry contexts (`InstrumentTooltip.java:45-48`, `JukeboxPlayableTooltip.java:44-45`, `BannerPatternLayersTooltip.java:52-56`), and vanilla name providers are superseded by `Item.getName`/`ItemTranslator.getCustomName`.

**#6914 — BLOCKED across every published artifact.** The maven release line ends at `26.3-4` (the fork's version) and the newest `26.3-SNAPSHOT` build is `14` (`20261014.110736`). `javap` on that snapshot shows `PotionContents` still has only `(int,int,List,String)` plus the four getters — no `hasCustomColor`, `OptionalInt` or presence flag. Absent and present `-1` colors both decode to `-1`, and `writePotionContents` gates on `>= 0`, so the distinction is destroyed before hashing. Only GeyserMC/MCProtocolLib#942 plus a new release can resolve it.

**Backlog audit J/K/L/M.** Four read-only auditors processed 1594 closed `UNFIXED` records and produced **319 evidence-backed dispositions**: 189 NOT_APPLICABLE, 66 UPSTREAM_FIXED, 31 DUPLICATE, 16 UNSUPPORTED and 17 BLOCKED; 1275 stayed `UNFIXED`. Note that this checkout is shallow (grafted at `777434cf`), so shards J and M downgraded every cited fix commit they could not prove an ancestor; the UPSTREAM_FIXED set above comes only from shards K and L, where ancestry was verified with `compare <sha>...master` or `git merge-base --is-ancestor`.

### Wave 18 — full-history verification of open issues and backlog N/O/P/Q

The checkout was unshallowed before this wave (`git rev-list --count HEAD` went from 249 to 5322, with no `.git/shallow`), so `git merge-base --is-ancestor <sha> HEAD` could finally verify fix commits that earlier auditors had to downgrade. Six workers ran: two on the 89 remaining open issues and four on 1275 closed UNFIXED records. One source fix landed; the rest were evidence-based tracker dispositions. Verification is compile-only.

**Open issues.** One portable upstream fix was found and ported:

- #2769 (`allow-bedrock-bridging=false` ignored by touch users) — upstream PR #6220's Bedrock interact-rotation check added at `BedrockInventoryTransactionTranslator.java:204-207`, commit `40c0cf638`, merged as `7598a5870`.

Eight issues were proven already fixed and promoted to FIXED: #5231, #5057, #4383, #4058, #3358, #2490, #2358 (fork commit `817a95c29`, verified an ancestor of `origin/master`, with `EntityCache.java:90,180,188-227` and `JavaSetEntityLinkTranslator.java:70-81`) and #3458 (upstream PR #5381 present at `GeyserSession.java:2001-2026` and `ChunkUtils.java:115,125`). The other batch found no portable work: `upstream/master` (`d2cf20058c`) is an ancestor of fork HEAD, and none of those issues has a merged fix or a clear reproducible fork-code root cause; partial upstream work was noted for #6802 (`PaintingEntity.java:106-112`) and #6202 (`TooltipProviders.java:99-124`).

**Backlog audit N/O/P/Q.** Four read-only auditors processed 1275 closed UNFIXED records and produced **574 evidence-backed dispositions**: 177 UPSTREAM_FIXED, 107 UNSUPPORTED, 269 NOT_APPLICABLE, 11 DUPLICATE and 10 BLOCKED; 701 stayed `UNFIXED`. Every UPSTREAM_FIXED claim was verified with `git merge-base --is-ancestor <sha> HEAD` plus a fork `file:line` — for example #6583 → this campaign's text-display commit `994cd6a8b`, #6353 → upstream PR #6351, #3495 → the skull-skin NPE fix, #5236 → `006fe75e5a`. Duplicate classifications were only accepted when the target record was itself already fixed (#4434/#3729 were kept UNFIXED for that reason), and unverifiable "fixed in latest build" closures were likewise left alone.


### Wave 19 — blocked-record dependency and history re-verification

All 55 `BLOCKED` records were re-checked against published dependency metadata, upstream commit ancestry (GitHub `compare` `behind_by=0`), and the current local jar. Nine stale dispositions were promoted to `UPSTREAM_FIXED`: #64, #132, #144, #545 and #4844 (MCProtocolLib fixes merged and ancestral to master, older than the pinned `protocol:26.3-4`), #5667 (Floodgate `096c605`), #904 (mappings `83a34687` ancestral to the pinned mappings submodule), #5714 (frp#5119 merged) and #6807 (libdatachannel-java `c2d3c9a6` musl-stack fix ancestral to upstream `main`, predating the pinned snapshot build). PR #6914 remains blocked: `javap` on `protocol-26.3-4.jar` shows `PotionContents(int,int,List,String)` with only `getPotionId/getCustomColor/getCustomEffects/getCustomName` and no `hasCustomColor`/`OptionalInt`, while maven-releases latest is 26.3-4 and maven-snapshots newest 26.3-SNAPSHOT build is 14 (20261014.110736); upstream GeyserMC/MCProtocolLib#942 is still open. No dependency version bump, source edit or runtime verification was performed in this wave.

## Priority queue — all OPEN issues and OPEN PRs

These are the records that still need triage or an adoption decision. Closed/merged records are in the JSON file.

### Open issues

_(291 records)_

| ID | Title | URL | Status |
| ---: | --- | --- | --- |
| 6491152696 | Item names are italicised for bedrock players | https://github.com/GeyserMC/Geyser/issues/6929 | `FIXED` |
| 6478673359 | Bedrock player moves extremely fast after a cross-world teleport until relog | https://github.com/GeyserMC/Geyser/issues/6927 | `DUPLICATE` |
| 6449358192 | NetherNet Doesn't Work on FreeBSD | https://github.com/GeyserMC/Geyser/issues/6923 | `NOT_APPLICABLE` |
| 6413008617 | [NetherNet] Native handles and GeyserSession objects are never released after Bedrock players disconnect (Velocity, Windows) – ~90 handles + ~7–11 MB leaked per connection | https://github.com/GeyserMC/Geyser/issues/6920 | `FIXED` |
| 6398503486 | Regression of #6834 in b1302: players joining through a signaling relay (MCXboxBroadcast) get the relay's IP again | https://github.com/GeyserMC/Geyser/issues/6919 | `DUPLICATE` |
| 6392359368 | Bedrock 26.6x (NetherNet): tilling grass/dirt with a hoe is reverted, farmland never reaches the Java server | https://github.com/GeyserMC/Geyser/issues/6918 | `UNFIXED` |
| 6363398043 | Nethernet and Playit: Connecting via an external IPv4 address and receiving a local IPv4 address. | https://github.com/GeyserMC/Geyser/issues/6915 | `NOT_APPLICABLE` |
| 6353841610 | Bedrock Client Cannot Connect (End of Stream) | https://github.com/GeyserMC/Geyser/issues/6913 | `NOT_APPLICABLE` |
| 6280416949 | Bedrock players experience abnormally fast movement speed after changing worlds or reconnecting | https://github.com/GeyserMC/Geyser/issues/6903 | `FIXED` |
| 6274805438 | bedrock player can fast place glowstone when keep holding place button after looking away from a respawn anchor | https://github.com/GeyserMC/Geyser/issues/6901 | `FIXED` |
| 6265656032 | Bedrock players are joining via ipv6 addresses | https://github.com/GeyserMC/Geyser/issues/6899 | `BLOCKED` |
| 6256269328 | I am getting kicked from the server (Bedrock Edition). | https://github.com/GeyserMC/Geyser/issues/6894 | `NOT_APPLICABLE` |
| 6256027037 | EncoderException "MessageToMessageCodec$2 must produce at least one message" kicks Bedrock players on Velocity (since 2026-11-06, also on b1288) | https://github.com/GeyserMC/Geyser/issues/6893 | `FIXED` |
| 6229661892 | Bedrock players in a boat desync at a static world border (Java players unaffected) | https://github.com/GeyserMC/Geyser/issues/6884 | `FIXED` |
| 6222127447 | Accept PROXY protocol v1 on webrtc-proxy-protocol-relays (or document that only v2 is supported) | https://github.com/GeyserMC/Geyser/issues/6881 | `NOT_APPLICABLE` |
| 6201847840 | Text display entities show through walls | https://github.com/GeyserMC/Geyser/issues/6876 | `FIXED` |
| 6145901311 | GUI Closes on Menu Transfer... sometimes. | https://github.com/GeyserMC/Geyser/issues/6858 | `FIXED` |
| 6138669848 | Very high latency, missing block drops and lag when bedrock players join fabric server. | https://github.com/GeyserMC/Geyser/issues/6854 | `FIXED` |
| 6134779753 | Geyser servers don't appear under Friends > LAN Games for 26.60 clients (NetherNet LAN discovery on UDP 7551 is never started) | https://github.com/GeyserMC/Geyser/issues/6853 | `FIXED` |
| 6133451417 | Bedrock 26.60 camera/input delay on Anarchy backend through Velocity; Hub works normally | https://github.com/GeyserMC/Geyser/issues/6851 | `UNFIXED` |
| 6132388000 | NetherNet seems to kick some bedrock players at seemingly random intervals | https://github.com/GeyserMC/Geyser/issues/6850 | `FIXED` |
| 6132001962 | Flickering Bug for bedrock | https://github.com/GeyserMC/Geyser/issues/6849 | `UNFIXED` |
| 6131252494 | Geyser doesn't work on bionic c library | https://github.com/GeyserMC/Geyser/issues/6848 | `FIXED` |
| 6084617421 | Velocity MOTD showing instead of the passthrough from Paper | https://github.com/GeyserMC/Geyser/issues/6842 | `FIXED` |
| 6082502683 | NetherNet: IPv4-bound Geyser logs "juice: Send failed, errno=97" WARNs for clients' IPv6 ICE candidates | https://github.com/GeyserMC/Geyser/issues/6841 | `UNSUPPORTED` |
| 6049372589 | Add a dump option to indicate whether NetherNet/signaling is being used by another component. | https://github.com/GeyserMC/Geyser/issues/6831 | `FIXED` |
| 6048759025 | Text Displays appear way higher than they should on Bedrock Edition | https://github.com/GeyserMC/Geyser/issues/6830 | `FIXED` |
| 6012421771 | NetherNet DLL blocked by Windows Smart App Control on Windows 11 | https://github.com/GeyserMC/Geyser/issues/6822 | `UNSUPPORTED` |
| 5958150713 | Wierd Itemswap/Item deletion in Creative Mode | https://github.com/GeyserMC/Geyser/issues/6813 | `FIXED` |
| 5922887066 | Players in death state and cannot respawn urgent bug | https://github.com/GeyserMC/Geyser/issues/6805 | `FIXED` |
| 5916322359 | Some items in Bedrock are invisible | https://github.com/GeyserMC/Geyser/issues/6802 | `UNFIXED` |
| 5914104967 | Implements block_placer component | https://github.com/GeyserMC/Geyser/issues/6801 | `NOT_APPLICABLE` |
| 5910296029 | Player takes fall damage when landing on a waterlogged chest while colliding with the side of an adjacent full block | https://github.com/GeyserMC/Geyser/issues/6798 | `DUPLICATE` |
| 5903758607 | Bedrock blocks dont appear server side | https://github.com/GeyserMC/Geyser/issues/6795 | `BLOCKED` |
| 5839390609 | Custom entities in mapping | https://github.com/GeyserMC/Geyser/issues/6770 | `UNSUPPORTED` |
| 5726325261 | Can't use axes, shovels or hoes with Java Edition 26.3 | https://github.com/GeyserMC/Geyser/issues/6753 | `UNFIXED` |
| 5634129504 | Boat ejects Bedrock player in bubble columns longer than 8 blocks | https://github.com/GeyserMC/Geyser/issues/6741 | `FIXED` |
| 5578485037 | purpur-geyser bug | https://github.com/GeyserMC/Geyser/issues/6734 | `UPSTREAM_FIXED` |
| 5494589283 | Sliding display entity translation updates are sent to the client but do not visually apply on Bedrock | https://github.com/GeyserMC/Geyser/issues/6723 | `NOT_APPLICABLE` |
| 5494554880 | Doors with a dynamic barrier block hitbox have unreliable click detection on Bedrock | https://github.com/GeyserMC/Geyser/issues/6722 | `UNFIXED` |
| 5476694429 | Protocol 2193 clients never render system chat (all Floodgate-origin chat invisible) on b1243 | https://github.com/GeyserMC/Geyser/issues/6715 | `FIXED` |
| 5437008603 | [bug] Got disconnected from the skin uploader: failed to find the given code in combination with the verify code | https://github.com/GeyserMC/Geyser/issues/6699 | `NOT_APPLICABLE` |
| 5429408847 | [Fabric] JVM remains alive after clean shutdown due to FloodgateSkinUploader WebSocket threads | https://github.com/GeyserMC/Geyser/issues/6695 | `FIXED` |
| 5391485278 | falling in the void infinitely | https://github.com/GeyserMC/Geyser/issues/6692 | `DUPLICATE` |
| 5352851619 | Ghost block problem with specific blocks and some blocks only above y224? | https://github.com/GeyserMC/Geyser/issues/6687 | `UNSUPPORTED` |
| 5286055183 | [BUG] 1.21.1 recipes (e.g., Warped Planks) are missing or not craftable on Bedrock clients | https://github.com/GeyserMC/Geyser/issues/6684 | `UNFIXED` |
| 5285638522 | [Bedrock] GUI clicks became unreliable after Geyser b1225 | https://github.com/GeyserMC/Geyser/issues/6683 | `FIXED` |
| 5249345569 | Taking, discarding and moving items | https://github.com/GeyserMC/Geyser/issues/6677 | `FIXED` |
| 5247497208 | Server-side Entity Culling | https://github.com/GeyserMC/Geyser/issues/6676 | `UNSUPPORTED` |
| 5244501862 | Bedrock player skin not showing on Java - "Ignoring player info update" logged for Floodgate players | https://github.com/GeyserMC/Geyser/issues/6675 | `FIXED` |
| 5242221308 | Stopsound command fix | https://github.com/GeyserMC/Geyser/issues/6674 | `UNSUPPORTED` |
| 5234500656 | Sudden Speedmissmatch of Donkeys (Probably Horses too) when using Swiftness  potions | https://github.com/GeyserMC/Geyser/issues/6670 | `UNFIXED` |
| 5233488856 | Hopper minecart appears as furnace minecart on Bedrock (Geyser 2.11.2-b1230) | https://github.com/GeyserMC/Geyser/issues/6668 | `DUPLICATE` |
| 5219153527 | Resource packs zipped on MacOS do not read properly | https://github.com/GeyserMC/Geyser/issues/6663 | `FIXED` |
| 5217173715 | BetterView causes chunks to start flickering on bedrock | https://github.com/GeyserMC/Geyser/issues/6661 | `UNSUPPORTED` |
| 5211481247 | All Bedrocks skins do not show for Java clients | https://github.com/GeyserMC/Geyser/issues/6659 | `DUPLICATE` |
| 5192944394 | ItemStack rejected without cause | https://github.com/GeyserMC/Geyser/issues/6653 | `UNFIXED` |
| 5186978565 | Paper Anti-Xray engine-mode 2 causes Bedrock clientbound updates to freeze and eventually time out | https://github.com/GeyserMC/Geyser/issues/6650 | `NOT_APPLICABLE` |
| 5184877875 | There no punching Animation in 1st Person when using animation packs | https://github.com/GeyserMC/Geyser/issues/6649 | `UNSUPPORTED` |
| 5176721889 | Bedrock GUI buttons sometimes trigger the wrong button after Geyser update | https://github.com/GeyserMC/Geyser/issues/6648 | `FIXED` |
| 5164064477 | Pearl catch issue | https://github.com/GeyserMC/Geyser/issues/6646 | `NOT_APPLICABLE` |
| 5157745642 | Chunk loading issue | https://github.com/GeyserMC/Geyser/issues/6641 | `DUPLICATE` |
| 5157629372 | Buggy inventory and crafting | https://github.com/GeyserMC/Geyser/issues/6640 | `UNSUPPORTED` |
| 5156275649 | Sprites in quick actions components cause error | https://github.com/GeyserMC/Geyser/issues/6639 | `UPSTREAM_FIXED` |
| 5144259158 | Bedrock players cannot break vanilla blocks after Geyser update | https://github.com/GeyserMC/Geyser/issues/6637 | `UNSUPPORTED` |
| 5141425466 | Add container_empty predicate for Geyser Custom Items V2 | https://github.com/GeyserMC/Geyser/issues/6634 | `NOT_APPLICABLE` |
| 5135599202 | Hydraulic and Consoles: A small but no doubtfully painful to fix bug | https://github.com/GeyserMC/Geyser/issues/6632 | `NOT_APPLICABLE` |
| 5091246493 | Custom Entity API non-vanilla entities | https://github.com/GeyserMC/Geyser/issues/6598 | `NOT_APPLICABLE` |
| 5084497235 | Bedrock mobile users either unable to join or crashing immediately. | https://github.com/GeyserMC/Geyser/issues/6596 | `UNFIXED` |
| 5072539239 | Ty-el's Vietnamese Language Pack not work with GeyserMc | https://github.com/GeyserMC/Geyser/issues/6592 | `UNSUPPORTED` |
| 5061576088 | Bedroack cant load chunks | https://github.com/GeyserMC/Geyser/issues/6587 | `NOT_APPLICABLE` |
| 5055981657 | /fly toggled on causing swimming and block breaking issues | https://github.com/GeyserMC/Geyser/issues/6584 | `FIXED` |
| 5040716111 | Players at Y 64 get inexplicably stuck | https://github.com/GeyserMC/Geyser/issues/6580 | `UNFIXED` |
| 5011405882 | Crafting Crash - Book & Quill / Book | https://github.com/GeyserMC/Geyser/issues/6577 | `UNFIXED` |
| 5001803063 | Bedrock chat not visible to Java players & Bedrock skins show as Steve/Alex | https://github.com/GeyserMC/Geyser/issues/6574 | `UNFIXED` |
| 4979519119 | Click register delay | https://github.com/GeyserMC/Geyser/issues/6570 | `FIXED` |
| 4969896275 | Bedrock Players left incapacitated by ImageFrame maps | https://github.com/GeyserMC/Geyser/issues/6569 | `NOT_APPLICABLE` |
| 4953133141 | ViaProxy: first join via NetherNet relay times out at ~19s (rejoin works); unguarded getLocalAddress() NPE in GeyserViaProxyPlugin channel-init | https://github.com/GeyserMC/Geyser/issues/6567 | `FIXED` |
| 4934805024 | Recipe book auto-craft reverts inventory / returns ingredients instead of crafted item | https://github.com/GeyserMC/Geyser/issues/6563 | `DUPLICATE` |
| 4915679666 | Cant see Skins | https://github.com/GeyserMC/Geyser/issues/6558 | `UPSTREAM_FIXED` |
| 4915592951 | Possible regression with "forward-player-ping" causing Bedrock timeouts after long teleports | https://github.com/GeyserMC/Geyser/issues/6557 | `FIXED` |
| 4915065150 | Unable to wear armor after crafting | https://github.com/GeyserMC/Geyser/issues/6556 | `BLOCKED` |
| 4902059744 | Movement of non-living entities (Display entities) isn't interpolated, causing visible drag for anything riding one | https://github.com/GeyserMC/Geyser/issues/6551 | `FIXED` |
| 4889086403 | Update bedrock limiations to include attributes | https://github.com/GeyserMC/Geyser/issues/6544 | `NOT_APPLICABLE` |
| 4868140574 | Allow custom Bedrock item identifiers in horse armor GUI acceptedItems | https://github.com/GeyserMC/Geyser/issues/6535 | `FIXED` |
| 4866786667 | Players kicked while right-click on block | https://github.com/GeyserMC/Geyser/issues/6532 | `UNSUPPORTED` |
| 4859485425 | enchant table cannot be used | https://github.com/GeyserMC/Geyser/issues/6527 | `UNFIXED` |
| 4819367010 | Bedrock Worlds tab server entry disappears or connects to wrong port behind NAT port remapping | https://github.com/GeyserMC/Geyser/issues/6508 | `NOT_APPLICABLE` |
| 4787423496 | Dialog buttons with object atlas sprite labels should map to FormImage.Type.PATH on Bedrock, but currently error with Expected tag to be a literal string... and render blank. | https://github.com/GeyserMC/Geyser/issues/6499 | `UNSUPPORTED` |
| 4776889765 | Failed to correctly map the zombie villager's profession | https://github.com/GeyserMC/Geyser/issues/6495 | `NOT_APPLICABLE` |
| 4756478294 | Minecart Visual Lag/desync | https://github.com/GeyserMC/Geyser/issues/6485 | `UNSUPPORTED` |
| 4737289181 | [ViaProxy] Client is having trouble establishing the connection | https://github.com/GeyserMC/Geyser/issues/6478 | `UNSUPPORTED` |
| 4695749715 | PS5 Connection timeout after latest MC update | https://github.com/GeyserMC/Geyser/issues/6466 | `BLOCKED` |
| 4684297026 | Stuck in furnace GUI, can't back out. | https://github.com/GeyserMC/Geyser/issues/6458 | `UPSTREAM_FIXED` |
| 4677471227 | Bedrock player's POV does not update when controlling mounted mobs (ALL ENTITIES) | https://github.com/GeyserMC/Geyser/issues/6454 | `UNFIXED` |
| 4613680562 | Bedrock player sees water but java dont | https://github.com/GeyserMC/Geyser/issues/6446 | `FIXED` |
| 4592672702 | The dialog box is closing the currently open inventory on the server-side | https://github.com/GeyserMC/Geyser/issues/6443 | `FIXED` |
| 4479609325 | Crafting issue PLEASE HELP | https://github.com/GeyserMC/Geyser/issues/6420 | `DUPLICATE` |
| 4444025051 | Horses broken | https://github.com/GeyserMC/Geyser/issues/6408 | `NOT_APPLICABLE` |
| 4428303756 | Bucket misalignment/ghosting | https://github.com/GeyserMC/Geyser/issues/6399 | `BLOCKED` |
| 4427799377 | Floating Items during Crafting for  some BEDROCK PLAYERS | https://github.com/GeyserMC/Geyser/issues/6398 | `NOT_APPLICABLE` |
| 4426982552 | Chests visual bug | https://github.com/GeyserMC/Geyser/issues/6397 | `UNSUPPORTED` |
| 4424154581 | Bedrock users spawn facing wrong direction | https://github.com/GeyserMC/Geyser/issues/6395 | `DUPLICATE` |
| 4411316441 | BEDROCK INVENTORY BUG | https://github.com/GeyserMC/Geyser/issues/6373 | `UNFIXED` |
| 4402991483 | Globally linked Java skin does not show on Bedrock | https://github.com/GeyserMC/Geyser/issues/6368 | `UPSTREAM_FIXED` |
| 4367591866 | Bedrock mobile players crafted items do not stack in inventory | https://github.com/GeyserMC/Geyser/issues/6342 | `UNSUPPORTED` |
| 4367583984 | Bamboo and crop blocks cause rubber-banding/glitching for Bedrock mobile players | https://github.com/GeyserMC/Geyser/issues/6341 | `FIXED` |
| 4307745168 | Minecraft armour trims problem | https://github.com/GeyserMC/Geyser/issues/6320 | `UNFIXED` |
| 4259757094 | Custom Skulls Bug? | https://github.com/GeyserMC/Geyser/issues/6302 | `UNFIXED` |
| 4198616510 | Bedrock 26.10/26.12 (protocol 944) horse riding desync: delayed first jump, rubberbanding, dismount snapback | https://github.com/GeyserMC/Geyser/issues/6283 | `UNFIXED` |
| 4196235341 | Mapping api v2 predicate property fishing_rod_cast or crossbow charged Not compatible with Java players | https://github.com/GeyserMC/Geyser/issues/6281 | `UPSTREAM_FIXED` |
| 4186822367 | Bedrock clients are crashing while somebody has a custom armor with armor trim equipped | https://github.com/GeyserMC/Geyser/issues/6278 | `NOT_APPLICABLE` |
| 4154311005 | Custom item attirbutes do not work proplerly on bedrock edition | https://github.com/GeyserMC/Geyser/issues/6266 | `UNSUPPORTED` |
| 4113646890 | Bedrock Spectator Mode not working as intended. | https://github.com/GeyserMC/Geyser/issues/6248 | `FIXED` |
| 4110465428 | Title: Bedrock recipe book auto-crafting does not work correctly through Geyser | https://github.com/GeyserMC/Geyser/issues/6246 | `FIXED` |
| 4079217873 | Dolphin Tunnels not working for bedrock players | https://github.com/GeyserMC/Geyser/issues/6239 | `UPSTREAM_FIXED` |
| 4041295810 | signing writable books if they have NBT does not work | https://github.com/GeyserMC/Geyser/issues/6224 | `FIXED` |
| 4005465880 | PS5 Bedrock Edition Crashing consistently | https://github.com/GeyserMC/Geyser/issues/6213 | `UNSUPPORTED` |
| 3997506286 | Players using Bedrock Edition on mobile devices are unable to enchant items or books. | https://github.com/GeyserMC/Geyser/issues/6210 | `UNFIXED` |
| 3973938805 | Lore issue | https://github.com/GeyserMC/Geyser/issues/6202 | `UNFIXED` |
| 3897906890 | [Towny] geyser bedrock players can attack by quickly breaking blocks and attacking in pvp disabled town | https://github.com/GeyserMC/Geyser/issues/6159 | `NOT_APPLICABLE` |
| 3864556632 | Add an option for geyser to temporary solve "Not receiving chunk" | https://github.com/GeyserMC/Geyser/issues/6142 | `NOT_APPLICABLE` |
| 3851074699 | Can't Bedrock GUI | https://github.com/GeyserMC/Geyser/issues/6134 | `FIXED` |
| 3844057698 | Client crash/freeze when entering area with many mobs/animals/entities | https://github.com/GeyserMC/Geyser/issues/6129 | `UNFIXED` |
| 3841214418 | [Bug Report] ArtMap used to work properly but no longer accurate. | https://github.com/GeyserMC/Geyser/issues/6128 | `UNFIXED` |
| 3824109144 | Enchant table is not working | https://github.com/GeyserMC/Geyser/issues/6116 | `BLOCKED` |
| 3823915450 | Player head texture occasionally not updated | https://github.com/GeyserMC/Geyser/issues/6115 | `UNFIXED` |
| 3823874351 | Items stuck in cursor after furnace interaction | https://github.com/GeyserMC/Geyser/issues/6113 | `FIXED` |
| 3798882794 | Players can't connect if they have an oversize book in their inventory | https://github.com/GeyserMC/Geyser/issues/6098 | `FIXED` |
| 3795623377 | Blocks don't break when Tools have CustomModel | https://github.com/GeyserMC/Geyser/issues/6097 | `UNFIXED` |
| 3790893570 | Custom blocks under state_overrides with different block identifiers | https://github.com/GeyserMC/Geyser/issues/6092 | `NOT_APPLICABLE` |
| 3781719547 | Skin and cape data API as BufferedImage | https://github.com/GeyserMC/Geyser/issues/6086 | `NOT_APPLICABLE` |
| 3773728835 | Bedrock is very buggy | https://github.com/GeyserMC/Geyser/issues/6079 | `UNFIXED` |
| 3771522889 | Nintendo Switch 1 OLED Game Crash on Join | https://github.com/GeyserMC/Geyser/issues/6078 | `UNFIXED` |
| 3760739627 | Custom mapped (note)blocks don't render their texture/model if they're a falling block | https://github.com/GeyserMC/Geyser/issues/6072 | `NOT_APPLICABLE` |
| 3720071081 | Add the SHA hash when outputting version | https://github.com/GeyserMC/Geyser/issues/6037 | `FIXED` |
| 3717137044 | Copper Bars craft into Trap Doors on Bedrock (iOS) | https://github.com/GeyserMC/Geyser/issues/6032 | `FIXED` |
| 3708933008 | Nintendo Switch players and Pack file size | https://github.com/GeyserMC/Geyser/issues/6023 | `UNSUPPORTED` |
| 3700997392 | Support for proper tooltip display of unsafe enchantments | https://github.com/GeyserMC/Geyser/issues/6020 | `UNSUPPORTED` |
| 3690401445 | Support registering extension commands without a root command | https://github.com/GeyserMC/Geyser/issues/6018 | `NOT_APPLICABLE` |
| 3675577904 | Bedrock has lag but not java. | https://github.com/GeyserMC/Geyser/issues/6013 | `UNFIXED` |
| 3661063687 | A clearer world boundary for bedrock edition players. | https://github.com/GeyserMC/Geyser/issues/6006 | `NOT_APPLICABLE` |
| 3618783248 | Not properly calculating custom tool break speed | https://github.com/GeyserMC/Geyser/issues/5982 | `NOT_APPLICABLE` |
| 3618358473 | Weak hits, Knockback doesn't push hard as java! 1.8.9 pvp. | https://github.com/GeyserMC/Geyser/issues/5981 | `UNFIXED` |
| 3603900841 | Crafting Issues and Villager Trading bug(ΒΕ) | https://github.com/GeyserMC/Geyser/issues/5974 | `UNFIXED` |
| 3603774586 | Practicles looping spamming + hit sounds  after bedrock player dies | https://github.com/GeyserMC/Geyser/issues/5973 | `UNFIXED` |
| 3530206270 | armorstand mount is bugged on bedrock | https://github.com/GeyserMC/Geyser/issues/5917 | `NOT_APPLICABLE` |
| 3529484524 | Blocks disappearing for java players | https://github.com/GeyserMC/Geyser/issues/5914 | `UNFIXED` |
| 3526305160 | Bedrock pov does not update when controlling mounted mobs (MythicMobs/Model Engine) | https://github.com/GeyserMC/Geyser/issues/5911 | `UNFIXED` |
| 3523810929 | Blocks don't place properly | https://github.com/GeyserMC/Geyser/issues/5910 | `UNFIXED` |
| 3523399664 | Convert Blocks into skulls For hat wearing helmet | https://github.com/GeyserMC/Geyser/issues/5909 | `NOT_APPLICABLE` |
| 3521350341 | Player Heads Glitching as Steve + Steve Entity | https://github.com/GeyserMC/Geyser/issues/5907 | `NOT_APPLICABLE` |
| 3521287921 | Mining speed difference between custom textured shovel and normal shovel | https://github.com/GeyserMC/Geyser/issues/5906 | `FIXED` |
| 3521263925 | Uncrafting Netherite Block to Ingots not working (Minor Bug) | https://github.com/GeyserMC/Geyser/issues/5905 | `NOT_APPLICABLE` |
| 3489885492 | Command Description Bug | https://github.com/GeyserMC/Geyser/issues/5886 | `NOT_APPLICABLE` |
| 3484828649 | anvil bug | https://github.com/GeyserMC/Geyser/issues/5879 | `UNFIXED` |
| 3481725971 | iOS Bedrock crash on specific GeyserMC server | https://github.com/GeyserMC/Geyser/issues/5873 | `UNFIXED` |
| 3478851061 | Disconnect when a player has a large number of advancements | https://github.com/GeyserMC/Geyser/issues/5869 | `NOT_APPLICABLE` |
| 3465131333 | Inaccurate Y-Axis motion when falling/jumping flagging anticheats. | https://github.com/GeyserMC/Geyser/issues/5852 | `UNSUPPORTED` |
| 3462668505 | Mining with pickaxe opens menu when i left click on mobile | https://github.com/GeyserMC/Geyser/issues/5851 | `UNFIXED` |
| 3456191420 | Bedrock players mine faster than Java when spam clicking | https://github.com/GeyserMC/Geyser/issues/5843 | `NOT_APPLICABLE` |
| 3432207558 | 1 out of 2 PS5 Players on same Network getting disconnected | https://github.com/GeyserMC/Geyser/issues/5830 | `UNFIXED` |
| 3374925969 | 2 Bug on Bedrock Client (maybe only IOS User, im using iPad) | https://github.com/GeyserMC/Geyser/issues/5793 | `UNFIXED` |
| 3353283339 | Sometimes Minecraft Bedrock crashes while driving happy ghasts | https://github.com/GeyserMC/Geyser/issues/5783 | `UNFIXED` |
| 3338574194 | Problem in Achievements translation | https://github.com/GeyserMC/Geyser/issues/5770 | `FIXED` |
| 3322757341 | The player's movements are strange. | https://github.com/GeyserMC/Geyser/issues/5755 | `UNFIXED` |
| 3305700003 | ghost doors | https://github.com/GeyserMC/Geyser/issues/5746 | `FIXED` |
| 3282457747 | show_in_tooltip Java component tag is not resolved properly | https://github.com/GeyserMC/Geyser/issues/5736 | `FIXED` |
| 3206648842 | Falling Blocks `setVelocity` doesn't work | https://github.com/GeyserMC/Geyser/issues/5655 | `UNSUPPORTED` |
| 3190235250 | Bedrock Edition 1.21.81 Cannot Interact with Fake Players Created by /bot or Use Custom Items from Holoprint (Leaves 1.21.4 + Geyser 2.8.0-b867; Breaks Holoprint Functionality) | https://github.com/GeyserMC/Geyser/issues/5634 | `UNFIXED` |
| 3166049718 | Unable to craft firework rockets on Pocket Edition | https://github.com/GeyserMC/Geyser/issues/5611 | `UNFIXED` |
| 3149044216 | Hand animations and rendering issues with Actions & Stuff | https://github.com/GeyserMC/Geyser/issues/5597 | `NOT_APPLICABLE` |
| 3133351765 | Geyser - Anvil Works different on Java/Bedrock | https://github.com/GeyserMC/Geyser/issues/5584 | `UNFIXED` |
| 3070695219 | Scale command not showing changed 1st person perspective on Bedrock | https://github.com/GeyserMC/Geyser/issues/5554 | `NOT_APPLICABLE` |
| 3056086028 | Cannot use Fishing Rods with Custom Model Data | https://github.com/GeyserMC/Geyser/issues/5547 | `UNSUPPORTED` |
| 2995847786 | bedrock players get stuck in an infinite falling state if they get killed upon logout | https://github.com/GeyserMC/Geyser/issues/5489 | `BLOCKED` |
| 2984093096 | Chunks don't load after joining the server | https://github.com/GeyserMC/Geyser/issues/5474 | `UNFIXED` |
| 2974939937 | Can't move items with custom tags | https://github.com/GeyserMC/Geyser/issues/5464 | `UNFIXED` |
| 2958634184 | Players getting kicked | https://github.com/GeyserMC/Geyser/issues/5444 | `UNFIXED` |
| 2948344051 | chunks do not go and the player falls constantly down to -200 blocks and cannot do anything | https://github.com/GeyserMC/Geyser/issues/5431 | `UNFIXED` |
| 2920514585 | Working piston bolts not rendering correctly on bedrock | https://github.com/GeyserMC/Geyser/issues/5408 | `UNSUPPORTED` |
| 2908555375 | Custom Player Head Misalignment | https://github.com/GeyserMC/Geyser/issues/5404 | `FIXED` |
| 2888772644 | Not receiving chunk | https://github.com/GeyserMC/Geyser/issues/5386 | `UNFIXED` |
| 2878608332 | Hardcore Hearts | https://github.com/GeyserMC/Geyser/issues/5373 | `NOT_APPLICABLE` |
| 2875732073 | Players disconnected after teleportation | https://github.com/GeyserMC/Geyser/issues/5371 | `FIXED` |
| 2857247988 | Bedrock players experience extreme lag when crafting using crafting table | https://github.com/GeyserMC/Geyser/issues/5350 | `UNFIXED` |
| 2828733774 | Item flags on bedrock items don't hide properly | https://github.com/GeyserMC/Geyser/issues/5319 | `NOT_APPLICABLE` |
| 2827479445 | Support Open-Directorys for resource packs | https://github.com/GeyserMC/Geyser/issues/5318 | `FIXED` |
| 2776467193 | Not all maps display correctly | https://github.com/GeyserMC/Geyser/issues/5270 | `NOT_APPLICABLE` |
| 2767079695 | InventoryClickEvent gets triggered multiple times when player inventory gets closed and crafting slots are full | https://github.com/GeyserMC/Geyser/issues/5258 | `FIXED` |
| 2763399238 | Bedrock Edition Animated Skins Not Showing | https://github.com/GeyserMC/Geyser/issues/5253 | `NOT_APPLICABLE` |
| 2760504860 | Stonecutter and Mod GUI menu move item not working for PS4 Bedrock Users | https://github.com/GeyserMC/Geyser/issues/5246 | `UNFIXED` |
| 2743754360 | Bedrock players getting console spam "moved too quickly!" | https://github.com/GeyserMC/Geyser/issues/5231 | `FIXED` |
| 2739739899 | Bedrock player does not receive knockback when an entity with a negative explosion range affects the player. | https://github.com/GeyserMC/Geyser/issues/5227 | `FIXED` |
| 2724412280 | [API] ServerboundDiagnosticsPacket data | https://github.com/GeyserMC/Geyser/issues/5199 | `NOT_APPLICABLE` |
| 2697961951 | Text preprocess / postprocess API | https://github.com/GeyserMC/Geyser/issues/5166 | `NOT_APPLICABLE` |
| 2681347646 | egg throwing on bedrock is too low pitch | https://github.com/GeyserMC/Geyser/issues/5159 | `UNSUPPORTED` |
| 2660360181 | Bedrock mining advantage | https://github.com/GeyserMC/Geyser/issues/5152 | `NOT_APPLICABLE` |
| 2634025004 | Lead links sometimes don't appear. | https://github.com/GeyserMC/Geyser/issues/5123 | `FIXED` |
| 2573240553 | Bug with GSit /lay command on bedrock | https://github.com/GeyserMC/Geyser/issues/5073 | `DUPLICATE` |
| 2541243115 | Cannot move player correctly, cannot interact with things | https://github.com/GeyserMC/Geyser/issues/5057 | `FIXED` |
| 2540190174 | Implement JFR profiler on the "geyser" command for standalone. | https://github.com/GeyserMC/Geyser/issues/5053 | `NOT_APPLICABLE` |
| 2535701984 | Crafting issue | https://github.com/GeyserMC/Geyser/issues/5047 | `DUPLICATE` |
| 2525964971 | Custom Tools with attributes not working on bedrock  | https://github.com/GeyserMC/Geyser/issues/5037 | `NOT_APPLICABLE` |
| 2512015492 | the shopkeeper plugin is not working | https://github.com/GeyserMC/Geyser/issues/5025 | `DUPLICATE` |
| 2508374022 | Character Creator Items Support for only Bedrock Players | https://github.com/GeyserMC/Geyser/issues/5020 | `NOT_APPLICABLE` |
| 2503140101 | Sitting players float in the pov of bedrock players | https://github.com/GeyserMC/Geyser/issues/5017 | `UNFIXED` |
| 2485051625 | Redstone dust direction | https://github.com/GeyserMC/Geyser/issues/4992 | `UNSUPPORTED` |
| 2482019437 | Piston heads | https://github.com/GeyserMC/Geyser/issues/4985 | `UNSUPPORTED` |
| 2479498301 | An RCON client to JE console in Geyser Standalone | https://github.com/GeyserMC/Geyser/issues/4981 | `UNFIXED` |
| 2470737416 | Geyser players can't move edited items | https://github.com/GeyserMC/Geyser/issues/4970 | `UNFIXED` |
| 2434631878 | Ghost Blocks when attempting to place a block that has an active item Cooldown  | https://github.com/GeyserMC/Geyser/issues/4911 | `FIXED` |
| 2434115139 | Geyser players cannot use enchanted books on anvil | https://github.com/GeyserMC/Geyser/issues/4909 | `UNFIXED` |
| 2420932284 | Non-functionality of custom arrows with applied texture with Geyser | https://github.com/GeyserMC/Geyser/issues/4881 | `UNSUPPORTED` |
| 2414195123 | Can't Strip Logs When Holding Shield | https://github.com/GeyserMC/Geyser/issues/4871 | `UNSUPPORTED` |
| 2406819757 | Crafting certain items puts them into crafting grid instead of inventory | https://github.com/GeyserMC/Geyser/issues/4855 | `UNFIXED` |
| 2406672284 | GENERIC_STEP_HEIGHT attribute is not handled correctly for players | https://github.com/GeyserMC/Geyser/issues/4854 | `UNSUPPORTED` |
| 2394087076 | Item drop animation issue(honeyblock,bedrock) | https://github.com/GeyserMC/Geyser/issues/4832 | `UNFIXED` |
| 2380557976 | falling_dust showing incorrectly | https://github.com/GeyserMC/Geyser/issues/4803 | `UNSUPPORTED` |
| 2342293640 | [Geyser-Spigot] Missing locale: de_CH (Commit 0fcf0f9) | https://github.com/GeyserMC/Geyser/issues/4725 | `UPSTREAM_FIXED` |
| 2326841512 | Firework Crafting Issues for Bedrock Players | https://github.com/GeyserMC/Geyser/issues/4707 | `UNFIXED` |
| 2255098084 | Signs dissapear after relogging | https://github.com/GeyserMC/Geyser/issues/4588 | `FIXED` |
| 2154934969 | Bedrock Classic Skin Wrapping Incorrectly on Model | https://github.com/GeyserMC/Geyser/issues/4464 | `NOT_APPLICABLE` |
| 2146683715 | Weird Slow Crafting | https://github.com/GeyserMC/Geyser/issues/4453 | `UNFIXED` |
| 2097630743 | Player hand shaking | https://github.com/GeyserMC/Geyser/issues/4409 | `UNFIXED` |
| 2069382809 | Bedrock players getting "moved too quickly!" WARN using warps and TP to claim | https://github.com/GeyserMC/Geyser/issues/4383 | `FIXED` |
| 2061878718 | Elytra cape texture doesn't get displayed on Bedrock | https://github.com/GeyserMC/Geyser/issues/4374 | `NOT_APPLICABLE` |
| 2054838682 | Custom Itemsadder shield blocking not working for bedrock players | https://github.com/GeyserMC/Geyser/issues/4359 | `NOT_APPLICABLE` |
| 2022879416 | Some Player Head textures display white when placed | https://github.com/GeyserMC/Geyser/issues/4325 | `FIXED` |
| 2015861853 | You cannot place torches on containers while flying + sneaking | https://github.com/GeyserMC/Geyser/issues/4317 | `NOT_APPLICABLE` |
| 2005161425 | ActionBar bounces up and down | https://github.com/GeyserMC/Geyser/issues/4304 | `UNFIXED` |
| 1978045936 | Teleported item frames not updating position on bedrock | https://github.com/GeyserMC/Geyser/issues/4274 | `UPSTREAM_FIXED` |
| 1964944357 | Auto update skins? | https://github.com/GeyserMC/Geyser/issues/4249 | `NOT_APPLICABLE` |
| 1943675261 | A way to forward command descriptions in geyser standalone | https://github.com/GeyserMC/Geyser/issues/4215 | `NOT_APPLICABLE` |
| 1931357359 | Bedrock players don't see skins with custom models | https://github.com/GeyserMC/Geyser/issues/4195 | `NOT_APPLICABLE` |
| 1916653451 | Suggestion: make players who have invisibility temporarily visible when the glowing affect is applied. | https://github.com/GeyserMC/Geyser/issues/4156 | `NOT_APPLICABLE` |
| 1888639047 | Lectern signal strength inconsistent with Java edition | https://github.com/GeyserMC/Geyser/issues/4113 | `NOT_APPLICABLE` |
| 1884438284 | Villager trading on mobile disregards which trade you select, and always gives you the first trade. | https://github.com/GeyserMC/Geyser/issues/4102 | `UNFIXED` |
| 1873967017 | Trading doesn't seem to be working in the end only for geyser users. | https://github.com/GeyserMC/Geyser/issues/4092 | `UNFIXED` |
| 1870724269 | Allow items to map to custom blocks | https://github.com/GeyserMC/Geyser/issues/4090 | `UNFIXED` |
| 1853575554 | Dismounting Fake Entity | https://github.com/GeyserMC/Geyser/issues/4058 | `FIXED` |
| 1819258094 | FishHook on Lava with CustomFishing | https://github.com/GeyserMC/Geyser/issues/4012 | `UNSUPPORTED` |
| 1806744583 | Client crashes when starting to type a command | https://github.com/GeyserMC/Geyser/issues/3990 | `UNSUPPORTED` |
| 1805860584 | Unfair Advantage in Mining Speeds between Bedrock & Java | https://github.com/GeyserMC/Geyser/issues/3987 | `UNSUPPORTED` |
| 1788104735 | Fast Build with glass panes and other blocks broken but looks like it works | https://github.com/GeyserMC/Geyser/issues/3937 | `UNFIXED` |
| 1784729187 | Bedrock-Issue with close-event | https://github.com/GeyserMC/Geyser/issues/3932 | `FIXED` |
| 1778025057 | Unable to brush suspicious sand/gravel | https://github.com/GeyserMC/Geyser/issues/3921 | `UNFIXED` |
| 1772429544 | BE players can't see their current location on the map in nether or the end | https://github.com/GeyserMC/Geyser/issues/3910 | `UNFIXED` |
| 1750783737 | BE slim (3px) skin is not reflected correctly | https://github.com/GeyserMC/Geyser/issues/3856 | `NOT_APPLICABLE` |
| 1739441457 | item/block display entities | https://github.com/GeyserMC/Geyser/issues/3810 | `UNSUPPORTED` |
| 1686001459 | Java players can't push bedrock players | https://github.com/GeyserMC/Geyser/issues/3702 | `UNFIXED` |
| 1671866467 | Cannot Copy Books | https://github.com/GeyserMC/Geyser/issues/3682 | `UNFIXED` |
| 1666874262 | Bedrock can fly under certain conditions | https://github.com/GeyserMC/Geyser/issues/3678 | `FIXED` |
| 1641340815 | Custom Grass/Leaf colors | https://github.com/GeyserMC/Geyser/issues/3645 | `UNFIXED` |
| 1599570030 | Items with "AttributeModifiers" in their NBTData get stuck in clients cursor and are not being displayed properly. | https://github.com/GeyserMC/Geyser/issues/3580 | `UNFIXED` |
| 1580737714 | Support multiple ip binds | https://github.com/GeyserMC/Geyser/issues/3547 | `NOT_APPLICABLE` |
| 1559783482 | Mounting cod on player | https://github.com/GeyserMC/Geyser/issues/3520 | `NOT_APPLICABLE` |
| 1512242345 | when holding items in both hands and interacting geyser does not correctly send both the main hand and offhand interactions to the event handler | https://github.com/GeyserMC/Geyser/issues/3480 | `UNFIXED` |
| 1497715636 | Please implement "aerial perspective" for how chunks look. | https://github.com/GeyserMC/Geyser/issues/3458 | `FIXED` |
| 1495006234 | GeyserApi.api().connectionByUuid() is flaky | https://github.com/GeyserMC/Geyser/issues/3456 | `FIXED` |
| 1483856962 | Allows custom InteractiveTag for Entity | https://github.com/GeyserMC/Geyser/issues/3441 | `UNFIXED` |
| 1455706435 | overlapping command enums | https://github.com/GeyserMC/Geyser/issues/3411 | `UNFIXED` |
| 1453152029 | Complex Spigot+Paper server player moving and climbing rebound | https://github.com/GeyserMC/Geyser/issues/3407 | `UNFIXED` |
| 1428000485 | Employ fix for headless Armor Stand | https://github.com/GeyserMC/Geyser/issues/3373 | `UNSUPPORTED` |
| 1422823529 | Packet intercepting and sending API | https://github.com/GeyserMC/Geyser/issues/3365 | `NOT_APPLICABLE` |
| 1419228638 | Camouflage blocks, GeyserMC players see teleportation | https://github.com/GeyserMC/Geyser/issues/3358 | `FIXED` |
| 1397034352 | Command description support for geyser standalone | https://github.com/GeyserMC/Geyser/issues/3337 | `NOT_APPLICABLE` |
| 1395538072 | Stopsound stops all sounds on bedrock regardless of chosen category | https://github.com/GeyserMC/Geyser/issues/3335 | `UNSUPPORTED` |
| 1306933326 | Horse Issues | https://github.com/GeyserMC/Geyser/issues/3146 | `NOT_APPLICABLE` |
| 1282694633 | Bedrock block break time when flying is not correct | https://github.com/GeyserMC/Geyser/issues/3084 | `FIXED` |
| 1269700402 | Respawn Problem | https://github.com/GeyserMC/Geyser/issues/3048 | `UNFIXED` |
| 1181673807 | Can't boost with Firework when flying with Elytra in 1.12.2 creative | https://github.com/GeyserMC/Geyser/issues/2904 | `NOT_APPLICABLE` |
| 1164369454 | Disguise Issue | https://github.com/GeyserMC/Geyser/issues/2879 | `UNFIXED` |
| 1122496813 | Allow query port and bedrock port to be on the same port | https://github.com/GeyserMC/Geyser/issues/2807 | `NOT_APPLICABLE` |
| 1112663867 | Bedrock bridging possible despite allow-bedrock-bridging being false | https://github.com/GeyserMC/Geyser/issues/2787 | `UNFIXED` |
| 1104960974 | Bedrock godbridging block doesn’t affect blocks placed on the sides | https://github.com/GeyserMC/Geyser/issues/2769 | `FIXED` |
| 1104122871 | Tooltips are automatically turned off while rejoining | https://github.com/GeyserMC/Geyser/issues/2768 | `NOT_APPLICABLE` |
| 1068005488 | Geyser doesn't like old-style skin files | https://github.com/GeyserMC/Geyser/issues/2658 | `NOT_APPLICABLE` |
| 1052791662 | Xbox achievements not unlocking properly with Geyser | https://github.com/GeyserMC/Geyser/issues/2629 | `UNSUPPORTED` |
| 988382023 | Move mappings to config files | https://github.com/GeyserMC/Geyser/issues/2514 | `NOT_APPLICABLE` |
| 976467865 | Other players' boats have very jerky movement | https://github.com/GeyserMC/Geyser/issues/2490 | `FIXED` |
| 945607347 | Content log spam on server join/switch worlds & command suggestions not working ([Commands][error]-Keywords in commands need to be lower case. '<argument>' was not registered.) | https://github.com/GeyserMC/Geyser/issues/2385 | `UPSTREAM_FIXED` |
| 938291459 | End crystals don't move when pushed by pistons | https://github.com/GeyserMC/Geyser/issues/2358 | `FIXED` |
| 869272079 | Bedrock clients are unable to crawl when using the GSit plugin | https://github.com/GeyserMC/Geyser/issues/2180 | `UNSUPPORTED` |
| 809712905 | Itemframe Placing Bug | https://github.com/GeyserMC/Geyser/issues/1938 | `UNSUPPORTED` |
| 805141067 | Infinite up movement while elytra flying in gamemode creative | https://github.com/GeyserMC/Geyser/issues/1916 | `UNSUPPORTED` |
| 802261469 | Standing against a wall and placing a door inside of yourself causes issues | https://github.com/GeyserMC/Geyser/issues/1902 | `FIXED` |
| 789551268 | Hide enchants attribute doesn't work | https://github.com/GeyserMC/Geyser/issues/1852 | `UNSUPPORTED` |
| 777380397 | Holding shift while planting crops causes the food to be eaten | https://github.com/GeyserMC/Geyser/issues/1782 | `UNFIXED` |
| 775688478 | Support for 1.16 custom biomes | https://github.com/GeyserMC/Geyser/issues/1752 | `UNFIXED` |
| 633624088 | Splitscreen support | https://github.com/GeyserMC/Geyser/issues/750 | `FIXED` |

### Open pull requests

_(57 records)_

| ID | Title | URL | Status |
| ---: | --- | --- | --- |
| 5467968815 | Fix heterogeneous list hashing | https://github.com/GeyserMC/Geyser/pull/6938 | `FIXED` |
| 5446883801 | Improve functionality of the Nether roof workaround | https://github.com/GeyserMC/Geyser/pull/6936 | `FIXED` |
| 5398766503 | Fix: include Git SHA in Spigot plugin version | https://github.com/GeyserMC/Geyser/pull/6931 | `FIXED` |
| 5387806158 | Fill server-side command suggestions on Bedrock | https://github.com/GeyserMC/Geyser/pull/6928 | `FIXED` |
| 5368363595 | Keep the name tag of entities that only carry text displays | https://github.com/GeyserMC/Geyser/pull/6925 | `FIXED` |
| 5360752965 | Introduce `.geyserpack` loading | https://github.com/GeyserMC/Geyser/pull/6922 | `FIXED` |
| 5330888326 | FIX synchronize item frames after teleportation | https://github.com/GeyserMC/Geyser/pull/6921 | `FIXED` |
| 5295765293 | Register vanilla tools as custom items to fix block breaking times | https://github.com/GeyserMC/Geyser/pull/6916 | `FIXED` |
| 5286632827 | build(deps): use the MCProtocolLib codec fixes build | https://github.com/GeyserMC/Geyser/pull/6914 | `BLOCKED` |
| 5275355313 | Match Vanilla styles in custom names and lore to fully normalize the text component | https://github.com/GeyserMC/Geyser/pull/6909 | `DUPLICATE` |
| 5233348431 | Add texture variation and alpha masked tint to material instance API | https://github.com/GeyserMC/Geyser/pull/6905 | `FIXED` |
| 5216900616 | Add culling to custom block geometry component | https://github.com/GeyserMC/Geyser/pull/6902 | `FIXED` |
| 5205909839 | Fix Xbox 26.60 LAN discovery and joins | https://github.com/GeyserMC/Geyser/pull/6896 | `FIXED` |
| 5194626275 | Improved debugging information in Geyser dumps | https://github.com/GeyserMC/Geyser/pull/6890 | `FIXED` |
| 5176617470 | Fix destruction stages | https://github.com/GeyserMC/Geyser/pull/6883 | `FIXED` |
| 5130807082 | Debugging NetherNet issues | https://github.com/GeyserMC/Geyser/pull/6873 | `DUPLICATE` |
| 5112693462 | Load custom mappings from urls and extensions | https://github.com/GeyserMC/Geyser/pull/6866 | `FIXED` |
| 5112427414 | Add optional tooltip style markers for resource packs | https://github.com/GeyserMC/Geyser/pull/6863 | `FIXED` |
| 5062483162 | Add ip-discovery, for servers where STUN cannot find the public address | https://github.com/GeyserMC/Geyser/pull/6844 | `FIXED` |
| 5053287223 | Apply server-sided Dolphin's Grace | https://github.com/GeyserMC/Geyser/pull/6840 | `FIXED` |
| 5035241143 | Improve translation of commands | https://github.com/GeyserMC/Geyser/pull/6833 | `FIXED` |
| 5023900867 | Custom biome API, mappings, and generated Bedrock visuals | https://github.com/GeyserMC/Geyser/pull/6829 | `DUPLICATE` |
| 4943303365 | ViaProxy: don't fail IP passthrough when a downstream session has no local address yet | https://github.com/GeyserMC/Geyser/pull/6812 | `FIXED` |
| 4938616313 | Implement abstract model for sharing data across sessions | https://github.com/GeyserMC/Geyser/pull/6811 | `FIXED` |
| 4842813446 | Implement Text Displays via Primitive Text | https://github.com/GeyserMC/Geyser/pull/6768 | `FIXED` |
| 4805839353 | Fix custom tridents and spears | https://github.com/GeyserMC/Geyser/pull/6759 | `FIXED` |
| 4744708298 | fix: prevent NPE when attacking Ender Dragon parts | https://github.com/GeyserMC/Geyser/pull/6748 | `FIXED` |
| 4569560222 | Fix collision loading and addon-pack flags for non-vanilla blocks | https://github.com/GeyserMC/Geyser/pull/6724 | `FIXED` |
| 4540681458 | Fix client-side waterlogging prediction on doors and non-waterloggable blocks | https://github.com/GeyserMC/Geyser/pull/6706 | `FIXED` |
| 4496366694 | Lava fishing workaround | https://github.com/GeyserMC/Geyser/pull/6694 | `FIXED` |
| 4476271563 | Add support for Npc Dialogues | https://github.com/GeyserMC/Geyser/pull/6690 | `FIXED` |
| 4457015299 | Xbox split-screen support, reworked onto master and fixed on hardware | https://github.com/GeyserMC/Geyser/pull/6689 | `FIXED` |
| 4440438587 | Move recipe shift clicking to use ServerboundPlaceRecipePacket | https://github.com/GeyserMC/Geyser/pull/6686 | `FIXED` |
| 4345263370 | Avoid have no response when trading using touch or the Shift key cause by client bug since v1.21.30 | https://github.com/GeyserMC/Geyser/pull/6667 | `FIXED` |
| 4336073923 | Improve `manifest.json` search loop | https://github.com/GeyserMC/Geyser/pull/6664 | `FIXED` |
| 4286226682 | Add model-specific custom block item overrides support | https://github.com/GeyserMC/Geyser/pull/6643 | `FIXED` |
| 4258541122 | Fix: Bound the Bedrock projection of oversized books | https://github.com/GeyserMC/Geyser/pull/6627 | `FIXED` |
| 4258382559 | Fix: Let real blocks take priority over item frames | https://github.com/GeyserMC/Geyser/pull/6626 | `FIXED` |
| 4234891940 | Add extensible server-authoritative custom block breaking | https://github.com/GeyserMC/Geyser/pull/6605 | `FIXED` |
| 4203696760 | Introduce useful debug command | https://github.com/GeyserMC/Geyser/pull/6586 | `FIXED` |
| 4172942473 | Fix geyser connection timeout caused by translation expansion | https://github.com/GeyserMC/Geyser/pull/6579 | `FIXED` |
| 4170224258 | Download Java resourcepacks sent by the server, and load translations from them | https://github.com/GeyserMC/Geyser/pull/6578 | `FIXED` |
| 4150378049 | Refactor WebUtils to make use of modern HTTP APIs in Java | https://github.com/GeyserMC/Geyser/pull/6573 | `FIXED` |
| 4111042967 | Interpolate per-tick entity movement on Bedrock | https://github.com/GeyserMC/Geyser/pull/6566 | `NOT_APPLICABLE` |
| 4083854339 | Improve bamboo collision | https://github.com/GeyserMC/Geyser/pull/6560 | `FIXED` |
| 4078328957 | Fix collision for copper bars and turtle eggs | https://github.com/GeyserMC/Geyser/pull/6555 | `FIXED` |
| 4008215209 | Change logger to a static reference and add some debugging commands | https://github.com/GeyserMC/Geyser/pull/6513 | `NOT_APPLICABLE` |
| 3948152368 | Attempt to lower scoreboard allocs | https://github.com/GeyserMC/Geyser/pull/6489 | `FIXED` |
| 3707133775 | Fix block breaking | https://github.com/GeyserMC/Geyser/pull/6419 | `FIXED` |
| 3662155537 | Add proper combat sounds | https://github.com/GeyserMC/Geyser/pull/6393 | `FIXED` |
| 3142621945 | Feature: Reloadable Mappings | https://github.com/GeyserMC/Geyser/pull/6081 | `FIXED` |
| 3044136529 | Workaround for client-sided removal of all items from the crafting grid on inventory close | https://github.com/GeyserMC/Geyser/pull/6008 | `FIXED` |
| 2875767111 | Custom Translation API | https://github.com/GeyserMC/Geyser/pull/5860 | `FIXED` |
| 2670923346 | Full Java tooltip parity (as much as that is possible) | https://github.com/GeyserMC/Geyser/pull/5692 | `PARTIAL` |
| 2662538702 | Geyser Networking API | https://github.com/GeyserMC/Geyser/pull/5685 | `FIXED` |
| 2593566343 | Anvil updater refactor and other small refactors | https://github.com/GeyserMC/Geyser/pull/5595 | `FIXED` |
| 2047932455 | Support for max stack size | https://github.com/GeyserMC/Geyser/pull/5011 | `FIXED` |
