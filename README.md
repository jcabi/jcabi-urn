<img src="https://www.jcabi.com/logo-square.svg" width="64px" height="64px" />

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](http://www.rultor.com/b/jcabi/jcabi-urn)](http://www.rultor.com/p/jcabi/jcabi-urn)

[![mvn](https://github.com/jcabi/jcabi-urn/actions/workflows/mvn.yml/badge.svg)](https://github.com/jcabi/jcabi-urn/actions/workflows/mvn.yml)
[![PDD status](http://www.0pdd.com/svg?name=jcabi/jcabi-urn)](http://www.0pdd.com/p?name=jcabi/jcabi-urn)
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

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```
