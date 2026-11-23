# ArkFlame/Geyser — Upstream Issue/PR Tracker

_Generated at: 2026-11-23T14:40:48Z_

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
| `UNFIXED` | 5028 |
| `IN_PROGRESS` | 28 |
| `FIXED` | 0 |
| `UPSTREAM_FIXED` | 1760 |
| `DUPLICATE` | 0 |
| `BLOCKED` | 1 |
| `UNSUPPORTED` | 0 |
| `NOT_APPLICABLE` | 0 |

## Campaign audit gap

This pass did not audit the bulk of the inventory. Bulk closed issues and closed-unmerged PRs remain `UNFIXED`/unaudited and are retained only in the JSON. The actionable focus of this campaign is the 347-item open queue (291 open issues plus 56 open PRs). No runtime verification has occurred: worker patches are static-only and no patch has yet been integrated into the final branch. Compilation is deferred to a single post-integration CI build. No fix in this tracker is claimed as verified.

## Priority queue — all OPEN issues and OPEN PRs

These are the records that still need triage or an adoption decision. Closed/merged records are in the JSON file.

### Open issues

_(291 records)_

| ID | Title | URL | Status |
| ---: | --- | --- | --- |
| 6491152696 | Item names are italicised for bedrock players | https://github.com/GeyserMC/Geyser/issues/6929 | `UNFIXED` |
| 6478673359 | Bedrock player moves extremely fast after a cross-world teleport until relog | https://github.com/GeyserMC/Geyser/issues/6927 | `UNFIXED` |
| 6449358192 | NetherNet Doesn't Work on FreeBSD | https://github.com/GeyserMC/Geyser/issues/6923 | `UNFIXED` |
| 6413008617 | [NetherNet] Native handles and GeyserSession objects are never released after Bedrock players disconnect (Velocity, Windows) – ~90 handles + ~7–11 MB leaked per connection | https://github.com/GeyserMC/Geyser/issues/6920 | `UNFIXED` |
| 6398503486 | Regression of #6834 in b1302: players joining through a signaling relay (MCXboxBroadcast) get the relay's IP again | https://github.com/GeyserMC/Geyser/issues/6919 | `UNFIXED` |
| 6392359368 | Bedrock 26.6x (NetherNet): tilling grass/dirt with a hoe is reverted, farmland never reaches the Java server | https://github.com/GeyserMC/Geyser/issues/6918 | `UNFIXED` |
| 6363398043 | Nethernet and Playit: Connecting via an external IPv4 address and receiving a local IPv4 address. | https://github.com/GeyserMC/Geyser/issues/6915 | `UNFIXED` |
| 6353841610 | Bedrock Client Cannot Connect (End of Stream) | https://github.com/GeyserMC/Geyser/issues/6913 | `UNFIXED` |
| 6280416949 | Bedrock players experience abnormally fast movement speed after changing worlds or reconnecting | https://github.com/GeyserMC/Geyser/issues/6903 | `UNFIXED` |
| 6274805438 | bedrock player can fast place glowstone when keep holding place button after looking away from a respawn anchor | https://github.com/GeyserMC/Geyser/issues/6901 | `UNFIXED` |
| 6265656032 | Bedrock players are joining via ipv6 addresses | https://github.com/GeyserMC/Geyser/issues/6899 | `BLOCKED` |
| 6256269328 | I am getting kicked from the server (Bedrock Edition). | https://github.com/GeyserMC/Geyser/issues/6894 | `UNFIXED` |
| 6256027037 | EncoderException "MessageToMessageCodec$2 must produce at least one message" kicks Bedrock players on Velocity (since 2026-11-06, also on b1288) | https://github.com/GeyserMC/Geyser/issues/6893 | `UNFIXED` |
| 6229661892 | Bedrock players in a boat desync at a static world border (Java players unaffected) | https://github.com/GeyserMC/Geyser/issues/6884 | `UNFIXED` |
| 6222127447 | Accept PROXY protocol v1 on webrtc-proxy-protocol-relays (or document that only v2 is supported) | https://github.com/GeyserMC/Geyser/issues/6881 | `UNFIXED` |
| 6201847840 | Text display entities show through walls | https://github.com/GeyserMC/Geyser/issues/6876 | `UNFIXED` |
| 6145901311 | GUI Closes on Menu Transfer... sometimes. | https://github.com/GeyserMC/Geyser/issues/6858 | `UNFIXED` |
| 6138669848 | Very high latency, missing block drops and lag when bedrock players join fabric server. | https://github.com/GeyserMC/Geyser/issues/6854 | `UNFIXED` |
| 6134779753 | Geyser servers don't appear under Friends > LAN Games for 26.60 clients (NetherNet LAN discovery on UDP 7551 is never started) | https://github.com/GeyserMC/Geyser/issues/6853 | `IN_PROGRESS` |
| 6133451417 | Bedrock 26.60 camera/input delay on Anarchy backend through Velocity; Hub works normally | https://github.com/GeyserMC/Geyser/issues/6851 | `UNFIXED` |
| 6132388000 | NetherNet seems to kick some bedrock players at seemingly random intervals | https://github.com/GeyserMC/Geyser/issues/6850 | `UNFIXED` |
| 6132001962 | Flickering Bug for bedrock | https://github.com/GeyserMC/Geyser/issues/6849 | `UNFIXED` |
| 6131252494 | Geyser doesn't work on bionic c library | https://github.com/GeyserMC/Geyser/issues/6848 | `UNFIXED` |
| 6084617421 | Velocity MOTD showing instead of the passthrough from Paper | https://github.com/GeyserMC/Geyser/issues/6842 | `UNFIXED` |
| 6082502683 | NetherNet: IPv4-bound Geyser logs "juice: Send failed, errno=97" WARNs for clients' IPv6 ICE candidates | https://github.com/GeyserMC/Geyser/issues/6841 | `UNFIXED` |
| 6049372589 | Add a dump option to indicate whether NetherNet/signaling is being used by another component. | https://github.com/GeyserMC/Geyser/issues/6831 | `UNFIXED` |
| 6048759025 | Text Displays appear way higher than they should on Bedrock Edition | https://github.com/GeyserMC/Geyser/issues/6830 | `UNFIXED` |
| 6012421771 | NetherNet DLL blocked by Windows Smart App Control on Windows 11 | https://github.com/GeyserMC/Geyser/issues/6822 | `UNFIXED` |
| 5958150713 | Wierd Itemswap/Item deletion in Creative Mode | https://github.com/GeyserMC/Geyser/issues/6813 | `UNFIXED` |
| 5922887066 | Players in death state and cannot respawn urgent bug | https://github.com/GeyserMC/Geyser/issues/6805 | `UNFIXED` |
| 5916322359 | Some items in Bedrock are invisible | https://github.com/GeyserMC/Geyser/issues/6802 | `UNFIXED` |
| 5914104967 | Implements block_placer component | https://github.com/GeyserMC/Geyser/issues/6801 | `UNFIXED` |
| 5910296029 | Player takes fall damage when landing on a waterlogged chest while colliding with the side of an adjacent full block | https://github.com/GeyserMC/Geyser/issues/6798 | `UNFIXED` |
| 5903758607 | Bedrock blocks dont appear server side | https://github.com/GeyserMC/Geyser/issues/6795 | `UNFIXED` |
| 5839390609 | Custom entities in mapping | https://github.com/GeyserMC/Geyser/issues/6770 | `UNFIXED` |
| 5726325261 | Can't use axes, shovels or hoes with Java Edition 26.3 | https://github.com/GeyserMC/Geyser/issues/6753 | `UNFIXED` |
| 5634129504 | Boat ejects Bedrock player in bubble columns longer than 8 blocks | https://github.com/GeyserMC/Geyser/issues/6741 | `UNFIXED` |
| 5578485037 | purpur-geyser bug | https://github.com/GeyserMC/Geyser/issues/6734 | `UNFIXED` |
| 5494589283 | Sliding display entity translation updates are sent to the client but do not visually apply on Bedrock | https://github.com/GeyserMC/Geyser/issues/6723 | `UNFIXED` |
| 5494554880 | Doors with a dynamic barrier block hitbox have unreliable click detection on Bedrock | https://github.com/GeyserMC/Geyser/issues/6722 | `UNFIXED` |
| 5476694429 | Protocol 2193 clients never render system chat (all Floodgate-origin chat invisible) on b1243 | https://github.com/GeyserMC/Geyser/issues/6715 | `UNFIXED` |
| 5437008603 | [bug] Got disconnected from the skin uploader: failed to find the given code in combination with the verify code | https://github.com/GeyserMC/Geyser/issues/6699 | `UNFIXED` |
| 5429408847 | [Fabric] JVM remains alive after clean shutdown due to FloodgateSkinUploader WebSocket threads | https://github.com/GeyserMC/Geyser/issues/6695 | `UNFIXED` |
| 5391485278 | falling in the void infinitely | https://github.com/GeyserMC/Geyser/issues/6692 | `UNFIXED` |
| 5352851619 | Ghost block problem with specific blocks and some blocks only above y224? | https://github.com/GeyserMC/Geyser/issues/6687 | `UNFIXED` |
| 5286055183 | [BUG] 1.21.1 recipes (e.g., Warped Planks) are missing or not craftable on Bedrock clients | https://github.com/GeyserMC/Geyser/issues/6684 | `UNFIXED` |
| 5249345569 | Taking, discarding and moving items | https://github.com/GeyserMC/Geyser/issues/6677 | `UNFIXED` |
| 5247497208 | Server-side Entity Culling | https://github.com/GeyserMC/Geyser/issues/6676 | `UNFIXED` |
| 5244501862 | Bedrock player skin not showing on Java - "Ignoring player info update" logged for Floodgate players | https://github.com/GeyserMC/Geyser/issues/6675 | `UNFIXED` |
| 5242221308 | Stopsound command fix | https://github.com/GeyserMC/Geyser/issues/6674 | `UNFIXED` |
| 5234500656 | Sudden Speedmissmatch of Donkeys (Probably Horses too) when using Swiftness  potions | https://github.com/GeyserMC/Geyser/issues/6670 | `UNFIXED` |
| 5233488856 | Hopper minecart appears as furnace minecart on Bedrock (Geyser 2.11.2-b1230) | https://github.com/GeyserMC/Geyser/issues/6668 | `UNFIXED` |
| 5219153527 | Resource packs zipped on MacOS do not read properly | https://github.com/GeyserMC/Geyser/issues/6663 | `UNFIXED` |
| 5285638522 | [Bedrock] GUI clicks became unreliable after Geyser b1225 | https://github.com/GeyserMC/Geyser/issues/6683 | `UNFIXED` |
| 5217173715 | BetterView causes chunks to start flickering on bedrock | https://github.com/GeyserMC/Geyser/issues/6661 | `UNFIXED` |
| 5211481247 | All Bedrocks skins do not show for Java clients | https://github.com/GeyserMC/Geyser/issues/6659 | `UNFIXED` |
| 5192944394 | ItemStack rejected without cause | https://github.com/GeyserMC/Geyser/issues/6653 | `UNFIXED` |
| 5186978565 | Paper Anti-Xray engine-mode 2 causes Bedrock clientbound updates to freeze and eventually time out | https://github.com/GeyserMC/Geyser/issues/6650 | `UNFIXED` |
| 5184877875 | There no punching Animation in 1st Person when using animation packs | https://github.com/GeyserMC/Geyser/issues/6649 | `UNFIXED` |
| 5176721889 | Bedrock GUI buttons sometimes trigger the wrong button after Geyser update | https://github.com/GeyserMC/Geyser/issues/6648 | `UNFIXED` |
| 5164064477 | Pearl catch issue | https://github.com/GeyserMC/Geyser/issues/6646 | `UNFIXED` |
| 5157745642 | Chunk loading issue | https://github.com/GeyserMC/Geyser/issues/6641 | `UNFIXED` |
| 5157629372 | Buggy inventory and crafting | https://github.com/GeyserMC/Geyser/issues/6640 | `UNFIXED` |
| 5156275649 | Sprites in quick actions components cause error | https://github.com/GeyserMC/Geyser/issues/6639 | `IN_PROGRESS` |
| 5144259158 | Bedrock players cannot break vanilla blocks after Geyser update | https://github.com/GeyserMC/Geyser/issues/6637 | `UNFIXED` |
| 5141425466 | Add container_empty predicate for Geyser Custom Items V2 | https://github.com/GeyserMC/Geyser/issues/6634 | `UNFIXED` |
| 5135599202 | Hydraulic and Consoles: A small but no doubtfully painful to fix bug | https://github.com/GeyserMC/Geyser/issues/6632 | `UNFIXED` |
| 5091246493 | Custom Entity API non-vanilla entities | https://github.com/GeyserMC/Geyser/issues/6598 | `UNFIXED` |
| 5084497235 | Bedrock mobile users either unable to join or crashing immediately. | https://github.com/GeyserMC/Geyser/issues/6596 | `UNFIXED` |
| 5072539239 | Ty-el's Vietnamese Language Pack not work with GeyserMc | https://github.com/GeyserMC/Geyser/issues/6592 | `UNFIXED` |
| 5061576088 | Bedroack cant load chunks | https://github.com/GeyserMC/Geyser/issues/6587 | `UNFIXED` |
| 5055981657 | /fly toggled on causing swimming and block breaking issues | https://github.com/GeyserMC/Geyser/issues/6584 | `UNFIXED` |
| 5040716111 | Players at Y 64 get inexplicably stuck | https://github.com/GeyserMC/Geyser/issues/6580 | `UNFIXED` |
| 5011405882 | Crafting Crash - Book & Quill / Book | https://github.com/GeyserMC/Geyser/issues/6577 | `UNFIXED` |
| 5001803063 | Bedrock chat not visible to Java players & Bedrock skins show as Steve/Alex | https://github.com/GeyserMC/Geyser/issues/6574 | `UNFIXED` |
| 4979519119 | Click register delay | https://github.com/GeyserMC/Geyser/issues/6570 | `UNFIXED` |
| 4969896275 | Bedrock Players left incapacitated by ImageFrame maps | https://github.com/GeyserMC/Geyser/issues/6569 | `UNFIXED` |
| 4953133141 | ViaProxy: first join via NetherNet relay times out at ~19s (rejoin works); unguarded getLocalAddress() NPE in GeyserViaProxyPlugin channel-init | https://github.com/GeyserMC/Geyser/issues/6567 | `IN_PROGRESS` |
| 4934805024 | Recipe book auto-craft reverts inventory / returns ingredients instead of crafted item | https://github.com/GeyserMC/Geyser/issues/6563 | `UNFIXED` |
| 4915679666 | Cant see Skins | https://github.com/GeyserMC/Geyser/issues/6558 | `UNFIXED` |
| 4915592951 | Possible regression with "forward-player-ping" causing Bedrock timeouts after long teleports | https://github.com/GeyserMC/Geyser/issues/6557 | `UNFIXED` |
| 4915065150 | Unable to wear armor after crafting | https://github.com/GeyserMC/Geyser/issues/6556 | `UNFIXED` |
| 4902059744 | Movement of non-living entities (Display entities) isn't interpolated, causing visible drag for anything riding one | https://github.com/GeyserMC/Geyser/issues/6551 | `UNFIXED` |
| 4889086403 | Update bedrock limiations to include attributes | https://github.com/GeyserMC/Geyser/issues/6544 | `UNFIXED` |
| 4868140574 | Allow custom Bedrock item identifiers in horse armor GUI acceptedItems | https://github.com/GeyserMC/Geyser/issues/6535 | `UNFIXED` |
| 4866786667 | Players kicked while right-click on block | https://github.com/GeyserMC/Geyser/issues/6532 | `UNFIXED` |
| 4859485425 | enchant table cannot be used | https://github.com/GeyserMC/Geyser/issues/6527 | `UNFIXED` |
| 4819367010 | Bedrock Worlds tab server entry disappears or connects to wrong port behind NAT port remapping | https://github.com/GeyserMC/Geyser/issues/6508 | `UNFIXED` |
| 4787423496 | Dialog buttons with object atlas sprite labels should map to FormImage.Type.PATH on Bedrock, but currently error with Expected tag to be a literal string... and render blank. | https://github.com/GeyserMC/Geyser/issues/6499 | `UNFIXED` |
| 4776889765 | Failed to correctly map the zombie villager's profession | https://github.com/GeyserMC/Geyser/issues/6495 | `UNFIXED` |
| 4756478294 | Minecart Visual Lag/desync | https://github.com/GeyserMC/Geyser/issues/6485 | `UNFIXED` |
| 4737289181 | [ViaProxy] Client is having trouble establishing the connection | https://github.com/GeyserMC/Geyser/issues/6478 | `UNFIXED` |
| 4695749715 | PS5 Connection timeout after latest MC update | https://github.com/GeyserMC/Geyser/issues/6466 | `UNFIXED` |
| 4684297026 | Stuck in furnace GUI, can't back out. | https://github.com/GeyserMC/Geyser/issues/6458 | `UNFIXED` |
| 4677471227 | Bedrock player's POV does not update when controlling mounted mobs (ALL ENTITIES) | https://github.com/GeyserMC/Geyser/issues/6454 | `UNFIXED` |
| 4613680562 | Bedrock player sees water but java dont | https://github.com/GeyserMC/Geyser/issues/6446 | `UNFIXED` |
| 4592672702 | The dialog box is closing the currently open inventory on the server-side | https://github.com/GeyserMC/Geyser/issues/6443 | `UNFIXED` |
| 4479609325 | Crafting issue PLEASE HELP | https://github.com/GeyserMC/Geyser/issues/6420 | `UNFIXED` |
| 4444025051 | Horses broken | https://github.com/GeyserMC/Geyser/issues/6408 | `UNFIXED` |
| 4428303756 | Bucket misalignment/ghosting | https://github.com/GeyserMC/Geyser/issues/6399 | `IN_PROGRESS` |
| 4427799377 | Floating Items during Crafting for  some BEDROCK PLAYERS | https://github.com/GeyserMC/Geyser/issues/6398 | `UNFIXED` |
| 4426982552 | Chests visual bug | https://github.com/GeyserMC/Geyser/issues/6397 | `UNFIXED` |
| 4424154581 | Bedrock users spawn facing wrong direction | https://github.com/GeyserMC/Geyser/issues/6395 | `UNFIXED` |
| 4411316441 | BEDROCK INVENTORY BUG | https://github.com/GeyserMC/Geyser/issues/6373 | `UNFIXED` |
| 4402991483 | Globally linked Java skin does not show on Bedrock | https://github.com/GeyserMC/Geyser/issues/6368 | `UNFIXED` |
| 4367591866 | Bedrock mobile players crafted items do not stack in inventory | https://github.com/GeyserMC/Geyser/issues/6342 | `UNFIXED` |
| 4367583984 | Bamboo and crop blocks cause rubber-banding/glitching for Bedrock mobile players | https://github.com/GeyserMC/Geyser/issues/6341 | `UNFIXED` |
| 4307745168 | Minecraft armour trims problem | https://github.com/GeyserMC/Geyser/issues/6320 | `UNFIXED` |
| 4259757094 | Custom Skulls Bug? | https://github.com/GeyserMC/Geyser/issues/6302 | `UNFIXED` |
| 4198616510 | Bedrock 26.10/26.12 (protocol 944) horse riding desync: delayed first jump, rubberbanding, dismount snapback | https://github.com/GeyserMC/Geyser/issues/6283 | `UNFIXED` |
| 4196235341 | Mapping api v2 predicate property fishing_rod_cast or crossbow charged Not compatible with Java players | https://github.com/GeyserMC/Geyser/issues/6281 | `UNFIXED` |
| 4186822367 | Bedrock clients are crashing while somebody has a custom armor with armor trim equipped | https://github.com/GeyserMC/Geyser/issues/6278 | `UNFIXED` |
| 4154311005 | Custom item attirbutes do not work proplerly on bedrock edition | https://github.com/GeyserMC/Geyser/issues/6266 | `UNFIXED` |
| 4113646890 | Bedrock Spectator Mode not working as intended. | https://github.com/GeyserMC/Geyser/issues/6248 | `UNFIXED` |
| 4110465428 | Title: Bedrock recipe book auto-crafting does not work correctly through Geyser | https://github.com/GeyserMC/Geyser/issues/6246 | `UNFIXED` |
| 4079217873 | Dolphin Tunnels not working for bedrock players | https://github.com/GeyserMC/Geyser/issues/6239 | `UNFIXED` |
| 4041295810 | signing writable books if they have NBT does not work | https://github.com/GeyserMC/Geyser/issues/6224 | `UNFIXED` |
| 4005465880 | PS5 Bedrock Edition Crashing consistently | https://github.com/GeyserMC/Geyser/issues/6213 | `UNFIXED` |
| 3997506286 | Players using Bedrock Edition on mobile devices are unable to enchant items or books. | https://github.com/GeyserMC/Geyser/issues/6210 | `UNFIXED` |
| 3973938805 | Lore issue | https://github.com/GeyserMC/Geyser/issues/6202 | `UNFIXED` |
| 3897906890 | [Towny] geyser bedrock players can attack by quickly breaking blocks and attacking in pvp disabled town | https://github.com/GeyserMC/Geyser/issues/6159 | `UNFIXED` |
| 3864556632 | Add an option for geyser to temporary solve "Not receiving chunk" | https://github.com/GeyserMC/Geyser/issues/6142 | `UNFIXED` |
| 3851074699 | Can't Bedrock GUI | https://github.com/GeyserMC/Geyser/issues/6134 | `UNFIXED` |
| 3844057698 | Client crash/freeze when entering area with many mobs/animals/entities | https://github.com/GeyserMC/Geyser/issues/6129 | `UNFIXED` |
| 3841214418 | [Bug Report] ArtMap used to work properly but no longer accurate. | https://github.com/GeyserMC/Geyser/issues/6128 | `UNFIXED` |
| 3824109144 | Enchant table is not working | https://github.com/GeyserMC/Geyser/issues/6116 | `IN_PROGRESS` |
| 3823915450 | Player head texture occasionally not updated | https://github.com/GeyserMC/Geyser/issues/6115 | `UNFIXED` |
| 3823874351 | Items stuck in cursor after furnace interaction | https://github.com/GeyserMC/Geyser/issues/6113 | `UNFIXED` |
| 3798882794 | Players can't connect if they have an oversize book in their inventory | https://github.com/GeyserMC/Geyser/issues/6098 | `IN_PROGRESS` |
| 3795623377 | Blocks don't break when Tools have CustomModel | https://github.com/GeyserMC/Geyser/issues/6097 | `UNFIXED` |
| 3790893570 | Custom blocks under state_overrides with different block identifiers | https://github.com/GeyserMC/Geyser/issues/6092 | `UNFIXED` |
| 3781719547 | Skin and cape data API as BufferedImage | https://github.com/GeyserMC/Geyser/issues/6086 | `UNFIXED` |
| 3773728835 | Bedrock is very buggy | https://github.com/GeyserMC/Geyser/issues/6079 | `UNFIXED` |
| 3771522889 | Nintendo Switch 1 OLED Game Crash on Join | https://github.com/GeyserMC/Geyser/issues/6078 | `UNFIXED` |
| 3760739627 | Custom mapped (note)blocks don't render their texture/model if they're a falling block | https://github.com/GeyserMC/Geyser/issues/6072 | `UNFIXED` |
| 3720071081 | Add the SHA hash when outputting version | https://github.com/GeyserMC/Geyser/issues/6037 | `IN_PROGRESS` |
| 3717137044 | Copper Bars craft into Trap Doors on Bedrock (iOS) | https://github.com/GeyserMC/Geyser/issues/6032 | `UNFIXED` |
| 3708933008 | Nintendo Switch players and Pack file size | https://github.com/GeyserMC/Geyser/issues/6023 | `UNFIXED` |
| 3700997392 | Support for proper tooltip display of unsafe enchantments | https://github.com/GeyserMC/Geyser/issues/6020 | `UNFIXED` |
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
| 3521350341 | Player Heads Glitching as Steve + Steve Entity | https://github.com/GeyserMC/Geyser/issues/5907 | `IN_PROGRESS` |
| 3521287921 | Mining speed difference between custom textured shovel and normal shovel | https://github.com/GeyserMC/Geyser/issues/5906 | `UNFIXED` |
| 3521263925 | Uncrafting Netherite Block to Ingots not working (Minor Bug) | https://github.com/GeyserMC/Geyser/issues/5905 | `UNFIXED` |
| 3489885492 | Command Description Bug | https://github.com/GeyserMC/Geyser/issues/5886 | `UNFIXED` |
| 3484828649 | anvil bug | https://github.com/GeyserMC/Geyser/issues/5879 | `UNFIXED` |
| 3481725971 | iOS Bedrock crash on specific GeyserMC server | https://github.com/GeyserMC/Geyser/issues/5873 | `UNFIXED` |
| 3478851061 | Disconnect when a player has a large number of advancements | https://github.com/GeyserMC/Geyser/issues/5869 | `UNFIXED` |
| 3465131333 | Inaccurate Y-Axis motion when falling/jumping flagging anticheats. | https://github.com/GeyserMC/Geyser/issues/5852 | `IN_PROGRESS` |
| 3462668505 | Mining with pickaxe opens menu when i left click on mobile | https://github.com/GeyserMC/Geyser/issues/5851 | `UNFIXED` |
| 3456191420 | Bedrock players mine faster than Java when spam clicking | https://github.com/GeyserMC/Geyser/issues/5843 | `UNFIXED` |
| 3432207558 | 1 out of 2 PS5 Players on same Network getting disconnected | https://github.com/GeyserMC/Geyser/issues/5830 | `UNFIXED` |
| 3374925969 | 2 Bug on Bedrock Client (maybe only IOS User, im using iPad) | https://github.com/GeyserMC/Geyser/issues/5793 | `UNFIXED` |
| 3353283339 | Sometimes Minecraft Bedrock crashes while driving happy ghasts | https://github.com/GeyserMC/Geyser/issues/5783 | `UNFIXED` |
| 3338574194 | Problem in Achievements translation | https://github.com/GeyserMC/Geyser/issues/5770 | `UNFIXED` |
| 3322757341 | The player's movements are strange. | https://github.com/GeyserMC/Geyser/issues/5755 | `UNFIXED` |
| 3305700003 | ghost doors | https://github.com/GeyserMC/Geyser/issues/5746 | `UNFIXED` |
| 3282457747 | show_in_tooltip Java component tag is not resolved properly | https://github.com/GeyserMC/Geyser/issues/5736 | `UNFIXED` |
| 3206648842 | Falling Blocks `setVelocity` doesn't work | https://github.com/GeyserMC/Geyser/issues/5655 | `UNFIXED` |
| 3190235250 | Bedrock Edition 1.21.81 Cannot Interact with Fake Players Created by /bot or Use Custom Items from Holoprint (Leaves 1.21.4 + Geyser 2.8.0-b867; Breaks Holoprint Functionality) | https://github.com/GeyserMC/Geyser/issues/5634 | `UNFIXED` |
| 3166049718 | Unable to craft firework rockets on Pocket Edition | https://github.com/GeyserMC/Geyser/issues/5611 | `UNFIXED` |
| 3149044216 | Hand animations and rendering issues with Actions & Stuff | https://github.com/GeyserMC/Geyser/issues/5597 | `UNFIXED` |
| 3133351765 | Geyser - Anvil Works different on Java/Bedrock | https://github.com/GeyserMC/Geyser/issues/5584 | `UNFIXED` |
| 3070695219 | Scale command not showing changed 1st person perspective on Bedrock | https://github.com/GeyserMC/Geyser/issues/5554 | `UNFIXED` |
| 3056086028 | Cannot use Fishing Rods with Custom Model Data | https://github.com/GeyserMC/Geyser/issues/5547 | `UNFIXED` |
| 2995847786 | bedrock players get stuck in an infinite falling state if they get killed upon logout | https://github.com/GeyserMC/Geyser/issues/5489 | `IN_PROGRESS` |
| 2984093096 | Chunks don't load after joining the server | https://github.com/GeyserMC/Geyser/issues/5474 | `UNFIXED` |
| 2974939937 | Can't move items with custom tags | https://github.com/GeyserMC/Geyser/issues/5464 | `UNFIXED` |
| 2958634184 | Players getting kicked | https://github.com/GeyserMC/Geyser/issues/5444 | `UNFIXED` |
| 2948344051 | chunks do not go and the player falls constantly down to -200 blocks and cannot do anything | https://github.com/GeyserMC/Geyser/issues/5431 | `UNFIXED` |
| 2920514585 | Working piston bolts not rendering correctly on bedrock | https://github.com/GeyserMC/Geyser/issues/5408 | `UNFIXED` |
| 2908555375 | Custom Player Head Misalignment | https://github.com/GeyserMC/Geyser/issues/5404 | `UNFIXED` |
| 2888772644 | Not receiving chunk | https://github.com/GeyserMC/Geyser/issues/5386 | `UNFIXED` |
| 2878608332 | Hardcore Hearts | https://github.com/GeyserMC/Geyser/issues/5373 | `UNFIXED` |
| 2875732073 | Players disconnected after teleportation | https://github.com/GeyserMC/Geyser/issues/5371 | `UNFIXED` |
| 2857247988 | Bedrock players experience extreme lag when crafting using crafting table | https://github.com/GeyserMC/Geyser/issues/5350 | `UNFIXED` |
| 2828733774 | Item flags on bedrock items don't hide properly | https://github.com/GeyserMC/Geyser/issues/5319 | `UNFIXED` |
| 2827479445 | Support Open-Directorys for resource packs | https://github.com/GeyserMC/Geyser/issues/5318 | `UNFIXED` |
| 2776467193 | Not all maps display correctly | https://github.com/GeyserMC/Geyser/issues/5270 | `UNFIXED` |
| 2767079695 | InventoryClickEvent gets triggered multiple times when player inventory gets closed and crafting slots are full | https://github.com/GeyserMC/Geyser/issues/5258 | `UNFIXED` |
| 2763399238 | Bedrock Edition Animated Skins Not Showing | https://github.com/GeyserMC/Geyser/issues/5253 | `UNFIXED` |
| 2760504860 | Stonecutter and Mod GUI menu move item not working for PS4 Bedrock Users | https://github.com/GeyserMC/Geyser/issues/5246 | `UNFIXED` |
| 2743754360 | Bedrock players getting console spam "moved too quickly!" | https://github.com/GeyserMC/Geyser/issues/5231 | `UNFIXED` |
| 2739739899 | Bedrock player does not receive knockback when an entity with a negative explosion range affects the player. | https://github.com/GeyserMC/Geyser/issues/5227 | `UNFIXED` |
| 2724412280 | [API] ServerboundDiagnosticsPacket data | https://github.com/GeyserMC/Geyser/issues/5199 | `UNFIXED` |
| 2697961951 | Text preprocess / postprocess API | https://github.com/GeyserMC/Geyser/issues/5166 | `UNFIXED` |
| 2681347646 | egg throwing on bedrock is too low pitch | https://github.com/GeyserMC/Geyser/issues/5159 | `UNFIXED` |
| 2660360181 | Bedrock mining advantage | https://github.com/GeyserMC/Geyser/issues/5152 | `UNFIXED` |
| 2634025004 | Lead links sometimes don't appear. | https://github.com/GeyserMC/Geyser/issues/5123 | `UNFIXED` |
| 2573240553 | Bug with GSit /lay command on bedrock | https://github.com/GeyserMC/Geyser/issues/5073 | `UNFIXED` |
| 2541243115 | Cannot move player correctly, cannot interact with things | https://github.com/GeyserMC/Geyser/issues/5057 | `UNFIXED` |
| 2540190174 | Implement JFR profiler on the "geyser" command for standalone. | https://github.com/GeyserMC/Geyser/issues/5053 | `UNFIXED` |
| 2535701984 | Crafting issue | https://github.com/GeyserMC/Geyser/issues/5047 | `UNFIXED` |
| 2525964971 | Custom Tools with attributes not working on bedrock  | https://github.com/GeyserMC/Geyser/issues/5037 | `UNFIXED` |
| 2512015492 | the shopkeeper plugin is not working | https://github.com/GeyserMC/Geyser/issues/5025 | `UNFIXED` |
| 2508374022 | Character Creator Items Support for only Bedrock Players | https://github.com/GeyserMC/Geyser/issues/5020 | `UNFIXED` |
| 2503140101 | Sitting players float in the pov of bedrock players | https://github.com/GeyserMC/Geyser/issues/5017 | `UNFIXED` |
| 2485051625 | Redstone dust direction | https://github.com/GeyserMC/Geyser/issues/4992 | `UNFIXED` |
| 2482019437 | Piston heads | https://github.com/GeyserMC/Geyser/issues/4985 | `UNFIXED` |
| 2479498301 | An RCON client to JE console in Geyser Standalone | https://github.com/GeyserMC/Geyser/issues/4981 | `UNFIXED` |
| 2470737416 | Geyser players can't move edited items | https://github.com/GeyserMC/Geyser/issues/4970 | `UNFIXED` |
| 2434631878 | Ghost Blocks when attempting to place a block that has an active item Cooldown  | https://github.com/GeyserMC/Geyser/issues/4911 | `UNFIXED` |
| 2434115139 | Geyser players cannot use enchanted books on anvil | https://github.com/GeyserMC/Geyser/issues/4909 | `UNFIXED` |
| 2420932284 | Non-functionality of custom arrows with applied texture with Geyser | https://github.com/GeyserMC/Geyser/issues/4881 | `UNFIXED` |
| 2414195123 | Can't Strip Logs When Holding Shield | https://github.com/GeyserMC/Geyser/issues/4871 | `IN_PROGRESS` |
| 2406819757 | Crafting certain items puts them into crafting grid instead of inventory | https://github.com/GeyserMC/Geyser/issues/4855 | `UNFIXED` |
| 2406672284 | GENERIC_STEP_HEIGHT attribute is not handled correctly for players | https://github.com/GeyserMC/Geyser/issues/4854 | `UNFIXED` |
| 2394087076 | Item drop animation issue(honeyblock,bedrock) | https://github.com/GeyserMC/Geyser/issues/4832 | `UNFIXED` |
| 2380557976 | falling_dust showing incorrectly | https://github.com/GeyserMC/Geyser/issues/4803 | `UNFIXED` |
| 2342293640 | [Geyser-Spigot] Missing locale: de_CH (Commit 0fcf0f9) | https://github.com/GeyserMC/Geyser/issues/4725 | `UNFIXED` |
| 2326841512 | Firework Crafting Issues for Bedrock Players | https://github.com/GeyserMC/Geyser/issues/4707 | `UNFIXED` |
| 2255098084 | Signs dissapear after relogging | https://github.com/GeyserMC/Geyser/issues/4588 | `UNFIXED` |
| 2154934969 | Bedrock Classic Skin Wrapping Incorrectly on Model | https://github.com/GeyserMC/Geyser/issues/4464 | `UNFIXED` |
| 2146683715 | Weird Slow Crafting | https://github.com/GeyserMC/Geyser/issues/4453 | `UNFIXED` |
| 2097630743 | Player hand shaking | https://github.com/GeyserMC/Geyser/issues/4409 | `UNFIXED` |
| 2069382809 | Bedrock players getting "moved too quickly!" WARN using warps and TP to claim | https://github.com/GeyserMC/Geyser/issues/4383 | `UNFIXED` |
| 2061878718 | Elytra cape texture doesn't get displayed on Bedrock | https://github.com/GeyserMC/Geyser/issues/4374 | `UNFIXED` |
| 2054838682 | Custom Itemsadder shield blocking not working for bedrock players | https://github.com/GeyserMC/Geyser/issues/4359 | `UNFIXED` |
| 2022879416 | Some Player Head textures display white when placed | https://github.com/GeyserMC/Geyser/issues/4325 | `UNFIXED` |
| 2015861853 | You cannot place torches on containers while flying + sneaking | https://github.com/GeyserMC/Geyser/issues/4317 | `UNFIXED` |
| 2005161425 | ActionBar bounces up and down | https://github.com/GeyserMC/Geyser/issues/4304 | `UNFIXED` |
| 1978045936 | Teleported item frames not updating position on bedrock | https://github.com/GeyserMC/Geyser/issues/4274 | `UNFIXED` |
| 1964944357 | Auto update skins? | https://github.com/GeyserMC/Geyser/issues/4249 | `UNFIXED` |
| 1943675261 | A way to forward command descriptions in geyser standalone | https://github.com/GeyserMC/Geyser/issues/4215 | `UNFIXED` |
| 1931357359 | Bedrock players don't see skins with custom models | https://github.com/GeyserMC/Geyser/issues/4195 | `UNFIXED` |
| 1916653451 | Suggestion: make players who have invisibility temporarily visible when the glowing affect is applied. | https://github.com/GeyserMC/Geyser/issues/4156 | `UNFIXED` |
| 1888639047 | Lectern signal strength inconsistent with Java edition | https://github.com/GeyserMC/Geyser/issues/4113 | `UNFIXED` |
| 1884438284 | Villager trading on mobile disregards which trade you select, and always gives you the first trade. | https://github.com/GeyserMC/Geyser/issues/4102 | `UNFIXED` |
| 1873967017 | Trading doesn't seem to be working in the end only for geyser users. | https://github.com/GeyserMC/Geyser/issues/4092 | `UNFIXED` |
| 1870724269 | Allow items to map to custom blocks | https://github.com/GeyserMC/Geyser/issues/4090 | `UNFIXED` |
| 1853575554 | Dismounting Fake Entity | https://github.com/GeyserMC/Geyser/issues/4058 | `UNFIXED` |
| 1819258094 | FishHook on Lava with CustomFishing | https://github.com/GeyserMC/Geyser/issues/4012 | `UNFIXED` |
| 1806744583 | Client crashes when starting to type a command | https://github.com/GeyserMC/Geyser/issues/3990 | `UNFIXED` |
| 1805860584 | Unfair Advantage in Mining Speeds between Bedrock & Java | https://github.com/GeyserMC/Geyser/issues/3987 | `UNFIXED` |
| 1788104735 | Fast Build with glass panes and other blocks broken but looks like it works | https://github.com/GeyserMC/Geyser/issues/3937 | `UNFIXED` |
| 1784729187 | Bedrock-Issue with close-event | https://github.com/GeyserMC/Geyser/issues/3932 | `UNFIXED` |
| 1778025057 | Unable to brush suspicious sand/gravel | https://github.com/GeyserMC/Geyser/issues/3921 | `UNFIXED` |
| 1772429544 | BE players can't see their current location on the map in nether or the end | https://github.com/GeyserMC/Geyser/issues/3910 | `UNFIXED` |
| 1750783737 | BE slim (3px) skin is not reflected correctly | https://github.com/GeyserMC/Geyser/issues/3856 | `UNFIXED` |
| 1739441457 | item/block display entities | https://github.com/GeyserMC/Geyser/issues/3810 | `UNFIXED` |
| 1686001459 | Java players can't push bedrock players | https://github.com/GeyserMC/Geyser/issues/3702 | `UNFIXED` |
| 1671866467 | Cannot Copy Books | https://github.com/GeyserMC/Geyser/issues/3682 | `UNFIXED` |
| 1666874262 | Bedrock can fly under certain conditions | https://github.com/GeyserMC/Geyser/issues/3678 | `UNFIXED` |
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
| 1428000485 | Employ fix for headless Armor Stand | https://github.com/GeyserMC/Geyser/issues/3373 | `UNFIXED` |
| 1422823529 | Packet intercepting and sending API | https://github.com/GeyserMC/Geyser/issues/3365 | `UNFIXED` |
| 1419228638 | Camouflage blocks, GeyserMC players see teleportation | https://github.com/GeyserMC/Geyser/issues/3358 | `UNFIXED` |
| 1397034352 | Command description support for geyser standalone | https://github.com/GeyserMC/Geyser/issues/3337 | `UNFIXED` |
| 1395538072 | Stopsound stops all sounds on bedrock regardless of chosen category | https://github.com/GeyserMC/Geyser/issues/3335 | `UNFIXED` |
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
| 1052791662 | Xbox achievements not unlocking properly with Geyser | https://github.com/GeyserMC/Geyser/issues/2629 | `UNFIXED` |
| 988382023 | Move mappings to config files | https://github.com/GeyserMC/Geyser/issues/2514 | `UNFIXED` |
| 976467865 | Other players' boats have very jerky movement | https://github.com/GeyserMC/Geyser/issues/2490 | `UNFIXED` |
| 945607347 | Content log spam on server join/switch worlds & command suggestions not working ([Commands][error]-Keywords in commands need to be lower case. '<argument>' was not registered.) | https://github.com/GeyserMC/Geyser/issues/2385 | `UNFIXED` |
| 938291459 | End crystals don't move when pushed by pistons | https://github.com/GeyserMC/Geyser/issues/2358 | `UNFIXED` |
| 869272079 | Bedrock clients are unable to crawl when using the GSit plugin | https://github.com/GeyserMC/Geyser/issues/2180 | `UNFIXED` |
| 809712905 | Itemframe Placing Bug | https://github.com/GeyserMC/Geyser/issues/1938 | `UNFIXED` |
| 805141067 | Infinite up movement while elytra flying in gamemode creative | https://github.com/GeyserMC/Geyser/issues/1916 | `UNFIXED` |
| 802261469 | Standing against a wall and placing a door inside of yourself causes issues | https://github.com/GeyserMC/Geyser/issues/1902 | `UNFIXED` |
| 789551268 | Hide enchants attribute doesn't work | https://github.com/GeyserMC/Geyser/issues/1852 | `UNFIXED` |
| 777380397 | Holding shift while planting crops causes the food to be eaten | https://github.com/GeyserMC/Geyser/issues/1782 | `UNFIXED` |
| 775688478 | Support for 1.16 custom biomes | https://github.com/GeyserMC/Geyser/issues/1752 | `UNFIXED` |
| 633624088 | Splitscreen support | https://github.com/GeyserMC/Geyser/issues/750 | `UNFIXED` |

