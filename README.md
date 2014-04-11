<img src="http://img.jcabi.com/logo-square.png" width="64px" height="64px" />
 
[![Build Status](https://travis-ci.org/jcabi/jcabi-urn.svg?branch=master)](https://travis-ci.org/jcabi/jcabi-urn)

More details are here: [www.jcabi.com/jcabi-urn](http://www.jcabi.com/jcabi-urn/index.html)

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

You need just this dependency:

```xml
<dependency>
  <groupId>com.jcabi</groupId>
  <artifactId>jcabi-urn</artifactId>
  <version>0.8</version>
</dependency>
```

## Questions?

If you have any questions about the framework, or something doesn't work as expected,
please [submit an issue here](https://github.com/yegor256/jcabi/issues/new).
If you want to discuss, please use our [Google Group](https://groups.google.com/forum/#!forum/jcabi).

## How to contribute?

Fork the repository, make changes, submit a pull request.
We promise to review your changes same day and apply to
the `master` branch, if they look correct.

Please run Maven build before submitting a pull request:

```
$ mvn clean install -Pqulice
```
