<img src="http://img.jcabi.com/logo-square.svg" width="64px" height="64px" />

[![Managed by Zerocracy](https://www.0crat.com/badge/C3RUBL5H9.svg)](http://www.0crat.com/p/C3RUBL5H9)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-urn)](http://www.rultor.com/p/jcabi/jcabi-urn)

[![Build Status](https://travis-ci.org/jcabi/jcabi-urn.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-urn)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-urn)](http://www.0pdd.com/p?name=jcabi/jcabi-urn)
[![Build status](https://ci.appveyor.com/api/projects/status/9eu1jjs99bfji7da/branch/master?svg=true)](https://ci.appveyor.com/project/yegor256/jcabi-urn/branch/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-urn/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.jcabi/jcabi-urn)
[![Javadoc](https://javadoc.io/badge/com.jcabi/jcabi-urn.svg)](http://www.javadoc.io/doc/com.jcabi/jcabi-urn)

More details are here: [urn.jcabi.com](http://urn.jcabi.com/index.html)

`URN` is an immutable implementation of a Uniform Resource Name (URN)
according to [RFC 2141](http://tools.ietf.org/html/rfc2141):

```java
import com.jcabi.urn.URN;
public class Main {
  public static void main(String[] args) {
    URN urn = new URN("urn:test:my-example");
    assert urn.nid().equals("test");
    assert urn.nss().equals("my-example");
  }
}
```

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/yegor256/jcabi/issues/new).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```
