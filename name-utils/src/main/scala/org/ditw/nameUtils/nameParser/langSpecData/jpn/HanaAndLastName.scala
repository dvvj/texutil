package org.ditw.nameUtils.nameParser.langSpecData.jpn

import scala.io.Source

/**
  * Created by dev on 2018-02-13.
  */
object HanaAndLastName extends Serializable {

  private val _names =
    """
      |Abe
      |Aburami
      |Adachi
      |Agawa
      |Aida
      |Aikawa
      |Aino
      |Aizawa
      |Akabori
      |Akaboshi
      |Akagi
      |Akai
      |Akao
      |Akamatsu
      |Akashi
      |Akeda
      |Akemi
      |Akita
      |Akiyama
      |Amaki
      |Amakusa
      |Amane
      |Amano
      |Amo
      |Anami
      |Ando
      |Anno
      |Anzai
      |Aoki
      |Aoyama
      |Asagiri
      |Asakura
      |Asahina
      |Araki
      |Arakaki
      |Arai
      |Aramaki
      |Arata
      |Arii
      |Arioka
      |Arita
      |Asada
      |Asano
      |Ashida
      |Ashikaga
      |Ayukawa
      |Azuma
      |Azumano
      |Baba
      |Bai
      |Bandai
      |Beppu
      |Bessho
      |Chano
      |Chiba
      |Chikafuji
      |Chikamoto
      |Chikasue
      |Chinen
      |Dai
      |Daibuku
      |Daichi
      |Daicho
      |Daido
      |Daidoji
      |Daigo
      |Daigoku
      |Daigoho
      |Daiho
      |Daijo
      |Daiku
      |Dainichi
      |Daitoku
      |Daitokuji
      |Daiwa
      |Daiyo
      |Dan
      |Date
      |Dazai
      |Degawa
      |Deguchi
      |Den
      |Deon
      |Deshi
      |Deshima
      |Deshimaru
      |Deura
      |Deushi
      |Dewa
      |Deyama
      |Dezaki
      |Doi
      |Doiuchi
      |Dokite
      |Domon
      |Earashi
      |Eda
      |Edagawa
      |Edogawa
      |Eki
      |Eku
      |Endo
      |Eniwa
      |Ezura
      |Enoshima
      |Fuchizaki
      |Fuchizawa
      |Fujii
      |Fujimori
      |Fujimoto
      |Fujisaki
      |Fujinaka
      |Fujioka
      |Fujita
      |Fujiwara
      |Fujiyama
      |Fukuda
      |Fukumoto
      |Fukuyama
      |Fumi
      |Fumimoto
      |Furukawa
      |Futamata
      |Fujibayashi
      |Ganbe
      |Go
      |Gobu
      |Goda
      |Godai
      |Gorumba
      |Goto
      |Guionu
      |Hagino
      |Hagiwara
      |Hakuta
      |Hamamoto
      |Hamanaka
      |Hamasaki
      |Hana
      |Hanaori
      |Haneda
      |Hanawa
      |Hara
      |Harada
      |Haruno
      |Hasemi
      |Hasegawa
      |Hitachiin
      |Haseyama
      |Hashiguchi
      |Hashimoto
      |Hata
      |Hatano
      |Hatsuharu
      |Hatta
      |Hattori
      |Hayakawa
      |Hayasaka
      |Hayashi
      |Hayashida
      |Hazuki
      |Hibino
      |Hidaka
      |Higashi
      |Higuchi
      |Hiiragi
      |Hikari
      |Hikono
      |Hirai
      |Haramatsu
      |Hirano
      |Hata
      |Hirata
      |Hirota
      |Hirose
      |Hojo
      |Honda
      |Honma
      |Horie
      |Horiguchi
      |Horii
      |Horikawa
      |Horimoto
      |Horio
      |Horiuchi
      |Hoshide
      |Hoshi
      |Hoshino
      |Hoshiyama
      |Hosoi
      |Hosokawa
      |Hosonuma
      |Hyūga
      |Ichinose
      |Ichikawa
      |Ichimura
      |Ide
      |Ifukube
      |Iga
      |Igarashi
      |Igawa
      |Iguchi
      |Iida
      |Iijima
      |Iino
      |Iinuma
      |Ike
      |Ikeda
      |Ikegami
      |Ikemizu
      |Ikenami
      |Imagawa
      |Imai
      |Imaizumi
      |Imakake
      |Imoto
      |Inaba
      |Inada
      |Inamura
      |Ino
      |Inogashira
      |Inoue *Also spelled Inouye
      |Ioki
      |Ishida
      |Ishiguro
      |Ishii
      |Ishikawa
      |Ishiki
      |Ishimori
      |Ishimoto
      |Ishimura
      |Ishiyama
      |Ishizaki
      |Isshiki
      |Iso
      |Isobe
      |Isono
      |Itada
      |Itagaki
      |Ito
      |Itoh
      |Iwai
      |Iwaki
      |Iwakura
      |Iwamoto
      |Iwasa
      |Iwasaki
      |Iwasawa
      |Iwata
      |Izumi
      |Jakushi
      |Jihara
      |Jikihara
      |Jinbo
      |Jingū
      |Jingūji
      |Jinmei
      |Jinnai
      |Jinnaka
      |Jinnouchi
      |Jojima
      |Jonouchi
      |Jū
      |Jodai
      |Jogo
      |Joko
      |Jouchi
      |Jūge
      |Jūji
      |Junpei
      |Kadokawa
      |Kagura
      |Kaiba
      |Kaito
      |Kajiura
      |Kakei
      |Kaku
      |Kamimura
      |Kamiya
      |Kan
      |Kanada
      |Kanai
      |Kanan
      |Kanashiro
      |Kanayama
      |Kaneda
      |Kanegai
      |Kanemoto
      |Kaneyama
      |Kaneshige
      |Kanno
      |Kanzaki
      |Karasuma
      |Kashima
      |Kashino
      |Kashiwada
      |Kasuga
      |Katayama
      |Katono
      |Kato
      |Kawaguchi
      |Kawai
      |Kawamori
      |Kawamoto
      |Kawamura
      |Kawasaki
      |Kayano
      |Kazama
      |Ki
      |Kichida
      |Kido
      |Kikkawa
      |Kikuchi
      |Kimizuka
      |Kimoto
      |Kimura
      |Kinomoto
      |Kinoshita
      |Kiryū
      |Kishida
      |Kishii
      |Kishimoto
      |Kishino
      |Kitagawa
      |Kitamura
      |Kitani
      |Kitano
      |Kitaoka
      |Kobayakawa
      |Kobayashi
      |Kobe
      |Koda
      |Kogo
      |Koide
      |Koike
      |Koiwai
      |Koizumi
      |Kokubunji
      |Kokaji
      |Komagata
      |Komatsu
      |Komoda
      |Komiya
      |Kon
      |Konami
      |Kondo
      |Konno
      |Kosaka
      |Koshiba
      |Koshino
      |Kotono
      |Koyama
      |Kubo
      |Kubota
      |Kumagai
      |Kumakubo
      |Kume
      |Kumiko
      |Kumode
      |Kumon
      |Kunimitsu
      |Kunishige
      |Kuramoto
      |Kurata
      |Kuraya
      |Kuribayashi
      |Kurita
      |Kuroda
      |Kuroki
      |Kuroi
      |Kurosaki
      |Kurosawa
      |Kurono
      |Kusanagi
      |Kuwabara
      |Kyoto
      |Maaka
      |Maebara
      |Maeda
      |Mamiya
      |Manmitsu
      |Masamoto
      |Matsuda
      |Matsueda
      |Matsui
      |Matsumoto
      |Matsuoka
      |Matsurino
      |Matsusaka
      |Matsushige
      |Matsushima
      |Matsushita
      |Matsuura
      |Matsuzaka
      |Matsuzaki
      |Mazakada
      |Mazaki
      |Michimoto
      |Mihama
      |Miki
      |Minami
      |Minamoto
      |Mineto
      |Miura
      |Miyagi
      |Miyahara
      |Miyahira
      |Miyamoto
      |Miyasawa
      |Miyazaki
      |Miyazawa
      |Mizuguchi
      |Mizuhara
      |Monden
      |Mori
      |Moriguchi
      |Morihara
      |Morimoto
      |Morinaka
      |Morioka
      |Morita
      |Moroboshi
      |Murakami
      |Muramaru
      |Muramoto
      |Muranaka
      |Muraoka
      |Murata
      |Murayama
      |Muto
      |Nagai
      |Nagasaki
      |Nagata
      |Nagato
      |Naito
      |Naka
      |Nakagawa
      |Nakajima
      |Nakamoto
      |Nakamura
      |Nakano
      |Nakao
      |Nakaoka
      |Nakashima
      |Nakata
      |Nakatani
      |Nakayama
      |Nanba
      |Nara
      |Narita
      |Narusawa
      |Natsume
      |Naya
      |Nekotani
      |Nekoya
      |Nezu
      |Niidome
      |Niimi
      |Nikaido
      |Nimoto
      |Ninomiya
      |Nishi
      |Nishibayashi
      |Nishida
      |Nishihori
      |Nishijima
      |Nishimoto
      |Nishimura
      |Nishino
      |Nishioka
      |Nishiyama
      |Nishizaki
      |Nishizawa
      |Nitta
      |Noda
      |Nogami
      |Noguchi
      |Nomiya
      |Nomura
      |Nonaka
      |Nozawa
      |Nozomi
      |Obara
      |Obata
      |Ochi
      |Ochiai
      |Oda
      |Ode
      |Oe
      |Ogasawara
      |Ogata
      |Ogawa
      |Ogaya
      |Ogiwara
      |Oguri
      |Oguro
      |ohara
      |Oide
      |Oikawa
      |Oishi
      |Oka
      |Okabayashi
      |Okabe
      |Okada
      |Okajima
      |Okamoto
      |Okamura
      |Okayama
      |Okazaki
      |Okimura
      |Okimoto
      |Okinaka
      |Okubo
      |Okuda
      |okuma
      |Okuyama
      |Omi
      |Omoto
      |Omura
      |Onishi
      |Onizuka
      |Ono
      |Onogi
      |Onouye
      |omi
      |ono
      |Orido
      |Oshii
      |Oshima
      |Oshiro
      |osugi
      |Ota
      |Otake
      |Otani
      |Otsuka
      |Ouye
      |Oyama
      |Ozikawa
      |Rai
      |Raikatuji
      |Rakujochigusa
      |Rakuyama
      |Reizei
      |Ri
      |Rin
      |Rikiishi
      |Rikitake
      |Rippūkan
      |Rokkaku
      |Rokuda
      |Rokuhara
      |Rokumeikan
      |Rokupyakuda
      |Rokutanda
      |Roppiyakuda
      |Royama
      |Ryusaki
      |Rimando
      |Sada
      |Sagara
      |Sage
      |Sagawa
      |Saito
      |Sakai
      |Sakaki
      |Sakakibara
      |Sakamaki
      |Sakamoto
      |Sakata
      |Sakuma
      |Sakuraba
      |Sakurai
      |Sakuragi
      |Sakurauchi
      |Sannai
      |Sano
      |Sanuki
      |Sanzenin
      |Sasaki
      |Sasahara
      |Sato
      |Satobayashi
      |Satoshige
      |Sawachika
      |Sawada
      |Sawamura
      |Seino
      |Sekimoto
      |Senda
      |Seriou
      |Seta
      |Seto
      |Shiba
      |Shibata
      |Shida
      |Shigezawa
      |Shiina
      |Shimabukuro
      |Shimada
      |Shimamoto
      |Shimamura
      |Shimatani
      |Shimazaki
      |Shimazu
      |Shimizu
      |Shizuka
      |Shizuki
      |Shimodoi
      |Shimura
      |Shinjo
      |Shinseki
      |Shinohara
      |Shinozuka
      |Shintani
      |Shirai
      |Shiraishi
      |Shiratori
      |Shirayama
      |Sho
      |Shoji
      |Shureno
      |Soga
      |Sogabe
      |Sonoda
      |Sonozaki
      |Sotomura
      |Sugawara
      |Sugihara
      |Sugimoto
      |Sugiyama
      |Sudo
      |Sugo
      |Suo
      |Suwa
      |Suzukawa
      |Suzuki
      |Tachibana
      |Tachikawa
      |Tagata
      |Tagawa
      |Tahara
      |Taira
      |Takada
      |Takaeda
      |Takahashi
      |Takagi
      |Takaki
      |Takano
      |Takamoto
      |Takamura
      |Takanashi
      |Takanishi
      |Takaoka
      |Takara
      |Takasu
      |Takatsuka
      |Takayama
      |Takeda
      |Takei
      |Takemoto
      |Takenaka
      |Takeuchi
      |Takino
      |Tamiya
      |Tamaribuchi
      |Tamura
      |Tanaka
      |Tani
      |Taniguchi
      |Tanimoto
      |Tanizaki
      |Tanouye
      |Tashiro
      |Tebi
      |Tendo
      |Terada
      |Terasawa
      |Terashima
      |Terashita
      |Terauchi
      |Tezuka
      |Tomada
      |Todo
      |Toguchi
      |Tojo
      |Tominaga
      |Tominaka
      |Tomita
      |Tomooka
      |Toriyama
      |Toshima
      |Toyoda
      |Tsubasa
      |Tsubata
      |Tsuda
      |Tsuchiya
      |Tsugawa
      |Tsuji
      |Tsukamoto
      |Tsumoto
      |Tsunekawa
      |Tsunemoto
      |Tsuneyoshi
      |Tsunoda
      |Tsurumaki
      |Tsurumaru
      |Tsushiro
      |Tsushima
      |Tsutsui
      |Tsutsuji
      |Ubagai
      |Ubai
      |Ubaraki
      |Uchibori
      |Uchida
      |Uchigasaki
      |Uchiha
      |Uchihara
      |Uchino
      |Uchiyama
      |Uda
      |Udagawa
      |Ueda
      |Uehara
      |Uematsu
      |Ueno
      |Uenuma
      |Ueo
      |Ueshiba
      |Uesugi
      |Umemiya
      |Umemori
      |Umemoto
      |Umeno
      |Umetsu
      |Umezuka
      |Uno
      |Uotani
      |Ura
      |Urase
      |Urashima
      |Uraya
      |Urayama
      |Urushihara
      |Urushiyama
      |Usuda
      |Usui
      |Utagawa
      |Utsubo
      |Utsunomiya
      |Uyama
      |Uzuhara
      |Uzumaki
      |Wada
      |Wakabayashi
      |Wakahisa
      |Wakamiya
      |Wakamoto
      |Wakatsuki
      |Wakayama
      |Waki
      |Washio
      |Watabe
      |Watai
      |Watamura
      |Watanabe
      |Wauke
      |Yabuki
      |Yada
      |Yagami
      |Yagi
      |Yajima
      |Yakamoto
      |Yamada
      |Yamagata
      |Yamagishi
      |Yamagoe
      |Yamaguchi
      |Yamamiya
      |Yamamoto
      |Yamamura
      |Yamanaka
      |Yamane
      |Yamaoka
      |Yamasaki
      |Yamashiro
      |Yamashita
      |Yamato
      |Yamauchi
      |Yamazaki
      |Yamazawa
      |Yanagi
      |Yanase
      |Yano
      |Yasuda
      |Yatabe
      |Yazawa
      |Yofu
      |Yokomine
      |Yokomoto
      |Yokoyama
      |Yomoda
      |Yonai
      |Yonamine
      |Yoneda
      |Yonemoto
      |Yonemura
      |Yoshida
      |Yoshii
      |Yoshikawa
      |Yoshimoto
      |Yoshimura
      |Yoshinaga
      |Yoshishige
      |Yoshioka
      |Yoshizaki
      |Yoshizawa
      |Yotsuya
      |Yumigano
      |Yukimura
      |Yukimoto
      |Yutani
      |Zaan
      |Zen
      |Zenigata
      |Zenitani
      |Zeniya
    """.stripMargin

