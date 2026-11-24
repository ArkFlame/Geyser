/*
 * Copyright (c) 2026 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.translator.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.kyori.adventure.key.Key;
import org.cloudburstmc.protocol.bedrock.data.command.CommandEnumConstraint;
import org.cloudburstmc.protocol.bedrock.data.command.CommandEnumData;
import org.geysermc.geyser.command.CommandRegistry;
import org.geysermc.geyser.command.GeyserCommandSource;
import org.geysermc.geyser.registry.java.JavaRegistryKey;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.session.cache.ServerSuggestionCache;
import org.geysermc.geyser.session.cache.tags.Tag;
import org.geysermc.geyser.translator.command.BuiltInCommandEnums;
import org.geysermc.mcprotocollib.protocol.data.game.command.CommandNode;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Accessors(fluent = true)
public final class CommandTranslationData implements CommonTranslationData.RecordBased {
    private static final Pattern LOWERCASE_LETTERS = Pattern.compile("^[a-z]+$");

    @Getter
    private final CommonTranslationData commonData;
    @Getter
    private final GeyserCommandSource commandSource;
    @Getter
    private final CommandRegistry commandRegistry;
    @Getter
    private final CommandNode[] nodes;
    @Getter
    private final int firstNodeIndex;

    private final GeyserSession session;
    private final ServerSuggestionCache suggestionCache;

    private final Map<JavaRegistryKey<?>, String[]> registryElements = new Reference2ObjectOpenHashMap<>();
    private final Map<JavaRegistryKey<?>, String[]> registryElementsAndTags = new Reference2ObjectOpenHashMap<>();
    private final Map<BuiltInCommandEnums.Key, CommandEnumData> builtInEnums = new Reference2ObjectOpenHashMap<>();
    private final Map<Map<String, Set<CommandEnumConstraint>>, CommandEnumData> enums = new Object2ObjectOpenHashMap<>();
    private final Set<String> softEnumNames = new ObjectOpenHashSet<>();
    private final Set<String> hardEnumNames = new ObjectOpenHashSet<>();
    /**
     * Command texts up to an argument the Java server suggests itself, e.g. "/home "
     */
    private final Set<String> serverRequests = new ObjectOpenHashSet<>();

    public CommandTranslationData(GeyserSession session, CommandNode[] nodes, int firstNodeIndex) {
        this.session = session;
        this.suggestionCache = session.getServerSuggestionCache();
        this.commonData = new CommonTranslationData.Impl(session);
        this.commandSource = session;
        this.commandRegistry = session.getGeyser().commandRegistry();
        this.nodes = nodes;
        this.firstNodeIndex = firstNodeIndex;

        BuiltInCommandEnums.register(new BuiltInCommandEnums.Registry() {
            @Override
            public void accept(BuiltInCommandEnums.Key key, String... values) {
                Map<String, Set<CommandEnumConstraint>> constraints = new Object2ObjectLinkedOpenHashMap<>();
                for (String value : values) {
                    constraints.put(value, Set.of());
                }
                // commandName doesn't matter in this context, all names should be unique anyway
                builtInEnums.put(key, constructEnum("", key.name(), constraints));
            }

            @Override
            public void acceptSoft(BuiltInCommandEnums.Key key, String... values) {
                Map<String, Set<CommandEnumConstraint>> constraints = new Object2ObjectLinkedOpenHashMap<>();
                for (String value : values) {
                    constraints.put(value, Set.of());
                }
                builtInEnums.put(key, constructSoftEnum(key.name(), constraints));
            }

            @Override
            public String[] teams() {
                return session.getWorldCache().getScoreboard().getTeamNames().toArray(String[]::new);
            }

            @Override
            public String[] levels() {
                return session.getLevels();
            }
        });
    }

    /**
     * Updates the session's view of the commands the Java server sent us.
     */
    public void updateKnownCommands(Set<String> knownCommands, Set<String> restrictedCommands) {
        session.setKnownCommands(knownCommands.stream().map(command -> command.toLowerCase(java.util.Locale.ROOT)).distinct().toList());
        session.setRestrictedCommands(restrictedCommands.stream().map(command -> command.toLowerCase(java.util.Locale.ROOT)).distinct().toList());
    }

    public String[] getRegistryElements(JavaRegistryKey<?> registry) {
        return registryElements.computeIfAbsent(registry, $ -> javaRegistries().registry(registry).keys().stream()
            .map(Key::asString)
            .toArray(String[]::new));
    }

    public String[] getRegistryElementsAndTags(JavaRegistryKey<?> registry) {
        return registryElementsAndTags.computeIfAbsent(registry, $ -> Stream.concat(javaRegistries().tags(registry)
                .map(Tag::tag)
                .map(Key::asString)
                // Prefix tags with a '#'
                .map(string -> '#' + string), Arrays.stream(getRegistryElements(registry)))
            .toArray(String[]::new));
    }

    public CommandEnumData builtInEnum(BuiltInCommandEnums.Key key) {
        return Objects.requireNonNull(builtInEnums.get(key), "built-in enum not registered: " + key.name());
    }

    public CommandEnumData constructEnum(String commandName, String proposedName, Map<String, Set<CommandEnumConstraint>> constraints) {
        return enums.computeIfAbsent(constraints, $ -> {
            boolean soft = needsSoftEnum(constraints);
            String safeName = findSafeEnumName(soft ? softEnumNames : hardEnumNames, commandName, proposedName);
            return new CommandEnumData(safeName, constraints, soft);
        });
    }

    /**
     * Registers the command text an argument needs suggestions for, and returns a name for its enum.
     *
     * @param typed the command typed up to this argument, e.g. "/home"
     * @param paramName the name of the argument node
     */
    public String registerServerRequest(String typed, String paramName) {
        serverRequests.add(typed + " ");
        String base = typed.startsWith("/") ? typed.substring(1) : typed;
        return plainEnumName(base + "_" + paramName);
    }

    /**
     * @return the suggestions the Java server sent for the given command text
     */
    public Set<String> suggestions(String text) {
        return suggestionCache.suggestions(text);
    }

    /**
     * @return the suggestions for the argument after {@code value}
     */
    public Set<String> suggestionsAfter(String text, String value) {
        return suggestionCache.suggestionsAfter(text, value);
    }

    public boolean onlyPlayers(Set<String> values) {
        return suggestionCache.onlyPlayers(values);
    }

    /**
     * @return a hard enum name not used by any other enum in this packet
     */
    public String uniqueServerEnumName(String proposed) {
        String plain = plainEnumName(proposed);
        String unique = plain;
        for (int i = 2; !hardEnumNames.add(unique); i++) {
            unique = plain + i;
        }
        return unique;
    }

    /**
     * Server-suggested values keep their case, so they cannot be a soft enum (Bedrock does not offer those in chat)
     * and only lower case values are sent as enums by the caller.
     */
    public CommandEnumData constructServerEnum(String name, Map<String, Set<CommandEnumConstraint>> constraints) {
        return new CommandEnumData(name, constraints, false);
    }

    /**
     * Asks the server for the registered suggestions, and sends the commands again once they are in.
     *
     * @param resendCommands resends the command packet, building the packet again with the new suggestions
     */
    public void updateServerSuggestionRequests(Runnable resendCommands) {
        suggestionCache.setRequests(Set.copyOf(serverRequests), resendCommands);
    }

    private static String plainEnumName(String proposed) {
        // Bedrock shows the enum name in the command hint; keep it to plain characters
        return proposed.replaceAll("[^A-Za-z0-9_]", "_");
    }

    private CommandEnumData constructSoftEnum(String proposedName, Map<String, Set<CommandEnumConstraint>> constraints) {
        String safeName = findSafeEnumName(softEnumNames, "", proposedName);
        return new CommandEnumData(safeName, constraints, true);
    }

    private static String findSafeEnumName(Set<String> usedNames, String commandName, String proposedName) {
        if (usedNames.add(proposedName)) {
            return proposedName;
        }

        String safeName = commandName + "_" + proposedName;
        for (int i = 2; usedNames.contains(safeName); i++) {
            safeName = commandName + "_" + proposedName + "_" + i;
        }
        usedNames.add(safeName);
        return safeName;
    }

    /**
     * Bedrock rejects fixed enum values that are not lowercase ("Keywords in commands need to be
     * lower case"), while soft enum values keep their case. Brigadier literals are case-sensitive,
     * so the exact spelling has to survive the round trip. Vanilla BDS uses the same split.
     */
    private static boolean needsSoftEnum(Map<String, Set<CommandEnumConstraint>> constraints) {
        for (String key : constraints.keySet()) {
            if (!LOWERCASE_LETTERS.matcher(key).matches()) {
                return true;
            }
        }
        return false;
    }
}
