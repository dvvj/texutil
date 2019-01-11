package org.ditw.nameUtils.nameParser.utils
import org.ditw.nameUtils.nameParser.{ParserHelpers, TrialParsers}
import org.ditw.nameUtils.nameParser.{ParserHelpers, TrialParsers}
import org.ditw.nameUtils.nameparser.NameParser

/**
  * Created by dev on 2017-09-06.
  */
object AcademicTitles extends Serializable {

  //      APN: advanced practice nurse
  //     FAAN: Fellow of the American Academy of Nursing.
  //       RD: registered dietitian.
  //     DRPH: Doctor of Public Health.
  //     LMSW: Licensed Master Social Worker
  //     LCSW: licensed clinical social worker
  //  CCC-SLP: Certificate of Clinical Competence (CCC), Speech-Language Pathology (CCC-SLP)
  //      CRA: Clinical research associate
  //      MPA: Medical Prescribing Adviser
  //      SCD: Doctor of Science
  //    ED.D.: Doctor of Education
  //     FACP: Fellow of the American College of Physicians.
  //    D.D.S: Doctor of Dental Surgery.
  //     MBBS: Bachelor of Medicine and Surgery
  //      MPP: Master of Public Policy
  //     FACS: Fellow of the American College of Surgeons.
  //     MSCR: Master of Science in Clinical Research
  //    FRCPC: Fellow of the Royal College of Physicians and Surgeons of Canada
  //    FRCSC: Fellow of the Royal College of Surgeons of Canada
  //      BSN: Bachelor of Science in Nursing.
  //     DNSC: Doctor of Nursing Science.
  //      MSW: Master of Social Work
  //     MHSA: Master of Health Services Administration
  //      FNP: Family Nurse Practitioner
  //     CCFP: Certificate of the College of Family Physicians.
  //      DMD: Doctor of Dental Medicine
  //      MSN: Master of Science in Nursing
  //     FACC: Fellow, American College of Cardiology
  //      CNS: Clinical Nurse Specialist
  //    MRCOG: Member, Royal College of Obstetricians and Gynaecologists
  //      BDS: Bachelor of Dental Surgery
  //     FEBU: Fellow of the European Board of Urology
  //      MHA: Mental Health Association.
  //     CNOR: Certified Perioperative Nurse.
  //     FACP: Fellow of the American College of Physicians or Fellow of the American College of Prosthodontists.
  //       OD: Doctor of Optometry
  //     FCCP: Fellow of the American College of Chest Physicians
  //   FJFICM: Fellowship of the Joint Faculty of Intensive Care Medicine
  //    Ch.B.: Chirurgiae Baccalaureus, Bachelor of Surgery, in the U.K.

  private[nameParser] val _titles =
    """
      |facp
      |cra
      |mpa
      |apn
      |drph
      |dr.ph
      |dr.p.h.
      |faan
      |f.a.a.n.
      |ma.
      |ma.rd
      |ed.d.
      |rd
      |be
      |md
      |(md)
      |med.
      |m.ed.
      |phmd
      |dr
      |ph
      |site
      |prof
      |investigator
      |id
      |mph
      |rn
      |r.n.
      |professor
      |msc
      |ms
      |mbbs
      |trial
      |doctor
      |med
      |phd
      |phd.
      |bs
      |ucb
      |dmd
      |drmeddent
      |bds
      |do
      |pa
      |np
      |ba
      |pharm
      |mba
      |frcpch
      |program
      |director
      |msc
      |pt
      |dpm
      |contact
      |person
      |msci
      |medsci
      |dr
      |mrcp
      |chb
      |rph
      |pharmd
      |ccrp
      |dc
      |ltd
      |frcs
      |bsn
      |lector
      |aggregate
      |mb
      |bmbs
      |global
      |bsc
      |ccrc
      |frcp
      |frcr
      |advisor
      |mbchb
      |lmsw
      |lcsw
      |ccc-slp
      |scd
      |assoc.
      |d.d.s
      |d.d.s.
      |dds
      |m.sc
      |m.sc.
      |m.sc.ph.d
      |m.sc.ph.d.
      |bscmedsci
    """.stripMargin