  val lastNameSet:Set[String] = {
    val r1 = _names.lines.map(_.trim.toLowerCase).filter(!_.isEmpty).toSet
    val str = getClass.getResourceAsStream("/nameparser/japnese-lastnames-ex")
    val exLastNames = Source.fromInputStream(str).getLines()
    val r = r1 ++ exLastNames
    str.close()
    r
  }

  private val LastNameBlackList = Set(
    "wade"
  )

  private val doubleNameSeparator = "\\-"
  private def checkLastName(ln:String):Boolean = {
    val sp = ln.split(doubleNameSeparator)
    sp.forall(isNameFound)
  }

  def isNameFound(lastName:String):Boolean = {
    val n = normalize(lastName)
    if (LastNameBlackList.contains(n)) false
    else lastNameSet.contains(n)
  }

  private val _o = 'ō'
  private[nameParser] def normalize(n:String):String = {
    n.toLowerCase.trim.replace(_o, 'o')
  }

  // https://en.wikipedia.org/wiki/Kunrei-shiki_romanization
  private val _kanas =
    """a
      |ka	ki	ku	 ke	 ko	 kya kyu	 kyo
      |sa	si	 su	 se	 so	 sya	syu	 syo
      |ta	ti	 tu	 te	 to	 tya	tyu	 tyo
      |na	ni	 nu	 ne	 no	 nya	nyu	 nyo
      |ha	hi	 hu	 he	 ho	 hya	hyu	 hyo
      |ma	mi	 mu	 me	 mo	 mya	myu	 myo
      |ya	yu  yo
      |ra	ri	ru  re	ro	rya	ryu	ryo
      |wa	i u	e  o
      |ga	 gi	 gu	 ge	 go	 gya	 gyu gyo
      |za	 zi	 zu	 ze	 zo	 zya	 zyu zyo
      |da	 zi	 zu	 de	 do	 zya	 zyu zyo
      |ba	 bi	 bu	 be	 bo	 bya	 byu byo
      |pa	 pi	 pu	 pe	 po	 pya	 pyu pyo
      |sha	shi	 shu sho
      |tsu
      |cha	chi	chu	cho
      |fu
      |ja	ji	ju	jo
      |di	du
      |dya dyu dyo
      |kwa
      |gwa
      |wo
      |""".stripMargin

