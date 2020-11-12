package plugin
import java.io.File

import giter8.{Config, G8TemplateRenderer, Git, GitRepository, JGitInteractor, Ref}
import mill._
import mill.define.Discover

import scala.util.{Failure, Success}

object Giter8 extends mill.define.ExternalModule {

  def hello(name: String) = T.command{println(s"Hello $name")}

  /**
   *
   * @param repo See [[giter8.GitRepository.Matches]]
   * @param ref Branch.
   * @param directoryInRepo Only used if non standard, i.e. none of src/main/g8, root.
   * @param outputFolder Defaults to the name property.
   * @param knownHosts Git known hosts
   * @param properties A property map. E.g. Map("name" -> "My Project")
   * @param overwriteFiles Defaults to false.
   * @return Either a success message, or a error message.
   */
  def instantiate(repo: String, ref: String = "", directoryInRepo: String = "", outputFolder: String = "",
                  knownHosts: String = "", properties: Map[String, String] = Map(), overwriteFiles: Boolean = false
                 ) = T.command {
    val tempdir = new File(os.temp.dir().toIO, "giter8-" + System.nanoTime)
    val workingDir: File = new File(".")
    val arguments: Seq[String] = properties.map{ a => s"--${a._1}=${a._2}"}.toList
    val toOption: (String) => Option[String] = s => s match {case o:String if o.length > 0 => Some(o);case _ => None}
    for {
      conf <- Right(Config(repo, toOption(ref).map(Ref.Branch), overwriteFiles, Some(directoryInRepo),
        toOption(outputFolder), toOption(knownHosts)))
      repo <- GitRepository.fromString(repo)
      cloneDir <- cloneRepo(repo, conf.ref, tempdir, conf.knownHosts)
      templateDir = new File(cloneDir, conf.directory.getOrElse(""))
      result <- G8TemplateRenderer.render(templateDir, workingDir, arguments, conf.forceOverwrite,
        conf.out.map(new File(_)))
    } yield result
  }

  def cloneRepo(repo: GitRepository, ref: Option[Ref], tempdir: File, knownHosts: Option[String]): Either[String, File] =
    new Git(new JGitInteractor(knownHosts)).clone(repo, ref, tempdir) match {
      case Success(_) => Right(tempdir)
      case Failure(e) => Left(e.getMessage)
    }

  override def millDiscover: Discover[Giter8.this.type] = Discover[this.type]
}