  private[nameParser] val TitleSet = _titles.lines.filter(!_.isEmpty).toSet

  private[nameParser] val _componentTitles =
    """
      |facp
      |cra
      |ma.
      |mpa
      |ma.rd
      |ed.d.
      |apn
      |(md)
      |ccc-slp
      |dr.
      |doc.
      |dr
      |prof.
      |prof
      |investigator
      |professor
      |mba
      |frcpch
      |program
      |director
      |pt
      |dpm
      |mrcp
      |chb
      |rph
      |pharmd
      |ccrp
      |dc
      |frcs
      |bsn
      |lector
      |mb
      |bmbs
      |bsc
      |ccrc
      |frcp
      |frcr
      |advisor
      |mbchb
      |lmsw
      |lcsw
      |scd
      |assoc.
      |m.sc.ph.d
      |m.sc.ph.d.
      |ph
      |pd
      |pr
      |b.a.
      |m.a.
      |Pharm.
      |d.o.
      |mscs
      |pro.
      |prf
      |prf.
      |professor.
      |l.p.
      |clinpsyd
      |(hons)
      |p.c.
      |pr.
      |assist.
      |cand.
      |doz.
      |eng.
      |habil.
      |phil.
      |phdcand
      |ed.
      |priv.
      |dent.
      |ophth
      |ophth.
      |univ.
      |pharm
      |mcoptom
      |faao
      |fbcla
      |ccti
      |asist.prof.
      |dipl
      |dipl.
      |abom
      |pgdip
      |diping
      |dipn
      |dipcot
    """.stripMargin

  private val _dot = "."

  private def lines2Set(literals:String):Set[String] =
    literals.lines.filter(!_.isEmpty).toSet

  private val NoDesc = ""
  private[nameParser] def AbbrTitle(abbr:String, desc:String = NoDesc):Set[String] = {
    _TitlesFromAbbrs(abbr.map(_.toLower.toString))
  }