  val kanaSet:Set[String] = _kanas.lines.flatMap(_.split("\\s+")).toSet
  private[nameParser] def isKanaFound(kana:String):Boolean = {
    val n = normalize(kana)
    kanaSet.contains(n)
  }

  private val KanaBlackList = Set(
    "anita",
    "dane", "dean",
    "erin", "edison",
    "jason", "jan", "jane", "jamie", "juan", "joshua", "johanna",
    "marisa", "megan", "mariana", "mena",
    "natasha",
    "rose",
    "sabina", "sabine", "susan", "sean", "sonia", "susie", "serena",
    "teresa", "romain", "manu",
    "romina", "romana", "simon", "ryan", "dan", "damien", "erika", "irene", "monika", "sara",
    "tara", "jose", "jaime", "hugo", "marina", "mariza", "ana", "susana",
    "katie", "kate", "joan", "joanne", "karen", "karin", "page", "darin", "sharon", "jodie",
    "aurora", "karine", "mario", "nina", "simona", "mohan", "tonya", "tanya", "robin", "hasan",
    "uma", "mira", "diane", "diana"
  )


  private def checkOneKana(normed:String):Boolean = {
    val k =
      if (normed.endsWith(_n) && !normed.endsWith(_nn))
        normed.substring(0, normed.length-1)
      else normed
    kanaSet.contains(k)
  }

