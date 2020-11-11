import mill._
import mill.scalalib._
import publish._
import mill.api.Loose
import mill.define.Target

object template extends ScalaModule with PublishModule {
  override def scalaVersion: T[String] = T {"2.13.1"}

  val millVersion = "0.7.4"

  override def compileIvyDeps = Agg(
    ivy"com.lihaoyi::mill-scalalib:$millVersion"
  )

  override def ivyDeps: Target[Loose.Agg[Dep]] = super.ivyDeps() ++
    Agg(ivy"org.foundweekends.giter8:giter8-lib_2.13:0.13.1",
      ivy"org.foundweekends.giter8:giter8-cli-git_2.13:0.13.1")

  override def publishVersion = T{"0.0.1"}
  def pomSettings = PomSettings(
    description = "My first library",
    organization = "com.matru",
    url = "https://github.com/matru/template",
    licenses = Seq(License.MIT),
    versionControl = VersionControl.github("matru", "template"),
    developers = Seq(
      Developer("matru", "MatRu","https://github.com/matru")
    )
  )
}
