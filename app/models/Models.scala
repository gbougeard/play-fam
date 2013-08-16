package models



// Use the implicit threadLocalSession


/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

//http://www.slideshare.net/eishay/slick-on-play?ref=http://eng.42go.com/using-scala-slick-at-fortytwo/
//abstract class RSession(roSession: => Session) extends SessionWrapper(roSession)
//
//class ROSession(roSession: => Session) extends RSession(roSession)
//
//class RWSession(rwSession: => Session) extends RSession(rwSession)
//
//class Database @Inject()(val db: DataBaseComponent) {
//
//
//  private def rawSession = db.handle.createSession
//
//  def readOnly[T](f: ROSession => T): T = {
//    val s = rawSession.forParams(rsConcurrency = ResultSetConcurrency.ReadOnly)
//    try {
//      f(new ROSession(s))
//    } finally s.close()
//  }
//
//  def readWrite[T](f: RWSession => T): T = {
//    val s = rawSession.forParams(rsConcurrency = ResultSetConcurrency.Updatable)
//    try {
//      rw.withTransaction {
//        f(new RWSession(s))
//      }
//    } finally s.close()
//  }
//}
//
//
//trait Repo[M <: Model[M]] {
//  def get(id: Id[M])(implicit session: RSession): M
//
//  def all(id: Id[M])(implicit session: RSession): Seq[M]
//
//  def save(model: M)(implicit session: RWSession): M
//
//  def count(page: Int = 0, size: Int = 20)(implicit session: RSession): Seq[M]
//
//}
//
//abstract class RepoTable[M <: Model[M]](db: DataBaseComponent, name:String) extends Table[M](db.entityName(name)) with TableWithDDL {
//
//  def id = column[Id[M]]("ID", O.PrimaryKey, O.Nullable, O.AutoInc)
//  def createdAt = column[DateTime]("dt_creat", O.NotNull)
//  def updatedAt = column[DateTime]("dt_modif", O.NotNull)
//
//  def autoInc = * returning id
//
//  override def column[C:TypeMapper](name:String, options: ColumnOption[C]*)=
//  super.column(db.entityName(name), options:_*)
//}
//
//trait DbRepo[M <: Model[M]] extends Repo[M]{
//  protected def table: RepoTable[M]
//}



