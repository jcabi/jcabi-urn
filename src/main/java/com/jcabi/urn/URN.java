/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.urn;

import com.jcabi.aspects.Immutable;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.TreeMap;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * Uniform Resource Name (URN) as in
 * <a href="http://tools.ietf.org/html/rfc2141">RFC 2141</a>.
 *
 * <p>Usage is similar to {@link java.net.URI} or {@link java.net.URL}:
 *
 * <pre> URN urn = new URN("urn:foo:A123,456");
 * assert urn.nid().equals("foo");
 * assert urn.nss().equals("A123,456");</pre>
 *
 * <p><b>NOTICE:</b> the implementation is not fully compliant with RFC 2141.
 * It will become compliant in one of our future versions. Once it becomes
 * fully compliant this notice will be removed.
 *
 * @since 0.6
 * @see <a href="http://tools.ietf.org/html/rfc2141">RFC 2141</a>
 * @checkstyle AbbreviationAsWordInNameCheck (500 lines)
 */
@Immutable
@EqualsAndHashCode
@SuppressWarnings({
    "PMD.TooManyMethods", "PMD.UseConcurrentHashMap", "PMD.GodClass",
    "PMD.OnlyOneConstructorShouldDoInitialization"
})
public final class URN implements Comparable<URN>, Serializable {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = 0xBF46AFCD9612A6DFL;

    /**
     * Encoding to use.
     */
    private static final String ENCODING = "UTF-8";

    /**
     * NID of an empty URN.
     */
    private static final String EMPTY = "void";

    /**
     * The leading sequence.
     */
    private static final String PREFIX = "urn";

    /**
     * The separator.
     */
    private static final String SEP = ":";

    /**
     * Validating regular expr.
     */
    private static final String REGEX =
        // @checkstyle LineLength (1 line)
        "^(?i)^urn(?-i):[a-z]{1,31}(:([\\-a-zA-Z0-9/]|%[0-9a-fA-F]{2})*)+(\\?\\w+(=([\\-a-zA-Z0-9/]|%[0-9a-fA-F]{2})*)?(&\\w+(=([\\-a-zA-Z0-9/]|%[0-9a-fA-F]{2})*)?)*)?\\*?$";

    /**
     * The URI.
     */
    @SuppressWarnings("PMD.BeanMembersShouldSerialize")
    private final String uri;

    /**
     * Public ctor (for JAXB mostly) that creates an "empty" URN.
     */
    public URN() {
        this(URN.EMPTY, "");
    }

    /**
     * Public ctor.
     * @param text The text of the URN
     * @throws URISyntaxException If syntax is not correct
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    public URN(final String text) throws URISyntaxException {
        if (text == null) {
            throw new IllegalArgumentException("text can't be NULL");
        }
        if (!text.matches(URN.REGEX)) {
            throw new URISyntaxException(text, "Invalid format of URN");
        }
        this.uri = text;
        this.validate();
    }

    /**
     * Public ctor.
     * @param nid The namespace ID
     * @param nss The namespace specific string
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    public URN(final String nid, final String nss) {
        if (nid == null) {
            throw new IllegalArgumentException("NID can't be NULL");
        }
        if (nss == null) {
            throw new IllegalArgumentException("NSS can't be NULL");
        }
        this.uri = String.format(
            "%s%s%s%2$s%s",
            URN.PREFIX,
            URN.SEP,
            nid,
            URN.encode(nss)
        );
        try {
            this.validate();
        } catch (final URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * Creates an instance of URN and throws a runtime exception if
     * its syntax is not valid.
     * @param text The text of the URN
     * @return The URN created
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static URN create(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("URN can't be NULL");
        }
        try {
            return new URN(text);
        } catch (final URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Override
    public String toString() {
        return this.uri;
    }

    @Override
    public int compareTo(final URN urn) {
        return this.uri.compareTo(urn.uri);
    }

    /**
     * Is it a valid URN?
     * @param text The text to validate
     * @return Yes of no
     */
    @SuppressWarnings("PMD.ProhibitPublicStaticMethods")
    public static boolean isValid(final String text) {
        boolean valid = true;
        try {
            new URN(text);
        } catch (final URISyntaxException ex) {
            valid = false;
        }
        return valid;
    }