  private[nameParser] val TitleSetFromComponents =
    lines2Set(_componentTitles) ++
      AbbrTitle("ANP", "Advanced nurse practitioner (Medspeak-UK)") ++
      TitlesFromAbbrs("ass", "prof") ++
      AbbrTitle("BCOP", "BCNU, cyclophosphamide, Oncovin-vincristine, prednisone") ++
      TitlesFromAbbrs("b", "ch") ++
      AbbrTitle("BDS", "Bachelor of Dental Surgery") ++
      TitlesFromAbbrs("b", "chir") ++
      TitlesFromAbbrs("b", "d", "sc") ++
      TitlesFromAbbrs("b", "eng") ++
      TitlesFromAbbrs("b", "m", "b", "ch") ++
      TitlesFromAbbrs("b", "med", "sc") ++
      TitlesFromAbbrs("b", "med", "sci") ++
      TitlesFromAbbrs("b", "nurs") ++
      TitlesFromAbbrs("b", "pharm") ++
      TitlesFromAbbrs("b", "s") ++
      TitlesFromAbbrs("b", "s", "c") ++
      TitlesFromAbbrs("b", "sc") ++
      TitlesFromAbbrs("b", "sc", "n") ++
      TitlesFromAbbrs("b", "sc", "pt") ++
      TitlesFromAbbrs("bs", "c") ++
      AbbrTitle("BSN", "Bachelor of Science in Nursing.") ++
      TitlesFromAbbrs("cand", "med") ++
      TitlesFromAbbrs("c", "sc") ++
      AbbrTitle("CCFP", "Certificate of the College of Family Physicians.") ++
      TitlesFromAbbrs("ch", "b") ++
      AbbrTitle("CHT", "Certified Hand Therapist.") ++
      AbbrTitle("CNOR", "Certified Perioperative Nurse.") ++
      AbbrTitle("CNS", "Clinical Nurse Specialist") ++
      AbbrTitle("CPNP", "Certified pediatric nurse practitioner/associate.") ++
      TitlesFromAbbrs("c", "psych") ++
      AbbrTitle("DABT", "Diplomate of the American Board of Toxicology") ++
      AbbrTitle("DDS", "Doctor of Dental Surgery.") ++
      AbbrTitle("DMD", "Doctor of Dental Medicine.") ++
      TitlesFromAbbrs("d", "m", "sc") ++
      TitlesFromAbbrs("d", "m", "sci") ++
      AbbrTitle("DNS", "Doctor of Nursing Science (also DNSc)") ++
      TitlesFromAbbrs("d", "n", "sc") ++
      TitlesFromAbbrs("d", "ph") ++
      TitlesFromAbbrs("d", "phil") ++
      TitlesFromAbbrs("d", "sc") ++
      TitlesFromAbbrs("d", "sci") ++
      TitlesFromAbbrs("ds", "m", "c") ++
      TitlesFromAbbrs("dr", "med") ++
      TitlesFromAbbrs("dr", "med", "dent") ++
      TitlesFromAbbrs("dr", "med", "sc") ++
      TitlesFromAbbrs("dr", "med", "sci") ++
      TitlesFromAbbrs("dr", "m", "sc") ++
      TitlesFromAbbrs("dr", "ph") ++
      TitlesFromAbbrs("dr", "phil") ++
      TitlesFromAbbrs("dr", "sc") ++
      TitlesFromAbbrs("dr", "sci") ++
      AbbrTitle("FAAN", "Fellow of the American Academy of Nursing.") ++
      AbbrTitle("FAHA", "Fellow of the American Heart Association") ++
      AbbrTitle("FACC", "Fellow, American College of Cardiology") ++
      AbbrTitle("FACP", "Fellow of the American College of Physicians.") ++
      TitlesFromAbbrs("f", "a", "c", "pa") ++
      AbbrTitle("FACS", "Fellow of the American College of Surgeons.") ++
      AbbrTitle("FASGE", "Fellow of the American Society for Gastrointestinal Endoscopy") ++
      AbbrTitle("FCARCSI", "Fellow of the College of Anaesthetists, Royal College of Surgeons in Ireland (formerly FFARCSI)") ++
      AbbrTitle("FCCP", "Fellow of the American College of Chest Physicians") ++
      AbbrTitle("FCO") ++
      AbbrTitle("FEBU", "Fellow of the European Board of Urology") ++
      AbbrTitle("FETCS", "Fellow of the European Board of Thoracic and Cardiovascular Surgeons") ++
      AbbrTitle("FJFICM", "Fellowship of the Joint Faculty of Intensive Care Medicine") ++
      TitlesFromAbbrs("f", "med", "sci") ++
      AbbrTitle("FNP", "Family Nurse Practitioner") ++
      AbbrTitle("FRCA", "Fellow of the Royal College of Anaesthetists") ++
      AbbrTitle("FRCS", "Fellow of the Royal College of Surgeons (of England).") ++
      TitlesFromAbbrs("f", "r", "c", "ophth") ++
      TitlesFromAbbrs("f", "r", "c", "p") ++
      TitlesFromAbbrs("f", "r", "c", "path") ++
      TitlesFromAbbrs("f", "r", "c", "psych") ++
      AbbrTitle("FRCPC", "Fellow of the Royal College of Physicians and Surgeons of Canada") ++
      AbbrTitle("FRCPCH", "Fellow of the Royal College of Paediatrics and Child Health (UK)") ++
      AbbrTitle("FRCSC", "Fellow of the Royal College of Surgeons of Canada") ++
      AbbrTitle("FRS", "Fellow of the Royal Society.") ++
      TitlesFromAbbrs("hab", "n", "med") ++
      TitlesFromAbbrs("m", "a", "sc") ++
      AbbrTitle("MB", "Medicinae Baccalaureus (Bachelor of Medicine)") ++
      AbbrTitle("MBBS", "Bachelor of Medicine and Surgery") ++
      TitlesFromAbbrs("m", "b", "b", "ch") ++
      TitlesFromAbbrs("m", "b", "b", "chir") ++
      TitlesFromAbbrs("m", "b", "ch", "b") ++
      TitlesFromAbbrs("m", "b", "ch", "s") ++
      TitlesFromAbbrs("m", "ch") ++
      TitlesFromAbbrs("m", "d") ++
      TitlesFromAbbrs("m", "d", "s") ++
      TitlesFromAbbrs("m", "div") ++
      TitlesFromAbbrs("m", "ed") ++
      TitlesFromAbbrs("m", "eng") ++
      TitlesFromAbbrs("m", "engr") ++
      TitlesFromAbbrs("m", "h", "a") ++
      TitlesFromAbbrs("m", "h", "s") ++
      TitlesFromAbbrs("m", "h", "s", "a") ++
      TitlesFromAbbrs("m", "h", "sc") ++
      TitlesFromAbbrs("m", "h", "sc", "s") ++
      TitlesFromAbbrs("m", "med") ++
      TitlesFromAbbrs("m", "med", "sc") ++
      TitlesFromAbbrs("m", "m", "sc") ++
      TitlesFromAbbrs("m", "p", "h") ++
      TitlesFromAbbrs("m", "phil") ++
      TitlesFromAbbrs("m", "p", "p") ++
      AbbrTitle("MRCOG", "Member, Royal College of Obstetricians and Gynaecologists") ++
      AbbrTitle("MRCS", "Member of the Royal College of Surgeons") ++
      TitlesFromAbbrs("m", "s") ++
      TitlesFromAbbrs("m", "sc") ++
      TitlesFromAbbrs("m", "sc", "eng") ++
      TitlesFromAbbrs("m", "sci") ++
      TitlesFromAbbrs("m", "sc", "n") ++
      TitlesFromAbbrs("m", "sc", "pt") ++
      AbbrTitle("MSCR", "Master of Science in Clinical Research") ++
      TitlesFromAbbrs("m", "s", "d") ++
      TitlesFromAbbrs("m", "s", "hyg") ++
      TitlesFromAbbrs("m", "st") ++
      TitlesFromAbbrs("ms", "c") ++
      AbbrTitle("MSN", "Master of Science in Nursing") ++
      AbbrTitle("MSW", "Master of Social Work") ++
      TitlesFromAbbrs("md", "ph") ++
      TitlesFromAbbrs("md", "phd") ++
      TitlesFromAbbrs("md", "sc") ++
      TitlesFromAbbrs("med", "sc", "d") ++
      TitlesFromAbbrs("med", "sci") ++
      TitlesFromAbbrs("med", "stud") ++
      TitlesFromAbbrs("m", "u", "dr") ++
      TitlesFromAbbrs("n", "p") ++
      TitlesFromAbbrs("ob", "gyn") ++
      AbbrTitle("OCN", "Oncology Certified Nurse.") ++
      TitlesFromAbbrs("p", "h", "d") ++
      TitlesFromAbbrs("ph", "d") ++
      TitlesFromAbbrs("ph", "d", "s") ++
      TitlesFromAbbrs("phd", "c") ++
      TitlesFromAbbrs("ph", "md") ++
      TitlesFromAbbrs("psy", "d") ++
      TitlesFromAbbrs("pharm", "d") ++
      TitlesFromAbbrs("r", "d") ++
      AbbrTitle("RNC", "Registered Nurse, Certified.") ++
      TitlesFromAbbrs("r", "n") ++
      TitlesFromAbbrs("r", "nutr") ++
      TitlesFromAbbrs("sc", "m")

