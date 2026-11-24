# ArkFlame/Geyser — Upstream Issue/PR Tracker

_Generated at: 2026-11-23T15:00:40Z_

This tracker seeds the fork audit campaign by enumerating every issue and pull request in `GeyserMC/Geyser` (upstream) and `ArkFlame/Geyser` (fork). The machine-readable source of truth is [`arkflame-geyser-tracker.json`](arkflame-geyser-tracker.json); this document is a human-readable summary.

## Dynamically discovered counts

| Repo | Type | Open | Closed (unmerged) | Merged | Total |
| --- | --- | ---: | ---: | ---: | ---: |
| ArkFlame/Geyser | issue | 0 | 0 | 0 | 0 |
| ArkFlame/Geyser | pr | 0 | 0 | 0 | 0 |
| **ArkFlame/Geyser** | **all** | | | | **0** |
| GeyserMC/Geyser | issue | 291 | 4268 | 0 | 4559 |
| GeyserMC/Geyser | pr | 56 | 442 | 1760 | 2258 |
| **GeyserMC/Geyser** | **all** | | | | **6817** |
| **ALL REPOS** | **issue** | 291 | 4268 | 0 | 4559 |
| **ALL REPOS** | **pr** | 56 | 442 | 1760 | 2258 |
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

## Aggregate status counts

| Status | Count |
| --- | ---: |
| `UNFIXED` | 3276 |
| `IN_PROGRESS` | 0 |
| `FIXED` | 61 |
| `UPSTREAM_FIXED` | 2349 |
| `DUPLICATE` | 388 |
| `BLOCKED` | 10 |
| `UNSUPPORTED` | 299 |
| `NOT_APPLICABLE` | 434 |

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

This pass did not audit the bulk of the inventory. Bulk closed issues and closed-unmerged PRs remain `UNFIXED`/unaudited and are retained only in the JSON. The actionable focus of this campaign is the 347-item open queue (291 open issues plus 56 open PRs). Worker patches have since been integrated into the canonical branch; see Final integration state below. A compile-only CI build passed, but no runtime verification has occurred and no fix in this tracker is claimed as runtime-verified.

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

Wave-7 re-verified closed upstream records and one closed PR against the integrated source on `integration/final-release` @ `8e90a388cca4d93c4f3ba5894de4227d31b2f2bb`. Cited fix commits were checked as ancestors of that HEAD with `git merge-base --is-ancestor`. This is a documentation-only change: no source fix was introduced and no build or test was run. The CI build at `8e90a388` was still running when this was recorded, so no build result is claimed and no runtime or gameplay verification is asserted for any disposition. Reference: https://github.com/GeyserMC/Geyser/issues/6903.

| ID | Disposition | Evidence / reference |
| ---: | --- | --- |
| #6857 | `FIXED` | Integrated at `8e90a388cca4d93c4f3ba5894de4227d31b2f2bb` — soft enums for mixed-case command literals, porting upstream #6619; PR #6857 ('Sync 6619') was closed by its author as a mistake, so the fix never landed through the PR. CI running at that HEAD, no result claimed — https://github.com/GeyserMC/Geyser/pull/6857 , https://github.com/GeyserMC/Geyser/pull/6619 |
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

## Priority queue — all OPEN issues and OPEN PRs

These are the records that still need triage or an adoption decision. Closed/merged records are in the JSON file.

### Open issues

_(291 records)_

