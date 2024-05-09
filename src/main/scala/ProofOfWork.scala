import java.security.MessageDigest
import java.nio.charset.StandardCharsets
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object ProofOfWork {
  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime()
    val block = "new block"
    val target = BigInt("1FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)
    val nonce = proveWork(block, target)


    val endTime = System.nanoTime()
    println("Winning nonce: " + nonce)
    println("Time: " + (endTime - startTime)/1e6d + " milliseconds")
  }

  private def proveWork(block: String, target: BigInt): Long = {
    var nonce = 0L
    var i = 0
    while (i == 0) {
      val result = Future(checkHash(block, nonce, target))
      if (Await.result(result, Duration.Inf)) {
        i = 1
      } else {
        nonce += 1
      }
    }
    nonce

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