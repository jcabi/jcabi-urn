/*
 * SPDX-FileCopyrightText: Copyright (c) 2012-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.jcabi.urn;

import java.util.UUID;

/**
 * Mocker of {@link URN}.
 *
 * @since 0.6
 * @checkstyle AbbreviationAsWordInNameCheck (500 lines)
 */
public final class URNMocker {

    /**
     * Namespace ID.
     */
    private transient String nid;

    /**
     * Nammespace specific string.
     */
    private transient String nss;

    /**
     * Public ctor.
     */
    public URNMocker() {
        this.nid = "test";
        this.nss = UUID.randomUUID().toString();
    }

    /**
     * With this namespace.
     * @param name The namespace
     * @return This object
     */
    public URNMocker withNid(final String name) {
        this.nid = name;
        return this;
    }

    /**
     * With this nss.
     * @param text The nss
     * @return This object
     */
    public URNMocker withNss(final String text) {
        this.nss = text;
        return this;
    }

    /**
     * Mock it.
     * @return Mocked URN
     */
    public URN mock() {
        return new URN(this.nid, this.nss);
    }

}
