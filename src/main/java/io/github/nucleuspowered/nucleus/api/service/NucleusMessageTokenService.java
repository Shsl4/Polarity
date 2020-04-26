/*
 * This file is part of Nucleus, licensed under the MIT License (MIT). See the LICENSE.txt file
 * at the root of this project for more details.
 */
package io.github.nucleuspowered.nucleus.api.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.github.nucleuspowered.nucleus.api.exceptions.NucleusException;
import io.github.nucleuspowered.nucleus.api.exceptions.PluginAlreadyRegisteredException;
import io.github.nucleuspowered.nucleus.api.text.NucleusTextTemplate;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.util.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Allows plugins to register their own tokens for use in templated messages, and to obtain {@link NucleusTextTemplate} instances.
 */
public interface NucleusMessageTokenService {

    /**
     * Registers a {@link TokenParser} for the specified {@link PluginContainer}. Plugins may only register ONE {@link TokenParser}.
     *
     * @param pluginContainer The {@link PluginContainer} of the plugin.
     * @param parser The {@link TokenParser} that recieves the identifier from the token, along with any contextual variables and
     *               the target {@link MessageReceiver} of the message.
     * @throws PluginAlreadyRegisteredException Thrown if the token has been registered previously.
     */
    void register(PluginContainer pluginContainer, TokenParser parser) throws PluginAlreadyRegisteredException;

    /**
     * Unregisters the {@link TokenParser} for a plugin.
     *
     * @param pluginContainer The {@link PluginContainer} of the plugin.
     * @return <code>true</code> if successful.
     */
    boolean unregister(PluginContainer pluginContainer);

    /**
     * A primary token is a token that does not have a plugin identifier - such as <code>{{prefix}}</code>. Plugins can register their
     * own tokens on a first come first served basis. There are some notes:
     *
     * <ul>
     *     <li>
     *         If the token you wish to register has already been registered, then you cannot register over it.
     *     </li>
     *     <li>
     *         Tokens are case insensitive.
     *     </li>
     *     <li>
     *         Tokens cannot have space, colon, pipe or brace characters in them.
     *     </li>
     *     <li>
     *         The token is simply an alias for {{pl:plugin:identifier}}. The primary identifier does NOT have to be the same as the token's
     *         identifier.
     *     </li>
     * </ul>
     *
     * Primary tokens <em>can</em> have extra data, <strong>but</strong> extra data must appear after a pipe character ("|"). So, a token "bacon"
     * that has the extra data "eggs" would be {{bacon|eggs}}. You only need to register the "bacon" part.
     *
     * @param primaryIdentifier The identifier that you wish to be able to use, without {} marks. So, if you want to register {{clan}}, the input
     * should be "clan".
     * @param registeringPlugin The {@link PluginContainer} of the plugin that will control this token.
     * @param identiferToMapTo The identifier that this token will map to - so if {{clan}} should map to {{pl:clans:clanname}}, this should be
     * clanname.
     * @return <code>true</code> if the mapping was registered.
     */
    boolean registerPrimaryToken(String primaryIdentifier, PluginContainer registeringPlugin, String identiferToMapTo);

    /**
     * Gets the {@link TokenParser} for the specified {@link PluginContainer}, if it exists.
     *
     * @param pluginContainer The {@link PluginContainer} of the pluginContainer that registered the token.
     * @return The {@link TokenParser} that is run for the token, if it exists.
     */
    default Optional<TokenParser> getTokenParser(PluginContainer pluginContainer) {
        Preconditions.checkNotNull(pluginContainer, "pluginContainer");
        return getTokenParser(pluginContainer.getId());
    }

    /**
     * Gets the {@link TokenParser} for the specified plugin id, if it exists.
     *
     * @param plugin The ID of the plugin that registered the token.
     * @return The {@link TokenParser} that is run for the token, if it exists.
     */
    Optional<TokenParser> getTokenParser(String plugin);

    /**
     * Gets the result of a token's registered {@link TokenParser} on a {@link CommandSource}
     *
     * @param plugin The ID of the plugin that registered the token.
     * @param token The identifier that is passed to the {@link TokenParser}.
     * @param source The {@link CommandSource} to perform the operation with.
     * @return The {@link Text}, if any.
     */
    default Optional<Text> applyToken(String plugin, String token, CommandSource source) {
        return applyToken(plugin, token, source, Maps.newHashMap());
    }

    /**
     * Gets the {@link TokenParser} and specific token identifier for a primary token.
     *
     * @param primaryToken The primary token, without { and } characters.
     * @return A {@link Tuple} with the {@link TokenParser} and specific identifier to pass to the parser.
     */
    Optional<Tuple<TokenParser, String>> getPrimaryTokenParserAndIdentifier(String primaryToken);

    /**
     * Gets the currently registered primary tokens.
     *
     * @return The primary tokens.
     */
    List<String> getPrimaryTokens();

