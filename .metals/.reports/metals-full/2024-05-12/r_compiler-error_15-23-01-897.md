file:///D:/HTWG/Master/Concurrency/ConcurrencyScala/src/main/scala/ProofOfWork.scala
### java.lang.AssertionError: NoDenotation.owner

occurred in the presentation compiler.

presentation compiler configuration:
Scala version: 3.3.3
Classpath:
<HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala3-library_3\3.3.3\scala3-library_3-3.3.3.jar [exists ], <HOME>\AppData\Local\Coursier\cache\v1\https\repo1.maven.org\maven2\org\scala-lang\scala-library\2.13.12\scala-library-2.13.12.jar [exists ]
Options:



action parameters:
offset: 503
uri: file:///D:/HTWG/Master/Concurrency/ConcurrencyScala/src/main/scala/ProofOfWork.scala
text:
```scala
import java.security.MessageDigest
import java.nio.charset.StandardCharsets
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Success

object ProofOfWork {
  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime()
    val block = "new block"
    val target = BigInt("1FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)
    val promise = PromiseLong[@@]
    proveWork(block, target, promise)
    val nonce = Await.result(promise.future, Duration.Inf)


    val endTime = System.nanoTime()
    println("Winning nonce: " + nonce)
    println("Time: " + (endTime - startTime)/1e6d + " milliseconds")
  }

  private def proveWork(block: String, target: BigInt, promise: Promise[Long]): Unit = {
    var nonce = 0L
    var i = 0
    while (i == 0) {
      val result = Future(checkHash(block, nonce, target))
      result.onComplete{
        case Success(true) => {
          promise.success(nonce)
          i = 1;
        }
        case Success(false) => nonce += 1
      }
    }
  }

  private def checkHash(block: String, nonce: Long, target: BigInt): Boolean = {
    val digest = MessageDigest.getInstance("SHA-256")
    val input = block + nonce
    val encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8))
    val encodedhash2 = digest.digest(encodedhash)
    val hashValue = BigInt(1, encodedhash2)
    hashValue.compareTo(target) < 0
  }
}
```



#### Error stacktrace:

```
dotty.tools.dotc.core.SymDenotations$NoDenotation$.owner(SymDenotations.scala:2607)
	scala.meta.internal.pc.SignatureHelpProvider$.isValid(SignatureHelpProvider.scala:83)
	scala.meta.internal.pc.SignatureHelpProvider$.notCurrentApply(SignatureHelpProvider.scala:96)
	scala.meta.internal.pc.SignatureHelpProvider$.$anonfun$1(SignatureHelpProvider.scala:48)
	scala.collection.StrictOptimizedLinearSeqOps.dropWhile(LinearSeq.scala:280)
	scala.collection.StrictOptimizedLinearSeqOps.dropWhile$(LinearSeq.scala:278)
	scala.collection.immutable.List.dropWhile(List.scala:79)
	scala.meta.internal.pc.SignatureHelpProvider$.signatureHelp(SignatureHelpProvider.scala:48)
	scala.meta.internal.pc.ScalaPresentationCompiler.signatureHelp$$anonfun$1(ScalaPresentationCompiler.scala:414)
```
#### Short summary: 

java.lang.AssertionError: NoDenotation.owner