| ID | Title | URL | Status |
| ---: | --- | --- | --- |
| 6491152696 | Item names are italicised for bedrock players | https://github.com/GeyserMC/Geyser/issues/6929 | `FIXED` |
| 6478673359 | Bedrock player moves extremely fast after a cross-world teleport until relog | https://github.com/GeyserMC/Geyser/issues/6927 | `DUPLICATE` |
| 6449358192 | NetherNet Doesn't Work on FreeBSD | https://github.com/GeyserMC/Geyser/issues/6923 | `UNFIXED` |
| 6413008617 | [NetherNet] Native handles and GeyserSession objects are never released after Bedrock players disconnect (Velocity, Windows) – ~90 handles + ~7–11 MB leaked per connection | https://github.com/GeyserMC/Geyser/issues/6920 | `UNFIXED` |
| 6398503486 | Regression of #6834 in b1302: players joining through a signaling relay (MCXboxBroadcast) get the relay's IP again | https://github.com/GeyserMC/Geyser/issues/6919 | `DUPLICATE` |
| 6392359368 | Bedrock 26.6x (NetherNet): tilling grass/dirt with a hoe is reverted, farmland never reaches the Java server | https://github.com/GeyserMC/Geyser/issues/6918 | `UNFIXED` |
| 6363398043 | Nethernet and Playit: Connecting via an external IPv4 address and receiving a local IPv4 address. | https://github.com/GeyserMC/Geyser/issues/6915 | `UNFIXED` |
| 6353841610 | Bedrock Client Cannot Connect (End of Stream) | https://github.com/GeyserMC/Geyser/issues/6913 | `UNFIXED` |
| 6280416949 | Bedrock players experience abnormally fast movement speed after changing worlds or reconnecting | https://github.com/GeyserMC/Geyser/issues/6903 | `FIXED` |
| 6274805438 | bedrock player can fast place glowstone when keep holding place button after looking away from a respawn anchor | https://github.com/GeyserMC/Geyser/issues/6901 | `FIXED` |
| 6265656032 | Bedrock players are joining via ipv6 addresses | https://github.com/GeyserMC/Geyser/issues/6899 | `BLOCKED` |
| 6256269328 | I am getting kicked from the server (Bedrock Edition). | https://github.com/GeyserMC/Geyser/issues/6894 | `UNFIXED` |
| 6256027037 | EncoderException "MessageToMessageCodec$2 must produce at least one message" kicks Bedrock players on Velocity (since 2026-11-06, also on b1288) | https://github.com/GeyserMC/Geyser/issues/6893 | `FIXED` |
| 6229661892 | Bedrock players in a boat desync at a static world border (Java players unaffected) | https://github.com/GeyserMC/Geyser/issues/6884 | `UNFIXED` |
| 6222127447 | Accept PROXY protocol v1 on webrtc-proxy-protocol-relays (or document that only v2 is supported) | https://github.com/GeyserMC/Geyser/issues/6881 | `UNFIXED` |
| 6201847840 | Text display entities show through walls | https://github.com/GeyserMC/Geyser/issues/6876 | `FIXED` |
| 6145901311 | GUI Closes on Menu Transfer... sometimes. | https://github.com/GeyserMC/Geyser/issues/6858 | `UNFIXED` |
| 6138669848 | Very high latency, missing block drops and lag when bedrock players join fabric server. | https://github.com/GeyserMC/Geyser/issues/6854 | `FIXED` |
| 6134779753 | Geyser servers don't appear under Friends > LAN Games for 26.60 clients (NetherNet LAN discovery on UDP 7551 is never started) | https://github.com/GeyserMC/Geyser/issues/6853 | `FIXED` |
| 6133451417 | Bedrock 26.60 camera/input delay on Anarchy backend through Velocity; Hub works normally | https://github.com/GeyserMC/Geyser/issues/6851 | `UNFIXED` |
| 6132388000 | NetherNet seems to kick some bedrock players at seemingly random intervals | https://github.com/GeyserMC/Geyser/issues/6850 | `UNFIXED` |
| 6132001962 | Flickering Bug for bedrock | https://github.com/GeyserMC/Geyser/issues/6849 | `UNFIXED` |
| 6131252494 | Geyser doesn't work on bionic c library | https://github.com/GeyserMC/Geyser/issues/6848 | `FIXED` |
| 6084617421 | Velocity MOTD showing instead of the passthrough from Paper | https://github.com/GeyserMC/Geyser/issues/6842 | `UNFIXED` |
| 6082502683 | NetherNet: IPv4-bound Geyser logs "juice: Send failed, errno=97" WARNs for clients' IPv6 ICE candidates | https://github.com/GeyserMC/Geyser/issues/6841 | `UNSUPPORTED` |
| 6049372589 | Add a dump option to indicate whether NetherNet/signaling is being used by another component. | https://github.com/GeyserMC/Geyser/issues/6831 | `FIXED` |
| 6048759025 | Text Displays appear way higher than they should on Bedrock Edition | https://github.com/GeyserMC/Geyser/issues/6830 | `FIXED` |
| 6012421771 | NetherNet DLL blocked by Windows Smart App Control on Windows 11 | https://github.com/GeyserMC/Geyser/issues/6822 | `UNSUPPORTED` |
| 5958150713 | Wierd Itemswap/Item deletion in Creative Mode | https://github.com/GeyserMC/Geyser/issues/6813 | `UNFIXED` |
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
| 5249345569 | Taking, discarding and moving items | https://github.com/GeyserMC/Geyser/issues/6677 | `UNFIXED` |
| 5247497208 | Server-side Entity Culling | https://github.com/GeyserMC/Geyser/issues/6676 | `UNSUPPORTED` |
| 5244501862 | Bedrock player skin not showing on Java - "Ignoring player info update" logged for Floodgate players | https://github.com/GeyserMC/Geyser/issues/6675 | `FIXED` |
| 5242221308 | Stopsound command fix | https://github.com/GeyserMC/Geyser/issues/6674 | `UNSUPPORTED` |
| 5234500656 | Sudden Speedmissmatch of Donkeys (Probably Horses too) when using Swiftness  potions | https://github.com/GeyserMC/Geyser/issues/6670 | `UNFIXED` |
| 5233488856 | Hopper minecart appears as furnace minecart on Bedrock (Geyser 2.11.2-b1230) | https://github.com/GeyserMC/Geyser/issues/6668 | `DUPLICATE` |
| 5219153527 | Resource packs zipped on MacOS do not read properly | https://github.com/GeyserMC/Geyser/issues/6663 | `FIXED` |
| 5285638522 | [Bedrock] GUI clicks became unreliable after Geyser b1225 | https://github.com/GeyserMC/Geyser/issues/6683 | `FIXED` |
| 5217173715 | BetterView causes chunks to start flickering on bedrock | https://github.com/GeyserMC/Geyser/issues/6661 | `UNSUPPORTED` |
| 5211481247 | All Bedrocks skins do not show for Java clients | https://github.com/GeyserMC/Geyser/issues/6659 | `DUPLICATE` |
| 5192944394 | ItemStack rejected without cause | https://github.com/GeyserMC/Geyser/issues/6653 | `UNFIXED` |
| 5186978565 | Paper Anti-Xray engine-mode 2 causes Bedrock clientbound updates to freeze and eventually time out | https://github.com/GeyserMC/Geyser/issues/6650 | `UNFIXED` |
| 5184877875 | There no punching Animation in 1st Person when using animation packs | https://github.com/GeyserMC/Geyser/issues/6649 | `UNSUPPORTED` |
| 5176721889 | Bedrock GUI buttons sometimes trigger the wrong button after Geyser update | https://github.com/GeyserMC/Geyser/issues/6648 | `UNFIXED` |
| 5164064477 | Pearl catch issue | https://github.com/GeyserMC/Geyser/issues/6646 | `NOT_APPLICABLE` |
| 5157745642 | Chunk loading issue | https://github.com/GeyserMC/Geyser/issues/6641 | `DUPLICATE` |
| 5157629372 | Buggy inventory and crafting | https://github.com/GeyserMC/Geyser/issues/6640 | `UNSUPPORTED` |
| 5156275649 | Sprites in quick actions components cause error | https://github.com/GeyserMC/Geyser/issues/6639 | `UPSTREAM_FIXED` |
| 5144259158 | Bedrock players cannot break vanilla blocks after Geyser update | https://github.com/GeyserMC/Geyser/issues/6637 | `UNSUPPORTED` |
| 5141425466 | Add container_empty predicate for Geyser Custom Items V2 | https://github.com/GeyserMC/Geyser/issues/6634 | `UNFIXED` |
| 5135599202 | Hydraulic and Consoles: A small but no doubtfully painful to fix bug | https://github.com/GeyserMC/Geyser/issues/6632 | `UNFIXED` |
| 5091246493 | Custom Entity API non-vanilla entities | https://github.com/GeyserMC/Geyser/issues/6598 | `NOT_APPLICABLE` |
| 5084497235 | Bedrock mobile users either unable to join or crashing immediately. | https://github.com/GeyserMC/Geyser/issues/6596 | `UNFIXED` |
| 5072539239 | Ty-el's Vietnamese Language Pack not work with GeyserMc | https://github.com/GeyserMC/Geyser/issues/6592 | `UNSUPPORTED` |
| 5061576088 | Bedroack cant load chunks | https://github.com/GeyserMC/Geyser/issues/6587 | `UNFIXED` |
| 5055981657 | /fly toggled on causing swimming and block breaking issues | https://github.com/GeyserMC/Geyser/issues/6584 | `UNFIXED` |
| 5040716111 | Players at Y 64 get inexplicably stuck | https://github.com/GeyserMC/Geyser/issues/6580 | `UNFIXED` |
| 5011405882 | Crafting Crash - Book & Quill / Book | https://github.com/GeyserMC/Geyser/issues/6577 | `UNFIXED` |
| 5001803063 | Bedrock chat not visible to Java players & Bedrock skins show as Steve/Alex | https://github.com/GeyserMC/Geyser/issues/6574 | `UNFIXED` |
| 4979519119 | Click register delay | https://github.com/GeyserMC/Geyser/issues/6570 | `FIXED` |
| 4969896275 | Bedrock Players left incapacitated by ImageFrame maps | https://github.com/GeyserMC/Geyser/issues/6569 | `UNFIXED` |
| 4953133141 | ViaProxy: first join via NetherNet relay times out at ~19s (rejoin works); unguarded getLocalAddress() NPE in GeyserViaProxyPlugin channel-init | https://github.com/GeyserMC/Geyser/issues/6567 | `FIXED` |
| 4934805024 | Recipe book auto-craft reverts inventory / returns ingredients instead of crafted item | https://github.com/GeyserMC/Geyser/issues/6563 | `DUPLICATE` |
| 4915679666 | Cant see Skins | https://github.com/GeyserMC/Geyser/issues/6558 | `UPSTREAM_FIXED` |
| 4915592951 | Possible regression with "forward-player-ping" causing Bedrock timeouts after long teleports | https://github.com/GeyserMC/Geyser/issues/6557 | `BLOCKED` |
| 4915065150 | Unable to wear armor after crafting | https://github.com/GeyserMC/Geyser/issues/6556 | `BLOCKED` |
| 4902059744 | Movement of non-living entities (Display entities) isn't interpolated, causing visible drag for anything riding one | https://github.com/GeyserMC/Geyser/issues/6551 | `FIXED` |
| 4889086403 | Update bedrock limiations to include attributes | https://github.com/GeyserMC/Geyser/issues/6544 | `NOT_APPLICABLE` |
| 4868140574 | Allow custom Bedrock item identifiers in horse armor GUI acceptedItems | https://github.com/GeyserMC/Geyser/issues/6535 | `FIXED` |
| 4866786667 | Players kicked while right-click on block | https://github.com/GeyserMC/Geyser/issues/6532 | `UNSUPPORTED` |
| 4859485425 | enchant table cannot be used | https://github.com/GeyserMC/Geyser/issues/6527 | `UNFIXED` |
| 4819367010 | Bedrock Worlds tab server entry disappears or connects to wrong port behind NAT port remapping | https://github.com/GeyserMC/Geyser/issues/6508 | `UNFIXED` |
| 4787423496 | Dialog buttons with object atlas sprite labels should map to FormImage.Type.PATH on Bedrock, but currently error with Expected tag to be a literal string... and render blank. | https://github.com/GeyserMC/Geyser/issues/6499 | `UNSUPPORTED` |
| 4776889765 | Failed to correctly map the zombie villager's profession | https://github.com/GeyserMC/Geyser/issues/6495 | `UNFIXED` |
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
| 4427799377 | Floating Items during Crafting for  some BEDROCK PLAYERS | https://github.com/GeyserMC/Geyser/issues/6398 | `UNFIXED` |
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
| 4186822367 | Bedrock clients are crashing while somebody has a custom armor with armor trim equipped | https://github.com/GeyserMC/Geyser/issues/6278 | `UNFIXED` |
| 4154311005 | Custom item attirbutes do not work proplerly on bedrock edition | https://github.com/GeyserMC/Geyser/issues/6266 | `UNSUPPORTED` |
| 4113646890 | Bedrock Spectator Mode not working as intended. | https://github.com/GeyserMC/Geyser/issues/6248 | `FIXED` |
| 4110465428 | Title: Bedrock recipe book auto-crafting does not work correctly through Geyser | https://github.com/GeyserMC/Geyser/issues/6246 | `UNFIXED` |
| 4079217873 | Dolphin Tunnels not working for bedrock players | https://github.com/GeyserMC/Geyser/issues/6239 | `UPSTREAM_FIXED` |
| 4041295810 | signing writable books if they have NBT does not work | https://github.com/GeyserMC/Geyser/issues/6224 | `UNFIXED` |
| 4005465880 | PS5 Bedrock Edition Crashing consistently | https://github.com/GeyserMC/Geyser/issues/6213 | `UNSUPPORTED` |
| 3997506286 | Players using Bedrock Edition on mobile devices are unable to enchant items or books. | https://github.com/GeyserMC/Geyser/issues/6210 | `UNFIXED` |
| 3973938805 | Lore issue | https://github.com/GeyserMC/Geyser/issues/6202 | `UNFIXED` |
| 3897906890 | [Towny] geyser bedrock players can attack by quickly breaking blocks and attacking in pvp disabled town | https://github.com/GeyserMC/Geyser/issues/6159 | `UNFIXED` |
| 3864556632 | Add an option for geyser to temporary solve "Not receiving chunk" | https://github.com/GeyserMC/Geyser/issues/6142 | `UNFIXED` |
| 3851074699 | Can't Bedrock GUI | https://github.com/GeyserMC/Geyser/issues/6134 | `UNFIXED` |
| 3844057698 | Client crash/freeze when entering area with many mobs/animals/entities | https://github.com/GeyserMC/Geyser/issues/6129 | `UNFIXED` |
| 3841214418 | [Bug Report] ArtMap used to work properly but no longer accurate. | https://github.com/GeyserMC/Geyser/issues/6128 | `UNFIXED` |
| 3824109144 | Enchant table is not working | https://github.com/GeyserMC/Geyser/issues/6116 | `BLOCKED` |
| 3823915450 | Player head texture occasionally not updated | https://github.com/GeyserMC/Geyser/issues/6115 | `UNFIXED` |
| 3823874351 | Items stuck in cursor after furnace interaction | https://github.com/GeyserMC/Geyser/issues/6113 | `UNFIXED` |
| 3798882794 | Players can't connect if they have an oversize book in their inventory | https://github.com/GeyserMC/Geyser/issues/6098 | `FIXED` |
| 3795623377 | Blocks don't break when Tools have CustomModel | https://github.com/GeyserMC/Geyser/issues/6097 | `UNFIXED` |
| 3790893570 | Custom blocks under state_overrides with different block identifiers | https://github.com/GeyserMC/Geyser/issues/6092 | `UNFIXED` |
| 3781719547 | Skin and cape data API as BufferedImage | https://github.com/GeyserMC/Geyser/issues/6086 | `NOT_APPLICABLE` |
| 3773728835 | Bedrock is very buggy | https://github.com/GeyserMC/Geyser/issues/6079 | `UNFIXED` |
| 3771522889 | Nintendo Switch 1 OLED Game Crash on Join | https://github.com/GeyserMC/Geyser/issues/6078 | `UNFIXED` |
| 3760739627 | Custom mapped (note)blocks don't render their texture/model if they're a falling block | https://github.com/GeyserMC/Geyser/issues/6072 | `UNFIXED` |
| 3720071081 | Add the SHA hash when outputting version | https://github.com/GeyserMC/Geyser/issues/6037 | `FIXED` |
| 3717137044 | Copper Bars craft into Trap Doors on Bedrock (iOS) | https://github.com/GeyserMC/Geyser/issues/6032 | `UNFIXED` |
| 3708933008 | Nintendo Switch players and Pack file size | https://github.com/GeyserMC/Geyser/issues/6023 | `UNSUPPORTED` |
| 3700997392 | Support for proper tooltip display of unsafe enchantments | https://github.com/GeyserMC/Geyser/issues/6020 | `UNSUPPORTED` |
| 3690401445 | Support registering extension commands without a root command | https://github.com/GeyserMC/Geyser/issues/6018 | `UNFIXED` |
| 3675577904 | Bedrock has lag but not java. | https://github.com/GeyserMC/Geyser/issues/6013 | `UNFIXED` |
| 3661063687 | A clearer world boundary for bedrock edition players. | https://github.com/GeyserMC/Geyser/issues/6006 | `UNFIXED` |
| 3618783248 | Not properly calculating custom tool break speed | https://github.com/GeyserMC/Geyser/issues/5982 | `UNFIXED` |
| 3618358473 | Weak hits, Knockback doesn't push hard as java! 1.8.9 pvp. | https://github.com/GeyserMC/Geyser/issues/5981 | `UNFIXED` |
| 3603900841 | Crafting Issues and Villager Trading bug(ΒΕ) | https://github.com/GeyserMC/Geyser/issues/5974 | `UNFIXED` |
| 3603774586 | Practicles looping spamming + hit sounds  after bedrock player dies | https://github.com/GeyserMC/Geyser/issues/5973 | `UNFIXED` |
| 3530206270 | armorstand mount is bugged on bedrock | https://github.com/GeyserMC/Geyser/issues/5917 | `UNFIXED` |
| 3529484524 | Blocks disappearing for java players | https://github.com/GeyserMC/Geyser/issues/5914 | `UNFIXED` |
| 3526305160 | Bedrock pov does not update when controlling mounted mobs (MythicMobs/Model Engine) | https://github.com/GeyserMC/Geyser/issues/5911 | `UNFIXED` |
| 3523810929 | Blocks don't place properly | https://github.com/GeyserMC/Geyser/issues/5910 | `UNFIXED` |
| 3523399664 | Convert Blocks into skulls For hat wearing helmet | https://github.com/GeyserMC/Geyser/issues/5909 | `UNFIXED` |
| 3521350341 | Player Heads Glitching as Steve + Steve Entity | https://github.com/GeyserMC/Geyser/issues/5907 | `BLOCKED` |
| 3521287921 | Mining speed difference between custom textured shovel and normal shovel | https://github.com/GeyserMC/Geyser/issues/5906 | `UNFIXED` |
| 3521263925 | Uncrafting Netherite Block to Ingots not working (Minor Bug) | https://github.com/GeyserMC/Geyser/issues/5905 | `UNFIXED` |
| 3489885492 | Command Description Bug | https://github.com/GeyserMC/Geyser/issues/5886 | `UNFIXED` |
| 3484828649 | anvil bug | https://github.com/GeyserMC/Geyser/issues/5879 | `UNFIXED` |
| 3481725971 | iOS Bedrock crash on specific GeyserMC server | https://github.com/GeyserMC/Geyser/issues/5873 | `UNFIXED` |
| 3478851061 | Disconnect when a player has a large number of advancements | https://github.com/GeyserMC/Geyser/issues/5869 | `UNFIXED` |
| 3465131333 | Inaccurate Y-Axis motion when falling/jumping flagging anticheats. | https://github.com/GeyserMC/Geyser/issues/5852 | `UNSUPPORTED` |
| 3462668505 | Mining with pickaxe opens menu when i left click on mobile | https://github.com/GeyserMC/Geyser/issues/5851 | `UNFIXED` |
| 3456191420 | Bedrock players mine faster than Java when spam clicking | https://github.com/GeyserMC/Geyser/issues/5843 | `UNFIXED` |
| 3432207558 | 1 out of 2 PS5 Players on same Network getting disconnected | https://github.com/GeyserMC/Geyser/issues/5830 | `UNFIXED` |
| 3374925969 | 2 Bug on Bedrock Client (maybe only IOS User, im using iPad) | https://github.com/GeyserMC/Geyser/issues/5793 | `UNFIXED` |
| 3353283339 | Sometimes Minecraft Bedrock crashes while driving happy ghasts | https://github.com/GeyserMC/Geyser/issues/5783 | `UNFIXED` |
| 3338574194 | Problem in Achievements translation | https://github.com/GeyserMC/Geyser/issues/5770 | `UNFIXED` |
| 3322757341 | The player's movements are strange. | https://github.com/GeyserMC/Geyser/issues/5755 | `UNFIXED` |
| 3305700003 | ghost doors | https://github.com/GeyserMC/Geyser/issues/5746 | `FIXED` |
| 3282457747 | show_in_tooltip Java component tag is not resolved properly | https://github.com/GeyserMC/Geyser/issues/5736 | `UNFIXED` |
| 3206648842 | Falling Blocks `setVelocity` doesn't work | https://github.com/GeyserMC/Geyser/issues/5655 | `UNSUPPORTED` |
| 3190235250 | Bedrock Edition 1.21.81 Cannot Interact with Fake Players Created by /bot or Use Custom Items from Holoprint (Leaves 1.21.4 + Geyser 2.8.0-b867; Breaks Holoprint Functionality) | https://github.com/GeyserMC/Geyser/issues/5634 | `UNFIXED` |
| 3166049718 | Unable to craft firework rockets on Pocket Edition | https://github.com/GeyserMC/Geyser/issues/5611 | `UNFIXED` |
| 3149044216 | Hand animations and rendering issues with Actions & Stuff | https://github.com/GeyserMC/Geyser/issues/5597 | `UNFIXED` |
| 3133351765 | Geyser - Anvil Works different on Java/Bedrock | https://github.com/GeyserMC/Geyser/issues/5584 | `UNFIXED` |
| 3070695219 | Scale command not showing changed 1st person perspective on Bedrock | https://github.com/GeyserMC/Geyser/issues/5554 | `UNFIXED` |
| 3056086028 | Cannot use Fishing Rods with Custom Model Data | https://github.com/GeyserMC/Geyser/issues/5547 | `UNSUPPORTED` |
| 2995847786 | bedrock players get stuck in an infinite falling state if they get killed upon logout | https://github.com/GeyserMC/Geyser/issues/5489 | `BLOCKED` |
| 2984093096 | Chunks don't load after joining the server | https://github.com/GeyserMC/Geyser/issues/5474 | `UNFIXED` |
| 2974939937 | Can't move items with custom tags | https://github.com/GeyserMC/Geyser/issues/5464 | `UNFIXED` |
| 2958634184 | Players getting kicked | https://github.com/GeyserMC/Geyser/issues/5444 | `UNFIXED` |
| 2948344051 | chunks do not go and the player falls constantly down to -200 blocks and cannot do anything | https://github.com/GeyserMC/Geyser/issues/5431 | `UNFIXED` |
| 2920514585 | Working piston bolts not rendering correctly on bedrock | https://github.com/GeyserMC/Geyser/issues/5408 | `UNSUPPORTED` |
| 2908555375 | Custom Player Head Misalignment | https://github.com/GeyserMC/Geyser/issues/5404 | `UNFIXED` |
| 2888772644 | Not receiving chunk | https://github.com/GeyserMC/Geyser/issues/5386 | `UNFIXED` |
| 2878608332 | Hardcore Hearts | https://github.com/GeyserMC/Geyser/issues/5373 | `UNFIXED` |
| 2875732073 | Players disconnected after teleportation | https://github.com/GeyserMC/Geyser/issues/5371 | `UNFIXED` |
| 2857247988 | Bedrock players experience extreme lag when crafting using crafting table | https://github.com/GeyserMC/Geyser/issues/5350 | `UNFIXED` |
| 2828733774 | Item flags on bedrock items don't hide properly | https://github.com/GeyserMC/Geyser/issues/5319 | `UNFIXED` |
| 2827479445 | Support Open-Directorys for resource packs | https://github.com/GeyserMC/Geyser/issues/5318 | `UNFIXED` |
| 2776467193 | Not all maps display correctly | https://github.com/GeyserMC/Geyser/issues/5270 | `UNFIXED` |
| 2767079695 | InventoryClickEvent gets triggered multiple times when player inventory gets closed and crafting slots are full | https://github.com/GeyserMC/Geyser/issues/5258 | `FIXED` |
| 2763399238 | Bedrock Edition Animated Skins Not Showing | https://github.com/GeyserMC/Geyser/issues/5253 | `UNFIXED` |
| 2760504860 | Stonecutter and Mod GUI menu move item not working for PS4 Bedrock Users | https://github.com/GeyserMC/Geyser/issues/5246 | `UNFIXED` |
| 2743754360 | Bedrock players getting console spam "moved too quickly!" | https://github.com/GeyserMC/Geyser/issues/5231 | `UNFIXED` |
| 2739739899 | Bedrock player does not receive knockback when an entity with a negative explosion range affects the player. | https://github.com/GeyserMC/Geyser/issues/5227 | `UNFIXED` |
| 2724412280 | [API] ServerboundDiagnosticsPacket data | https://github.com/GeyserMC/Geyser/issues/5199 | `UNFIXED` |
| 2697961951 | Text preprocess / postprocess API | https://github.com/GeyserMC/Geyser/issues/5166 | `UNFIXED` |
| 2681347646 | egg throwing on bedrock is too low pitch | https://github.com/GeyserMC/Geyser/issues/5159 | `UNSUPPORTED` |
| 2660360181 | Bedrock mining advantage | https://github.com/GeyserMC/Geyser/issues/5152 | `UNFIXED` |
| 2634025004 | Lead links sometimes don't appear. | https://github.com/GeyserMC/Geyser/issues/5123 | `UNFIXED` |
| 2573240553 | Bug with GSit /lay command on bedrock | https://github.com/GeyserMC/Geyser/issues/5073 | `DUPLICATE` |
| 2541243115 | Cannot move player correctly, cannot interact with things | https://github.com/GeyserMC/Geyser/issues/5057 | `UNFIXED` |
| 2540190174 | Implement JFR profiler on the "geyser" command for standalone. | https://github.com/GeyserMC/Geyser/issues/5053 | `UNFIXED` |
| 2535701984 | Crafting issue | https://github.com/GeyserMC/Geyser/issues/5047 | `DUPLICATE` |
| 2525964971 | Custom Tools with attributes not working on bedrock  | https://github.com/GeyserMC/Geyser/issues/5037 | `UNFIXED` |
| 2512015492 | the shopkeeper plugin is not working | https://github.com/GeyserMC/Geyser/issues/5025 | `DUPLICATE` |
| 2508374022 | Character Creator Items Support for only Bedrock Players | https://github.com/GeyserMC/Geyser/issues/5020 | `UNFIXED` |
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
| 2255098084 | Signs dissapear after relogging | https://github.com/GeyserMC/Geyser/issues/4588 | `UNFIXED` |
| 2154934969 | Bedrock Classic Skin Wrapping Incorrectly on Model | https://github.com/GeyserMC/Geyser/issues/4464 | `UNFIXED` |
| 2146683715 | Weird Slow Crafting | https://github.com/GeyserMC/Geyser/issues/4453 | `UNFIXED` |
| 2097630743 | Player hand shaking | https://github.com/GeyserMC/Geyser/issues/4409 | `UNFIXED` |
| 2069382809 | Bedrock players getting "moved too quickly!" WARN using warps and TP to claim | https://github.com/GeyserMC/Geyser/issues/4383 | `UNFIXED` |
| 2061878718 | Elytra cape texture doesn't get displayed on Bedrock | https://github.com/GeyserMC/Geyser/issues/4374 | `UNFIXED` |
| 2054838682 | Custom Itemsadder shield blocking not working for bedrock players | https://github.com/GeyserMC/Geyser/issues/4359 | `UNFIXED` |
| 2022879416 | Some Player Head textures display white when placed | https://github.com/GeyserMC/Geyser/issues/4325 | `FIXED` |
| 2015861853 | You cannot place torches on containers while flying + sneaking | https://github.com/GeyserMC/Geyser/issues/4317 | `UNFIXED` |
| 2005161425 | ActionBar bounces up and down | https://github.com/GeyserMC/Geyser/issues/4304 | `UNFIXED` |
| 1978045936 | Teleported item frames not updating position on bedrock | https://github.com/GeyserMC/Geyser/issues/4274 | `UPSTREAM_FIXED` |
| 1964944357 | Auto update skins? | https://github.com/GeyserMC/Geyser/issues/4249 | `UNFIXED` |
| 1943675261 | A way to forward command descriptions in geyser standalone | https://github.com/GeyserMC/Geyser/issues/4215 | `UNFIXED` |
| 1931357359 | Bedrock players don't see skins with custom models | https://github.com/GeyserMC/Geyser/issues/4195 | `UNFIXED` |
| 1916653451 | Suggestion: make players who have invisibility temporarily visible when the glowing affect is applied. | https://github.com/GeyserMC/Geyser/issues/4156 | `UNFIXED` |
| 1888639047 | Lectern signal strength inconsistent with Java edition | https://github.com/GeyserMC/Geyser/issues/4113 | `UNFIXED` |
| 1884438284 | Villager trading on mobile disregards which trade you select, and always gives you the first trade. | https://github.com/GeyserMC/Geyser/issues/4102 | `UNFIXED` |
| 1873967017 | Trading doesn't seem to be working in the end only for geyser users. | https://github.com/GeyserMC/Geyser/issues/4092 | `UNFIXED` |
| 1870724269 | Allow items to map to custom blocks | https://github.com/GeyserMC/Geyser/issues/4090 | `UNFIXED` |
| 1853575554 | Dismounting Fake Entity | https://github.com/GeyserMC/Geyser/issues/4058 | `UNFIXED` |
| 1819258094 | FishHook on Lava with CustomFishing | https://github.com/GeyserMC/Geyser/issues/4012 | `UNSUPPORTED` |
| 1806744583 | Client crashes when starting to type a command | https://github.com/GeyserMC/Geyser/issues/3990 | `UNSUPPORTED` |
| 1805860584 | Unfair Advantage in Mining Speeds between Bedrock & Java | https://github.com/GeyserMC/Geyser/issues/3987 | `UNSUPPORTED` |
| 1788104735 | Fast Build with glass panes and other blocks broken but looks like it works | https://github.com/GeyserMC/Geyser/issues/3937 | `UNFIXED` |
| 1784729187 | Bedrock-Issue with close-event | https://github.com/GeyserMC/Geyser/issues/3932 | `FIXED` |
| 1778025057 | Unable to brush suspicious sand/gravel | https://github.com/GeyserMC/Geyser/issues/3921 | `UNFIXED` |
| 1772429544 | BE players can't see their current location on the map in nether or the end | https://github.com/GeyserMC/Geyser/issues/3910 | `UNFIXED` |
| 1750783737 | BE slim (3px) skin is not reflected correctly | https://github.com/GeyserMC/Geyser/issues/3856 | `UNFIXED` |
| 1739441457 | item/block display entities | https://github.com/GeyserMC/Geyser/issues/3810 | `UNSUPPORTED` |
| 1686001459 | Java players can't push bedrock players | https://github.com/GeyserMC/Geyser/issues/3702 | `UNFIXED` |
| 1671866467 | Cannot Copy Books | https://github.com/GeyserMC/Geyser/issues/3682 | `UNFIXED` |
| 1666874262 | Bedrock can fly under certain conditions | https://github.com/GeyserMC/Geyser/issues/3678 | `FIXED` |
| 1641340815 | Custom Grass/Leaf colors | https://github.com/GeyserMC/Geyser/issues/3645 | `UNFIXED` |
| 1599570030 | Items with "AttributeModifiers" in their NBTData get stuck in clients cursor and are not being displayed properly. | https://github.com/GeyserMC/Geyser/issues/3580 | `UNFIXED` |
| 1580737714 | Support multiple ip binds | https://github.com/GeyserMC/Geyser/issues/3547 | `UNFIXED` |
| 1559783482 | Mounting cod on player | https://github.com/GeyserMC/Geyser/issues/3520 | `UNFIXED` |
| 1512242345 | when holding items in both hands and interacting geyser does not correctly send both the main hand and offhand interactions to the event handler | https://github.com/GeyserMC/Geyser/issues/3480 | `UNFIXED` |
| 1497715636 | Please implement "aerial perspective" for how chunks look. | https://github.com/GeyserMC/Geyser/issues/3458 | `UNFIXED` |
| 1495006234 | GeyserApi.api().connectionByUuid() is flaky | https://github.com/GeyserMC/Geyser/issues/3456 | `UNFIXED` |
| 1483856962 | Allows custom InteractiveTag for Entity | https://github.com/GeyserMC/Geyser/issues/3441 | `UNFIXED` |
| 1455706435 | overlapping command enums | https://github.com/GeyserMC/Geyser/issues/3411 | `UNFIXED` |
| 1453152029 | Complex Spigot+Paper server player moving and climbing rebound | https://github.com/GeyserMC/Geyser/issues/3407 | `UNFIXED` |
| 1428000485 | Employ fix for headless Armor Stand | https://github.com/GeyserMC/Geyser/issues/3373 | `UNSUPPORTED` |
| 1422823529 | Packet intercepting and sending API | https://github.com/GeyserMC/Geyser/issues/3365 | `UNFIXED` |
| 1419228638 | Camouflage blocks, GeyserMC players see teleportation | https://github.com/GeyserMC/Geyser/issues/3358 | `UNFIXED` |
| 1397034352 | Command description support for geyser standalone | https://github.com/GeyserMC/Geyser/issues/3337 | `UNFIXED` |
| 1395538072 | Stopsound stops all sounds on bedrock regardless of chosen category | https://github.com/GeyserMC/Geyser/issues/3335 | `UNSUPPORTED` |
| 1306933326 | Horse Issues | https://github.com/GeyserMC/Geyser/issues/3146 | `UNFIXED` |
| 1282694633 | Bedrock block break time when flying is not correct | https://github.com/GeyserMC/Geyser/issues/3084 | `UNFIXED` |
| 1269700402 | Respawn Problem | https://github.com/GeyserMC/Geyser/issues/3048 | `UNFIXED` |
| 1181673807 | Can't boost with Firework when flying with Elytra in 1.12.2 creative | https://github.com/GeyserMC/Geyser/issues/2904 | `UNFIXED` |
| 1164369454 | Disguise Issue | https://github.com/GeyserMC/Geyser/issues/2879 | `UNFIXED` |
| 1122496813 | Allow query port and bedrock port to be on the same port | https://github.com/GeyserMC/Geyser/issues/2807 | `UNFIXED` |
| 1112663867 | Bedrock bridging possible despite allow-bedrock-bridging being false | https://github.com/GeyserMC/Geyser/issues/2787 | `UNFIXED` |
| 1104960974 | Bedrock godbridging block doesn’t affect blocks placed on the sides | https://github.com/GeyserMC/Geyser/issues/2769 | `UNFIXED` |
| 1104122871 | Tooltips are automatically turned off while rejoining | https://github.com/GeyserMC/Geyser/issues/2768 | `UNFIXED` |
| 1068005488 | Geyser doesn't like old-style skin files | https://github.com/GeyserMC/Geyser/issues/2658 | `UNFIXED` |
| 1052791662 | Xbox achievements not unlocking properly with Geyser | https://github.com/GeyserMC/Geyser/issues/2629 | `UNSUPPORTED` |
| 988382023 | Move mappings to config files | https://github.com/GeyserMC/Geyser/issues/2514 | `UNFIXED` |
| 976467865 | Other players' boats have very jerky movement | https://github.com/GeyserMC/Geyser/issues/2490 | `UNFIXED` |
| 945607347 | Content log spam on server join/switch worlds & command suggestions not working ([Commands][error]-Keywords in commands need to be lower case. '<argument>' was not registered.) | https://github.com/GeyserMC/Geyser/issues/2385 | `UPSTREAM_FIXED` |
| 938291459 | End crystals don't move when pushed by pistons | https://github.com/GeyserMC/Geyser/issues/2358 | `UNFIXED` |
| 869272079 | Bedrock clients are unable to crawl when using the GSit plugin | https://github.com/GeyserMC/Geyser/issues/2180 | `UNSUPPORTED` |
| 809712905 | Itemframe Placing Bug | https://github.com/GeyserMC/Geyser/issues/1938 | `UNSUPPORTED` |
| 805141067 | Infinite up movement while elytra flying in gamemode creative | https://github.com/GeyserMC/Geyser/issues/1916 | `UNSUPPORTED` |
| 802261469 | Standing against a wall and placing a door inside of yourself causes issues | https://github.com/GeyserMC/Geyser/issues/1902 | `UNFIXED` |
| 789551268 | Hide enchants attribute doesn't work | https://github.com/GeyserMC/Geyser/issues/1852 | `UNSUPPORTED` |
| 777380397 | Holding shift while planting crops causes the food to be eaten | https://github.com/GeyserMC/Geyser/issues/1782 | `UNFIXED` |
| 775688478 | Support for 1.16 custom biomes | https://github.com/GeyserMC/Geyser/issues/1752 | `UNFIXED` |
| 633624088 | Splitscreen support | https://github.com/GeyserMC/Geyser/issues/750 | `FIXED` |

