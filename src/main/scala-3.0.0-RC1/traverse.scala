package zero.ext

import scala.annotation.tailrec
import scala.collection.mutable
import option.*

extension [A,B](x: Option[Either[A,B]])
  inline def sequence: Either[A, Option[B]] =
    x match
      case Some(Right(y)) => y.some.right
      case Some(Left(y)) => y.left
      case None => none.right

extension [A,B](xs: Seq[Either[A, B]])
  @tailrec private def _sequence(ys: Seq[Either[A, B]], acc: Vector[B]): Either[A, Vector[B]] =
    ys.headOption match
      case None => Right(acc)
      case Some(l@Left(_)) => l.coerceRight
      case Some(Right(z)) => _sequence(ys.tail, acc :+ z)
  inline def sequence: Either[A, Vector[B]] = _sequence(xs, Vector.empty)

  @tailrec private def _sequence_(ys: Seq[Either[A, B]]): Either[A, Unit] =
    ys.headOption match
      case None => Right(())
      case Some(l@Left(_)) => l.coerceRight
      case Some(Right(z)) => _sequence_(ys.tail)
  inline def sequence_ : Either[A, Unit] = _sequence_(xs)

extension [A](xs: List[Option[A]])
  @tailrec private def _sequence(ys: List[Option[A]], acc: Vector[A]): Option[List[A]] =
    ys match
      case Nil => Some(acc.toList)
      case None :: zs => None 
      case Some(z) :: zs => _sequence(zs, acc :+ z)
  inline def sequence: Option[List[A]] = _sequence(xs, Vector.empty)

extension [K, V, E](xs: Map[K, Either[E, V]])
  @tailrec private def _sequence(rem: Map[K, Either[E, V]], acc: mutable.Map[K, V]): Either[E, Map[K, V]] =
    rem.headOption match
      case Some((k, Right(v))) => _sequence(rem.tail, acc += (k->v))
      case Some((k, l@Left(_))) => l.coerceRight
      case None => acc.to(Map).right
  inline def sequence: Either[E, Map[K, V]] = _sequence(xs, mutable.Map.empty)

given CanEqual[None.type, Option[?]] = CanEqual.derived
