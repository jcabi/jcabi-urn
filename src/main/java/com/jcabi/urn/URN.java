/**
 * Copyright (c) 2012-2013, JCabi.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the jcabi.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jcabi.urn;

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Tv;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

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
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 0.6
 * @see <a href="http://tools.ietf.org/html/rfc2141">RFC 2141</a>
 */
@Immutable
@EqualsAndHashCode
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.UseConcurrentHashMap" })
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
     * Regular expression matching NID.
     */
    private static final String REGEX_NID =
            "[a-zA-Z0-9]{1,31}";

    /**
     * Regular expression matching NSS.
     */
    private static final String REGEX_NSS =
            "([\\-a-zA-Z0-9()+,\\-\\.:=@;$_!\\*']|%[0-9a-fA-F]{2})*";

    /**
     * Regular expression matching URN prefix, case insensitive.
     */
    private static final String REGEX_URN_PREFIX = "(?i)urn(?-i)";

    /**
     * Validating regular expr.
     */
    private static final String REGEX =
        "^" + REGEX_URN_PREFIX + SEP + REGEX_NID + SEP + REGEX_NSS + "$";

    /**
     * The URI.
     */
    @SuppressWarnings("PMD.BeanMembersShouldSerialize")
    private final String uri;

    /**
     * Public ctor (for JAXB mostly) that creates an "empty" URN.
     */
    public URN() throws UnsupportedEncodingException {
        this(URN.EMPTY, "");
    }

    /**
     * Public ctor.
     * @param text The text of the URN
     * @throws URISyntaxException If syntax is not correct
     */
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
    public URN(final String nid, final String nss) throws UnsupportedEncodingException {
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
        if (!nid.matches(REGEX_NID)) {
            throw new IllegalArgumentException(
                String.format(
                    "NID '%s' is not valid.",
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
     * Perform proper URL encoding with the text.
     * @param text The text to encode
     * @return The encoded text
     */
    private static String encode(final String text) throws UnsupportedEncodingException {
        final StringBuilder encoded = new StringBuilder(Tv.HUNDRED);
        for (final char chr : text.toCharArray()) {
            if (URN.allowed(chr)) {
                encoded.append(chr);
            } else {
                encoded.append(URLEncoder.encode(String.valueOf(chr), URN.ENCODING));
            }
        }
        return encoded.toString();
    }

    /**
     * This char is allowed in URN's NSS part?
     * @param chr The character
     * @return It is allowed?
     */
    private static boolean allowed(final char chr) {
        // @checkstyle BooleanExpressionComplexity (4 lines)
        return chr >= 'A' && chr <= 'Z'
            || chr >= '0' && chr <= '9'
            || chr >= 'a' && chr <= 'z'
            || chr == '(' || chr == ')'
            || chr == '+' || chr == ','
            || chr == '-' || chr == '.'
            || chr == ':' || chr == '='
            || chr == '@' || chr == ';'
            || chr == '$' || chr == '_'
            || chr == '!' || chr == '*'
            || chr == '\'';
    }

}
