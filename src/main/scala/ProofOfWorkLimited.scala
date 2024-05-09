import java.security.MessageDigest
import java.nio.charset.StandardCharsets
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object ProofOfWorkLimited {
  def main(args: Array[String]): Unit = {
    val startTime = System.nanoTime()
    val block = "new block"
    val target = BigInt("1FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16)

    val nonce = proveWork(block, target)

    val endTime = System.nanoTime()
    println("Winning nonce: " + nonce)
    println("Time: " + (endTime - startTime) / 1e6d + " milliseconds")
  }

  private def proveWork(block: String, target: BigInt): Long = {
    var nonce = 0L
    val futureCount = 1000

    while(true) {
      val futureResult: Seq[Future[Boolean]] =
        (nonce to nonce + futureCount).map { i =>
          Future(checkHash(block, i, target))
        }
       val allCompleted: Future[Seq[Boolean]] = Future.sequence(futureResult)
      val index = Await.result(allCompleted,Duration.Inf).indexOf(true)
      if(index == -1) {
        nonce = nonce + futureCount
      } else {
        nonce = nonce + index
        return nonce
      }
    }
    0
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
