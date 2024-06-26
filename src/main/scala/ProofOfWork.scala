import java.security.MessageDigest
import java.nio.charset.StandardCharsets
import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util.Success
import scala.util.Failure

object ProofOfWork {
  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime()
    val block = "new block"
    val target = BigInt("1FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)
    val promise = Promise[Long]
    proveWork(block, target, promise)
    val nonce = Await.result(promise.future, Duration.Inf)


    val endTime = System.nanoTime()
    println("Winning nonce: " + nonce)
    println("Time: " + (endTime - startTime)/1e6d + " milliseconds")
  }

  private def proveWork(block: String, target: BigInt, promise: Promise[Long]): Unit = {
    var nonce = 0L
    while (!promise.isCompleted) {
      val result = Future(checkHash(block, nonce, target))
      result.onComplete{
        case Success((true, usedNonce)) => {
          promise.trySuccess(usedNonce)
        }
        case Success((false, usedNonce)) => nonce += 1
        case Failure(_) => 
      }
    }
  }

  private def checkHash(block: String, nonce: Long, target: BigInt): (Boolean, Long) = {
    val digest = MessageDigest.getInstance("SHA-256")
    val input = block + nonce
    val encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8))
    val encodedhash2 = digest.digest(encodedhash)
    val hashValue = BigInt(1, encodedhash2)
    if (hashValue.compareTo(target) < 0){
      (true, nonce)
    } else {
      (false, nonce)
    }
  }
}