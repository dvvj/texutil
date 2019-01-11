package org.ditw.nameUtils.misc

/**
  * Created by dev on 2017-06-21.
  */
object ComparisonUtils {

  case class MatchStats(tp:Int, total:Int, trueTotal:Int) {
    def prec = 1.0*tp/total
    def perfectPrecision = tp == total
    def recall = 1.0*tp/trueTotal
    def f1 = 2*prec*recall / (prec+recall)
    override def toString = f"$prec%.3f\t($tp/$total)\t$recall%.3f\t($tp/$trueTotal)\t$f1%.3f"
  }

//  type ActIdType = String
//  type ActorIdType = String

//  trait Act {
//    val uid:ActIdType
//    val actorIds:Array[ActorIdType]
//  }

  trait Actor[T] {
    val t:T

    type ActorIdType
    type ActIdType

    val uid:ActorIdType
    def actIds:Array[ActIdType]
  }

  type Aux[TActor,TActorId,TAct] = Actor[TActor] {
    type ActorIdType = TActorId
    type ActIdType = TAct
  }

//  trait ComparisonResult[T<:Act] {
//    val actsV1:Set[T]
//    val actsV2:Set[T]
//  }

  type CountIntersectFunc[TActIdType] = (Array[TActIdType], Array[TActIdType]) => Int

  case class ActorMatch[TActor1, TActor2, TActorId1, TActorId2, TAct](
    matchActor:TActor1,
    baseActor:TActor2,
    intersectCounter:Option[CountIntersectFunc[TAct]] = None)
  (implicit evd1:TActor1 => Aux[TActor1,TActorId1,TAct], evd2: TActor2 => Aux[TActor2,TActorId2,TAct]) {
//    private def matchActIds = matchActor.actIds.toSet
//    private def baseActIds = baseActor.actIds.toSet
//    private def matchedIds = matchActor.actIds.intersect(baseActor.actIds)
    private val intersectCount =
      if (intersectCounter.nonEmpty) intersectCounter.get(matchActor.actIds, baseActor.actIds)
      else matchActor.actIds.intersect(baseActor.actIds).length
    private val _stats = MatchStats(intersectCount, matchActor.actIds.length, baseActor.actIds.length)
    def matchStats():MatchStats = _stats
  }


//  def resultFrom[T<:Act](a1:Set[T], a2:Set[T]) = {
//    new ComparisonResult[T] {
//      override val actsV1 = a1
//      override val actsV2 = a2
//    }
//  }

//  def tryMatch[T<:Act](actsToMatch:Set[T], actsBase:Set[T]):ComparisonResult[T] = {
//    val a1Ids = actsToMatch.map(_.uid).toList.sorted
//    val a2Ids = actsBase.map(_.uid).toList.sorted
//    if (a1Ids != a2Ids) throw new IllegalArgumentException(s"Matching differents act sets: [$a1Ids] vs [$a2Ids]")
//
//    //if ()
//    resultFrom(actsToMatch, actsBase)
//  }

  def weightedF1(matchStas:Iterable[MatchStats], total:Int):Double = {
    matchStas.map { ms =>
      val w = ms.trueTotal*1.0/total
      w*ms.f1
    }.sum
  }

  private class DoubleTupleOrdered(val f1:Double, val tieBreaker:Double) extends Ordered[DoubleTupleOrdered] {
    import scala.math.Ordered.orderingToOrdered
    def compare(that: DoubleTupleOrdered): Int =
      (this.f1, this.tieBreaker) compare (that.f1, that.tieBreaker)
  }
  private def doubleTupleOrdered(v1:Double, v2:Double) = new DoubleTupleOrdered(v1, v2)
  private def actorMatch2DoubleTuple[TMatch, TBase, TMatchId, TBaseId, TAct](
    am:ActorMatch[TMatch, TBase, TMatchId, TBaseId, TAct],
    tieBreaker:TieBreaker[TBase])
    :DoubleTupleOrdered = {
    new DoubleTupleOrdered(am.matchStats().f1, tieBreaker(am.baseActor))
  }

