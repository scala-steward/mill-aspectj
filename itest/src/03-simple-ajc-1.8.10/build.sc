import mill._
import mill.scalalib._
import mill.define.Command

import $file.plugins
import de.tobiasroeser.mill.aspectj._

import $ivy.`org.scalatest::scalatest:3.2.19`
import org.scalatest.Assertions

object main extends AspectjModule {

  def aspectjVersion = "1.8.10"

  def ajcOptions = Seq("-8")

  object test extends JavaModuleTests {
    // compatibility with older Mill versions
    def testFrameworks: T[Seq[String]] = Seq(testFramework())
    def testFramework: T[String] = "com.novocode.junit.JUnitFramework"
    def ivyDeps = Agg(
      ivy"com.novocode:junit-interface:0.11",
      ivy"de.tototec:de.tobiasroeser.lambdatest:0.7.0"
    )
  }

}

def verify(): Command[Unit] =
  if (System.getProperty("java.specification.version").startsWith("1.")) T.command {
    // Ajc 1.8.10 doesn't work with Java9+
    val A = new Assertions {}
    import A._

    val cr = main.compile()
    main.test.test()()
    ()
  }
  else T.command {
    println("Skipping test with ajc 1.8.10 on Java9+")
    ()
  }