    /**
     * Does it match the pattern?
     * @param pattern The pattern to match
     * @return Yes of no
     */
    public boolean matches(final String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern can't be NULL");
        }
        boolean matches = false;
        if (this.toString().equals(pattern)) {
            matches = true;
        } else if (pattern.endsWith("*")) {
            final String body = pattern.substring(0, pattern.length() - 1);
            matches = this.uri.startsWith(body);
        }
        return matches;
    }

    /**
     * Is it empty?
     * @return Yes of no
     */
    public boolean isEmpty() {
        return URN.EMPTY.equals(this.nid());
    }

    /**
     * Convert it to URI.
     * @return The URI
     */
    public URI toURI() {
        return URI.create(this.uri);
    }

    /**
     * Get namespace ID.
     * @return Namespace ID
     */
    public String nid() {
        return this.segment(1);
    }

    /**
     * Get namespace specific string.
     * @return Namespace specific string
     */
    public String nss() {
        try {
            return URLDecoder.decode(this.segment(2), URN.ENCODING);
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Get all params.
     * @return The params
     */
    public Map<String, String> params() {
        return URN.demap(this.toString());
    }

    /**
     * Get query param by name.
     * @param name Name of parameter
     * @return The value of it
     */
    public String param(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("param name can't be NULL");
        }
        final Map<String, String> params = this.params();
        if (!params.containsKey(name)) {
            throw new IllegalArgumentException(
                String.format(
                    "Param '%s' not found in '%s', among %s",
                    name,
                    this,
                    params.keySet()
                )
            );
        }
        return params.get(name);
    }

    /**
     * Add (overwrite) a query param and return a new URN.
     * @param name Name of parameter
     * @param value The value of parameter
     * @return New URN
     */
    public URN param(final String name, final Object value) {
        if (name == null) {
            throw new IllegalArgumentException("param can't be NULL");
        }
        if (value == null) {
            throw new IllegalArgumentException("param value can't be NULL");
        }
        final Map<String, String> params = this.params();
        params.put(name, value.toString());
        return URN.create(
            String.format(
                "%s%s",
                StringUtils.split(this.toString(), '?')[0],
                URN.enmap(params)
            )
        );
    }

    /**
     * Get just body of URN, without params.
     * @return Clean version of it
     */
    public URN pure() {
        String urn = this.toString();
        if (this.hasParams()) {
            // @checkstyle MultipleStringLiterals (1 line)
            urn = urn.substring(0, urn.indexOf('?'));
        }
        return URN.create(urn);
    }

    /**
     * Whether this URN has params?
     * @return Has them?
     */
    public boolean hasParams() {
        // @checkstyle MultipleStringLiterals (1 line)
        return this.toString().contains("?");
    }

    /**
     * Get segment by position.
     * @param pos Its position
     * @return The segment
     */
    private String segment(final int pos) {
        return StringUtils.splitPreserveAllTokens(
            this.uri,
            URN.SEP,
            // @checkstyle MagicNumber (1 line)
            3
        )[pos];
    }

    /**
     * Validate URN.
     * @throws URISyntaxException If it's not valid
     */
    private void validate() throws URISyntaxException {
        if (this.isEmpty() && !this.nss().isEmpty()) {
            throw new URISyntaxException(
                this.toString(),
                "Empty URN can't have NSS"
            );
        }
        final String nid = this.nid();
        if (!nid.matches("^[a-z]{1,31}$")) {
            throw new IllegalArgumentException(
                String.format(
                    "NID '%s' can contain up to 31 low case letters",
                    this.nid()
                )
            );
        }
        if (StringUtils.equalsIgnoreCase(URN.PREFIX, nid)) {
            throw new IllegalArgumentException(
                "NID can't be 'urn' according to RFC 2141, section 2.1"
            );
        }
    }

    /**
     * Decode query part of the URN into Map.
     * @param urn The URN to demap
     * @return The map of values
     */
    private static Map<String, String> demap(final String urn) {
        final Map<String, String> map = new TreeMap<>();
        final String[] sectors = StringUtils.split(urn, '?');
        if (sectors.length == 2) {
            final String[] parts = StringUtils.split(sectors[1], '&');
            for (final String part : parts) {
                final String[] pair = StringUtils.split(part, '=');
                final String value;
                if (pair.length == 2) {
                    try {
                        value = URLDecoder.decode(pair[1], URN.ENCODING);
                    } catch (final UnsupportedEncodingException ex) {
                        throw new IllegalStateException(ex);
                    }
                } else {
                    value = "";
                }
                map.put(pair[0], value);
            }
        }
        return map;
    }

    /**
     * Encode map of params into query part of URN.
     * @param params Map of params to convert to query suffix
     * @return The suffix of URN, starting with "?"
     */
    private static String enmap(final Map<String, String> params) {
        final StringBuilder query = new StringBuilder(100);
        if (!params.isEmpty()) {
            query.append('?');
            boolean first = true;
            for (final Map.Entry<String, String> param : params.entrySet()) {
                if (!first) {
                    query.append('&');
                }
                query.append(param.getKey());
                if (!param.getValue().isEmpty()) {
                    query.append('=').append(URN.encode(param.getValue()));
                }
                first = false;
            }
        }
        return query.toString();
    }

    /**
     * Perform proper URL encoding with the text.
     * @param text The text to encode
     * @return The encoded text
     */
    private static String encode(final String text) {
        final byte[] bytes;
        try {
            bytes = text.getBytes(URN.ENCODING);
        } catch (final UnsupportedEncodingException ex) {
            throw new IllegalStateException(ex);
        }
        final StringBuilder encoded = new StringBuilder(100);
        for (final byte chr : bytes) {
            if (URN.allowed(chr)) {
                encoded.append((char) chr);
            } else {
                encoded.append('%').append(String.format("%X", chr));
            }
        }
        return encoded.toString();
    }

    /**
     * This char is allowed in URN's NSS part?
     * @param chr The character
     * @return It is allowed?
     */
    private static boolean allowed(final byte chr) {
        // @checkstyle BooleanExpressionComplexity (4 lines)
        return chr >= 'A' && chr <= 'Z'
            || chr >= '0' && chr <= '9'
            || chr >= 'a' && chr <= 'z'
            || chr == '/' || chr == '-';
    }

}