  type TieBreaker[T] = T => Double
  def sortedIdsByMaxF1[TMatch, TBase, TMatchId, TBaseId, TAct](
    matches:Map[TMatchId,List[ActorMatch[TMatch, TBase, TMatchId, TBaseId, TAct]]]
  )(implicit tieBreaker:Option[TieBreaker[TBase]]) : List[TMatchId] = {
    if (tieBreaker.isEmpty) {
      implicit val ordering = Ordering[Double].reverse
      matches.toList.sortBy { mp =>
        val mlst = mp._2
        val max = if (mlst.isEmpty) Double.MinValue
        else mlst.map(_.matchStats().f1).min //.minBy(_.matchStats.f1).matchStats.f1
        max
      }.map(_._1)
    }
    else {
      implicit val ordering = Ordering[DoubleTupleOrdered].reverse
      matches.toList.sortBy { mp =>
        val mlst = mp._2
        val max =
          if (mlst.isEmpty) doubleTupleOrdered(Double.MinValue, Double.MinValue)
          else {
            val sorted = mlst.map { m =>
              val dp = actorMatch2DoubleTuple(m, tieBreaker.get)
              dp
            }.sorted
            sorted.head  //.minBy(_.matchStats.f1).matchStats.f1
          }
        max
      }.map(_._1)
    }
  }

  import collection.mutable
  def naiveMatchByF1[TMatch, TBase, TMatchId, TBaseId, TAct](
    matches:Map[TMatchId,List[ActorMatch[TMatch, TBase, TMatchId, TBaseId, TAct]]]
  )(implicit tieBreaker:Option[TieBreaker[TBase]])
    :Map[TMatchId,ActorMatch[TMatch, TBase, TMatchId, TBaseId, TAct]] = {
    val sortedIds = sortedIdsByMaxF1(matches)
    val resultMap = mutable.Map[TMatchId, ActorMatch[TMatch, TBase, TMatchId, TBaseId, TAct]]()
    val matchedTrueSet = mutable.Set[TBase]()
    sortedIds.foreach { eid =>
      val ml = matches(eid)
      val unmatchedList = ml.filter(m => !matchedTrueSet.contains(m.baseActor))
      if (unmatchedList.nonEmpty) {
        resultMap += eid -> unmatchedList.head
        matchedTrueSet += unmatchedList.head.baseActor
      }
    }
    resultMap.toMap
  }

  case class MatchResult[TMatch, TBase, TMatchId, TBaseId, TAct](
    possibleMatches:Map[TMatchId,List[ActorMatch[TMatch, TBase, TMatchId, TBaseId, TAct]]],
    bestMatch:Map[TMatchId,ActorMatch[TMatch, TBase, TMatchId, TBaseId, TAct]],
    actCount:Int
  ) {
    private val _bestMatchScore = weightedF1(bestMatch.map(_._2.matchStats), actCount)

    def bestMatchScore():Double = _bestMatchScore
  }


  def evalMatches[TMatch, TBase, TMatchId, TBaseId, TAct](
    bases:Iterable[TBase], //Map[TBaseId,TBase],
    toMatches:Iterable[TMatch], //Map[TMatchId,TMatch],
    e2id:TMatch => TMatchId,
    testCompatible:(TMatch,TBase) => Boolean,
    intersectCounter:Option[CountIntersectFunc[TAct]] = None
  )(implicit evd1: TMatch => Aux[TMatch, TMatchId, TAct],
     evd2: TBase => Aux[TBase, TBaseId, TAct],
     tieBreaker:Option[TieBreaker[TBase]]
  ):MatchResult[TMatch, TBase, TMatchId, TBaseId, TAct] = {
    val matchesListMap = toMatches.map { m =>
      val allMatchStats =
        bases.flatMap(
          b => if (testCompatible(m,b)) Option(ActorMatch[TMatch, TBase, TMatchId, TBaseId, TAct](m, b, intersectCounter)) else None
        )

      val non0Matches = allMatchStats.filter(_.matchStats.tp > 0).toList
      val sortedMatchList =
        if (tieBreaker.isEmpty) non0Matches.sortBy(_.matchStats.f1)(Ordering[Double].reverse)
        else {
          non0Matches.sortBy(m => actorMatch2DoubleTuple(m, tieBreaker.get))(Ordering[DoubleTupleOrdered].reverse)
        }
      e2id(m) -> sortedMatchList
    }.toMap

    val paired = naiveMatchByF1(matchesListMap)
    MatchResult(
      possibleMatches = matchesListMap,
      bestMatch = paired,
      actCount = bases.flatMap(_.actIds).size
    )
  }

  def TestCompatible_NoTest[T1,T2](t1:T1, t2:T2):Boolean = true

}