### Open pull requests

_(56 records)_

| ID | Title | URL | Status |
| ---: | --- | --- | --- |
| 5446883801 | Improve functionality of the Nether roof workaround | https://github.com/GeyserMC/Geyser/pull/6936 | `NOT_APPLICABLE` |
| 5398766503 | Fix: include Git SHA in Spigot plugin version | https://github.com/GeyserMC/Geyser/pull/6931 | `FIXED` |
| 5387806158 | Fill server-side command suggestions on Bedrock | https://github.com/GeyserMC/Geyser/pull/6928 | `NOT_APPLICABLE` |
| 5368363595 | Keep the name tag of entities that only carry text displays | https://github.com/GeyserMC/Geyser/pull/6925 | `FIXED` |
| 5360752965 | Introduce `.geyserpack` loading | https://github.com/GeyserMC/Geyser/pull/6922 | `UNFIXED` |
| 5330888326 | FIX synchronize item frames after teleportation | https://github.com/GeyserMC/Geyser/pull/6921 | `FIXED` |
| 5295765293 | Register vanilla tools as custom items to fix block breaking times | https://github.com/GeyserMC/Geyser/pull/6916 | `NOT_APPLICABLE` |
| 5286632827 | build(deps): use the MCProtocolLib codec fixes build | https://github.com/GeyserMC/Geyser/pull/6914 | `UNFIXED` |
| 5275355313 | Match Vanilla styles in custom names and lore to fully normalize the text component | https://github.com/GeyserMC/Geyser/pull/6909 | `DUPLICATE` |
| 5233348431 | Add texture variation and alpha masked tint to material instance API | https://github.com/GeyserMC/Geyser/pull/6905 | `UNFIXED` |
| 5216900616 | Add culling to custom block geometry component | https://github.com/GeyserMC/Geyser/pull/6902 | `NOT_APPLICABLE` |
| 5205909839 | Fix Xbox 26.60 LAN discovery and joins | https://github.com/GeyserMC/Geyser/pull/6896 | `FIXED` |
| 5194626275 | Improved debugging information in Geyser dumps | https://github.com/GeyserMC/Geyser/pull/6890 | `FIXED` |
| 5176617470 | Fix destruction stages | https://github.com/GeyserMC/Geyser/pull/6883 | `FIXED` |
| 5130807082 | Debugging NetherNet issues | https://github.com/GeyserMC/Geyser/pull/6873 | `DUPLICATE` |
| 5112693462 | Load custom mappings from urls and extensions | https://github.com/GeyserMC/Geyser/pull/6866 | `UNFIXED` |
| 5112427414 | Add optional tooltip style markers for resource packs | https://github.com/GeyserMC/Geyser/pull/6863 | `NOT_APPLICABLE` |
| 5062483162 | Add ip-discovery, for servers where STUN cannot find the public address | https://github.com/GeyserMC/Geyser/pull/6844 | `FIXED` |
| 5053287223 | Apply server-sided Dolphin's Grace | https://github.com/GeyserMC/Geyser/pull/6840 | `FIXED` |
| 5035241143 | Improve translation of commands | https://github.com/GeyserMC/Geyser/pull/6833 | `NOT_APPLICABLE` |
| 5023900867 | Custom biome API, mappings, and generated Bedrock visuals | https://github.com/GeyserMC/Geyser/pull/6829 | `DUPLICATE` |
| 4943303365 | ViaProxy: don't fail IP passthrough when a downstream session has no local address yet | https://github.com/GeyserMC/Geyser/pull/6812 | `FIXED` |
| 4938616313 | Implement abstract model for sharing data across sessions | https://github.com/GeyserMC/Geyser/pull/6811 | `NOT_APPLICABLE` |
| 4842813446 | Implement Text Displays via Primitive Text | https://github.com/GeyserMC/Geyser/pull/6768 | `NOT_APPLICABLE` |
| 4805839353 | Fix custom tridents and spears | https://github.com/GeyserMC/Geyser/pull/6759 | `FIXED` |
| 4744708298 | fix: prevent NPE when attacking Ender Dragon parts | https://github.com/GeyserMC/Geyser/pull/6748 | `FIXED` |
| 4569560222 | Fix collision loading and addon-pack flags for non-vanilla blocks | https://github.com/GeyserMC/Geyser/pull/6724 | `UNFIXED` |
| 4540681458 | Fix client-side waterlogging prediction on doors and non-waterloggable blocks | https://github.com/GeyserMC/Geyser/pull/6706 | `FIXED` |
| 4496366694 | Lava fishing workaround | https://github.com/GeyserMC/Geyser/pull/6694 | `UNFIXED` |
| 4476271563 | Add support for Npc Dialogues | https://github.com/GeyserMC/Geyser/pull/6690 | `NOT_APPLICABLE` |
| 4457015299 | Xbox split-screen support, reworked onto master and fixed on hardware | https://github.com/GeyserMC/Geyser/pull/6689 | `FIXED` |
| 4440438587 | Move recipe shift clicking to use ServerboundPlaceRecipePacket | https://github.com/GeyserMC/Geyser/pull/6686 | `UNFIXED` |
| 4345263370 | Avoid have no response when trading using touch or the Shift key cause by client bug since v1.21.30 | https://github.com/GeyserMC/Geyser/pull/6667 | `FIXED` |
| 4336073923 | Improve `manifest.json` search loop | https://github.com/GeyserMC/Geyser/pull/6664 | `BLOCKED` |
| 4286226682 | Add model-specific custom block item overrides support | https://github.com/GeyserMC/Geyser/pull/6643 | `UNFIXED` |
| 4258541122 | Fix: Bound the Bedrock projection of oversized books | https://github.com/GeyserMC/Geyser/pull/6627 | `FIXED` |
| 4258382559 | Fix: Let real blocks take priority over item frames | https://github.com/GeyserMC/Geyser/pull/6626 | `FIXED` |
| 4234891940 | Add extensible server-authoritative custom block breaking | https://github.com/GeyserMC/Geyser/pull/6605 | `DUPLICATE` |
| 4203696760 | Introduce useful debug command | https://github.com/GeyserMC/Geyser/pull/6586 | `UNFIXED` |
| 4172942473 | Fix geyser connection timeout caused by translation expansion | https://github.com/GeyserMC/Geyser/pull/6579 | `FIXED` |
| 4170224258 | Download Java resourcepacks sent by the server, and load translations from them | https://github.com/GeyserMC/Geyser/pull/6578 | `NOT_APPLICABLE` |
| 4150378049 | Refactor WebUtils to make use of modern HTTP APIs in Java | https://github.com/GeyserMC/Geyser/pull/6573 | `UNFIXED` |
| 4111042967 | Interpolate per-tick entity movement on Bedrock | https://github.com/GeyserMC/Geyser/pull/6566 | `NOT_APPLICABLE` |
| 4083854339 | Improve bamboo collision | https://github.com/GeyserMC/Geyser/pull/6560 | `FIXED` |
| 4078328957 | Fix collision for copper bars and turtle eggs | https://github.com/GeyserMC/Geyser/pull/6555 | `FIXED` |
| 4008215209 | Change logger to a static reference and add some debugging commands | https://github.com/GeyserMC/Geyser/pull/6513 | `NOT_APPLICABLE` |
| 3948152368 | Attempt to lower scoreboard allocs | https://github.com/GeyserMC/Geyser/pull/6489 | `NOT_APPLICABLE` |
| 3707133775 | Fix block breaking | https://github.com/GeyserMC/Geyser/pull/6419 | `FIXED` |
| 3662155537 | Add proper combat sounds | https://github.com/GeyserMC/Geyser/pull/6393 | `NOT_APPLICABLE` |
| 3142621945 | Feature: Reloadable Mappings | https://github.com/GeyserMC/Geyser/pull/6081 | `UNFIXED` |
| 3044136529 | Workaround for client-sided removal of all items from the crafting grid on inventory close | https://github.com/GeyserMC/Geyser/pull/6008 | `FIXED` |
| 2875767111 | Custom Translation API | https://github.com/GeyserMC/Geyser/pull/5860 | `UNFIXED` |
| 2670923346 | Full Java tooltip parity (as much as that is possible) | https://github.com/GeyserMC/Geyser/pull/5692 | `UNFIXED` |
| 2662538702 | Geyser Networking API | https://github.com/GeyserMC/Geyser/pull/5685 | `UNFIXED` |
| 2593566343 | Anvil updater refactor and other small refactors | https://github.com/GeyserMC/Geyser/pull/5595 | `UNFIXED` |
| 2047932455 | Support for max stack size | https://github.com/GeyserMC/Geyser/pull/5011 | `UNFIXED` |
