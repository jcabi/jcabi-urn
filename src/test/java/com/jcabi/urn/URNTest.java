/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.urn;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.SerializationUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Uniform Resource Name (URN), tests.
 *
 * @since 0.6
 * @checkstyle AbbreviationAsWordInNameCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
final class URNTest {

    /**
     * URN can be instantiated from plain text.
     * @throws Exception If there is some problem inside
     */
    @Test
    void instantiatesFromText() throws Exception {
        final URN urn = new URN("urn:jcabi:jeff%20lebowski%2540");
        MatcherAssert.assertThat(urn.nid(), Matchers.equalTo("jcabi"));
        MatcherAssert.assertThat(
            urn.nss(),
            Matchers.equalTo("jeff lebowski%40")
        );
    }

    /**
     * URN can encode NSS properly.
     */
    @Test
    void encodesNssAsRequiredByUrlSyntax() {
        final URN urn = new URN("test", "walter sobchak!");
        MatcherAssert.assertThat(
            urn.toString(),
            Matchers.equalTo("urn:test:walter%20sobchak%21")
        );
    }

    /**
     * URN can throw exception when text is NULL.
     */
    @Test
    void throwsExceptionWhenTextIsNull() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new URN(null)
        );
    }

    /**
     * URN can be instantiated from components.
     */
    @Test
    void instantiatesFromComponents() {
        final String nid = "foo";
        final String nss = "\u8416 & \u8415 *&^%$#@!-~`\"'";
        final URN urn = new URN(nid, nss);
        MatcherAssert.assertThat(urn.nid(), Matchers.equalTo(nid));
        MatcherAssert.assertThat(urn.nss(), Matchers.equalTo(nss));
        MatcherAssert.assertThat(urn.toURI(), Matchers.instanceOf(URI.class));
    }

    /**
     * URN can throw exception when NID is NULL.
     */
    @Test
    void throwsExceptionWhenNidIsNull() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new URN(null, "some-test-nss")
        );
    }

    /**
     * URN can throw exception when NSS is NULL.
     */
    @Test
    void throwsExceptionWhenNssIsNull() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new URN("namespace1", null)
        );
    }

    /**
     * URN can be tested for equivalence of another URN.
     * @throws Exception If there is some problem inside
     */
    @Test
    void comparesForEquivalence() throws Exception {
        final String text = "urn:foo:some-other-specific-string";
        final URN first = new URN(text);
        final URN second = new URN(text);
        MatcherAssert.assertThat(first, Matchers.equalTo(second));
    }

    /**
     * URN can be tested for equivalence with another URI.
     * @throws Exception If there is some problem inside
     */
    @Test
    void comparesForEquivalenceWithUri() throws Exception {
        final String text = "urn:foo:somespecificstring";
        final URN first = new URN(text);
        final URI second = new URI(text);
        MatcherAssert.assertThat(first.equals(second), Matchers.is(false));
    }

    /**
     * URN can be tested for equivalence with string.
     * @throws Exception If there is some problem inside
     */
    @Test
    void comparesForEquivalenceWithString() throws Exception {
        final String text = "urn:foo:sometextastext";
        final URN first = new URN(text);
        MatcherAssert.assertThat(first.equals(text), Matchers.is(false));
    }

    /**
     * URN can be converted to string.
     * @throws Exception If there is some problem inside
     */
    @Test
    void convertsToString() throws Exception {
        final String text = "urn:foo:textofurn";
        final URN urn = new URN(text);
        MatcherAssert.assertThat(urn.toString(), Matchers.equalTo(text));
    }

    /**
     * URN can catch incorrect syntax.
     */
    @Test
    void catchesIncorrectURNSyntax() {
        Assertions.assertThrows(
            URISyntaxException.class,
            () -> new URN("some incorrect name")
        );
    }

    /**
     * URN can pass correct syntax.
     */
    @Test
    void passesCorrectURNSyntax() {
        final String[] texts = {
            "URN:hello:test",
            "urn:foo:some%20text%20with%20spaces",
            "urn:a:",
            "urn:a:?alpha=50",
            "urn:a:?boom",
            "urn:a:test?123",
            "urn:a:test?1a2b3c",
            "urn:a:test?1A2B3C",
            "urn:a:?alpha=abccde%20%45%4Fme",
            "urn:woquo:ns:pa/procure/BalanceRecord?name=*",
            "urn:a:?alpha=50&beta=u%20worksfine",
            "urn:verylongnamespaceid:",
            "urn:a:?alpha=50*",
            "urn:a:b/c/d",
        };
        for (final String text : texts) {
            final URN urn = URN.create(text);
            MatcherAssert.assertThat(
                URN.create(urn.toString()),
                Matchers.equalTo(urn)
            );
            MatcherAssert.assertThat("is valid", URN.isValid(urn.toString()));
        }
    }

    /**
     * URN can throw exception for incorrect syntax.
     */
    @Test
    void throwsExceptionForIncorrectURNSyntax() {
        final String[] texts = {
            "abc",
            "",
            "urn::",
            "urn:urn:hello",
            "urn:incorrect namespace name with spaces:test",
            "urn:abc+foo:test-me",
            "urn:test:?abc?",
            "urn:test:?abc=incorrect*value",
            "urn:test:?abc=invalid-symbols:^%$#&@*()!-in-argument-value",
            "urn:incorrect%20namespace:",
            "urn:verylongnameofanamespaceverylongnameofanamespace:",
            "urn:test:spaces are not allowed here",
            "urn:test:unicode-has-to-be-encoded:\u8514",
        };
        for (final String text : texts) {
            try {
                URN.create(text);
                MatcherAssert.assertThat(text, Matchers.nullValue());
            } catch (final IllegalArgumentException ex) {
                assert ex != null;
            }
        }
    }

    /**
     * URN can be "empty".
     */
    @Test
    void emptyURNIsAFirstClassCitizen() {
        final URN urn = new URN();
        MatcherAssert.assertThat(urn.isEmpty(), Matchers.equalTo(true));
    }

    /**
     * URN can be "empty" only in one form.
     */
    @Test
    void emptyURNHasOnlyOneVariant() {
        Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> new URN("void", "it-is-impossible-to-have-any-NSS-here")
        );
    }

    /**
     * URN can be "empty" only in one form, with from-text ctor.
     */
    @Test
    void emptyURNHasOnlyOneVariantWithTextCtor() {
        Assertions.assertThrows(
            URISyntaxException.class,
            () -> new URN("urn:void:it-is-impossible-to-have-any-NSS-here")
        );
    }

    /**
     * URN can match a pattern.
     * @throws Exception If there is some problem inside
     */
    @Test
    void matchesPatternWithAnotherURN() throws Exception {
        MatcherAssert.assertThat(
            "matches",
            new URN("urn:test:file").matches("urn:test:*")
        );
    }

    /**
     * URN can add and retrieve params.
     * @throws Exception If there is some problem inside
     */
    @Test
    void addAndRetrievesParamsByName() throws Exception {
        final String name = "crap";
        final String value = "@!$#^\u0433iu**76\u0945";
        final URN urn = new URN("urn:test:x?bb")
            .param("bar", "\u8514 value?")
            .param(name, value);
        MatcherAssert.assertThat(
            urn.toString(),
            Matchers.containsString("bar=%E8%94%94%20value%3F")
        );
        MatcherAssert.assertThat(urn.param("bb"), Matchers.equalTo(""));
        MatcherAssert.assertThat(urn.param(name), Matchers.equalTo(value));
    }

    /**
     * URN can fetch a pure part (without params) from itself.
     * @throws Exception If there is some problem inside
     */
    @Test
    void fetchesBodyWithoutParams() throws Exception {
        MatcherAssert.assertThat(
            new URN("urn:test:something?a=9&b=4").pure(),
            Matchers.equalTo(new URN("urn:test:something"))
        );
    }

    /**
     * URN can be serialized.
     * @throws Exception If there is some problem inside
     */
    @Test
    void serializesToBytes() throws Exception {
        final URN urn = new URN("urn:test:some-data-to-serialize");
        final byte[] bytes = SerializationUtils.serialize(urn);
        MatcherAssert.assertThat(
            ((URN) SerializationUtils.deserialize(bytes)).toString(),
            Matchers.equalTo(urn.toString())
        );
    }

    /**
     * URN can be persistent in params ordering.
     * @throws Exception If there is some problem inside
     */
    @Test
    void persistsOrderingOfParams() throws Exception {
        final List<String> params = Arrays.asList(
            "ft", "sec", "9", "123", "a1b2c3", "A", "B", "C"
        );
        URN first = new URN("urn:test:x");
        URN second = first;
        for (final String param : params) {
            first = first.param(param, "");
        }
        Collections.shuffle(params);
        for (final String param : params) {
            second = second.param(param, "");
        }
        MatcherAssert.assertThat(first, Matchers.equalTo(second));
    }

    /**
     * URN can be mocked.
     */
    @Test
    void mocksUrnWithAMocker() {
        MatcherAssert.assertThat(
            new URNMocker().mock(),
            Matchers.not(Matchers.equalTo(new URNMocker().mock()))
        );
    }

    /**
     * URN can be lexical equivalent according to RFC 2144, section 6.
     * @see <a href="http://www.ietf.org/rfc/rfc2141.txt"/>
     * @see <a href="https://github.com/jcabi/jcabi-urn/issues/8">issue</a>
     */
    @Test
    @Disabled
    void checkLexicalEquivalence() {
        final String[] urns = {
            "URN:foo:a123,456",
            "urn:foo:a123,456",
            "urn:FOO:a123,456",
            "urn:foo:a123%2C456",
            "URN:FOO:a123%2c456",
        };
        for (final String first : urns) {
            for (final String second : urns) {
                MatcherAssert.assertThat(
                    URN.create(first),
                    Matchers.equalTo(URN.create(second))
                );
            }
        }
    }

}