    /**
     * Gets the result of a token's registered {@link TokenParser} on a {@link CommandSource}
     *
     * @param plugin The ID of the plugin that registered the token.
     * @param token The identifier that is passed to the {@link TokenParser}.
     * @param source The {@link CommandSource} to perform the operation with.
     * @param variables The variables that could be used in the token.
     * @return The {@link Text}, if any.
     */
    default Optional<Text> applyToken(String plugin, String token, CommandSource source, Map<String, Object> variables) {
        Optional<TokenParser> tokenFunction = getTokenParser(plugin);
        return tokenFunction.flatMap(tokenParser -> tokenParser.parse(token, source, variables));

    }

    /**
     * Gets the result of a primary token's registered {@link TokenParser} on a {@link CommandSource}
     *
     * @param primaryToken The primary identifier that parsed.
     * @param source The {@link CommandSource} to perform the operation with.
     * @return The {@link Text}, if any.
     */
    default Optional<Text> applyPrimaryToken(String primaryToken, CommandSource source) {
        return applyPrimaryToken(primaryToken, source, Maps.newHashMap());
    }

    /**
     * Gets the result of a primary token's registered {@link TokenParser} on a {@link CommandSource}
     *
     * @param primaryToken The primary identifier that parsed.
     * @param source The {@link CommandSource} to perform the operation with.
     * @param variables The variables that could be used in the token.
     * @return The {@link Text}, if any.
     */
    default Optional<Text> applyPrimaryToken(String primaryToken, CommandSource source, Map<String, Object> variables) {
        Preconditions.checkArgument(primaryToken != null && !primaryToken.isEmpty());
        String[] tokenData = primaryToken.split("\\|", 2);
        return getPrimaryTokenParserAndIdentifier(tokenData[0].toLowerCase())
                .flatMap(x -> x.getFirst().parse(tokenData.length == 2 ? x.getSecond() + "|" + tokenData[1] : x.getSecond(), source, variables));
    }

    /**
     * Parses a token that might be either a primary token or a standard token.
     *
     * @param token The token, without the delimiters.
     * @param source The source to apply the tokens with.
     * @return The token result, if it exists.
     */
    default Optional<Text> parseToken(String token, CommandSource source) {
        return parseToken(token, source, null);
    }

    /**
     * Parses a token that might be either a primary token or a standard token.
     *
     * @param token The token, without the delimiters.
     * @param source The source to apply the tokens with.
     * @param variables The variables to pass to the parser.
     * @return The token result, if it exists.
     */
    Optional<Text> parseToken(String token, CommandSource source, @Nullable Map<String, Object> variables);

    /**
     * Allows users to register additional token delimiter formats. For example, if a token wanted to register {%id%} to run
     * {{pl:blah:id}}, then they would need to run:
     *
     * <pre>
     *     registerTokenFormat("{%", "%}", "pl:blah:$1")
     * </pre>
     * <p>
     *     Note that the third argument contains $1. This method will replace $1 with the ID.
     * </p>
     * <p>
     *     This will only apply to Ampersand formatted strings.
     * </p>
     *
     * @param tokenStart The start delimiter of a token to register.
     * @param tokenEnd The end delimiter of a token to register.
     * @param replacement The form of the token identifier to use (without the {{,}} delimiters).
     * @return <code>true</code> if successful.
     * @throws IllegalArgumentException thrown if the delimiters are illegal.
     */
    boolean registerTokenFormat(String tokenStart, String tokenEnd, String replacement) throws IllegalArgumentException;

    /**
     * Creates a {@link NucleusTextTemplate} from a string, which could be either Json or Ampersand formatted.
     *
     * @param string The string to register.
     * @return The {@link NucleusTextTemplate} that can be parsed.
     * @throws NucleusException thrown if the string could not be parsed.
     */
    NucleusTextTemplate createFromString(String string) throws NucleusException;

    /**
     * A parser for tokens directed at a plugin. Plugins can only register ONE of these.
     *
     * <p>
     *     To understand what is returned to the parser, it's worth reminding the implementor what the token that users will use look like:
     * </p>
     * <blockquote>
     *     {{pl:plugin-id:identifier:s}}
     * </blockquote>
     * <p>
     *     Plugins will only <em>ever</em> see the <code>identifier</code> part of the token. This token can take any form, as chosen by the
     *     plugin, except for the <code>}}</code> sequence.
     * </p>
     */
    @FunctionalInterface
    interface TokenParser {

        /**
         * Parses a plugin's token and returns {@link Text}, if any.
         *
         * @param tokenInput The identifier for the token.
         * @param source The {@link CommandSource} that will be viewing the output of this token.
         * @param variables A map of variable names to variable objects. Consult documentation for the
         *                  variables that might be caused by an event or command.
         * @return The {@link Text} to display, or {@link Optional#empty()} if the token cannot be parsed.
         */
        @Nonnull
        Optional<Text> parse(String tokenInput, CommandSource source, Map<String, Object> variables);
    }
}
