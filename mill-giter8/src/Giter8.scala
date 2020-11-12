package plugin
import java.io.File

import giter8.{Config, G8TemplateRenderer, Git, GitRepository, JGitInteractor, Ref}
import mill._
import mill.define.Discover

import scala.util.{Failure, Success}

object Giter8 extends mill.define.ExternalModule {

  def hello(name: String) = T.command{println(s"Hello $name")}

  def instantiate(repo: String, // The git repo path
                  ref: String = "", // Branch or tag
                  directoryInRepo: String = "", // Subdirectory within cloned repo
                  outputFolder: String = "", // Output directory
                  knownHosts: String = "", // ?
                  properties: Map[String, String] = Map(),
                  overwriteFiles: Boolean = false
                 ) = T.command {
    val Param = """(?s)^--(\S+)=(.+)$""".r
    val tempdir = new File(os.temp.dir().toIO, "giter8-" + System.nanoTime)
    val workingDir: File = new File(".")
    val arguments: Seq[String] = properties.map{ a => s"--${a._1}=${a._2}"}.toList
    val outputDir = outputFolder match {case o:String if o.length > 0 => Some(o);case _ => None}
    for {
      conf <- createConfig(repo, None/*ref*/, Some(directoryInRepo), outputDir, None/*knownHosts*/)
      repo <- GitRepository.fromString(repo)
      cloneDir <- cloneRepo(repo, conf.ref, tempdir, conf.knownHosts)
      templateDir = new File(cloneDir, conf.directory.getOrElse(""))
      result <- G8TemplateRenderer.render(templateDir, workingDir, arguments, overwriteFiles,
        conf.out.map(new File(_)))
    } yield result
  }


  def createConfig(repo: String,
                   ref: Option[Ref] = None,
                   directory: Option[String] = None,
                   out: Option[String],
                   knownHosts: Option[String] = None
                  ): Either[String, Config] = {
    Right(Config(repo, ref, forceOverwrite = true, directory, out, knownHosts))
  }

  def cloneRepo(repo: GitRepository, ref: Option[Ref], tempdir: File, knownHosts: Option[String]): Either[String, File] =
    new Git(new JGitInteractor(knownHosts)).clone(repo, ref, tempdir) match {
      case Success(_) => Right(tempdir)
      case Failure(e) => Left(e.getMessage)
    }

  override def millDiscover: Discover[Giter8.this.type] = Discover[this.type]
}