### Open pull requests

_(56 records)_

| ID | Title | URL | Status |
| ---: | --- | --- | --- |
| 5446883801 | Improve functionality of the Nether roof workaround | https://github.com/GeyserMC/Geyser/pull/6936 | `UNFIXED` |
| 5398766503 | Fix: include Git SHA in Spigot plugin version | https://github.com/GeyserMC/Geyser/pull/6931 | `IN_PROGRESS` |
| 5387806158 | Fill server-side command suggestions on Bedrock | https://github.com/GeyserMC/Geyser/pull/6928 | `UNFIXED` |
| 5368363595 | Keep the name tag of entities that only carry text displays | https://github.com/GeyserMC/Geyser/pull/6925 | `IN_PROGRESS` |
| 5360752965 | Introduce `.geyserpack` loading | https://github.com/GeyserMC/Geyser/pull/6922 | `UNFIXED` |
| 5330888326 | FIX synchronize item frames after teleportation | https://github.com/GeyserMC/Geyser/pull/6921 | `UNFIXED` |
| 5295765293 | Register vanilla tools as custom items to fix block breaking times | https://github.com/GeyserMC/Geyser/pull/6916 | `UNFIXED` |
| 5286632827 | build(deps): use the MCProtocolLib codec fixes build | https://github.com/GeyserMC/Geyser/pull/6914 | `UNFIXED` |
| 5275355313 | Match Vanilla styles in custom names and lore to fully normalize the text component | https://github.com/GeyserMC/Geyser/pull/6909 | `UNFIXED` |
| 5233348431 | Add texture variation and alpha masked tint to material instance API | https://github.com/GeyserMC/Geyser/pull/6905 | `UNFIXED` |
| 5216900616 | Add culling to custom block geometry component | https://github.com/GeyserMC/Geyser/pull/6902 | `UNFIXED` |
| 5205909839 | Fix Xbox 26.60 LAN discovery and joins | https://github.com/GeyserMC/Geyser/pull/6896 | `IN_PROGRESS` |
| 5194626275 | Improved debugging information in Geyser dumps | https://github.com/GeyserMC/Geyser/pull/6890 | `IN_PROGRESS` |
| 5176617470 | Fix destruction stages | https://github.com/GeyserMC/Geyser/pull/6883 | `IN_PROGRESS` |
| 5130807082 | Debugging NetherNet issues | https://github.com/GeyserMC/Geyser/pull/6873 | `UNFIXED` |
| 5112693462 | Load custom mappings from urls and extensions | https://github.com/GeyserMC/Geyser/pull/6866 | `UNFIXED` |
| 5112427414 | Add optional tooltip style markers for resource packs | https://github.com/GeyserMC/Geyser/pull/6863 | `UNFIXED` |
| 5062483162 | Add ip-discovery, for servers where STUN cannot find the public address | https://github.com/GeyserMC/Geyser/pull/6844 | `IN_PROGRESS` |
| 5053287223 | Apply server-sided Dolphin's Grace | https://github.com/GeyserMC/Geyser/pull/6840 | `UNFIXED` |
| 5035241143 | Improve translation of commands | https://github.com/GeyserMC/Geyser/pull/6833 | `UNFIXED` |
| 5023900867 | Custom biome API, mappings, and generated Bedrock visuals | https://github.com/GeyserMC/Geyser/pull/6829 | `UNFIXED` |
| 4943303365 | ViaProxy: don't fail IP passthrough when a downstream session has no local address yet | https://github.com/GeyserMC/Geyser/pull/6812 | `IN_PROGRESS` |
| 4938616313 | Implement abstract model for sharing data across sessions | https://github.com/GeyserMC/Geyser/pull/6811 | `UNFIXED` |
| 4842813446 | Implement Text Displays via Primitive Text | https://github.com/GeyserMC/Geyser/pull/6768 | `UNFIXED` |
| 4805839353 | Fix custom tridents and spears | https://github.com/GeyserMC/Geyser/pull/6759 | `IN_PROGRESS` |
| 4744708298 | fix: prevent NPE when attacking Ender Dragon parts | https://github.com/GeyserMC/Geyser/pull/6748 | `IN_PROGRESS` |
| 4569560222 | Fix collision loading and addon-pack flags for non-vanilla blocks | https://github.com/GeyserMC/Geyser/pull/6724 | `UNFIXED` |
| 4540681458 | Fix client-side waterlogging prediction on doors and non-waterloggable blocks | https://github.com/GeyserMC/Geyser/pull/6706 | `UNFIXED` |
| 4496366694 | Lava fishing workaround | https://github.com/GeyserMC/Geyser/pull/6694 | `UNFIXED` |
| 4476271563 | Add support for Npc Dialogues | https://github.com/GeyserMC/Geyser/pull/6690 | `UNFIXED` |
| 4457015299 | Xbox split-screen support, reworked onto master and fixed on hardware | https://github.com/GeyserMC/Geyser/pull/6689 | `IN_PROGRESS` |
| 4440438587 | Move recipe shift clicking to use ServerboundPlaceRecipePacket | https://github.com/GeyserMC/Geyser/pull/6686 | `UNFIXED` |
| 4345263370 | Avoid have no response when trading using touch or the Shift key cause by client bug since v1.21.30 | https://github.com/GeyserMC/Geyser/pull/6667 | `IN_PROGRESS` |
| 4336073923 | Improve `manifest.json` search loop | https://github.com/GeyserMC/Geyser/pull/6664 | `IN_PROGRESS` |
| 4286226682 | Add model-specific custom block item overrides support | https://github.com/GeyserMC/Geyser/pull/6643 | `UNFIXED` |
| 4258541122 | Fix: Bound the Bedrock projection of oversized books | https://github.com/GeyserMC/Geyser/pull/6627 | `IN_PROGRESS` |
| 4258382559 | Fix: Let real blocks take priority over item frames | https://github.com/GeyserMC/Geyser/pull/6626 | `UNFIXED` |
| 4234891940 | Add extensible server-authoritative custom block breaking | https://github.com/GeyserMC/Geyser/pull/6605 | `UNFIXED` |
| 4203696760 | Introduce useful debug command | https://github.com/GeyserMC/Geyser/pull/6586 | `UNFIXED` |
| 4172942473 | Fix geyser connection timeout caused by translation expansion | https://github.com/GeyserMC/Geyser/pull/6579 | `IN_PROGRESS` |
| 4170224258 | Download Java resourcepacks sent by the server, and load translations from them | https://github.com/GeyserMC/Geyser/pull/6578 | `UNFIXED` |
| 4150378049 | Refactor WebUtils to make use of modern HTTP APIs in Java | https://github.com/GeyserMC/Geyser/pull/6573 | `UNFIXED` |
| 4111042967 | Interpolate per-tick entity movement on Bedrock | https://github.com/GeyserMC/Geyser/pull/6566 | `UNFIXED` |
| 4083854339 | Improve bamboo collision | https://github.com/GeyserMC/Geyser/pull/6560 | `IN_PROGRESS` |
| 4078328957 | Fix collision for copper bars and turtle eggs | https://github.com/GeyserMC/Geyser/pull/6555 | `IN_PROGRESS` |
| 4008215209 | Change logger to a static reference and add some debugging commands | https://github.com/GeyserMC/Geyser/pull/6513 | `UNFIXED` |
| 3948152368 | Attempt to lower scoreboard allocs | https://github.com/GeyserMC/Geyser/pull/6489 | `UNFIXED` |
| 3707133775 | Fix block breaking | https://github.com/GeyserMC/Geyser/pull/6419 | `UNFIXED` |
| 3662155537 | Add proper combat sounds | https://github.com/GeyserMC/Geyser/pull/6393 | `UNFIXED` |
| 3142621945 | Feature: Reloadable Mappings | https://github.com/GeyserMC/Geyser/pull/6081 | `UNFIXED` |
| 3044136529 | Workaround for client-sided removal of all items from the crafting grid on inventory close | https://github.com/GeyserMC/Geyser/pull/6008 | `IN_PROGRESS` |
| 2875767111 | Custom Translation API | https://github.com/GeyserMC/Geyser/pull/5860 | `UNFIXED` |
| 2670923346 | Full Java tooltip parity (as much as that is possible) | https://github.com/GeyserMC/Geyser/pull/5692 | `UNFIXED` |
| 2662538702 | Geyser Networking API | https://github.com/GeyserMC/Geyser/pull/5685 | `UNFIXED` |
| 2593566343 | Anvil updater refactor and other small refactors | https://github.com/GeyserMC/Geyser/pull/5595 | `UNFIXED` |
| 2047932455 | Support for max stack size | https://github.com/GeyserMC/Geyser/pull/5011 | `UNFIXED` |
