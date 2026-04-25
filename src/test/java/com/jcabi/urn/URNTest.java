/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
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
 * @since 0.6
 * @checkstyle AbbreviationAsWordInNameCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
final class URNTest {

    /**
     * URN can be instantiated from plain text and provides correct NID.
     * @throws Exception If there is some problem inside
     */
    @Test
    void instantiatesFromTextWithCorrectNid() throws Exception {
        MatcherAssert.assertThat(
            "should provide correct NID",
            new URN("urn:jcabi:jeff%20lebowski%2540").nid(),
            Matchers.equalTo("jcabi")
        );
    }

    /**
     * URN can be instantiated from plain text and provides correct NSS.
     * @throws Exception If there is some problem inside
     */
    @Test
    void instantiatesFromTextWithCorrectNss() throws Exception {
        MatcherAssert.assertThat(
            "should provide correct NSS",
            new URN("urn:jcabi:jeff%20lebowski%2540").nss(),
            Matchers.equalTo("jeff lebowski%40")
        );
    }

    /**
     * URN can encode NSS properly.
     */
    @Test
    void encodesNssAsRequiredByUrlSyntax() {
        MatcherAssert.assertThat(
            "should encode NSS correctly",
            new URN("test", "walter sobchak!").toString(),
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
     * URN can be instantiated from components with correct NID.
     */
    @Test
    void instantiatesFromComponentsWithCorrectNid() {
        MatcherAssert.assertThat(
            "should preserve NID",
            new URN("foo", "\u8416 test").nid(),
            Matchers.equalTo("foo")
        );
    }

    /**
     * URN can be instantiated from components with correct NSS.
     */
    @Test
    void instantiatesFromComponentsWithCorrectNss() {
        MatcherAssert.assertThat(
            "should preserve NSS",
            new URN("bar", "\u8416 & \u8415 *&^%$#@!-~`\"'").nss(),
            Matchers.equalTo("\u8416 & \u8415 *&^%$#@!-~`\"'")
        );
    }

    /**
     * URN can be converted to URI.
     */
    @Test
    void convertsToUri() {
        MatcherAssert.assertThat(
            "should convert to URI",
            new URN("baz", "test").toURI(),
            Matchers.instanceOf(URI.class)
        );
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
        MatcherAssert.assertThat(
            "should be equal",
            new URN("urn:foo:some-other-specific-string"),
            Matchers.equalTo(new URN("urn:foo:some-other-specific-string"))
        );
    }

    /**
     * URN can be tested for equivalence with another URI.
     * @throws Exception If there is some problem inside
     */
    @Test
    void comparesForEquivalenceWithUri() throws Exception {
        MatcherAssert.assertThat(
            "should not be equal to URI",
            new URN("urn:foo:somespecificstring").equals(
                new URI("urn:foo:somespecificstring")
            ),
            Matchers.is(false)
        );
    }

    /**
     * URN can be tested for equivalence with string.
     * @throws Exception If there is some problem inside
     */
    @Test
    void comparesForEquivalenceWithString() throws Exception {
        MatcherAssert.assertThat(
            "should not be equal to String",
            new URN("urn:foo:sometextastext").equals("urn:foo:sometextastext"),
            Matchers.is(false)
        );
    }

    /**
     * URN can be converted to string.
     * @throws Exception If there is some problem inside
     */
    @Test
    void convertsToString() throws Exception {
        MatcherAssert.assertThat(
            "should convert to string",
            new URN("urn:foo:textofurn").toString(),
            Matchers.equalTo("urn:foo:textofurn")
        );
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
     * URN can pass correct syntax and round-trip.
     */
    @Test
    void passesCorrectURNSyntaxAndRoundTrips() {
        MatcherAssert.assertThat(
            "should round-trip all valid URNs",
            Arrays.asList(
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
                "urn:a:b/c/d"
            ),
            Matchers.everyItem(
                Matchers.is(
                    new org.hamcrest.CustomTypeSafeMatcher<String>("a round-trippable URN") {
                        @Override
                        protected boolean matchesSafely(final String text) {
                            return URN.isValid(text)
                                && URN.create(URN.create(text).toString())
                                    .equals(URN.create(text));
                        }
                    }
                )
            )
        );
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
        MatcherAssert.assertThat(
            "should be empty",
            new URN().isEmpty(),
            Matchers.equalTo(true)
        );
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
     * URN can encode params in string representation.
     * @throws Exception If there is some problem inside
     */
    @Test
    void encodesParamsInStringRepresentation() throws Exception {
        MatcherAssert.assertThat(
            "should encode param correctly",
            new URN("urn:test:x?bb")
                .param("bar", "\u8514 value?")
                .toString(),
            Matchers.containsString("bar=%E8%94%94%20value%3F")
        );
    }

    /**
     * URN can retrieve empty param value.
     * @throws Exception If there is some problem inside
     */
    @Test
    void retrievesEmptyParamValue() throws Exception {
        MatcherAssert.assertThat(
            "should retrieve empty param",
            new URN("urn:test:x?bb").param("bb"),
            Matchers.equalTo("")
        );
    }

    /**
     * URN can retrieve unicode param value.
     * @throws Exception If there is some problem inside
     */
    @Test
    void retrievesUnicodeParamValue() throws Exception {
        MatcherAssert.assertThat(
            "should retrieve unicode param",
            new URN("urn:test:x?bb")
                .param("crap", "@!$#^\u0433iu**76\u0945")
                .param("crap"),
            Matchers.equalTo("@!$#^\u0433iu**76\u0945")
        );
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
        MatcherAssert.assertThat(
            "should deserialize to same value",
            ((URN) SerializationUtils.deserialize(
                SerializationUtils.serialize(
                    new URN("urn:test:some-data-to-serialize")
                )
            )).toString(),
            Matchers.equalTo("urn:test:some-data-to-serialize")
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
