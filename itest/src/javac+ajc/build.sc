import mill._
import mill.scalalib._
import mill.define.Command

import $file.plugins
import de.tobiasroeser.mill.aspectj._

import $ivy.`org.scalatest::scalatest:3.2.19`
import org.scalatest.Assertions

object main extends AspectjModule {

  def aspectjVersion = "1.9.5"
  def aspectjCompileMode = CompileMode.OnlyAjSources

  override def javacOptions = Seq("-source", "1.8", "-target", "1.8")
  def ajcOptions = Seq("-1.8")

  object test extends JavaModuleTests {
    def javacOptions = Seq("-source", "1.8", "-target", "1.8")
    // compatibility with older Mill versions
    def testFrameworks: T[Seq[String]] = Seq(testFramework())
    def testFramework: T[String] = "com.novocode.junit.JUnitFramework"
    def ivyDeps = Agg(
      ivy"com.novocode:junit-interface:0.11",
      ivy"de.tototec:de.tobiasroeser.lambdatest:0.7.0"
    )
  }

}

def verify(): Command[Unit] = T.command {

  val A = new Assertions {}
  import A._

  val cr = main.compile()
  main.test.test()()

  assert(main.ajcSourceFiles().map(_.path) == Seq(main.millSourcePath / "src" / "BeforeAspect.aj"))

  ()
}