  private val _n = "n"
  private val _nn = "nn"

  private def _checkKanas(normed:String):Boolean = {
    if (KanaBlackList.contains(normed)) false
    else {
      (1 to normed.length).exists { idx =>
        val p0 = normed.substring(0, idx)
        if (checkOneKana(p0)) {
          val p1 = normed.substring(idx)
          if (p1.nonEmpty) checkKanas(p1)
          else true
        }
        else false
      }
    }
  }
  def checkKanas(nn:String):Boolean = {
    var n = normalize(nn)
    _checkKanas(n)
  }

  import org.ditw.nameUtils.nameParser.ParserHelpers._

  def checkKanaInPart(part:String):Boolean = {
    val subparts = splitComponents(part)
    subparts.forall(checkKanas)
  }
  private[nameParser] def isJapaneseName(lastName:String, firstName:String):Boolean = {
    if (checkLastName(lastName)) {
      if (firstName.length > 1) {
        val parts = splitBySpace(firstName)
        if (parts.nonEmpty) {
          if (!checkKanaInPart(parts(0))) false
          else {
            if (parts.length == 1) true
            else if (parts.length == 2) {
              if (parts(1).length == 1) {
                val initial = parts(0).head.toLower
                parts(1).head.toLower == initial
              }
              else false
            }
            else false
          }
        }
        else false
      }
      else true
    }
    else false
  }
}