  private [nameParser] def _TitlesFromAbbrs(abbrs:Seq[String]):Set[String] = {
    val dotless = abbrs.mkString
    Set(
      dotless,
      dotless + _dot,
      abbrs.mkString(_dot),
      abbrs.mkString("", _dot, _dot)
    )
  }

  private [nameParser] def TitlesFromAbbrs(abbrs:String*):Set[String] = {
    _TitlesFromAbbrs(abbrs.toSeq)
  }

  private[nameParser] val DecoratesAssociate = List(
    "a",
    "asc",
    "ass",
    "assist",
    "assistant",
    "asst",
    "asso",
    "assoc",
    "associate"
  )
  private[nameParser] val DecoratesProfOther = List(
    "adj",
    "adjunct",
    "full"
  )
  private[nameParser] val DecoratesPhd = List(
    "phd",
    "master"
  )
  private[nameParser] val Decorates4Doctors = List(
    "medical",
    "chief"
  )
  private[nameParser] val DecorableTitleStudent = List(
    "student",
    "stud",
    "study",
    "st"
  )
  private[nameParser] val DecorableTitleDoctor = List(
    "dr",
    "doc",
    "doctor"
  )

  private[nameParser] val DecorableTitleProfessor = List(
    "pro",
    "prof",
    "professor",
    "prf",
    "lect",
    "lecturer"
  )

  private[nameParser] val Decorates4Directors = List(
    "ivf laboratory",
    "laboratory",
    "ivf lab",
    "lab",
    "research"
  )
  private[nameParser] val DecorableTitleDirector = List(
    "director"
  )

  private val DecoratedTitlePairs = List(
    DecoratesAssociate -> DecorableTitleProfessor,
    DecoratesProfOther -> DecorableTitleProfessor,
    DecorableTitleProfessor -> DecorableTitleDoctor,
    Decorates4Doctors -> DecorableTitleDoctor,
    DecoratesPhd -> DecorableTitleStudent,
    Decorates4Directors -> DecorableTitleDirector
  )
  private[nameParser] val DecoratedTitles = {
    DecoratedTitlePairs.flatMap { p =>
      for (dec <- p._1; decTitle <- p._2) yield s"$dec $decTitle"
    }.toSet
  }

  private val _partTitlesCaseSensitive =
    """
      |BA
      |DO
      |EdD
      |MA
      |M.A
    """.stripMargin
  private[nameParser] val TitleSetFromPartsCaseSensitive = lines2Set(_partTitlesCaseSensitive)

  private val _partTitles =
    """
      |doctor
      |master
      |ass prof md
      |b pharm
      |b med sc
      |b sc phm
      |bsc pharma
      |staff grade dr
      |dm
      |esp
      |dipl psych
      |dr n med
      |dr sc med
      |dr sc nat
      |frcs ed
      |ld
      |lic med
      |od
      |pharm d
      |pharm. d.
      |ph d
      |ph. d
      |ph. d.
      |cp
      |msc pharm
      |m sc pharm
      |prof dr med
      |prof md
      |mb ch b
      |md phd
      |md ph d
      |med dent
      |med pract
      |ph d md
      |cand scient
      |univ doz
      |md res
      |principal investigator
      |trial coord
      |physician doctor
    """.stripMargin

  private[nameParser] val TitleSetFromParts = lines2Set(_partTitles) ++
    DecoratedTitles

  private[nameParser] val TitlesInTheLastTrialNameParts = Set(
    "master",
    "student",
    "masters",
    "physician",
    "doctor",
    "bachlor",
    "coordinator",
    "surgeon",
    "epidemiology",
    "fellow",
    "midwife",
    "nurse",
    "candidate",
    "doctoral",
    "pharmacist",
    "consultant",
    "psychologist",
    "psychology",
    "cardiologist",
    "principle",
    "paediatrician",
    "pediatrician",
    "dietician",
    "ophthalmology",
    "zoology",
    "postgraduate",
    "graduate",
    "physiotherapist",
    "fellowship",
    "lecturer",
    "medic",
    "scientist"
  )

  def titlesInTrialNames(trialNameSource:String):Boolean = {
    if (trialNameSource == null || NameParser.containsAcademicTitleTrash(trialNameSource)) false
    else {
      val parts = TrialParsers.splitTrialNameParts(trialNameSource) //.map(_.toLowerCase)
      if (parts.exists(TitleSetFromPartsCaseSensitive.contains)) true
      else {
        val partsLower = parts.map(_.toLowerCase)
        if (partsLower.exists(TitleSetFromParts.contains)) true
        else {
          val comps = partsLower.flatMap(ParserHelpers.splitBySpace)
          comps.exists(TitleSetFromComponents.contains)
        }
      }
    }
  }

}
